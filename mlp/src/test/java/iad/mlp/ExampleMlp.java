package iad.mlp;

import java.io.File;

import iad.mlp.actfun.ActivationFunction;
import iad.mlp.mlp.MultiLayerPerceptron;
import iad.mlp.actfun.Sigmoidal;
import iad.mlp.utlis.MLPUtils;

public class ExampleMlp {

	public static void main(String[] args) {

		// initalize mlp
		int[] layers = new int[] { 4, 12, 3 };
		double learningRate = 0.01;
		double momentum = 0.9;
		boolean useBias = true;
		ActivationFunction f = new Sigmoidal();
		int epochs = 10000;
		double div = 25;
		String in = "data/iris2.data";
		String out = "data/iris2out.data";
		String cmd = "gnuplot -c " + System.getProperty("user.dir")
                + "/gnuplot/plot.gpt " + out;

		MultiLayerPerceptron mlp = new MultiLayerPerceptron(layers, learningRate,
                momentum, useBias, f, div);

		// local
		double[][] inputs = MLPUtils.readMatrix(in, ",");
		double[][] outputs = MLPUtils.readMatrix(out, " ");

		mlp.learn(epochs, inputs, outputs);
		
		new File("results").mkdir();
		MLPUtils.serialize(mlp, "results/mlp.bin");
		mlp = MLPUtils.deserialize("results/mlp.bin");

		double[][] outFinal = new double[inputs.length][];
		for (int i = 0; i < inputs.length; i++) {
			outFinal[i] = mlp.execute(inputs[i]);
		}
        double[] error = MLPUtils.calcError(outFinal, outputs);

		MLPUtils.writeVector("results/quad.data", mlp.getQuadFunVals());
		MLPUtils.writeMatrix("results/out.data", outFinal);
		MLPUtils.writeVector("results/error.data", error);
		MLPUtils.rumCmd(cmd);
	}
}
