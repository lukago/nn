package iad.task1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Kohonen {

	private int neuronsNum;
	private int dataNum;
	private int dimensions;
	private int iterations;
	private boolean normalize;
	private double mapRadius;
	private double learningRateStart;
	private String filepath;
	private String separator;
	private String gptCols;
	private List<List<Double>> neurons;
	private List<Integer> indexes;
	private List<Double> sds;
	private List<Double> distances;

	private final String destDir = "results_khn/";
	private final String destFile = "khn.data";
	private final String destFileNeurons = "neurons.data";

	public Kohonen(int neuronsNum, int dimensions, String filepath, String separator, boolean normalize, String gptCols,
			double radius, double lambda, int iterations) {
		this.neuronsNum = neuronsNum;
		this.dimensions = dimensions;
		this.filepath = filepath;
		this.separator = separator;
		this.normalize = normalize;
		this.gptCols = gptCols;
		this.dataNum = FileHandler.getFileRowsNum(filepath);
		this.mapRadius = radius;
		this.learningRateStart = lambda;
		this.iterations = iterations;
		neurons = new ArrayList<List<Double>>();
		indexes = new ArrayList<Integer>();
		distances = new ArrayList<Double>();
		sds = FileHandler.calcSds(filepath, separator, dimensions);

		if (normalize) {
			FileHandler.normalize(filepath, separator, false, sds);
		}

		FileHandler.makeEmptyDir(destDir);
		FileHandler.copy(filepath, destDir + destFile);
	}

	private void initNeurons() {
		List<Double> avgs = new ArrayList<Double>();
		List<Double> sdevs = new ArrayList<Double>();
		List<Double> tmp = new ArrayList<Double>();
		Random r = new Random();

		for (int i = 0; i < dimensions; i++) {
			avgs.add(DataMath.arithmeticMean(FileHandler.getColumn(i, filepath, separator)));
			sdevs.add(DataMath.standardDeviation(FileHandler.getColumn(i, filepath, separator)));
		}

		for (int i = 0; i < neuronsNum; i++) {
			for (int j = 0; j < dimensions; j++) {
				tmp.add(r.nextGaussian() * sdevs.get(j) + avgs.get(j));
			}
			neurons.add(tmp);
			tmp = new ArrayList<Double>();
		}

		FileHandler.writeMatrix(neurons, destDir + destFileNeurons, separator);
	}

	/**
	 * Calc vector of indexes to closest neuron for each data point
	 */
	private void writeMinDistNeurons() {
		List<Double> dataPoint = new ArrayList<Double>();
		List<Double> distsToNeurons = new ArrayList<Double>();
		indexes = new ArrayList<Integer>();

		for (int i = 0; i < dataNum; i++) {
			dataPoint = FileHandler.getRow(i, filepath, separator);

			// create array with distances to each kp for one data point
			distsToNeurons = new ArrayList<Double>();
			for (int j = 0; j < neuronsNum; j++) {
				distsToNeurons.add(Metric.euclidean(neurons.get(j), dataPoint));
			}

			// get index of min from created array and add it to returned vector
			double min = DataMath.min(distsToNeurons);
			distances.add(min);
			for (int j = 0; j < distsToNeurons.size(); j++) {
				if (min == distsToNeurons.get(j)) {
					indexes.add(j);
					break;
				}
			}
		}

		FileHandler.appendColumn(filepath, destDir + destFile, separator, indexes);
	}

	private void learn(int epoch) {
		double distFromBMU, radiusOfNeigh, learningRate, influence, newWeight;
		int pointIndex;

		Random r = new Random();
		pointIndex = r.nextInt(dataNum);

		List<Double> point = FileHandler.getRow(pointIndex, filepath, separator);
		List<Double> nearestNeuron = neurons.get(indexes.get(pointIndex));
		double timeConst = (double) iterations / mapRadius;

		learningRate = learningRateStart;
		for (int i = 0; i < neuronsNum; i++) {
			for (int j = 0; j < neurons.get(i).size(); j++) {
				distFromBMU = Metric.euclidean(nearestNeuron, neurons.get(i));
				radiusOfNeigh = mapRadius * Math.exp(-(double) epoch / timeConst);
				learningRate = learningRate * Math.exp(-(double) epoch / iterations);
				influence = Math.exp(-(distFromBMU*distFromBMU) / (2 * radiusOfNeigh * radiusOfNeigh));
				newWeight = neurons.get(i).get(j) + learningRate * influence * (point.get(j) - neurons.get(i).get(j));
				neurons.get(i).set(j, newWeight);
			}
		}

		FileHandler.writeMatrix(neurons, destDir + destFileNeurons, separator);
	}

	public void calc() {
		try {
			initNeurons();
			writeMinDistNeurons();

			final Runtime rt = Runtime.getRuntime();
			String cmd = "gnuplot -c " + System.getProperty("user.dir") + "/plot_khn.gpt " + gptCols;
			rt.exec(cmd);

			for (int i = 0; i < iterations; i++) {
				writeMinDistNeurons();
				learn(i);

				try {
					TimeUnit.MILLISECONDS.sleep(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (normalize) {
				FileHandler.normalize(filepath, separator, true, sds);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
