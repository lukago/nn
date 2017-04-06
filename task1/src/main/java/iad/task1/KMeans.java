package iad.task1;

import java.util.ArrayList;
import java.util.List;

public class KMeans extends Neural {

	public final String destDir = "results_km/";
	public final String destFile = "kmeans.data";
	public final String neuronsFile = "neurons.data";
	public final String imgcprFile = "imgcpr.data";
	public final int rerolls = 10;

	public KMeans(int neuronsNum, int iterations, String srcFilePath, String separator, boolean normalize) {
		super(neuronsNum, iterations, srcFilePath, separator, normalize);

		FileHandler.makeEmptyDir(destDir);
		FileHandler.copy(srcFilePath, destDir + destFile);
		
		rerollDead(rerolls);
		deleteDead();
	}
	
	public void learn(int epoch) {
		List<List<Double>> newNeurons = new ArrayList<List<Double>>();
		List<List<Double>> dataCols = new ArrayList<List<Double>>();
		List<Double> colKp = new ArrayList<Double>();
		List<Double> tmpPoint = new ArrayList<Double>();
		
		for (int i = 0; i<dimensions; i++) {
			dataCols.add(Utils.getColumn(data, i));
		}
		
		for (int i = 0; i < neurons.size(); i++) {
			tmpPoint = new ArrayList<Double>();
			
			for (int j = 0; j < dimensions; j++) {		
				colKp = new ArrayList<Double>();
				
				for (int m = 0; m < data.size(); m++) {
					if (winnerIds.get(m) == i) {
						colKp.add(dataCols.get(j).get(m));
					}
				}
				tmpPoint.add(DataMath.arithmeticMean(colKp));
			}
			newNeurons.add(tmpPoint);
		}

		neurons = newNeurons;
	}

	public void calc(boolean plot, String gptCols, String plotFile) {
		if (plot) {
			FileHandler.writeMatrixWithId(neurons, destDir + neuronsFile, separator);
			FileHandler.appendColumn(srcFilePath, destDir + destFile, separator, winnerIds);
			String cmd = "gnuplot -c " + System.getProperty("user.dir") + "/" + plotFile + " " + gptCols;
			Utils.rumCmd(cmd);
		}

		List<List<Double>> prevDataPoints = new ArrayList<List<Double>>();
		for (int i = 0; i < iterations; i++) {
			prevDataPoints = neurons;
			calcWinnersIds();
			learn(i);

			if (plot) {
				FileHandler.writeMatrixWithId(neurons, destDir + neuronsFile, separator);
				FileHandler.appendColumn(srcFilePath, destDir + destFile, separator, winnerIds);
			}

			if (prevDataPoints.containsAll(neurons) && neurons.containsAll(prevDataPoints)) {
				break;
			}

			System.out.println(i);
		}

		FileHandler.writePointsAsClusters(winnerIds, neurons, destDir + imgcprFile, separator);
	}
}
