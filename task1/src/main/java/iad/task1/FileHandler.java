package iad.task1;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
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

	public static void writeMatrixWithId(List<List<Double>> ip, String filepath, String sep) {
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

	public static List<List<Integer>> readPixels(String fileSrc, String sep) {
		List<List<Integer>> ret = new ArrayList<List<Integer>>();
		try {
			for (String line : Files.readAllLines(Paths.get(fileSrc), StandardCharsets.UTF_8)) {
				String[] rowStr = line.split(sep);
				List<Integer> row = new ArrayList<Integer>();
				for (int i = 0; i < rowStr.length; i++) {
					int rgb = (int) Double.parseDouble(rowStr[i]);
					row.add(rgb);
				}
				ret.add(row);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static void parseIMG(String imgFile, String fileDest, String sep, int frameSize) {
		File fimg = new File(imgFile);
		BufferedImage image = null;
		int width = 0;
		int height = 0;

		try {
			image = ImageIO.read(fimg);
		} catch (IOException e) {
			e.printStackTrace();
		}

		width = image.getWidth();
		height = image.getHeight();
		if ((width * 3) % (frameSize) != 0 || (height) % (frameSize) != 0) {
			throw new IllegalArgumentException("cannot get frames");
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

	public static void writeMatrixToImage(List<List<Integer>> matrix, String imgFile, String filepath, String sep,
			int frameSize, String format) {
		BufferedImage image = null;
		int width = 0;
		int height = 0;

		try {
			File fimg = new File(imgFile);
			image = ImageIO.read(fimg);
		} catch (IOException e) {
			e.printStackTrace();
		}

		width = image.getWidth();
		height = image.getHeight();
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

		try {
			ImageIO.write(imageNew, format, new File(filepath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void compareImg(String imgS1, String imgS2) {
		BufferedImage img1 = null;
		BufferedImage img2 = null;
		try {
			img1 = ImageIO.read(new File(imgS1));
			img2 = ImageIO.read(new File(imgS2));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int width1 = img1.getWidth(null);
		int width2 = img2.getWidth(null);
		int height1 = img1.getHeight(null);
		int height2 = img2.getHeight(null);
		if ((width1 != width2) || (height1 != height2)) {
			System.err.println("Error: Images dimensions mismatch");
			System.exit(1);
		}
		long diff = 0;
		for (int y = 0; y < height1; y++) {
			for (int x = 0; x < width1; x++) {
				int rgb1 = img1.getRGB(x, y);
				int rgb2 = img2.getRGB(x, y);
				int r1 = (rgb1 >> 16) & 0xff;
				int g1 = (rgb1 >> 8) & 0xff;
				int b1 = (rgb1) & 0xff;
				int r2 = (rgb2 >> 16) & 0xff;
				int g2 = (rgb2 >> 8) & 0xff;
				int b2 = (rgb2) & 0xff;
				diff += Math.abs(r1 - r2);
				diff += Math.abs(g1 - g2);
				diff += Math.abs(b1 - b2);
			}
		}
		double n = width1 * height1 * 3;
		double p = diff / n / 255.0;
		System.out.println("diff percent: " + (p * 100.0));
	}
}
