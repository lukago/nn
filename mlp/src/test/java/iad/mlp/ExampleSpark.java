package iad.mlp;

import iad.mlp.utlis.ConfusionMatrix;
import iad.mlp.utlis.IOUtils;
import iad.mlp.utlis.IdxManager;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 6/9/2017.
 */
public class ExampleSpark {

    public static void main(String[] args) {

        Logger.getLogger("org").setLevel(Level.WARN);

        SparkConf sparkConf = new SparkConf()
                .setAppName("ExampleSpark")
                .setMaster("local");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        //String in = "data/iris2.data";
        //String out = "data/iris2outSVM.data";

        //double[][] inputs = IOUtils.readMatrix(in, ",");
        //double[] outputs = IOUtils.readVector(out);

        IdxManager idx = IOUtils.deserialize("data/idx.ser");
        IdxManager idxTest = IOUtils.deserialize("data/idx-test.ser");
        double[][] inputs = idx.getData();
        double[] outputs = idx.getLabelsVec();
        double[][] inputsTest = idxTest.getData();
        double[] outputsTest = idxTest.getLabelsVec();

        List<LabeledPoint> pointList = new ArrayList<>();
        for (int i = 0; i < outputs.length; i++) {
            pointList.add(new LabeledPoint(outputs[i], Vectors.dense(inputs[i])));
        }

        List<LabeledPoint> pointListTest = new ArrayList<>();
        for (int i = 0; i < outputsTest.length; i++) {
            pointListTest.add(new LabeledPoint(outputsTest[i],
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
        Integer numClasses = 10;
        Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
        String impurity = "gini";
        Integer maxDepth = 10;
        Integer maxBins = 32;

        // Train a DecisionTree model for classification.
        final DecisionTreeModel model = DecisionTree.trainClassifier(trainingData,
                numClasses, categoricalFeaturesInfo, impurity, maxDepth, maxBins);

        // Evaluate model on test instances and compute test error
        JavaPairRDD<Double, Double> predictionAndLabel =
                testData.mapToPair(
                        p -> new Tuple2<>(model.predict(p.features()), p.label()));
        Double testErr = 1.0 * predictionAndLabel.filter(
                pl -> !pl._1().equals(pl._2())).count() / testData.count();

        // results
        new File("results").mkdir();
        IOUtils.writeStr("results/dtree_error", Double.toString(testErr));
        IOUtils.writeStr("results/dtree_model", model.toDebugString());

        double[][] outFinal = new double[outputsTest.length][];
        for (int i = 0; i < outputsTest.length; i++) {
            outFinal[i] = valToVec(model.predict(Vectors.dense(inputsTest[i])));
        }

        ConfusionMatrix cm = new ConfusionMatrix(outFinal, idxTest.getLabels());
        cm.writeClassErrorMatrix("results/confusion_matrix.data");
    }

    private static double[] valToVec(double val) {
        double[] retArr = new double[10];
        retArr[(int) val] = 1.0;
        return retArr;
    }
}
