package iad.zad3;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import iad.zad3.Metric;
import iad.zad3.DataMath;
import iad.zad3.DataPoints;

public final class DataPoints {

	private DataPoints(){
	}
	
	private static final int dimensions = 2;
	
	public static void initData() {
		
		File directory = new File("data/");
		
		new File("results_data/").mkdir();
		directory = new File("results_data/");
		for (File f : directory.listFiles()) {
			if (f.getName().startsWith("sample")) {
			f.delete();
			}
		}
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
			for (int i = 0; i < dimensions; i++) {
				row.add(Double.parseDouble(rowStr[i]));
			}
		}
		br.close();

		return row;
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
	
	public static void calcResults(String filename, String sep, List<List<Double>> ip)
			throws IOException {
		List<Double> tab = new ArrayList<Double>();
		double min = 0;
		String line = null;
		FileInputStream fstream = new FileInputStream(filename);
		File file = new File("results_" + filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		//BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
		List<Double> rowVec = new ArrayList<Double>();

		// min distance for each row
		for (int i = 0; i < DataPoints.getFileRowsNum(filename); i++) {
			rowVec = DataPoints.getRow(i, filename, sep);
			tab = new ArrayList<Double>();
			for (int j = 0; j < ip.size(); j++) {
					tab.add(Metric.euclidean(ip.get(j), rowVec));
			}
			min = DataMath.min(tab);
			line = br.readLine();
			for (int j = 0; j < tab.size(); j++) {
				if (min == tab.get(j)) {
					BufferedWriter bw = new BufferedWriter(new FileWriter("results_data/sample1_C" + j, true));
					bw.write(line + sep + j + ".0\n");
					bw.close();
					break;
				}
			}
		}
		br.close();
		
	}
	
	public static List<List<Double>> calcNextCentroids(String sep, List<List<Double>> points, int n)
			throws IOException {
		List<List<Double>> centroids = new ArrayList<List<Double>>();
		
		for(int i = 0; i < n; i++) {
			centroids.add(Arrays.asList((DataMath.arithmeticMean(DataPoints.getColumn(0, "results_data/sample1_C" + i, sep))),
										(DataMath.arithmeticMean(DataPoints.getColumn(1, "results_data/sample1_C" + i, sep)))
										));
		}
		
		return centroids;
	}

	
	public static List<List<Double>> calcFirstCentroids(String filename, String sep, int n) throws IOException {
		List<List<Double>> centroids = new ArrayList<List<Double>>();
		List<Double> avgs = new ArrayList<Double>();
		List<Double> sds = new ArrayList<Double>();
		Random r = new Random();

		for (int i = 0; i < 2; i++) {
			avgs.add(DataMath.arithmeticMean(DataPoints.getColumn(i, filename, sep)));
			sds.add(DataMath.standardDeviation(DataPoints.getColumn(i, filename, sep)));
		}

		for (int i = 0; i < n; i++) {
			centroids.add(Arrays.asList(r.nextGaussian() * sds.get(0) + avgs.get(0),
					r.nextGaussian() * sds.get(1) + avgs.get(1)));
		}

		return centroids;
	}

	public static int getFileRowsNum(String filename) throws IOException {
		LineNumberReader lnr = null;
		lnr = new LineNumberReader(new FileReader(new File(filename)));
		lnr.skip(Long.MAX_VALUE);
		lnr.close();
		return lnr.getLineNumber();
	}
}
