package com.cac.camp.camp;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Debug;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.NonSparseToSparse;
import weka.core.FastVector;

/**
 * Created by jensemil on 28/11/14.
 */
public class WekaDataGenerator {

    public static void saveArff(List<DataWindow> dataWindows, String fileName, String className) {
        Instances sparseDataset = createArff(dataWindows, className);
        ArffSaver arffSaverInstance = new ArffSaver();
        arffSaverInstance.setInstances(sparseDataset);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName + ".arff");

        // saving file
        try {
            arffSaverInstance.setFile(file);
            arffSaverInstance.writeBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Instances createArff(List<DataWindow> windows, String className) {
        //Remove the first and last element. It might be bad data
        if(windows.size() > 3) {
            windows.remove(windows.size()-1);
            windows.remove(0);
        }

        FastVector attributes;
        Instances dataSet;
        double[] values;
        attributes = new FastVector();

        FastVector classNameList = new FastVector();
        classNameList.addElement("calmParty");
        classNameList.addElement("normalParty");
        classNameList.addElement("wildParty");


        attributes.addElement(new Attribute("maxMag"));
        attributes.addElement(new Attribute("minMag"));
        attributes.addElement(new Attribute("integral"));

        Attribute classNames = new Attribute("class", classNameList);

        attributes.addElement(classNames);

        dataSet = new Instances("DataWindow", attributes, 0);

        Iterator<DataWindow> iter = windows.iterator();
        while (iter.hasNext()) {
            DataWindow dw = iter.next();
            values = new double[dataSet.numAttributes()];
            values[0] = dw.getMax();
            values[1]= dw.getMin();
            values[2] = dw.getIntegral();

            if (className.equals("classify")) {
                // we are currently classifying
                values[3] = 0;
            } else {

                if (className.equals("calmParty")) {
                    values[3] = 0;
                } else if (className.equals("normalParty")) {
                    values[3] = 1;
                } else {
                    values[3] = 2;
                }

            }


            Instance instance = new SparseInstance(1.0, values);
            //instance.setClassValue(className);
            dataSet.add(instance);

        }

        NonSparseToSparse nonSparseToSparseInstance = new NonSparseToSparse();

        try {
            nonSparseToSparseInstance.setInputFormat(dataSet);
            Instances sparseDataset = Filter.useFilter(dataSet, nonSparseToSparseInstance);

            return sparseDataset;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void classify(List<DataWindow> dataWindowsCopy, LogAccActivity activity, AssetManager assetMgr) {

        for (DataWindow dw : dataWindowsCopy) {
            Double d = dw.getMax();
            Log.e("test", "maxMag: " + d);
        }


        String rootPath = "";
        J48 cls = new J48();
        try {
            ObjectInputStream ois = new ObjectInputStream(assetMgr.open(rootPath+"j48-party-classifier.model"));
            cls = (J48) ois.readObject();
            ois.close();
            //cls = (Classifier) weka.core.SerializationHelper.read(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //predict instance class values
        Instances originalTrain = createArff(dataWindowsCopy, "classify"); //load or create Instances to predict
        originalTrain.setClassIndex(originalTrain.numAttributes() - 1);


        //perform your prediction
        double value= 0;
        String result;
        List<String> results = new ArrayList<String>();

        try {
            for (int i = 0; i < dataWindowsCopy.size(); i++) {
                value = cls.classifyInstance(originalTrain.instance(i));
                String prediction = originalTrain.classAttribute().value((int)value);
                result = "The predicted value of window " +
                        Integer.toString(i) +
                        ": " + prediction + "\n";
                System.out.println(result);

                results.add(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //get the name of the class value
        activity.presentClassification(results);
    }


    public static String classify(List<DataWindow> dataWindowsCopy, AssetManager assetMgr, Activity activity) {

        String rootPath = "";
        J48 cls = new J48();
        try {
            ObjectInputStream ois = new ObjectInputStream(assetMgr.open(rootPath+"j48-party-classifier.model"));
            cls = (J48) ois.readObject();
            ois.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //predict instance class values
        Instances originalTrain = createArff(dataWindowsCopy, "classify"); //load or create Instances to predict
        originalTrain.setClassIndex(originalTrain.numAttributes() - 1);


        //perform your prediction
        double value;

        List<String> results = new ArrayList<String>();

        try {
            String hmmString = "";

            for (int i = 0; i < dataWindowsCopy.size(); i++) {
                value = cls.classifyInstance(originalTrain.instance(i));
                String prediction = originalTrain.classAttribute().value((int)value);

                results.add(prediction);

                if (prediction.equals("calmParty")) hmmString += "L"; // L for loungy
                if (prediction.equals("normalParty")) hmmString += "A"; // A for active
                if (prediction.equals("wildParty")) hmmString += "C"; // C for crazy
            }

            hmmString += "\n";
            FileLogger.write(hmmString, "wekaoutput.log", activity.getApplicationContext());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return getMaxClass(results, activity);
    }

    public static String getMaxClass(List<String> results, Activity activity) {
        // count number of occurrences in data collection

        Map<String, Integer> hm = new HashMap<String, Integer>();
        for (String pred : results) {

            if ( hm.containsKey(pred) ) {
                Integer count = hm.get(pred);
                hm.put(pred, count + 1);
            } else {
                hm.put(pred, 1);
            }


        }


        // get the resulting most probable classification
        String maxClass = hm.keySet().iterator().next();
        for (Map.Entry<String, Integer> entry : hm.entrySet()) {
            int currentCount = entry.getValue();
            int maxCount = hm.get(maxClass);
            if (currentCount > maxCount)
                maxClass = entry.getKey();
        }

        return maxClass;
    }
}
