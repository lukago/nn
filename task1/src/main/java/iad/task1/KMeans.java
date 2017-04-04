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
	private boolean image;
	private int iterations;
	private List<List<Double>> data;

	public final String destDir = "results_kmeans/";
	public final String destFile = "kmeans.data";
	public final String pointsFile = "points.data";
	public final String imgcprFile = "imgcpr.data";

	public KMeans(int kPointsNum, int dimensions, String filepath, String separator, boolean normalize,
			String gptCols, int iterations, boolean image) {
		this.kPointsNum = kPointsNum;
		this.dimensions = dimensions;
		this.filepath = filepath;
		this.separator = separator;
		this.normalize = normalize;
		this.gptCols = gptCols;
		this.image = image;
		this.iterations = iterations;
		kPoints = new ArrayList<List<Double>>();
		indexes = new ArrayList<Integer>();
		sds = Utils.calcSds(filepath, separator, dimensions);

		if (normalize) {
			Utils.normalize(filepath, separator, false, sds);
		}

		FileHandler.makeEmptyDir(destDir);
		FileHandler.copy(filepath, destDir + destFile);
		
		data = FileHandler.readData(filepath, separator);
	}

	private void initKPoints() {
		List<Double> avgs = new ArrayList<Double>();
		List<Double> sdevs = new ArrayList<Double>();
		List<Double> tmp = new ArrayList<Double>();
		Random r = new Random();

		for (int i = 0; i < dimensions; i++) {
			avgs.add(DataMath.arithmeticMean(Utils.getColumn(data, i)));
			sdevs.add(DataMath.standardDeviation(Utils.getColumn(data, i)));
		}

		for (int i = 0; i < kPointsNum; i++) {
			for (int j = 0; j < dimensions; j++) {
				tmp.add(r.nextGaussian() * sdevs.get(j) + avgs.get(j));
			}
			kPoints.add(tmp);
			tmp = new ArrayList<Double>();
		}
	}

	/**
	 * Calc vector of indexes to closest kp for each data point
	 */
	private void writeMinDistPoints() {
		List<Double> dataPoint = new ArrayList<Double>();
		List<Double> distsToKPs = new ArrayList<Double>();
		indexes = new ArrayList<Integer>();

		for (int i = 0; i < data.size(); i++) {
			dataPoint = data.get(i);

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
				colAll = Utils.getColumn(data, j);

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
	}

	public void calc(int msDelay) {
		try {
			initKPoints();
			writeMinDistPoints();
			
			if (!image) {
				FileHandler.writeMatrix(kPoints, destDir + pointsFile, separator);
				FileHandler.appendColumn(filepath, destDir + destFile, separator, indexes);
				final Runtime rt = Runtime.getRuntime();
				String cmd = "gnuplot -c " + System.getProperty("user.dir") + "/plot_km.gpt " + gptCols;
				rt.exec(cmd);
			}

			List<List<Double>> prevDataPoints = new ArrayList<List<Double>>();
			for(int i = 0; i< iterations; i++) {
				TimeUnit.MILLISECONDS.sleep(msDelay);
				prevDataPoints = kPoints;
				writeMinDistPoints();
				recalcKPoints();
				
				if (!image) {
					FileHandler.writeMatrix(kPoints, destDir + pointsFile, separator);
					FileHandler.appendColumn(filepath, destDir + destFile, separator, indexes);
				}	
				
				if (prevDataPoints.containsAll(kPoints) && kPoints.containsAll(prevDataPoints)) {
					break;
				}
				
				System.out.println(i);
			}

			if (normalize) {
				Utils.normalize(filepath, separator, true, sds);
			}
			
			FileHandler.writePointsAsClusters(indexes, kPoints, destDir + imgcprFile, separator);	

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
