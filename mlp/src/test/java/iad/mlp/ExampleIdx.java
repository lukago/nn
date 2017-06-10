package iad.mlp;

import iad.mlp.actfun.ActivationFunction;
import iad.mlp.actfun.Sigmoidal;
import iad.mlp.mlp.MultiLayerPerceptron;
import iad.mlp.utlis.IdxManager;
import iad.mlp.utlis.MLPUtils;

import java.io.File;

/**
 * Created on 6/10/2017.
 */
public class ExampleIdx {
    public static void main(String[] args) {

        int picsNum = 100;
        IdxManager idx = MLPUtils.deserialize("data/idx.ser");
        double[][] inputs = getRows(picsNum, idx.getData());
        double[][] outputs = getRows(picsNum, idx.getLabels());

        // initalize mlp
        String cmd = "gnuplot -c " +
                System.getProperty("user.dir") +
                "/gnuplot/plotIDX.gpt ";

        int[] layers = new int[]{
                inputs[0].length,
                inputs[0].length * 2,
                outputs[0].length};

        ActivationFunction f = new Sigmoidal();
        boolean useBias = true;
        double learningRate = 0.2;
        double momentum = 0.8;
        double div = 1;
        int epochs = 100;

        MultiLayerPerceptron mlp = new MultiLayerPerceptron(
                layers,
                learningRate,
                momentum,
                useBias, f,
                div);

        mlp.learn(epochs, inputs, outputs);

        new File("results").mkdir();
        MLPUtils.serialize(mlp, "results/mlp.bin");


        double[][] outFinal = new double[picsNum][];
        for (int i = 0; i < picsNum; i++) {
            outFinal[i] = mlp.execute(inputs[i]);
        }
        double[] error = MLPUtils.calcError(outFinal, outputs);

        System.out.println("Writing results...");
        MLPUtils.writeVector("results/quad.data", mlp.getQuadFunVals());
        MLPUtils.writeMatrix("results/out.data", outFinal);
        MLPUtils.writeMatrix("results/out1.data", outputs);
        MLPUtils.writeMatrix("results/out2.data", idx.getLabels());
        MLPUtils.writeVector("results/error.data", error);
        MLPUtils.rumCmd(cmd);
    }

    private static double[][] getRows(int end, double[][] array) {
        double[][] retMatrix = new double[end][];
        for (int i = 0; i < end; i++) {
            retMatrix[i] = array[i];
        }
        return retMatrix;
    }
}
