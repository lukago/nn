package iad.labTwo;

import java.util.ArrayList;

public class DataMath {

	public static double max(ArrayList<Double> list) {
		double max = list.get(0);
		for (Double i : list) {
			if (i > max)
				max = i;
		}
		return max;
	}

	public static double min(ArrayList<Double> list) {
		double min = list.get(0);
		for (Double i : list) {
			if (i < min)
				min = i;
		}
		return min;
	}

	public static double range(ArrayList<Double> list) {
		return max(list) - min(list);
	}

	public static double quartile(ArrayList<Double> list, int percentage) {
		list.sort(null);
		double n = list.size() * percentage / 100.0;
		int n1 = (int) Math.floor(n);
		int n2 = n1 + 1;

		return (list.get(n1) * (n2 - n) + list.get(n2) * (n - n1));
	}

	public static double powerMean(ArrayList<Double> list, double p) {
		double sum = 0;

		for (Double i : list) {
			sum += Math.pow(i, p);
		}

		return Math.pow(sum / list.size(), 1 / p);
	}

	public static double harmonicMean(ArrayList<Double> list) {
		return powerMean(list, -1);
	}

	public static double arithmeticMean(ArrayList<Double> list) {
		return powerMean(list, 1);
	}

	public static double geometricMean(ArrayList<Double> list) {
		double sum = 0;

		for (Double i : list) {
			sum += Math.log(Math.abs(i));
		}

		return Math.exp(sum / list.size());
	}

	public static double variance(ArrayList<Double> list) {
		double sum = 0;
		double avg = arithmeticMean(list);

		for (Double i : list) {
			sum += (i - avg) * (i - avg);
		}

		return sum / list.size();
	}

	public static double standardDeviation(ArrayList<Double> list) {
		return Math.sqrt(variance(list));
	}

	public static double kurtosis(ArrayList<Double> list) {
		double sum = 0;
		double avg = arithmeticMean(list);

		for (Double i : list) {
			sum += Math.pow(i - avg, 4);
		}

		double centralMoment = sum / list.size();
		return centralMoment / Math.pow(standardDeviation(list), 4) - 3;
	}

}
