package iad.lab3;

import java.util.Random;

public class Neuron {
	public double Value;
	public double[] Weights;
	public double[] PrevWeights;
	public double Bias;
	public double Delta;
	
	private static Random random = new Random();

	public Neuron(int prevLayerSize) {
		Weights = new double[prevLayerSize];
		PrevWeights = new double[prevLayerSize];
		Bias = random.nextGaussian();
		Delta = random.nextGaussian();
		Value = random.nextGaussian();

		for (int i = 0; i < Weights.length; i++) {
			Weights[i] = random.nextGaussian();
		}
	}
}
