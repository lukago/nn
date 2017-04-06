package iad.task1;

import java.util.List;
import java.util.Random;

public class Kohonen extends Neural {

	private double mapRadiusStart;
	private double learningRateStart;
	private double timeConst;

	public final String destDir = "results_khn/";
	public final String destFile = "khn.data";
	public final String neuronsFile = "neurons.data";
	public final String imgcprFile = "imgcpr.data";
	public final double minRadius = 1.0E-10;

	public Kohonen(int neuronsNum, int iterations, String srcFilePath, String separator, boolean normalize,
			double mapRadiusStart, double learningRateStart, double timeConst) {
		super(neuronsNum, iterations, srcFilePath, separator, normalize);

		this.mapRadiusStart = mapRadiusStart;
		this.learningRateStart = learningRateStart;
		this.timeConst = (iterations) / (mapRadiusStart * timeConst);

		FileHandler.makeEmptyDir(destDir);
		FileHandler.copy(srcFilePath, destDir + destFile);
	}

	public void learn(int epoch) {
		double distFromBMU, mapRadius, learningRate, influence, newWeight;
		int pointIndex;

		Random r = new Random();
		pointIndex = r.nextInt(data.size());

		List<Double> point = data.get(pointIndex);
		List<Double> nearestNeuron = neurons.get(winnerIds.get(pointIndex));

		mapRadius = mapRadiusStart * Math.exp(-(double) epoch / timeConst);
		learningRate = learningRateStart * Math.exp(-(double) epoch / iterations);

		if (mapRadius < minRadius) {
			mapRadius = minRadius;
		}
		// System.out.println(mapRadius + "\t" + learningRate + "\t");

		for (int i = 0; i < neurons.size(); i++) {
			distFromBMU = Metric.euclidean(nearestNeuron, neurons.get(i));
			influence = Math.exp(-(distFromBMU * distFromBMU) / (2 * mapRadius * mapRadius));

			for (int j = 0; j < neurons.get(i).size(); j++) {
				newWeight = neurons.get(i).get(j) + learningRate * influence * (point.get(j) - neurons.get(i).get(j));
				neurons.get(i).set(j, newWeight);
			}
		}
	}

	public void calc(boolean plot, String gptCols, String plotFile) {
		calcWinnersIds();

		if (plot) {
			FileHandler.writeMatrixWithId(neurons, destDir + neuronsFile, separator);
			FileHandler.appendColumn(srcFilePath, destDir + destFile, separator, winnerIds);
			String cmd = "gnuplot -c " + System.getProperty("user.dir") + "/" + plotFile + " " + gptCols;
			Utils.rumCmd(cmd);
		}

		for (int i = 0; i < iterations; i++) {
			calcWinnersIds();
			learn(i);

			if (plot) {
				FileHandler.writeMatrixWithId(neurons, destDir + neuronsFile, separator);
				FileHandler.appendColumn(srcFilePath, destDir + destFile, separator, winnerIds);
			}

			System.out.println(i);
		}

		FileHandler.writePointsAsClusters(winnerIds, neurons, destDir + imgcprFile, separator);
	}
}
