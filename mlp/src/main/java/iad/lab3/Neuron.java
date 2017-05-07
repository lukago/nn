package iad.lab3;

import java.io.Serializable;
import java.util.Random;

public class Neuron implements Serializable {

	private static final long serialVersionUID = -5899845557319234523L;
	
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
			Weights[i] = random.nextGaussian()/25.0;
		}
	}
	
}
