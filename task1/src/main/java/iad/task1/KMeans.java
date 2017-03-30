package iad.task1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class KMeans {

	private int kPointsNum;
	private int dimensions;
	private String filepath;
	private String separator;
	private List<List<Double>> kPoints;

	private final String destDir = "results_kmeans/";
	private final String destFile = "kmeans.data_";

	public KMeans(int kPointsNum, int dimensions, String filepath, String separator) {
		this.kPointsNum = kPointsNum;
		this.dimensions = dimensions;
		this.filepath = filepath;
		this.separator = separator;
		kPoints = new ArrayList<List<Double>>();

		FileHandler.makeEmptyDir(destDir);
	}

	public void initKPoints() {
		List<Double> avgs = new ArrayList<Double>();
		List<Double> sds = new ArrayList<Double>();
		List<Double> tmp = new ArrayList<Double>();
		Random r = new Random();

		for (int i = 0; i < dimensions; i++) {
			avgs.add(DataMath.arithmeticMean(FileHandler.getColumn(i, filepath, separator)));
			sds.add(DataMath.standardDeviation(FileHandler.getColumn(i, filepath, separator)));
		}

		for (int i = 0; i < kPointsNum; i++) {
			tmp = new ArrayList<Double>();
			for (int j = 0; j < dimensions; j++) {
				tmp.add(r.nextGaussian() * sds.get(j) + avgs.get(j));
			}
			kPoints.add(tmp);		
		}

		FileHandler.writeMatrix(kPoints, destDir + "points");
	}

	/**
	 * @return vector of indexes to closest kp for each data point
	 */
	public void writeMinDistPoints() {
		List<Double> rowVec = new ArrayList<Double>();
		List<Double> distsToKPs = new ArrayList<Double>();
		String line = null;

		for (int i = 0; i < FileHandler.getFileRowsNum(filepath); i++) {
			rowVec = FileHandler.getRow(i, filepath, separator);

			// create array with distances to each kp for one data point
			distsToKPs = new ArrayList<Double>();
			for (int j = 0; j < kPoints.size(); j++) {
				distsToKPs.add(Metric.euclidean(kPoints.get(j), rowVec));
			}

			// get index of min from created array and add it to returned vector
			double min = DataMath.min(distsToKPs);
			line = String.join("\t", rowVec.stream().map(o -> o.toString()).collect(Collectors.toList()));
			for (int j = 0; j < distsToKPs.size(); j++) {
				if (min == distsToKPs.get(j)) {
					FileHandler.appendLine(destDir + destFile + j, line + "\t" + j + "\n");
					break;
				}
			}
		}
	}

	public void recalcKPoints() {
		List<List<Double>> newKPoints = new ArrayList<List<Double>>();

		for (int i = 0; i < kPointsNum; i++) {
			newKPoints.add(Arrays.asList(
					DataMath.arithmeticMean(FileHandler.getColumn(0, destDir + destFile + i, separator)),
					DataMath.arithmeticMean(FileHandler.getColumn(1, destDir + destFile + i, separator))));
		}

		kPoints = newKPoints;
		FileHandler.writeMatrix(kPoints, destDir + "points");
	}

	public void calc() {
		try {
			initKPoints();
			writeMinDistPoints();
			boolean flag = true;

			final Runtime rt = Runtime.getRuntime();
			rt.exec("gnuplot " + System.getProperty("user.dir") + "/plot1.txt");

			List<List<Double>> prevDataPoints = new ArrayList<List<Double>>();
			while(flag) {	
				prevDataPoints = kPoints;
				writeMinDistPoints();
				recalcKPoints();
				if (!prevDataPoints.retainAll(kPoints)) {
					flag = false;
				}
				if (flag) {
					FileHandler.makeEmptyDir(destDir);
				}
			}

			rt.exec("gnuplot " + System.getProperty("user.dir") + "/plot2.txt");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
