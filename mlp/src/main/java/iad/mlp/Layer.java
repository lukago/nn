package iad.mlp;

import java.io.Serializable;

public class Layer implements Serializable {

	private static final long serialVersionUID = 7735337090092044738L;
	
	public Neuron neurons[];
	public int neuronsNum;

	public Layer(int neuronsNum, int prevNeuronsNum) {
		this.neuronsNum = neuronsNum;
		neurons = new Neuron[neuronsNum];

		for (int j = 0; j < neuronsNum; j++) {
			neurons[j] = new Neuron(prevNeuronsNum);
		}
	}
	
	@Override
	public String toString() {
		String ret = "LAYER\n";
		for (int i =0; i<neurons.length; i++) {
			ret += "Neuron " + i + ":\n";
			ret += neurons[i].toString() + "\n";
		}
		return ret;
	}
}
