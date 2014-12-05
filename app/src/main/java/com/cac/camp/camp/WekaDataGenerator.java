package com.cac.camp.camp;

import android.os.Debug;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.NonSparseToSparse;

/**
 * Created by jensemil on 28/11/14.
 */
public class WekaDataGenerator {

    public static void saveArff(List<DataWindow> dataWindows, String fileName, String className) throws IOException {
        Instances sparseDataset = createArff(dataWindows, className);
        ArffSaver arffSaverInstance = new ArffSaver();
        arffSaverInstance.setInstances(sparseDataset);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName + ".arff");
        arffSaverInstance.setFile(file);
        arffSaverInstance.writeBatch();
    }

    public static Instances createArff(List<DataWindow> windows, String className) {
        //Remove the first and last element. It might be bad data
        if(windows.size() > 2) {
            windows.remove(windows.size()-1);
            windows.remove(0);
        }

        ArrayList<Attribute> attributes;
        Instances dataSet;
        double[] values;
        attributes = new ArrayList<Attribute>();

        List<String> classNameList = new ArrayList<String>();
        classNameList.add("running");
        classNameList.add("walk");


        attributes.add(new Attribute("maxMag"));
        attributes.add(new Attribute("minMag"));
        attributes.add(new Attribute("integral"));

        Attribute classNames = new Attribute("class", classNameList);

        attributes.add(classNames);

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
            } else {

                if (className.equals("running")) {
                    values[3] = 0; //OfValue(className);

                } else {
                    values[3] = 1; //OfValue(className);
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


    public static void classify(List<DataWindow> dataWindowsCopy, LogAccActivity activity) {

        String rootPath = "";
        Classifier cls = null;
        try {
            cls = (Classifier) weka.core.SerializationHelper.read(rootPath+"j48-walk-run-model.model");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //predict instance class values
        Instances originalTrain = createArff(dataWindowsCopy, "classify"); //load or create Instances to predict

        //which instance to predict class value
        int s1=0;

        //perform your prediction
        //double value=cls.classifyInstance(originalTrain.instance(s1));

        //get the name of the class value
        String prediction= ""; //originalTrain.classAttribute().value((int)value);

        System.out.println("The predicted value of instance "+
                Integer.toString(s1)+
                ": "+prediction);
    }
}
