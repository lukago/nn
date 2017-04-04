package iad.task1;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

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

	public static void writeMatrix(List<List<Double>> ip, String filepath, String sep) {
		PrintWriter wr;
		try {
			wr = new PrintWriter(filepath, "UTF-8");

			for (int i = 0; i < ip.size(); i++) {
				for (int j = 0; j < ip.get(0).size(); j++) {
					wr.print(ip.get(i).get(j) + sep);
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
			Files.copy(new File(src).toPath(), new File(dest).toPath(), StandardCopyOption.REPLACE_EXISTING);
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

	public static void appendColumn(String fileSrc, String fileDest, String sep, List<?> col) {
		List<String> newLines = new ArrayList<>();
		int i = 0;
		try {
			for (String line : Files.readAllLines(Paths.get(fileSrc), StandardCharsets.UTF_8)) {
				newLines.add(line + sep + col.get(i));
				i++;
			}
			Files.write(Paths.get(fileDest), newLines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<List<Double>> readData(String fileSrc, String sep) {
		List<List<Double>> ret = new ArrayList<List<Double>>();
		try {
			for (String line : Files.readAllLines(Paths.get(fileSrc), StandardCharsets.UTF_8)) {
				String[] rowStr = line.split(sep);
				List<Double> row = new ArrayList<Double>();
				for (int i = 0; i < rowStr.length; i++) {
					row.add(Double.parseDouble(rowStr[i]));
				}
				ret.add(row);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static void parseIMG(String imgFile, String fileDest, String sep, int frameSize) throws IOException {
		File fimg = new File(imgFile);
		BufferedImage image = ImageIO.read(fimg);

		int width = image.getWidth();
		int height = image.getHeight();

		if ((width * 3) % (frameSize) != 0 || (height) % (frameSize) != 0) {
			throw new IOException();
		}

		int[][] result = new int[height][width * 3];

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				int clr = image.getRGB(col, row);
				int red = (clr & 0x00ff0000) >> 16;
				int green = (clr & 0x0000ff00) >> 8;
				int blue = clr & 0x000000ff;
				result[row][3 * col] = red;
				result[row][3 * col + 1] = green;
				result[row][3 * col + 2] = blue;
			}
		}

		List<List<Integer>> arrToWrite = new ArrayList<List<Integer>>();

		for (int topLeftCornerRow = 0; topLeftCornerRow < height; topLeftCornerRow += frameSize) {
			for (int topLeftCornerCol = 0; topLeftCornerCol < width * 3; topLeftCornerCol += frameSize * 3) {

				List<Integer> dataRow = new ArrayList<Integer>();
				for (int row = 0; row < frameSize; row++) {
					for (int col = 0; col < frameSize * 3; col++) {
						dataRow.add(result[topLeftCornerRow + row][topLeftCornerCol + col]);
					}
				}
				arrToWrite.add(dataRow);
			}
		}

		writeMatrixNoId(arrToWrite, fileDest, sep);
	}

	public static void writeMatrixNoId(List<List<Integer>> arrToWrite, String filepath, String sep) {
		PrintWriter wr;
		try {
			wr = new PrintWriter(filepath, "UTF-8");

			for (int i = 0; i < arrToWrite.size(); i++) {
				for (int j = 0; j < arrToWrite.get(0).size() - 1; j++) {
					wr.print((arrToWrite.get(i).get(j)) + sep);
				}
				wr.print((arrToWrite.get(i).get(arrToWrite.get(0).size() - 1)) + "\n");
			}

			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeMatrixToImage(List<List<Integer>> matrix, String imgFile, String filepath, String sep,
			int frameSize, String format) throws IOException {
		File fimg = new File(imgFile);
		BufferedImage image = ImageIO.read(fimg);

		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage imageNew = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = (WritableRaster) imageNew.getData();

		List<List<Integer>> arrToWrite = new ArrayList<List<Integer>>();
		List<List<Integer>> dataRows = new ArrayList<List<Integer>>();
		for (int i = 0; i < frameSize; i++) {
			dataRows.add(new ArrayList<Integer>());
		}

		for (int row = 0; row < matrix.size(); row++) {
			for (int i = 0; i < frameSize; i++) {
				for (int col = 0; col < frameSize * 3; col++) {
					dataRows.get(i).add(matrix.get(row).get(i * frameSize * 3 + col));
				}
				if (dataRows.get(i).size() == width * 3) {
					arrToWrite.add(dataRows.get(i));
					dataRows.set(i, new ArrayList<Integer>());
				}
			}
		}

		int[] pixels1D = new int[height * width * 3];
		for (int row = 0; row < arrToWrite.size(); row++) {
			for (int col = 0; col < arrToWrite.get(0).size(); col++) {
				pixels1D[row * arrToWrite.get(0).size() + col] = arrToWrite.get(row).get(col);
			}
		}

		raster.setPixels(0, 0, width, height, pixels1D);
		imageNew.setData(raster);
		ImageIO.write(imageNew, format, new File(filepath));
	}

	public static List<List<Integer>> readPixels(String fileSrc, String sep) throws IOException {
		List<List<Integer>> ret = new ArrayList<List<Integer>>();
		for (String line : Files.readAllLines(Paths.get(fileSrc), StandardCharsets.UTF_8)) {
			String[] rowStr = line.split(sep);
			List<Integer> row = new ArrayList<Integer>();
			for (int i = 0; i < rowStr.length; i++) {
				int rgb = (int) Double.parseDouble(rowStr[i]);
				row.add(rgb);
			}
			ret.add(row);
		}
		return ret;
	}
	
	public static void writePointsAsClusters(List<Integer> indexes, List<List<Double>> clusters, String filepath,
			String sep) {
		PrintWriter wr;
		try {
			wr = new PrintWriter(filepath, "UTF-8");

			for (int i = 0; i < indexes.size(); i++) {
				for (int j = 0; j < clusters.get(0).size() - 1; j++) {
					wr.print((clusters.get(indexes.get(i)).get(j)) + sep);
				}
				wr.print((clusters.get(indexes.get(i)).get(clusters.get(0).size() - 1)) + "\n");
			}

			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
