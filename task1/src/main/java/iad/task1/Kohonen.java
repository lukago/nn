package iad.task1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Kohonen {

	protected int neuronsNum;
	protected int dataNum;
	protected int dimensions;
	protected int iterations;
	protected boolean normalize;
	protected double mapRadius;
	protected double learningRateStart;
	protected double timeConst;
	protected String filepath;
	protected String separator;
	protected String gptCols;
	protected List<List<Double>> neurons;
	protected List<Integer> indexes;
	protected List<Double> sds;
	protected boolean image;
	protected List<List<Double>> data;

	public final String destDir = "results_khn/";
	public final String destFile = "khn.data";
	public final String destFileNeurons = "neurons.data";
	public final String imgcprFile = "imgcpr.data";

	public Kohonen(int neuronsNum, int dimensions, String filepath, String separator, boolean normalize, String gptCols,
			double mapRadius, double lambda, int iterations, double timeConst, boolean image) {
		this.neuronsNum = neuronsNum;
		this.dimensions = dimensions;
		this.filepath = filepath;
		this.separator = separator;
		this.normalize = normalize;
		this.gptCols = gptCols;
		this.dataNum = FileHandler.getFileRowsNum(filepath);
		this.mapRadius = mapRadius;
		this.learningRateStart = lambda;
		this.iterations = iterations;
		this.timeConst = (iterations) / (mapRadius * timeConst);
		this.neurons = new ArrayList<List<Double>>();
		this.indexes = new ArrayList<Integer>();
		this.sds = Utils.calcSds(filepath, separator, dimensions);
		this.image = image;

		if (normalize) {
			Utils.normalize(filepath, separator, false, sds);
		}

		FileHandler.makeEmptyDir(destDir);
		FileHandler.copy(filepath, destDir + destFile);
		
		data = FileHandler.readData(filepath, separator);
	}

	protected void initNeurons() {
		List<Double> avgs = new ArrayList<Double>();
		List<Double> sdevs = new ArrayList<Double>();
		List<Double> tmp = new ArrayList<Double>();
		Random r = new Random();

		for (int i = 0; i < dimensions; i++) {
			avgs.add(DataMath.arithmeticMean(Utils.getColumn(data, i)));
			sdevs.add(DataMath.standardDeviation(Utils.getColumn(data, i)));
		}

		for (int i = 0; i < neuronsNum; i++) {
			for (int j = 0; j < dimensions; j++) {
				tmp.add(r.nextGaussian() * sdevs.get(j) + avgs.get(j));
			}
			neurons.add(tmp);
			tmp = new ArrayList<Double>();
		}
	}

	/**
	 * Calc vector of indexes to closest neuron for each data point
	 */
	protected void writeMinDistNeurons() {
		List<Double> dataPoint = new ArrayList<Double>();
		List<Double> distsToNeurons = new ArrayList<Double>();
		indexes = new ArrayList<Integer>();

		for (int i = 0; i < dataNum; i++) {
			dataPoint = data.get(i);

			// create array with distances to each kp for one data point
			distsToNeurons = new ArrayList<Double>();
			for (int j = 0; j < neuronsNum; j++) {
				distsToNeurons.add(Metric.euclidean(neurons.get(j), dataPoint));
			}

			// get index of min from created array and add it to returned vector
			double min = DataMath.min(distsToNeurons);
			for (int j = 0; j < distsToNeurons.size(); j++) {
				if (min == distsToNeurons.get(j)) {
					indexes.add(j);
					break;
				}
			}
		}
	}

	protected void learn(int epoch) {
		double distFromBMU, mapRadiusNew, learningRate, influence, newWeight;
		int pointIndex;

		Random r = new Random();
		pointIndex = r.nextInt(dataNum);

		List<Double> point = data.get(pointIndex);
		List<Double> nearestNeuron = neurons.get(indexes.get(pointIndex));

		mapRadiusNew = mapRadius * Math.exp(-(double) epoch / timeConst);
		learningRate = learningRateStart * Math.exp(-(double) epoch / iterations);
		
		
		if( mapRadiusNew < 1.0E-10) {
			mapRadiusNew = 1.0E-10;
		}
		
		System.out.println(mapRadiusNew + "\t" + learningRate + "\t");

		for (int i = 0; i < neuronsNum; i++) {
			distFromBMU = Metric.euclidean(nearestNeuron, neurons.get(i));
			influence = Math.exp(-(distFromBMU * distFromBMU) / (2 * mapRadiusNew * mapRadiusNew));

			for (int j = 0; j < neurons.get(i).size(); j++) {
				newWeight = neurons.get(i).get(j) + learningRate * influence * (point.get(j) - neurons.get(i).get(j));
				neurons.get(i).set(j, newWeight);
			}
		}
	}

	public void calc() {
		try {
			initNeurons();
			writeMinDistNeurons();
			
			if (!image) {
				FileHandler.writeMatrix(neurons, destDir + destFileNeurons, separator);
				FileHandler.appendColumn(filepath, destDir + destFile, separator, indexes);
				final Runtime rt = Runtime.getRuntime();
				String cmd = "gnuplot -c " + System.getProperty("user.dir") + "/plot_khn.gpt " + gptCols;
				rt.exec(cmd);
			}

			for (int i = 0; i < iterations; i++) {
				writeMinDistNeurons();
				learn(i);
				System.out.println(i);
				
				if (!image) {
					FileHandler.writeMatrix(neurons, destDir + destFileNeurons, separator);
					FileHandler.appendColumn(filepath, destDir + destFile, separator, indexes);
				}
			}

			if (normalize) {
				Utils.normalize(filepath, separator, true, sds);
			}
			
			FileHandler.writePointsAsClusters(indexes, neurons, destDir + imgcprFile, separator);	

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
