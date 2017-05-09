package iad.mlp;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import iad.mlp.ActivationFunction;
import iad.mlp.MultiLayerPerceptron;
import iad.mlp.activationFunctions.Sigmoidal;
import iad.mlp.utlis.MLPUtils;

public class ExampleMlp {

	public static void main(String[] args) {

		// initalize mlp
		int[] layers = new int[] { 4, 8, 4 };
		double learningRate = 0.2;
		double momentum = 0.0;
		boolean useBias = false;
		ActivationFunction f = new Sigmoidal();
		int epochs = 10000;
		double div = 50;
		String in = "data/z2_in.data";
		String out = "data/z2_out.data";
		String cmd = "gnuplot -c " + System.getProperty("user.dir") + "/gnuplot/plot.gpt " + out;

		MultiLayerPerceptron mlp = new MultiLayerPerceptron(layers, learningRate, momentum, useBias, f, div);

		// local
		double[] epochOut;
		double[][] inputs = MLPUtils.readMatrix(in, "\t");
		double[][] outputs = MLPUtils.readMatrix(out, "\t");
		double[] quadFunVals = new double[epochs];
		List<Integer> indexes = new ArrayList<>();
		for (int i = 0; i < outputs.length; i++) {
			indexes.add(i);
		}

		for (int i = 0; i < epochs; i++) {
			Collections.shuffle(indexes);
			for (int j = 0; j < inputs.length; j++) {
				epochOut = mlp.backPropagate(inputs[indexes.get(j)], outputs[indexes.get(j)]);
				quadFunVals[i] += MLPUtils.quadraticCostFun(epochOut, outputs[indexes.get(j)]);
			}
		}
		
		new File("results").mkdir();
		MLPUtils.serialize(mlp, "results/mlp.bin");
		mlp = MLPUtils.deserialize("results/mlp.bin");

		double[][] outFinal = new double[inputs.length][];
		for (int i = 0; i < inputs.length; i++) {
			outFinal[i] = mlp.execute(inputs[i]);
			MLPUtils.writeMLPData("results/mlp" + i + ".data", mlp);
		}
		
		
		MLPUtils.writeQuadFun("results/quad.data", quadFunVals);
		MLPUtils.writeResults("results/out.data", outFinal);
		MLPUtils.writeError("results/error.data", outFinal, outputs);
		MLPUtils.rumCmd(cmd);
	}
}
