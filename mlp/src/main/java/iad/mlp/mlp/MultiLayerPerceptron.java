package iad.mlp.mlp;

import iad.mlp.actfun.ActivationFunction;
import iad.mlp.utils.ConfusionMatrix;
import iad.mlp.utils.MLPUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiLayerPerceptron implements Serializable {

    private double learningRate;
    private double momentum;
    private boolean useBias;
    private Layer[] layers;
    private ActivationFunction actFun;
    private double[] quadFunVals;
    private double[] percentageError;
    private int CM_PER_EPOCH;

    public MultiLayerPerceptron(int[] layersInfo, double learrningRate,
                                double momentum, boolean useBias,
                                ActivationFunction actFun,
                                double gaussDiv) {
        this.learningRate = learrningRate;
        this.momentum = momentum;
        this.useBias = useBias;
        this.actFun = actFun;
        Neuron.gaussDiv = gaussDiv;
        createLayers(layersInfo);
        CM_PER_EPOCH = 5;
    }

    public void learn(int epochs, double[][] inputs, double[][] outputs) {

        quadFunVals = new double[epochs];
        List<Double> percErrList = new ArrayList<>();
        double[][] epochsOuts = new double[inputs.length][];

        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < outputs.length; i++) {
            indexes.add(i);
        }

        for (int i = 0; i < epochs; i++) {
            Collections.shuffle(indexes);
            for (int j = 0; j < inputs.length; j++) {
                double[] epochOut = backPropagate(inputs[indexes.get(j)],
                        outputs[indexes.get(j)]);
                quadFunVals[i] += MLPUtils.quadraticCostFun(
                        epochOut, outputs[indexes.get(j)]);
            }

            if (i % CM_PER_EPOCH == 0) {
                for (int j = 0; j < inputs.length; j++) {
                    epochsOuts[j] = execute(inputs[j]);
                }

                ConfusionMatrix cm = new ConfusionMatrix(epochsOuts, outputs);
                percErrList.add(100.0 * cm.falsePositivesTotal / inputs.length);
                System.out.println(i + ": " + quadFunVals[i] + " " +
                        percErrList.get(percErrList.size() - 1));
            }
        }

        percentageError = percErrList.stream().mapToDouble(d -> d).toArray();
    }

    public double[] execute(double[] input) {
        // Put input to the first layer
        for (int i = 0; i < layers[0].neuronsNum; i++) {
            layers[0].neurons[i].value = input[i];
        }

        // transfer input from first layer to next layers
        for (int i = 1; i < layers.length; i++) {
            transferInput(i);
        }

        // get output form the last layer
        double output[] = new double[layers[layers.length - 1].neuronsNum];
        for (int i = 0; i < layers[layers.length - 1].neuronsNum; i++) {
            output[i] = layers[layers.length - 1].neurons[i].value;
        }

        return output;
    }

    public double[] backPropagate(double[] input, double[] exOutput) {
        double newOutput[] = execute(input);

        updateLastLayerDelta(newOutput, exOutput);

        for (int i = layers.length - 2; i >= 0; i--) {
            updateLayerDelta(i);
            updateLayerWeightsAndBias(i);
        }

        return newOutput;
    }

    private void updateLastLayerDelta(double[] newOutput, double exOutput[]) {
        double error, errAct;
        for (int i = 0; i < layers[layers.length - 1].neuronsNum; i++) {
            error = exOutput[i] - newOutput[i];
            errAct = error * actFun.evaluteDerivate(newOutput[i]);
            layers[layers.length - 1].neurons[i].delta = errAct;
        }
    }

    private void updateLayerDelta(int layer) {
        double error, delta, weight, errAct;
        for (int i = 0; i < layers[layer].neuronsNum; i++) {
            error = 0.0;
            for (int j = 0; j < layers[layer + 1].neuronsNum; j++) {
                delta = layers[layer + 1].neurons[j].delta;
                weight = layers[layer + 1].neurons[j].weights[i];
                error += delta * weight;
            }
            errAct = error * actFun.evaluteDerivate(layers[layer].neurons[i].value);
            layers[layer].neurons[i].delta = errAct;
        }
    }

    private void updateLayerWeightsAndBias(int layer) {
        double delta, value, weightsDiff, weightCurr, weightPrev, deltaValLr;

        for (int i = 0; i < layers[layer + 1].neuronsNum; i++) {
            for (int j = 0; j < layers[layer].neuronsNum; j++) {
                delta = layers[layer + 1].neurons[i].delta;
                value = layers[layer].neurons[j].value;
                deltaValLr = delta * value * learningRate;

                weightCurr = layers[layer + 1].neurons[i].weights[j];
                weightPrev = layers[layer + 1].neurons[i].prevWeights[j];
                weightsDiff = momentum * (weightCurr - weightPrev);

                layers[layer + 1].neurons[i].prevWeights[j] = weightCurr;
                layers[layer + 1].neurons[i].weights[j] += deltaValLr + weightsDiff;
            }
            delta = layers[layer + 1].neurons[i].delta;
            layers[layer + 1].neurons[i].bias += learningRate * delta;
        }
    }

    private void transferInput(int layer) {
        double newValue, weight, value;
        for (int i = 0; i < layers[layer].neuronsNum; i++) {
            newValue = 0.0;
            for (int j = 0; j < layers[layer - 1].neuronsNum; j++) {
                weight = layers[layer].neurons[i].weights[j];
                value = layers[layer - 1].neurons[j].value;
                newValue += weight * value;
            }
            if (useBias) {
                newValue += layers[layer].neurons[i].bias;
            }
            layers[layer].neurons[i].value = actFun.evalute(newValue);
        }
    }

    private void createLayers(int[] layersInfo) {
        layers = new Layer[layersInfo.length];
        for (int i = 0; i < layersInfo.length; i++) {
            if (i == 0) {
                this.layers[i] = new Layer(layersInfo[i], 0);
            } else {
                this.layers[i] = new Layer(layersInfo[i], layersInfo[i - 1]);
            }
        }
    }

    public Layer[] getLayers() {
        return layers;
    }

    public double[] getQuadFunVals() {
        return quadFunVals;
    }

    public double[] getPercentageError() {
        return percentageError;
    }

    public void setCmPerEpoch(int cmPerEpoch) {
        CM_PER_EPOCH = cmPerEpoch;
    }
}