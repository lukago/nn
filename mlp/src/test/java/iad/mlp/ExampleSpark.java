package iad.mlp;

import iad.mlp.utlis.MLPUtils;
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

        String in = "data/iris2.data";
        String out = "data/iris2outSVM.data";

        double[][] inputs = MLPUtils.readMatrix(in, ",");
        double[] outputs = MLPUtils.readVector(out);

        List<LabeledPoint> pointList = new ArrayList<>();
        for (int i=0 ; i<outputs.length; i++) {
            pointList.add(new LabeledPoint(outputs[i], Vectors.dense(inputs[i])));
        }

        JavaRDD<LabeledPoint> data = jsc.parallelize(pointList);

        // Split the data into training and test sets (30% held out for testing)
        JavaRDD<LabeledPoint>[] splits = data.randomSplit(new double[]{0.7, 0.3});
        JavaRDD<LabeledPoint> trainingData = splits[0];
        JavaRDD<LabeledPoint> testData = splits[1];

        // Set parameters.
        // Empty categoricalFeaturesInfo indicates all features are continuous.
        Integer numClasses = 3;
        Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
        String impurity = "gini";
        Integer maxDepth = 5;
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

        // Print results
        System.out.println("Test Error: " + testErr);
        System.out.println("Learned tree model:\n" + model.toDebugString());

        for (int i=0 ; i<outputs.length; i++) {
            System.out.println(i + ": " + model.predict(Vectors.dense(inputs[i])));
        }

    }
}
