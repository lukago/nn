package iad.mlp;

import iad.mlp.actfun.ActivationFunction;
import iad.mlp.actfun.Sigmoidal;
import iad.mlp.mlp.MultiLayerPerceptron;
import iad.mlp.utils.ConfusionMatrix;
import iad.mlp.utils.IOUtils;
import iad.mlp.utils.MLPUtils;
import iad.mlp.utils.SubsetManager;

import java.io.File;

public class ExampleMlp {

    public static void main(String[] args) {

        // initalize mlp
        int cmPerEpoch = 100;
        int[] layers = new int[]{4, 100, 3};
        double learningRate = 0.01;
        double momentum = 0.8;
        boolean useBias = true;
        ActivationFunction f = new Sigmoidal();
        int epochs = 9000;
        double div = 25;
        String in = "data/iris2.data";
        String out = "data/iris2out.data";
        String cmd = "gnuplot -c " + System.getProperty("user.dir")
                + "/gnuplot/plotIDX.gpt " + cmPerEpoch;

        MultiLayerPerceptron mlp = new MultiLayerPerceptron(layers, learningRate,
                momentum, useBias, f, div);

        // local
        double[][] inputs = IOUtils.readMatrix(in, ",");
        double[][] outputs = IOUtils.readMatrix(out, " ");
        // MLPUtils.normalize(inputs);

        SubsetManager sm = new SubsetManager(inputs, outputs, 0.8);
        sm.calc();

        inputs = sm.getData();
        outputs = sm.getLabels();
        double[][] inputsTest = sm.getDataTest();
        double[][] outputsTest = sm.getLabelsTest();

        mlp.setCmPerEpoch(cmPerEpoch);
        long startTime = System.currentTimeMillis();
        System.out.println(inputs.length);
        mlp.learn(epochs, inputs, outputs);
        long endTime = System.currentTimeMillis();
        long learnTime = endTime - startTime;

        new File("results").mkdir();
        IOUtils.serialize(mlp, "results/mlp.bin");
        mlp = IOUtils.deserialize("results/mlp.bin");

        double[][] outFinal = new double[inputsTest.length][];
        for (int i = 0; i < inputsTest.length; i++) {
            outFinal[i] = mlp.execute(inputsTest[i]);
        }
        double[] error = MLPUtils.calcError(outFinal, outputsTest);

        IOUtils.writeVector("results/quad.data", mlp.getQuadFunVals());
        IOUtils.writeMatrix("results/out.data", outFinal);
        IOUtils.writeMatrix("results/ex_out.data", outputs);
        IOUtils.writeVector("results/error.data", error);
        IOUtils.writeVector("results/perc_error.data", mlp.getPercentageError());
        IOUtils.writeStr("results/learn_time_ms.data", Long.toString(learnTime));

        ConfusionMatrix cm = new ConfusionMatrix(outFinal, outputsTest);
        cm.writeClassErrorMatrix("results/confusion_matrix.data");

        IOUtils.rumCmd(cmd);
    }
}
