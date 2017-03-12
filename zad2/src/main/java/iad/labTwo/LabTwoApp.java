package iad.labTwo;

import java.util.Arrays;
import java.util.Random;

public class LabTwoApp {

    public static void main(String[] args) {

        IrisData.readData("data/iris.data");
        
        /* test imp */
        Random r = new Random();
        double[] t = new double [200];
        double sd = 5;
        double mean = 100;
        
        for(int i = 0; i<200; i++) {
        	t[i] = r.nextGaussian()*sd+mean;
        }
        
        Arrays.sort(t);
        
        for(int i = 0; i<200; i++) {
        	System.out.println(t[i]);
        }
        
    }
}
