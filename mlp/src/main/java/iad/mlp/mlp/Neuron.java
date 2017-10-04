package iad.mlp.mlp;

import java.io.Serializable;
import java.util.Random;

public class Neuron implements Serializable {

    static double gaussDiv = 25.0;
    private static Random random = new Random();
    double value;
    double[] weights;
    double[] prevWeights;
    double bias;
    double delta;

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
}
