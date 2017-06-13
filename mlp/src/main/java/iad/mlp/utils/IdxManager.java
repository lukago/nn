package iad.mlp.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Created on 6/10/2017.
 */
public class IdxManager implements AutoCloseable, Serializable {

    String inputImagePath;
    String inputLabelPath;
    String outputPath;
    boolean writeImg;
    int numOfRows;
    int numOfCols;
    transient FileInputStream inImage;
    transient FileInputStream inLabel;

    double[][] data;
    int[][] pixelData;
    double[][] labels;
    double[] labelsVec;

    public IdxManager(String inputImagePath, String inputLabelPath,
                      String outputPath, boolean writeImg) {
        this.inputImagePath = inputImagePath;
        this.inputLabelPath = inputLabelPath;
        this.outputPath = outputPath;
        this.writeImg = writeImg;
    }

    public IdxManager(String inputImagePath, String inputLabelPath) {
        this(inputImagePath, inputLabelPath, null, false);
    }

    public void load() throws IOException {

        inImage = new FileInputStream(inputImagePath);
        inLabel = new FileInputStream(inputLabelPath);

        int magicNumberImages = (inImage.read() << 24) | (inImage.read() << 16)
                | (inImage.read() << 8) | (inImage.read());
        int numberOfImages = (inImage.read() << 24) | (inImage.read() << 16)
                | (inImage.read() << 8) | (inImage.read());
        numOfRows = (inImage.read() << 24) | (inImage.read() << 16)
                | (inImage.read() << 8) | (inImage.read());
        numOfCols = (inImage.read() << 24) | (inImage.read() << 16)
                | (inImage.read() << 8) | (inImage.read());

        int magicNumberLabels = (inLabel.read() << 24) | (inLabel.read() << 16)
                | (inLabel.read() << 8) | (inLabel.read());
        int numberOfLabels = (inLabel.read() << 24) | (inLabel.read() << 16)
                | (inLabel.read() << 8) | (inLabel.read());

        BufferedImage image = new BufferedImage(numOfCols,
                numOfRows, BufferedImage.TYPE_INT_ARGB);

        int numberOfPixels = numOfRows * numOfCols;
        int[] imgPixels = new int[numberOfPixels];
        int[] imgVals = new int[numberOfPixels];
        data = new double[numberOfLabels][];
        pixelData = new int[numberOfLabels][];
        labels = new double[numberOfLabels][10];
        labelsVec = new double[numberOfLabels];

        for (int i = 0; i < numberOfImages; i++) {

            for (int p = 0; p < numberOfPixels; p++) {
                int gray = 255 - inImage.read();
                imgPixels[p] = 0xFF000000 | (gray << 16) | (gray << 8) | gray;
                imgVals[p] = gray;
            }

            int label = inLabel.read();

            data[i] = Arrays.stream(imgVals).asDoubleStream().toArray();
            pixelData[i] = imgPixels;
            labels[i][label] = 1.0;
            labelsVec[i] = label;

            if (writeImg) {
                File outputfile = new File(outputPath + "img" + i + ".png");
                image.setRGB(0, 0, numOfCols, numOfRows, imgPixels, 0,
                        numOfCols);
                ImageIO.write(image, "png", outputfile);
            }

            if (i % 1000 == 0) {
                System.out.println("Number of images extracted: " + i);
                //return;
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (inImage != null) inImage.close();
        if (inLabel != null) inLabel.close();
    }

    public double[][] getData() {
        return data;
    }

    public double[][] getLabels() {
        return labels;
    }

    public double[] getLabelsVec() {
        return labelsVec;
    }

    public int[][] getPixelData() {
        return pixelData;
    }

    public int getNumOfRows() {
        return numOfRows;
    }

    public int getNumOfCols() {
        return numOfCols;
    }
}
