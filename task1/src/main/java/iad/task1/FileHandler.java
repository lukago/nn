package iad.task1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public final class FileHandler {

	private FileHandler() {
	}

	public static ArrayList<Double> getColumn(int colNum, String filepath, String sep) {
		ArrayList<Double> col = new ArrayList<Double>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filepath));
			String line = br.readLine();
			
			while (line != null) {
				String[] row = line.split(sep);
				col.add(Double.parseDouble(row[colNum]));
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return col;
	}

	public static ArrayList<Double> getRow(int rowNum, String filepath, String sep) {
		ArrayList<Double> row = new ArrayList<Double>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filepath));
			String line = new String();
	
			for (int i = 0; i <= rowNum; i++) {
				line = br.readLine();
			}
	
			if (line != null) {
				String[] rowStr = line.split(sep);
				for (int i = 0; i < rowStr.length; i++) {
					row.add(Double.parseDouble(rowStr[i]));
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return row;
	}

	public static void writeMatrix(List<List<Double>> ip, String filepath) {
		PrintWriter wr;
		try {
			wr = new PrintWriter(filepath, "UTF-8");
			
			for (int i = 0; i < ip.size(); i++) {
				for (int j = 0; j < ip.get(0).size(); j++) {
					wr.print(ip.get(i).get(j) + "\t");
				}
				wr.print(i + "\n");
			}
			
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getFileRowsNum(String filepath) {
		LineNumberReader lnr = null;
		try {
			lnr = new LineNumberReader(new FileReader(new File(filepath)));
			lnr.skip(Long.MAX_VALUE);
			lnr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lnr.getLineNumber();
	}
	
	public static void makeEmptyDir(String filepath) {
		new File(filepath).mkdir();
		File directory = new File(filepath);
		for (File f : directory.listFiles()) {
			f.delete();
		}
	}
	
	public static void copy(String src, String dest) {
		try {
			Files.copy(new File(src).toPath(), new File(dest).toPath(), 
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void appendLine(String filepath, String line) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(filepath, true));
			bw.write(line);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
