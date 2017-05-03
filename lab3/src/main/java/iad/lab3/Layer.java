package iad.lab3;

public class Layer {
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
