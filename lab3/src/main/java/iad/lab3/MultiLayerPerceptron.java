package iad.lab3;

public class MultiLayerPerceptron {
	protected double learningRate;
	protected boolean useBias;
	protected Layer[] layers;
	protected ActivationFunction actFun;

	public MultiLayerPerceptron(int[] layersInfo, double lr, Boolean bias, ActivationFunction fun) {
		learningRate = lr;
		useBias = bias;
		actFun = fun;
		createLayers(layersInfo);
	}

	public double[] execute(double[] input) {
		// Put input to the first layer
		for (int i = 0; i < layers[0].NeuronsNum; i++) {
			layers[0].Neurons[i].Value = input[i];
		}

		// transfer input from first layer to next layers
		for (int i = 1; i < layers.length; i++) {
			transferInput(i);
		}

		// get output form the last layer
		double output[] = new double[layers[layers.length - 1].NeuronsNum];
		for (int i = 0; i < layers[layers.length - 1].NeuronsNum; i++) {
			output[i] = layers[layers.length - 1].Neurons[i].Value;
		}

		return output;
	}

	public double[] backPropagate(double[] input, double[] exOutput) {
		double newOutput[] = execute(input);

		// last layer delta
		updateLastLayerDelta(newOutput, exOutput);

		// back propagate error from last layer to previous layers
		for (int i = layers.length - 2; i >= 0; i--) {
			updateLayerDelta(i);
			updateLayerWeightsAndBias(i);
		}

		// return info about propagation error
		return newOutput;
	}

	private void updateLastLayerDelta(double[] newOutput, double exOutput[]) {
		double error, errAct;
		for (int i = 0; i < layers[layers.length - 1].NeuronsNum; i++) {
			error = exOutput[i] - newOutput[i];
			errAct = error * actFun.evaluteDerivate(newOutput[i]);
			layers[layers.length - 1].Neurons[i].Delta = errAct;
		}
	}

	private void updateLayerDelta(int layer) {
		double error, delta, weight, errAct;
		for (int i = 0; i < layers[layer].NeuronsNum; i++) {
			error = 0.0;
			for (int j = 0; j < layers[layer + 1].NeuronsNum; j++) {
				delta = layers[layer + 1].Neurons[j].Delta;
				weight = layers[layer + 1].Neurons[j].Weights[i];
				error += delta * weight;
			}
			errAct = error * actFun.evaluteDerivate(layers[layer].Neurons[i].Value);
			layers[layer].Neurons[i].Delta = errAct;
		}
	}

	private void updateLayerWeightsAndBias(int layer) {
		double delta, value;
		for (int i = 0; i < layers[layer + 1].NeuronsNum; i++) {
			for (int j = 0; j < layers[layer].NeuronsNum; j++) {
				delta = layers[layer + 1].Neurons[i].Delta;
				value = layers[layer].Neurons[j].Value;
				layers[layer + 1].Neurons[i].Weights[j] += delta * value;
			}
			delta = layers[layer + 1].Neurons[i].Delta;
			layers[layer + 1].Neurons[i].Bias += learningRate * delta;
		}
	}

	private void transferInput(int layer) {
		double newValue, weight, value;
		for (int i = 0; i < layers[layer].NeuronsNum; i++) {
			newValue = 0.0;
			for (int j = 0; j < layers[layer - 1].NeuronsNum; j++) {
				weight = layers[layer].Neurons[i].Weights[j];
				value = layers[layer - 1].Neurons[j].Value;
				newValue += weight * value;
			}
			if (useBias) {
				newValue += layers[layer].Neurons[i].Bias;
			}
			layers[layer].Neurons[i].Value = actFun.evalute(newValue);
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
	
}