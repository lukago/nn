package iad.mlp.utlis;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import iad.mlp.Layer;
import iad.mlp.MultiLayerPerceptron;

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
	
	public static void writeMLPData(String filepath, MultiLayerPerceptron mlp) {
		Layer[] layers = mlp.getLayers();
		try (FileWriter ostream = new FileWriter(filepath)) {
			for (int i = 0; i < layers.length; i++) {
				ostream.write(layers[i].toString());				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeError(String filepath, double[][] newOutput, double exOutput[][]) {
		try (FileWriter ostream = new FileWriter(filepath)) {	
			double error = 0, errorSum = 0;
			for (int i = 0; i < exOutput.length; i++) {			
				for (int j = 0; j < exOutput[i].length; j++) {
					error += Math.abs(newOutput[i][j] - exOutput[i][j]);
				}
				errorSum += error;
				ostream.write(i + "\t" + error + "\n");
				error = 0;
			}
			ostream.write("\t" + errorSum + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public static void serialize(MultiLayerPerceptron mlp, String filepath) {
		FileOutputStream fileOut = null;
		ObjectOutputStream out = null;
		try {
			fileOut = new FileOutputStream(filepath);
			out = new ObjectOutputStream(fileOut);
			out.writeObject(mlp);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileOut.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static MultiLayerPerceptron deserialize(String filepath) {
		FileInputStream fileIn = null;
		ObjectInputStream in = null;
		MultiLayerPerceptron mlp = null; 
		try {
			fileIn = new FileInputStream(filepath);
			in = new ObjectInputStream(fileIn);
			mlp = (MultiLayerPerceptron) in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileIn.close();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return mlp;
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
