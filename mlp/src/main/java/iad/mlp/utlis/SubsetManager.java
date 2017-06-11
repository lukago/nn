package iad.mlp.utlis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 6/11/2017.
 */
public class SubsetManager {

    double[][] data;
    double[][] labels;
    double[][] dataTest;
    double[][] labelsTest;
    double percentage;

    public SubsetManager(double[][] data, double[][] labels, double percentage) {
        this.data = data;
        this.labels = labels;
        this.percentage = percentage;
    }

    public void calc() {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            indexes.add(i);
        }
        Collections.shuffle(indexes);

        int newLen = (int) (percentage * data.length);
        int testLen = data.length - newLen;
        double[][] dataSub = new double[newLen][];
        double[][] labelsSub = new double[newLen][];
        dataTest = new double[testLen][];
        labelsTest = new double[testLen][];

        for (int i = 0; i < newLen; i++) {
            dataSub[i] = data[indexes.get(i)];
            labelsSub[i] = labels[indexes.get(i)];
        }

        for (int i = 0; i < testLen; i++) {
            dataTest[i] = data[indexes.get(newLen + i)];
            labelsTest[i] = labels[indexes.get(newLen + i)];
        }
        data = dataSub;
        labels = labelsSub;
    }

    public double[][] getData() {
        return data;
    }

    public double[][] getLabels() {
        return labels;
    }

    public double[][] getDataTest() {
        return dataTest;
    }

    public double[][] getLabelsTest() {
        return labelsTest;
    }
}
