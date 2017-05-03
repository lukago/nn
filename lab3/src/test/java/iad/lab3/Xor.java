package iad.lab3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import iad.lab3.activationFunctions.Sigmoidal;
import iad.lab3.utlis.MLPUtils;

public class Xor {

	public static void main(String[] args) {
		int[] layers = new int[] { 2, 6, 1 };
		MultiLayerPerceptron net = new MultiLayerPerceptron(layers, 0.2, true, new Sigmoidal());
		
		String in = "data/xor_in.data";
		String out = "data/xor_out.data";
		double[][] inputs = MLPUtils.readMatrix(in, "\t");
		double[][] outputs = MLPUtils.readMatrix(out, "\t");
		List<Integer> indexes = new ArrayList<>();
		for (int i = 0; i<outputs.length; i++) {
			indexes.add(i);
		}

		/* Learning */
		int epochs = 1000;
		double[] quadFunVals = new double[epochs];
		double[] epochOut;
		for (int i = 0; i < epochs; i++) {
			Collections.shuffle(indexes);
			for (int j = 0; j < inputs.length; j++) {
				epochOut = net.backPropagate(inputs[indexes.get(j)], outputs[indexes.get(j)]);
				quadFunVals[i] = MLPUtils.quadraticCostFun(epochOut, outputs[indexes.get(j)]);
			}
		}

		double[][] outFinal = new double [inputs.length][];
		for (int i = 0; i < inputs.length; i++) {
			outFinal[i] = net.execute(inputs[i]);
		}
		
		// write results to plot
		MLPUtils.writeQuadFun("results/quad.data", quadFunVals);
		MLPUtils.writeResults("results/out.data", outFinal);
		
		String cmd = "gnuplot -c " + System.getProperty("user.dir") + "/gnuplot/plot.gpt " + out;
		MLPUtils.rumCmd(cmd);
	}
}
