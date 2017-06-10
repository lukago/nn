package iad.mlp.utlis;

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
    transient FileInputStream inImage;
    transient FileInputStream inLabel;

    double[][] data;
    double[][] labels;

    public IdxManager(String inputImagePath, String inputLabelPath,
                      String outputPath, boolean writeImg) {
        this.inputImagePath = inputImagePath;
        this.inputLabelPath = inputLabelPath;
        this.outputPath = outputPath;
        this.writeImg = writeImg;
    }

    public void load() throws IOException {

        int[] hashMap = new int[10];

        inImage = new FileInputStream(inputImagePath);
        inLabel = new FileInputStream(inputLabelPath);

        int magicNumberImages = (inImage.read() << 24) | (inImage.read() << 16)
                | (inImage.read() << 8) | (inImage.read());
        int numberOfImages = (inImage.read() << 24) | (inImage.read() << 16)
                | (inImage.read() << 8) | (inImage.read());
        int numberOfRows = (inImage.read() << 24) | (inImage.read() << 16)
                | (inImage.read() << 8) | (inImage.read());
        int numberOfColumns = (inImage.read() << 24) | (inImage.read() << 16)
                | (inImage.read() << 8) | (inImage.read());

        int magicNumberLabels = (inLabel.read() << 24) | (inLabel.read() << 16)
                | (inLabel.read() << 8) | (inLabel.read());
        int numberOfLabels = (inLabel.read() << 24) | (inLabel.read() << 16)
                | (inLabel.read() << 8) | (inLabel.read());

        BufferedImage image = new BufferedImage(numberOfColumns,
                numberOfRows, BufferedImage.TYPE_INT_ARGB);

        int numberOfPixels = numberOfRows * numberOfColumns;
        int[] imgPixels = new int[numberOfPixels];
        data = new double[numberOfLabels][];
        labels = new double[numberOfLabels][10];

        for (int i = 0; i < numberOfImages; i++) {

            if (i % 5000 == 0) {
                System.out.println("Number of images extracted: " + i);
            }

            for (int p = 0; p < numberOfPixels; p++) {
                int gray = 255 - inImage.read();
                imgPixels[p] = 0xFF000000 | (gray << 16) | (gray << 8) | gray;
            }

            int label = inLabel.read();

            data[i] = Arrays.stream(imgPixels).asDoubleStream().toArray();
            labels[i][label] = 1.0;

            hashMap[label]++;
            if (writeImg) {
                File outputfile = new File(outputPath + label + "_0" +
                        hashMap[label] + ".png");
                image.setRGB(0, 0, numberOfColumns, numberOfRows, imgPixels, 0,
                        numberOfColumns);
                ImageIO.write(image, "png", outputfile);
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
}
