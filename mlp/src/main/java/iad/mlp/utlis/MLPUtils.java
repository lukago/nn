package iad.mlp.utlis;

public final class MLPUtils {

    private MLPUtils() {
    }

    public static double max(double[][] list) {
        double max = list[0][0];
        for (int i = 0; i < list.length; i++) {
            for (int j = 0; j < list[i].length; j++) {
                if (list[i][j] > max) max = list[i][j];
            }
        }
        return max;
    }

    public static int maxId(double[] list) {
        int id = 0;
        for (int i = 0; i < list.length; i++) {
            if (list[i] > list[id]) id = i;
        }
        return id;
    }

    public static double min(double[][] list) {
        double min = list[0][0];
        for (int i = 0; i < list.length; i++) {
            for (int j = 0; j < list[i].length; j++) {
                if (list[i][j] < min) min = list[i][j];
            }
        }
        return min;
    }

    public static void normalize(double[][] data) {
        double max = max(data);
        double min = min(data);

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                data[i][j] = (data[i][j] - min) / (max - min);
            }
        }
    }

    public static double quadraticCostFun(double[] newOutput, double exOutput[]) {
        double ret = 0.0;
        for (int i = 0; i < exOutput.length; i++) {
            ret += Math.pow(exOutput[i] - newOutput[i], 2);
        }
        return ret;
    }

    public static double[] calcError(double[][] newOutput, double exOutput[][]) {

        double[] error = new double[newOutput.length];
        double errorSum = 0;
        for (int i = 0; i < newOutput.length; i++) {
            for (int j = 0; j < exOutput[i].length; j++) {
                errorSum += Math.abs(newOutput[i][j] - exOutput[i][j]);
            }
            error[i] = errorSum;
            errorSum = 0;
        }

        return error;
    }
}
