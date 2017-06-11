package iad.mlp.mlp;

import java.io.Serializable;

public class Layer implements Serializable {

    Neuron neurons[];
    int neuronsNum;

    public Layer(int neuronsNum, int prevNeuronsNum) {
        this.neuronsNum = neuronsNum;
        neurons = new Neuron[neuronsNum];

        for (int j = 0; j < neuronsNum; j++) {
            neurons[j] = new Neuron(prevNeuronsNum);
        }
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("LAYER\n");
        for (int i = 0; i < neurons.length; i++) {
            ret.append("Neuron ").append(i).append(":\n");
            ret.append(neurons[i].toString()).append("\n");
        }
        return ret.toString();
    }
}
