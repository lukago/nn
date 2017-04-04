package iad.task1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class KMeans {

	private int kPointsNum;
	private int dimensions;
	private boolean normalize;
	private String filepath;
	private String separator;
	private String gptCols;
	private List<List<Double>> kPoints;
	private List<Integer> indexes;
	private List<Double> sds;

	private final String destDir = "results_kmeans/";
	private final String destFile = "kmeans.data";

	public KMeans(int kPointsNum, int dimensions, String filepath, String separator, boolean normalize,
			String gptCols) {
		this.kPointsNum = kPointsNum;
		this.dimensions = dimensions;
		this.filepath = filepath;
		this.separator = separator;
		this.normalize = normalize;
		this.gptCols = gptCols;
		kPoints = new ArrayList<List<Double>>();
		indexes = new ArrayList<Integer>();
		sds = Utils.calcSds(filepath, separator, dimensions);

		if (normalize) {
			Utils.normalize(filepath, separator, false, sds);
		}

		FileHandler.makeEmptyDir(destDir);
		FileHandler.copy(filepath, destDir + destFile);
	}

	private void initKPoints() {
		List<Double> avgs = new ArrayList<Double>();
		List<Double> sdevs = new ArrayList<Double>();
		List<Double> tmp = new ArrayList<Double>();
		Random r = new Random();

		for (int i = 0; i < dimensions; i++) {
			avgs.add(DataMath.arithmeticMean(FileHandler.getColumn(i, filepath, separator)));
			sdevs.add(DataMath.standardDeviation(FileHandler.getColumn(i, filepath, separator)));
		}

		for (int i = 0; i < kPointsNum; i++) {
			for (int j = 0; j < dimensions; j++) {
				tmp.add(r.nextGaussian() * sdevs.get(j) + avgs.get(j));
			}
			kPoints.add(tmp);
			tmp = new ArrayList<Double>();
		}

		FileHandler.writeMatrix(kPoints, destDir + "points", separator);
	}

	/**
	 * Calc vector of indexes to closest kp for each data point
	 */
	private void writeMinDistPoints() {
		List<Double> dataPoint = new ArrayList<Double>();
		List<Double> distsToKPs = new ArrayList<Double>();
		indexes = new ArrayList<Integer>();

		for (int i = 0; i < FileHandler.getFileRowsNum(filepath); i++) {
			dataPoint = FileHandler.getRow(i, filepath, separator);

			// create array with distances to each kp for one data point
			distsToKPs = new ArrayList<Double>();
			for (int j = 0; j < kPointsNum; j++) {
				distsToKPs.add(Metric.euclidean(kPoints.get(j), dataPoint));
			}

			// get index of min from created array and add it to returned vector
			double min = DataMath.min(distsToKPs);
			for (int j = 0; j < distsToKPs.size(); j++) {
				if (min == distsToKPs.get(j)) {
					indexes.add(j);
					break;
				}
			}
		}

		FileHandler.appendColumn(filepath, destDir + destFile, separator, indexes);
	}

	private void recalcKPoints() {
		List<List<Double>> newKPoints = new ArrayList<List<Double>>();
		List<Double> tmpPoint = new ArrayList<Double>();
		List<Double> colKp = new ArrayList<Double>();
		List<Double> colAll = new ArrayList<Double>();
		int nullPoints = 0;

		for (int i = 0; i < kPointsNum; i++) {
			tmpPoint = new ArrayList<Double>();

			for (int j = 0; j < dimensions; j++) {
				colKp = new ArrayList<Double>();
				colAll = FileHandler.getColumn(j, destDir + destFile, separator);

				for (int m = 0; m < colAll.size(); m++) {
					if (indexes.get(m) == i) {
						colKp.add(colAll.get(m));
					}
				}

				if (colKp.size() > 0) {
					tmpPoint.add(DataMath.arithmeticMean(colKp));
				} else {
					tmpPoint = null;
					nullPoints++;
					break;
				}
			}
			if (tmpPoint != null) {
				newKPoints.add(tmpPoint);
			}
		}

		kPointsNum -= nullPoints;
		kPoints = newKPoints;
		FileHandler.writeMatrix(kPoints, destDir + "points", separator);
	}

	public void calc(int msDelay) {
		try {
			initKPoints();
			writeMinDistPoints();
			boolean flag = true;

			final Runtime rt = Runtime.getRuntime();
			String cmd = "gnuplot -c " + System.getProperty("user.dir") + "/plot_km.gpt " + gptCols;
			rt.exec(cmd);

			List<List<Double>> prevDataPoints = new ArrayList<List<Double>>();
			while (flag) {
				TimeUnit.MILLISECONDS.sleep(msDelay);
				prevDataPoints = kPoints;
				writeMinDistPoints();
				recalcKPoints();
				if (prevDataPoints.containsAll(kPoints) && kPoints.containsAll(prevDataPoints)) {
					flag = false;
				}
			}

			if (normalize) {
				Utils.normalize(filepath, separator, true, sds);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
