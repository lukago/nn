package iad.mlp;

import java.io.Serializable;
import java.util.Random;

public class Neuron implements Serializable {

	private static final long serialVersionUID = -5899845557319234523L;
	private static Random random = new Random();
	public static double gaussDiv = 25.0;
	
	public double value;
	public double[] weights;
	public double[] prevWeights;
	public double bias;
	public double delta;

	public Neuron(int prevLayerSize) {
		weights = new double[prevLayerSize];
		prevWeights = new double[prevLayerSize];
		bias = random.nextGaussian();
		delta = random.nextGaussian();
		value = random.nextGaussian();

		for (int i = 0; i < weights.length; i++) {
			weights[i] = random.nextGaussian() / gaussDiv;
		}
	}
	
	@Override
	public String toString() {
		String ret = "Weights: ";
		for (double i : weights) {
			ret += i + " ";
		}
		ret += "\nValue: " + value;
		return ret;
	}
	
}
