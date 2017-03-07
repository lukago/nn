package one.iad;

import java.io.IOException;
import java.io.PrintWriter;

public class IrisApp {

    public static void main(String[] args) {

        IrisData.readData("iris.data");
        
        getResults("iris.data", ",");
        getResults("Iris-setosa.data", "\t");

    }

    public static void getResults(String filename, String sep) {
        try{
            PrintWriter wr = new PrintWriter(filename+"results", "UTF-8");          
            wr.println(filename);
            wr.println("sep len \tsep wid \tpen len \tpen wid");
            
            wr.println("\nmax:");
            for (int i =0; i < 4; i++) {
                wr.printf("%.2f", DataMath.max(IrisData.getColumn(i, filename, sep)));
                wr.print("\t\t");
            }
            
            wr.println("\nmin:");
            for (int i =0; i < 4; i++) {
                wr.printf("%.2f", DataMath.min(IrisData.getColumn(i, filename, sep)));
                wr.print("\t\t");
            }
            
            wr.println("\nrange:");
            for (int i =0; i < 4; i++) {
                wr.printf("%.2f", DataMath.range(IrisData.getColumn(i, filename, sep)));
                wr.print("\t\t");
            }
            
            wr.println("\n1st quartile:");
            for (int i =0; i < 4; i++) {
                wr.printf("%.2f", DataMath.quartile(IrisData.getColumn(i, filename, sep), 25));
                wr.print("\t\t");
            }
            
            wr.println("\n2nd quartile:");
            for (int i =0; i < 4; i++) {
                wr.printf("%.2f", DataMath.quartile(IrisData.getColumn(i, filename, sep), 50));
                wr.print("\t\t");
            }
            
            wr.println("\n3rd quartile:");
            for (int i =0; i < 4; i++) {
                wr.printf("%.2f", DataMath.quartile(IrisData.getColumn(i, filename, sep), 75));
                wr.print("\t\t");
            }
            
            wr.println("\nharmonic mean:");
            for (int i =0; i < 4; i++) {
                wr.printf("%.2f", DataMath.harmonicMean(IrisData.getColumn(i, filename, sep)));
                wr.print("\t\t");
            }
            
            wr.println("\ngeometric mean:");
            for (int i =0; i < 4; i++) {
                wr.printf("%.2f", DataMath.geometricMean(IrisData.getColumn(i, filename, sep)));
                wr.print("\t\t");
            }
            
            wr.println("\narithmetic mean:");
            for (int i =0; i < 4; i++) {
                wr.printf("%.2f", DataMath.arithmeticMean(IrisData.getColumn(i, filename, sep)));
                wr.print("\t\t");
            }
            
            wr.println("\nvariance:");
            for (int i =0; i < 4; i++) {
                wr.printf("%.2f", DataMath.variance(IrisData.getColumn(i, filename, sep)));
                wr.print("\t\t");
            }
            
            wr.println("\nstandard deviation:");
            for (int i =0; i < 4; i++) {
                wr.printf("%.2f", DataMath.standardDeviation(IrisData.getColumn(i, filename, sep)));
                wr.print("\t\t");
            }
            
            wr.println("\nkurtosis:");
            for (int i =0; i < 4; i++) {
                wr.printf("%.2f", DataMath.kurtosis(IrisData.getColumn(i, filename, sep)));
                wr.print("\t\t");
            }
            
            wr.close();
        
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
     
}
