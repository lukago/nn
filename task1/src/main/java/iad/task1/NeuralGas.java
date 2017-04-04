package iad.task1;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class NeuralGas extends Kohonen {

	public NeuralGas(int neuronsNum, int dimensions, String filepath, String separator, boolean normalize,
			String gptCols, double mapRadius, double lambda, int iterations, double timeConst) {
		super(neuronsNum, dimensions, filepath, separator, normalize, gptCols, mapRadius, lambda, iterations,
				timeConst);
		this.timeConst = iterations / (mapRadius * timeConst);
	}

	protected void learn(int epoch) {
		double learningRate, influence, newWeight, mapRadiusNew;
		int pointIndex;

		Random r = new Random();
		pointIndex = r.nextInt(dataNum);

		List<Double> point = FileHandler.getRow(pointIndex, filepath, separator);
		List<Double> nearestNeuron = neurons.get(indexes.get(pointIndex));

		mapRadiusNew = mapRadius * Math.exp(-(double) epoch / timeConst);
		learningRate = learningRateStart * Math.exp(-(double) epoch / iterations);
		//System.out.println(mapRadiusNew + "\t" + learningRate + "\t");

		Map<Double, List<Double>> map = new TreeMap<Double, List<Double>>();
		for (int i = 0; i < neuronsNum; i++) {
			map.put(Metric.euclidean(neurons.get(i), nearestNeuron), neurons.get(i));
		}

		int i = 0;
		for (Map.Entry<Double, List<Double>> e : map.entrySet()) {
			List<Double> neuron = e.getValue();
			for (int j = 0; j < neuron.size(); j++) {
				influence = Math.exp(-(i) / (mapRadiusNew));
				newWeight = neuron.get(j) + learningRate * influence * (point.get(j) - neuron.get(j));
				neuron.set(j, newWeight);
			}
			i++;
		}

		FileHandler.writeMatrix(neurons, destDir + destFileNeurons, separator);
	}
}
