package iad.labTwo;

import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class LabTwoApp {

	public static void main(String[] args) {

		IrisData.readData("data/iris.data");
		List<List<Double>> irisPoints = new ArrayList<List<Double>>();
		List<Double> medians = new ArrayList<Double>();
		List<Double> sds = new ArrayList<Double>();
		Random r = new Random();

		for (int i = 0; i < 4; i++) {
			medians.add(DataMath.quartile(IrisData.getColumn(i, "data/iris.data", ","), 50));
			sds.add(DataMath.standardDeviation(IrisData.getColumn(i, "data/iris.data", ",")));
		}

		for (int i = 0; i < 3; i++) {
			irisPoints.add(Arrays.asList(r.nextGaussian() * sds.get(0) + medians.get(0),
					r.nextGaussian() * sds.get(1) + medians.get(1), r.nextGaussian() * sds.get(2) + medians.get(2),
					r.nextGaussian() * sds.get(3) + medians.get(3)));
		}

		File directory = new File("results_data/");
		for (File f : directory.listFiles()) {
			f.delete();
		}

		String[] names = { "data/Iris-setosa.data", "data/Iris-virginica.data", "data/Iris-versicolor.data" };

		for (String s : names) {
			getResults(s, "\t", irisPoints, "taxicab");
			getResults(s, "\t", irisPoints, "euclidean");
			getResults(s, "\t", irisPoints, "minkowski");
			getResults(s, "\t", irisPoints, "chebyshev");
			getResults(s, "\t", irisPoints, "cosine");
		}
	}

	public static void getResults(String filename, String sep, List<List<Double>> ip, String metric) {
		try {
			Double[] tab = new Double[3];
			double min = 0;
			String line = null;
			FileInputStream fstream = new FileInputStream(filename);
			File file = new File("results_" + filename + "_" + metric);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));

			// min distance for each row
			for (int i = 0; i < IrisData.getFileRowsNum(filename); i++) {
				for (int j = 0; j < 3; j++) {
					switch (metric) {
					case "taxicab":
						tab[j] = Metric.taxicab(ip.get(j), IrisData.getRow(i, filename, sep));
						break;
					case "euclidean":
						tab[j] = Metric.euclidean(ip.get(j), IrisData.getRow(i, filename, sep));
						break;
					case "minkowski":
						tab[j] = Metric.minkowski(ip.get(j), IrisData.getRow(i, filename, sep), 3);
						break;
					case "chebyshev":
						tab[j] = Metric.chebyshev(ip.get(j), IrisData.getRow(i, filename, sep));
						break;
					case "cosine":
						tab[j] = Metric.cosineSimilarity(ip.get(j), IrisData.getRow(i, filename, sep));
						break;
					default:
						throw new IllegalArgumentException();
					}
				}
				min = DataMath.min((Arrays.asList(tab)));
				line = br.readLine();
				if (min == tab[0]) {
					bw.write(line + sep + "a\n");
				} else if (min == tab[1]) {
					bw.write(line + sep + "b\n");
				} else {
					bw.write(line + sep + "c\n");
				}
			}
			br.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
