package iad.labTwo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.IOException;

public class LabTwoApp {

	public static void main(String[] args) {

		try {
			String[] names = { "data/Iris-setosa.data", "data/Iris-virginica.data", "data/Iris-versicolor.data" };
			String[] metrics = {"taxicab", "euclidean", "minkowski", "chebyshev", "cosine"};
			List<Double> sds = new ArrayList<Double>();
			
			sds = IrisData.calcSds("data/iris.data", ",");
			IrisData.normalize("data/iris.data", ",", false, sds);
			
			IrisData.initData("data/iris.data");
			List<List<Double>> irisPoints = IrisData.calcPoints("data/iris.data", ",", 3);
			Collections.shuffle(irisPoints);
			IrisData.writePoints(irisPoints, "results_data/points");
			System.out.println("");
				
			for (String s : names) {
				IrisData.calcResults(s, "\t", irisPoints, metrics[0]);
				IrisData.calcResults(s, "\t", irisPoints, metrics[1]);
				IrisData.calcResults(s, "\t", irisPoints, metrics[2]);
				IrisData.calcResults(s, "\t", irisPoints, metrics[3]);
				IrisData.calcResults(s, "\t", irisPoints, metrics[4]);
			}
			
			/*IrisData.normalize("data/iris.data", ",", true, sds);
			for (String s : metrics) { 
				IrisData.normalize("results_data/Iris-setosa.data_"+s, "\t", true, sds);
				IrisData.normalize("results_data/Iris-virginica.data_"+s, "\t", true, sds);
				IrisData.normalize("results_data/Iris-versicolor.data_"+s, "\t", true, sds);
			}
			IrisData.normalize("results_data/points", "\t", true, sds);*/
			

			final Runtime rt = Runtime.getRuntime();
			rt.exec("gnuplot " + System.getProperty("user.dir") + "/plot.txt");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
}

