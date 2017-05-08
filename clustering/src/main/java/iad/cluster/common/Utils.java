package iad.cluster.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Utils {

	private Utils() {

	}

	public static void normalize(List<List<Double>> data, List<Double> sds, boolean renormalize) {
		for (List<Double> row : data) {
			for (int i = 0; i < row.size(); i++) {
				if (renormalize) {
					row.set(i, row.get(i) * sds.get(i));
				} else {
					row.set(i, row.get(i) * sds.get(i));
				}
			}
		}
	}

	public static List<Double> calcSds(List<List<Double>> data) {
		List<Double> sds = new ArrayList<Double>();
		for (int i = 0; i < data.get(0).size(); i++) {
			sds.add(DataMath.standardDeviation(getColumn(data, i)));
		}
		return sds;
	}

	public static <T> List<T> getColumn(List<List<T>> matrix, int col) {
		List<T> ret = new ArrayList<T>();
		for (List<T> i : matrix) {
			ret.add(i.get(col));
		}

		return ret;
	}

	public static void rumCmd(String cmd) {
		final Runtime rt = Runtime.getRuntime();
		try {
			rt.exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
