package iad.labTwo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class IrisData {

	private IrisData() {
	}

	private static final int features = 4;

	public static void initData(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = br.readLine();

		File directory = new File("data/");
		for (File f : directory.listFiles()) {
			if (f.getName().startsWith("Iris-")) {
				f.delete();
			}
		}

		new File("results_data/").mkdir();
		directory = new File("results_data/");
		for (File f : directory.listFiles()) {
			f.delete();
		}

		while (line != null) {
			String[] row = line.split(",");
			String data = row[0] + "\t" + row[1] + "\t" + row[2] + "\t" + row[3] + "\n";
			FileWriter out = new FileWriter("data/" + row[4] + ".data", true);
			out.write(data);
			out.close();
			line = br.readLine();
		}
		br.close();
	}

	public static ArrayList<Double> getColumn(int colNum, String filename, String separator) throws IOException {
		ArrayList<Double> col = new ArrayList<Double>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = br.readLine();

		while (line != null) {
			String[] row = line.split(separator);
			col.add(Double.parseDouble(row[colNum]));
			line = br.readLine();
		}
		br.close();

		return col;
	}

	public static ArrayList<Double> getRow(int rowNum, String filename, String separator) throws IOException {
		ArrayList<Double> row = new ArrayList<Double>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = new String();

		for (int i = 0; i <= rowNum; i++) {
			line = br.readLine();
		}

		if (line != null) {
			String[] rowStr = line.split(separator);
			for (int i = 0; i < features; i++) {
				row.add(Double.parseDouble(rowStr[i]));
			}
		}
		br.close();

		return row;
	}

	public static void normalize(String filename, String sep, boolean renormalize, List<Double> sds)
			throws IOException {

		List<String> newLines = new ArrayList<>();
		String str = "";
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
	}

	public static List<Double> calcSds(String filename, String sep) throws IOException {
		List<Double> sds = new ArrayList<Double>();
		for (int i = 0; i < 4; i++) {
			sds.add(DataMath.standardDeviation(getColumn(i, filename, sep)));
		}

		return sds;
	}

	public static void writePoints(List<List<Double>> ip, String filename) throws IOException {
		PrintWriter wr = new PrintWriter(filename, "UTF-8");

		for (int i = 0; i < ip.size(); i++) {
			for (int j = 0; j < ip.get(0).size(); j++) {
				wr.print(ip.get(i).get(j) + "\t");
			}
			wr.print(i + "\n");
		}
		wr.close();
	}

	public static void calcResults(String filename, String sep, List<List<Double>> ip, String metric)
			throws IOException {
		List<Double> tab = new ArrayList<Double>();
		double min = 0;
		String line = null;
		FileInputStream fstream = new FileInputStream(filename);
		File file = new File("results_" + filename + "_" + metric);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
		List<Double> rowVec = new ArrayList<Double>();

		// min distance for each row
		for (int i = 0; i < IrisData.getFileRowsNum(filename); i++) {
			rowVec = IrisData.getRow(i, filename, sep);
			tab = new ArrayList<Double>();
			for (int j = 0; j < ip.size(); j++) {
				switch (metric) {
				case "taxicab":
					tab.add(Metric.taxicab(ip.get(j), rowVec));
					break;
				case "euclidean":
					tab.add(Metric.euclidean(ip.get(j), rowVec));
					break;
				case "minkowski":
					tab.add(Metric.minkowski(ip.get(j), rowVec, 3));
					break;
				case "chebyshev":
					tab.add(Metric.chebyshev(ip.get(j), rowVec));
					break;
				case "cosine":
					tab.add(Metric.cosineSimilarity(ip.get(j), rowVec));
					break;
				default:
					throw new IllegalArgumentException();
				}
			}
			min = DataMath.min(tab);
			line = br.readLine();
			for (int j = 0; j < tab.size(); j++) {
				if (min == tab.get(j)) {
					bw.write(line + sep + j + ".0\n");
					break;
				}
			}
		}
		br.close();
		bw.close();
	}

	public static List<List<Double>> calcPoints(String filename, String sep, int n) throws IOException {
		List<List<Double>> irisPoints = new ArrayList<List<Double>>();
		List<Double> avgs = new ArrayList<Double>();
		List<Double> sds = new ArrayList<Double>();
		Random r = new Random();

		for (int i = 0; i < 4; i++) {
			avgs.add(DataMath.arithmeticMean(IrisData.getColumn(i, filename, sep)));
			sds.add(DataMath.standardDeviation(IrisData.getColumn(i, filename, sep)));
		}

		for (int i = 0; i < n; i++) {
			irisPoints.add(Arrays.asList(Math.abs(r.nextGaussian() * sds.get(0) + avgs.get(0)),
					Math.abs(r.nextGaussian() * sds.get(1) + avgs.get(1)),
					Math.abs(r.nextGaussian() * sds.get(2) + avgs.get(2)),
					Math.abs(r.nextGaussian() * sds.get(3) + avgs.get(3))));
		}

		return irisPoints;
	}

	public static int getFileRowsNum(String filename) throws IOException {
		LineNumberReader lnr = null;
		lnr = new LineNumberReader(new FileReader(new File(filename)));
		lnr.skip(Long.MAX_VALUE);
		lnr.close();
		return lnr.getLineNumber();
	}
}