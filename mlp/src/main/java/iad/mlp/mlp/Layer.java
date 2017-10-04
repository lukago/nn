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
}
