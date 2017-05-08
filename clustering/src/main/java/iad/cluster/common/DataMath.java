package iad.cluster.common;

import java.util.List;

public final class DataMath {

	private DataMath() {
	}

	public static double max(List<Double> list) {
		double max = list.get(0);
		for (Double i : list) {
			if (i > max) {
				max = i;
			}
		}
		return max;
	}

	public static double min(List<Double> list) {
		double min = list.get(0);
		for (Double i : list) {
			if (i < min) {
				min = i;
			}
		}
		return min;
	}

	public static double range(List<Double> list) {
		return max(list) - min(list);
	}

	public static double quartile(List<Double> list, int percentage) {
		list.sort(null);
		double n = list.size() * percentage / 100.0;
		int n1 = (int) Math.floor(n);
		int n2 = n1 + 1;

		return (list.get(n1) * (n2 - n) + list.get(n2) * (n - n1));
	}

	public static double powerMean(List<Double> list, double p) {
		double sum = 0;

		for (Double i : list) {
			sum += Math.pow(i, p);
		}

		return Math.pow(sum / list.size(), 1 / p);
	}

	public static double harmonicMean(List<Double> list) {
		return powerMean(list, -1);
	}

	public static double arithmeticMean(List<Double> list) {
		return powerMean(list, 1);
	}

	public static double geometricMean(List<Double> list) {
		double sum = 0;

		for (Double i : list) {
			sum += Math.log(Math.abs(i));
		}

		return Math.exp(sum / list.size());
	}

	public static double variance(List<Double> list) {
		double sum = 0;
		double avg = arithmeticMean(list);

		for (Double i : list) {
			sum += (i - avg) * (i - avg);
		}

		return sum / list.size();
	}

	public static double standardDeviation(List<Double> list) {
		return Math.sqrt(variance(list));
	}

	public static double kurtosis(List<Double> list) {
		double sum = 0;
		double avg = arithmeticMean(list);

		for (Double i : list) {
			sum += Math.pow(i - avg, 4);
		}

		double centralMoment = sum / list.size();
		return centralMoment / Math.pow(standardDeviation(list), 4) - 3;
	}

}
