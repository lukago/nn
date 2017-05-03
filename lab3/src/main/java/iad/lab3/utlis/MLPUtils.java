package iad.lab3.utlis;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class MLPUtils {

	private MLPUtils() {
	}

	public static double quadraticCostFun(double[] newOutput, double exOutput[]) {
		double ret = 0.0;
		for (int i = 0; i < exOutput.length; i++) {
			ret += Math.pow(exOutput[i] - newOutput[i], 2);
		}
		return ret;
	}

	public static void writeQuadFun(String filepath, double[] values) {
		try (FileWriter ostream = new FileWriter(filepath)) {
			for (int i = 0; i < values.length; i++) {
				ostream.write(i + "\t" + values[i] + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeResults(String filepath, double values[][]) {
		try (FileWriter ostream = new FileWriter(filepath)) {
			for (int i = 0; i < values.length; i++) {
				ostream.write(i + "\t");
				for (int j = 0; j < values[i].length; j++) {
					ostream.write(values[i][j] + "\t");
				}
				ostream.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static double[][] readMatrix(String filepath, String regex) {
		List<double[]> tmpMatrix = new ArrayList<double[]>();
		try {
			for (String line : Files.readAllLines(Paths.get(filepath))) {
				String[] rowStr = line.split(regex);
				double[] row = new double[rowStr.length];
				for (int i = 0; i < rowStr.length; i++) {
					row[i] = (Double.parseDouble(rowStr[i]));
				}
				tmpMatrix.add(row);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		double[][] ret = new double[tmpMatrix.size()][];
		for (int i = 0; i < tmpMatrix.size(); i++) {
			ret[i] = tmpMatrix.get(i);
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
