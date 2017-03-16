package iad.labTwo;

import java.util.List;
import java.io.IOException;

public class LabTwoApp {

	public static void main(String[] args) {

		try {
			IrisData.initData("data/iris.data");
			List<List<Double>> irisPoints = IrisData.calcPoints("data/iris.data", 3);
			IrisData.writePoints(irisPoints, "results_data/points");		
			List<Double> maxes = IrisData.calcMaxes();
			
			String[] names = { "data/Iris-setosa.data", "data/Iris-virginica.data", "data/Iris-versicolor.data" };

			for (String s : names) {
				IrisData.calcResults(s, "\t", irisPoints, "taxicab");
				IrisData.calcResults(s, "\t", irisPoints, "euclidean");
				IrisData.calcResults(s, "\t", irisPoints, "minkowski");
				IrisData.calcResults(s, "\t", irisPoints, "chebyshev");
				IrisData.calcResults(s, "\t", irisPoints, "cosine");
			}
			for (String s : names) {
				IrisData.normalizeRes(s, "\t", maxes, "taxicab");
				IrisData.normalizeRes(s, "\t", maxes, "euclidean");
				IrisData.normalizeRes(s, "\t", maxes, "minkowski");
				IrisData.normalizeRes(s, "\t", maxes, "chebyshev");
				IrisData.normalizeRes(s, "\t", maxes, "cosine");
			}
			IrisData.normalize("results_data/points", "\t", maxes);

			final Runtime rt = Runtime.getRuntime();
			rt.exec("gnuplot " + System.getProperty("user.dir") + "/plot.txt");			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
