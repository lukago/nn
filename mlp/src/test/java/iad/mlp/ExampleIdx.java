package iad.mlp;

import iad.mlp.actfun.ActivationFunction;
import iad.mlp.actfun.Sigmoidal;
import iad.mlp.mlp.MultiLayerPerceptron;
import iad.mlp.utils.*;

import java.io.File;
import java.io.IOException;

/**
 * Created on 6/10/2017.
 */
public class ExampleIdx {
    public static void main(String[] args) {
        //initData();

        int cmPerEpoch = 1;
        //int picsNum = 60000;
        IdxManager idx = IOUtils.deserialize("data/idx.ser");
        //IdxManager idxTest = IOUtils.deserialize("data/idx-test.ser");
        //double[][] inputs = getRows(0, picsNum, idx.getData());
        //double[][] outputs = getRows(0, picsNum, idx.getLabels());
        double[][] inputs = idx.getData();
        double[][] outputs = idx.getLabels();
        double[][] inputsTest = idx.getData();
        double[][] outputsTest = idx.getLabels();

        new File("results").mkdir();

        long startTimeHog = System.currentTimeMillis();
        inputs = HogManager.exportDataFeatures(inputs, idx.getNumOfRows(),
                idx.getNumOfCols());
        inputsTest = HogManager.exportDataFeatures(inputsTest, idx.getNumOfRows(),
                idx.getNumOfCols());
        long endTimeHog = System.currentTimeMillis();
        long learnTimeHog = endTimeHog - startTimeHog;
        IOUtils.writeStr("results/hog_time_ms.data", Long.toString(learnTimeHog));

        MLPUtils.normalize(inputs);
        MLPUtils.normalize(inputsTest);

        // initalize mlp
        String cmd = "gnuplot -c " + System.getProperty("user.dir") +
                "/gnuplot/plotIDX.gpt " + cmPerEpoch;

        int[] layers = new int[]{
                inputs[0].length,
                15,
                outputs[0].length};

        ActivationFunction f = new Sigmoidal();
        boolean useBias = true;
        double learningRate = 0.01;
        double momentum = 0.2;
        double div = 25;
        int epochs = 40;

        MultiLayerPerceptron mlp = new MultiLayerPerceptron(
                layers,
                learningRate,
                momentum,
                useBias, f,
                div);

        mlp.setCmPerEpoch(cmPerEpoch);

        System.out.println(inputs.length);
        System.out.println(inputs[inputs.length / 2].length);
        long startTime = System.currentTimeMillis();
        mlp.learn(epochs, inputs, outputs);
        long endTime = System.currentTimeMillis();
        long learnTime = endTime - startTime;


        IOUtils.serialize(mlp, "results/mlp.bin");


        double[][] outFinal = new double[inputsTest.length][];
        for (int i = 0; i < inputsTest.length; i++) {
            outFinal[i] = mlp.execute(inputsTest[i]);
        }
        double[] error = MLPUtils.calcError(outFinal, outputsTest);

        System.out.println("Writing results...");
        IOUtils.writeVector("results/quad.data", mlp.getQuadFunVals());
        IOUtils.writeVector("results/perc_error.data", mlp.getPercentageError());
        IOUtils.writeMatrix("results/out.data", outFinal);
        IOUtils.writeMatrix("results/ex_out.data", outputsTest);
        IOUtils.writeVector("results/error.data", error);
        IOUtils.writeStr("results/learn_time_ms.data", Long.toString(learnTime));


        ConfusionMatrix cm = new ConfusionMatrix(outFinal, outputsTest);
        cm.writeClassErrorMatrix("results/confusion_matrix.data");

        IOUtils.rumCmd(cmd);
    }

    static double[][] getRows(int start, int end, double[][] array) {
        double[][] retMatrix = new double[end - start][];
        int index = 0;
        for (int i = start; i < end; i++) {
            retMatrix[index++] = array[i];
        }
        return retMatrix;
    }

    static void initData() {
        String inImage = "data/t10k-images-idx3-ubyte";
        String inLabel = "data/t10k-labels-idx1-ubyte";
        try (IdxManager idx = new IdxManager(inImage, inLabel)) {
            idx.load();
            IOUtils.serialize(idx, "data/idx-test.ser");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String inImage2 = "data/train-images-idx3-ubyte";
        String inLabel2 = "data/train-labels-idx1-ubyte";
        try (IdxManager idx = new IdxManager(inImage2, inLabel2)) {
            idx.load();
            IOUtils.serialize(idx, "data/idx.ser");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
