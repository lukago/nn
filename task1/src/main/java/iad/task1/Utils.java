package iad.task1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class Utils {

	private Utils() {

	}

	public static void normalize(String filename, String sep, boolean renormalize, List<Double> sds) {

		List<String> newLines = new ArrayList<>();
		String str = "";
		try {
			for (String line : Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8)) {
				String[] row = line.split(sep);
				double[] dArray = new double[sds.size()];
				for (int i = 0; i < dArray.length; i++) {
					if (renormalize) {
						dArray[i] = Double.parseDouble(row[i]) * sds.get(i);
					} else {
						dArray[i] = Double.parseDouble(row[i]) / sds.get(i);
					}
					row[i] = Double.toString(dArray[i]) + sep;
					str += row[i];
				}
				for (int i = 0; i < (row.length - sds.size()); i++) {
					str += row[dArray.length + i] + sep;
				}
				newLines.add(str);
				str = "";
			}
			Files.write(Paths.get(filename), newLines, StandardCharsets.UTF_8);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	public static List<Double> calcSds(String filename, String sep, int cols) {
		List<Double> sds = new ArrayList<Double>();
		for (int i = 0; i < cols; i++) {
			sds.add(DataMath.standardDeviation(FileHandler.getColumn(i, filename, sep)));
		}
		return sds;
	}
	
	public static <T> List<T> getColumn(List<List<T>> matrix, int col) {
		List<T> ret = new ArrayList<T>();
		for(List<T> i : matrix) {
			ret.add(i.get(col));
		}
		
		return ret;
	}
	
}
