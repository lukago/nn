package iad.labTwo;

import java.util.List;
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
			irisPoints.add(Arrays.asList(
					r.nextGaussian() * sds.get(0) + medians.get(0),
					r.nextGaussian() * sds.get(1) + medians.get(1), 
					r.nextGaussian() * sds.get(2) + medians.get(2),
					r.nextGaussian() * sds.get(3) + medians.get(3)));
		}
		
		double x = 0;
		for (int i = 0; i<IrisData.getFileRowsNum("data/Iris-setosa.data"); i++) {
			x = Metric.taxicab(irisPoints.get(0), IrisData.getRow(i, "data/Iris-setosa.data", "\t"));
			System.out.println(i + 1 + "\t" + x);
			x = Metric.euclidean(irisPoints.get(0), IrisData.getRow(i, "data/Iris-setosa.data", "\t"));
			System.out.println(i + 1 + "\t" + x);
			x = Metric.minkowski(irisPoints.get(0), IrisData.getRow(i, "data/Iris-setosa.data", "\t"), 3);
			System.out.println(i + 1 + "\t" + x);
			x = Metric.chebyshev(irisPoints.get(0), IrisData.getRow(i, "data/Iris-setosa.data", "\t"));
			System.out.println(i + 1 + "\t" + x);
			x = Metric.cosineSimilarity(irisPoints.get(0), IrisData.getRow(i, "data/Iris-setosa.data", "\t"));
			System.out.println(i + 1 + "\t" + x);
			System.out.println();
		}

	}
}
