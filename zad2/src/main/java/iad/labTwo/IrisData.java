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
	
	private IrisData() { }

	public static void initData(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = br.readLine();

		File directory = new File("data/");
		for (File f : directory.listFiles()) {
			if (f.getName().startsWith("Iris-")) {
				f.delete();
			}
		}

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
			for (int i = 0; i < 4; i++) {
				row.add(Double.parseDouble(rowStr[i]));
			}
		}
		br.close();

		return row;
	}

	public static void normalize(String filename, String sep, List<Double> maxes) throws IOException {
		List<String> newLines = new ArrayList<>();
		for (String line : Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8)) {
			String[] row = line.split(sep);
			String newLine = "";
			double[] dArray = new double[maxes.size()];
			for (int i = 0; i < dArray.length; i++) {
				dArray[i] = Double.parseDouble(row[i]) / maxes.get(i);
				newLine += Double.toString(dArray[i]) + sep;
			}
			newLines.add(newLine);
		}
		Files.write(Paths.get(filename), newLines, StandardCharsets.UTF_8);

	}

	public static void normalizeRes(String filename, String sep, List<Double> maxes, String metric) throws IOException {
		normalize("results_" + filename + "_" + metric, sep, maxes);
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
		Double[] tab = new Double[3];
		double min = 0;
		String line = null;
		FileInputStream fstream = new FileInputStream(filename);
		File file = new File("results_" + filename + "_" + metric);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));

		// min distance for each row
		for (int i = 0; i < IrisData.getFileRowsNum(filename); i++) {
			for (int j = 0; j < 3; j++) {
				switch (metric) {
				case "taxicab":
					tab[j] = Metric.taxicab(ip.get(j), IrisData.getRow(i, filename, sep));
					break;
				case "euclidean":
					tab[j] = Metric.euclidean(ip.get(j), IrisData.getRow(i, filename, sep));
					break;
				case "minkowski":
					tab[j] = Metric.minkowski(ip.get(j), IrisData.getRow(i, filename, sep), 3);
					break;
				case "chebyshev":
					tab[j] = Metric.chebyshev(ip.get(j), IrisData.getRow(i, filename, sep));
					break;
				case "cosine":
					tab[j] = Metric.cosineSimilarity(ip.get(j), IrisData.getRow(i, filename, sep));
					break;
				default:
					throw new IllegalArgumentException();
				}
			}
			min = DataMath.min((Arrays.asList(tab)));
			line = br.readLine();
			if (min == tab[0]) {
				bw.write(line + sep + "0.0\n");
			} else if (min == tab[1]) {
				bw.write(line + sep + "1.0\n");
			} else {
				bw.write(line + sep + "2.0\n");
			}
		}
		br.close();
		bw.close();
	}
	
	public static List<List<Double>> calcPoints(String filename, int n) throws IOException {
		List<List<Double>> irisPoints = new ArrayList<List<Double>>();
		List<Double> avgs = new ArrayList<Double>();
		List<Double> sds = new ArrayList<Double>();
		Random r = new Random();

		for (int i = 0; i < 4; i++) {
			avgs.add(DataMath.arithmeticMean(IrisData.getColumn(i, filename, ",")));
			sds.add(DataMath.standardDeviation(IrisData.getColumn(i, filename, ",")));
		}

		for (int i = 0; i < n; i++) {
			irisPoints.add(Arrays.asList(r.nextGaussian() * sds.get(0) + avgs.get(0),
					r.nextGaussian() * sds.get(1) + avgs.get(1), r.nextGaussian() * sds.get(2) + avgs.get(2),
					r.nextGaussian() * sds.get(3) + avgs.get(3)));
		}
		
		return irisPoints;
	}
	
	public static List<Double> calcMaxes() throws IOException {
		List<Double> maxes = new ArrayList<Double>();
		double max, max1, max2;
		
		for (int i = 0; i < 4; i++) {
			max1 = DataMath.max(IrisData.getColumn(i, "data/iris.data", ","));
			max2 = DataMath.max(IrisData.getColumn(i, "results_data/points", "\t"));
			max = max1>max2 ? max1 : max2;
			maxes.add(max);
		}		
		double maxSep = DataMath.max(maxes.subList(0, 1));
		double maxPen = DataMath.max(maxes.subList(2, 3));
		
		return Arrays.asList(maxSep, maxSep, maxPen, maxPen, 1.0);
	}
	
	public static int getFileRowsNum(String filename) throws IOException {
		LineNumberReader lnr = null;
		lnr = new LineNumberReader(new FileReader(new File(filename)));
		lnr.skip(Long.MAX_VALUE);
		lnr.close();
		return lnr.getLineNumber();
	}
}
