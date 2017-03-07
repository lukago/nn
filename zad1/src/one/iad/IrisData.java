package one.iad;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class IrisData {

    public static void readData(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = br.readLine();
            
            while (line != null) {
                String[] row = line.split(",");   
                String data = row[0]+"\t"+row[1]+"\t"+row[2]+"\t"+row[3];

                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(row[4]+".data", true)));
                out.println(data);
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
