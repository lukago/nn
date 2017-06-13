package iad.mlp;

import iad.mlp.utils.ConfusionMatrix;
import iad.mlp.utils.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.DecisionTree;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import scala.Tuple2;

import java.io.File;
import java.util.*;

/**
 * Created on 6/13/2017.
 */
public class ExamlpeSparkMlp {

    public static void main(String[] args) {

        Logger.getLogger("org").setLevel(Level.WARN);

        SparkConf sparkConf = new SparkConf()
                .setAppName("ExampleSpark")
                .setMaster("local");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        String in = "data/iris2.data";
        String out = "data/iris2out.data";

        double[][] inputs = IOUtils.readMatrix(in, ",");
        double[][] outputs = IOUtils.readMatrix(out, " ");

        //SubsetManager sm = new SubsetManager(inputs, outputs, 0.8);
        //sm.calc();

        //inputs = sm.getData();
        //outputs = sm.getLabels();
        //double[][] inputsTest = sm.getDataTest();
        //double[][] outputsTest = sm.getLabelsTest();
        double[][] inputsTest = inputs;
        double[][] outputsTest = outputs;

        double[] outputsVec= new double[inputs.length];
        for (int i =0; i<outputsVec.length; i++) {
            outputsVec[i] = vecToVal(outputs[i]);
        }

        double[] outputsTestVec= new double[inputsTest.length];
        for (int i =0; i<outputsTestVec.length; i++) {
            outputsTestVec[i] = vecToVal(outputsTest[i]);
        }


        List<LabeledPoint> pointList = new ArrayList<>();
        for (int i = 0; i < outputs.length; i++) {
            pointList.add(new LabeledPoint(outputsVec[i], Vectors.dense(inputs[i])));
        }

        List<LabeledPoint> pointListTest = new ArrayList<>();
        for (int i = 0; i < outputsTest.length; i++) {
            pointListTest.add(new LabeledPoint(outputsTestVec[i],
                    Vectors.dense(inputsTest[i])));
        }

        JavaRDD<LabeledPoint> trainingData = jsc.parallelize(pointList);
        JavaRDD<LabeledPoint> testData = jsc.parallelize(pointListTest);

        // Split the data into training and test sets (30% held out for testing)
        //JavaRDD<LabeledPoint>[] splits = data.randomSplit(new double[]{0.7, 0.3});
        //JavaRDD<LabeledPoint> trainingData = splits[0];
        //JavaRDD<LabeledPoint> testData = splits[1];

        // Set parameters.
        // Empty categoricalFeaturesInfo indicates all features are continuous.
        Integer numClasses = 3;
        Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
        String impurity = "gini";
        Integer maxDepth = 10;
        Integer maxBins = 32;

        // Train a DecisionTree model for classification.
        long startTime = System.currentTimeMillis();
        final DecisionTreeModel model = DecisionTree.trainClassifier(trainingData,
                numClasses, categoricalFeaturesInfo, impurity, maxDepth, maxBins);
        long endTime = System.currentTimeMillis();
        long learnTime = endTime - startTime;

        // Evaluate model on test instances and compute test error
        JavaPairRDD<Double, Double> predictionAndLabel = testData.mapToPair(
                p -> new Tuple2<>(model.predict(p.features()), p.label()));
        Double testErr = 1.0 * predictionAndLabel.filter(
                pl -> !pl._1().equals(pl._2())).count() / testData.count();

        // results
        new File("results").mkdir();
        IOUtils.writeStr("results/dtree_error.data", Double.toString(testErr));
        IOUtils.writeStr("results/dtree_model.data", model.toDebugString());

        double[][] outFinal = new double[outputsTest.length][];
        for (int i = 0; i < outputsTest.length; i++) {
            outFinal[i] = valToVec(model.predict(Vectors.dense(inputsTest[i])));
        }

        ConfusionMatrix cm = new ConfusionMatrix(outFinal, outputsTest);
        cm.writeClassErrorMatrix("results/confusion_matrix.data");
        IOUtils.writeStr("results/learn_time_ms.data", Long.toString(learnTime));
    }

    private static double[] valToVec(double val) {
        double[] retArr = new double[3];
        retArr[(int) val] = 1.0;
        return retArr;
    }

    private static double vecToVal(double[] vec) {
        for (int i = 0; i<vec.length; i++) {
            if (vec[i]==1.0) return i;
        }
        System.out.println(Arrays.toString(vec));
        return -100;
    }
}
