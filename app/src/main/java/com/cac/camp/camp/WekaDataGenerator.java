package com.cac.camp.camp;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
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

    public static void saveArff(Instances sparseDataset) throws IOException {
        ArffSaver arffSaverInstance = new ArffSaver();
        arffSaverInstance.setInstances(sparseDataset);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "ESDN.arff");
        arffSaverInstance.setFile(file);


        arffSaverInstance.writeBatch();
    }

    public static void createArff() {
        ArrayList<Attribute> attributes;
        Instances dataSet;
        double[] values;
        attributes = new ArrayList<Attribute>();

        attributes.add(new Attribute("att1"));
        attributes.add(new Attribute("att2"));
        attributes.add(new Attribute("att3"));
        attributes.add(new Attribute("att4"));

        dataSet = new Instances("ESDN", attributes, 0);

        values = new double[dataSet.numAttributes()];
        values[0] = 3;
        values[1] =7;
        values[3] = 1;
        dataSet.add(new SparseInstance(1.0, values));

        values = new double[dataSet.numAttributes()];
        values[2] = 2;
        values[3] = 8;
        dataSet.add(new DenseInstance(1.0, values));

        NonSparseToSparse nonSparseToSparseInstance = new NonSparseToSparse();
        try {
            nonSparseToSparseInstance.setInputFormat(dataSet);
            Instances sparseDataset = Filter.useFilter(dataSet, nonSparseToSparseInstance);

            System.out.println(sparseDataset);

            saveArff(sparseDataset);

        } catch (Exception e) {
            e.printStackTrace();
        }




    }



}
