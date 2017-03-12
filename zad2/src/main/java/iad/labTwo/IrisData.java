package iad.labTwo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class IrisData {

	public static void readData(String filename) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();
			
			/* delete files if exist */
			File directory = new File("data/");
			for(File f: directory.listFiles())
			    if(f.getName().startsWith("Iris-"))
			        f.delete();

			while (line != null) {
				String[] row = line.split(",");
				String data = row[0] + "\t" + row[1] + "\t" + row[2] + "\t" + row[3] + "\n";
				
				FileWriter out = new FileWriter("data/" + row[4] + ".data", true);
				out.write(data);
				out.close();

				line = br.readLine();
			}
			br.close();

		} catch (FileNotFoundException fnfe) {
			System.out.println("file not found");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static ArrayList<Double> getColumn(int colNum, String filename, String separator) {
		ArrayList<Double> col = new ArrayList<Double>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();

			while (line != null) {
				String[] row = line.split(separator);
				col.add(Double.parseDouble(row[colNum]));

				line = br.readLine();
			}
			br.close();

		} catch (FileNotFoundException fnfe) {
			System.out.println("file not found");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return col;
	}

}
