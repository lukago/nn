package iad.lab3;

import java.io.Serializable;

public class Layer implements Serializable {

	private static final long serialVersionUID = 7735337090092044738L;
	
	public Neuron Neurons[];
	public int NeuronsNum;

	public Layer(int neuronsNum, int prevNeuronsNum) {
		NeuronsNum = neuronsNum;
		Neurons = new Neuron[NeuronsNum];

		for (int j = 0; j < NeuronsNum; j++) {
			Neurons[j] = new Neuron(prevNeuronsNum);
		}
	}
}
