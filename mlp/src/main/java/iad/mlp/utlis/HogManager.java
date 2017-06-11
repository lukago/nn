package iad.mlp.utlis;

import org.opencv.core.*;
import org.opencv.objdetect.HOGDescriptor;


/**
 * Created on 6/10/2017.
 */
public class HogManager {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static double[] exportImgFeatures(double[] data, int rows, int cols) {

        Mat mat = new Mat(rows, cols, CvType.CV_8U);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                mat.put(i, j, data[i * cols + j]);
            }
        }

        HOGDescriptor hog = new HOGDescriptor(
                new Size(28, 28), //winSize
                new Size(14, 14), //blocksize
                new Size(7, 7), //blockStride,
                new Size(14, 14), //cellSize,
                9); //nbins

        MatOfFloat descriptors = new MatOfFloat();
        hog.compute(mat, descriptors);

        float[] descArr = descriptors.toArray();
        double retArr[] = new double[descArr.length];
        for (int i = 0; i < descArr.length; i++) {
            retArr[i] = descArr[i];
        }
        return retArr;
    }

    public static double[][] exportDataFeatures(double[][] data, int rows, int cols) {
        double[][] retArr = new double[data.length][];
        for (int i = 0; i<data.length; i++) {
            retArr[i] = HogManager.exportImgFeatures(data[i], rows, cols);
        }
        return retArr;
    }

}
