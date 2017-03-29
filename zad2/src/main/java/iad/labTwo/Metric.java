package iad.labTwo;

import java.util.List;

public final class Metric {
	
	private Metric() { }

	public static double taxicab(List<Double> a, List<Double> b) {
		return minkowski(a, b, 1);
	}

	public static double euclidean(List<Double> a, List<Double> b) {
		return minkowski(a, b, 2);
	}
	
	public static double minkowski3p(List<Double> a, List<Double> b) {
		return minkowski(a, b, 3);
	}

	public static double minkowski(List<Double> a, List<Double> b, int p) {
		check(a, b);
		double distance = 0;
		for (int i = 0; i < a.size(); i++) {
			distance += Math.pow(Math.abs(a.get(i) - b.get(i)), p);
		}
		return Math.pow(distance, 1.0 / p);
	}

	public static double chebyshev(List<Double> a, List<Double> b) {
		check(a, b);
		double distance = 0;
		double max = Math.abs(a.get(0) - b.get(0));
		for (int i = 1; i < a.size(); i++) {
			distance = Math.abs(a.get(i) - b.get(i));
			if (distance > max) {
				max = distance;
			}
		}
		return max;
	}

	public static double cosineSimilarity(List<Double> a, List<Double> b) {
		check(a, b);
		double dotProduct = 0;
		double normA = 0;
		double normB = 0;
		for (int i = 0; i < a.size(); i++) {
			dotProduct += a.get(i) * b.get(i);
			normA += a.get(i) * a.get(i);
			normB += b.get(i) * b.get(i);
		}

		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}

	private static void check(List<Double> a, List<Double> b) {
		if (a.size() != b.size()) {
			throw new IllegalArgumentException("dimensions not equeal");
		}
	}
}
