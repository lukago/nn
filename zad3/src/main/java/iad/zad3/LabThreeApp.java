package iad.zad3;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import iad.zad3.DataPoints;

public class LabThreeApp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			String[] names = { "data/sample1.data", "data/sample2.data" };
			boolean flag = true;
			DataPoints.initData();
			List<List<Double>> dataPoints = DataPoints.calcFirstCentroids(names[0], "\t", 2);
			DataPoints.calcResults(names[0], "\t", dataPoints);
			
			final Runtime rt = Runtime.getRuntime();
			rt.exec("gnuplot " + System.getProperty("user.dir") + "/plot.txt");
			
			List<List<Double>> prevDataPoints = new ArrayList<List<Double>>();
			
			for(int i=0; i< 15 && flag == true; i++) {
				prevDataPoints = dataPoints;
				DataPoints.writePoints(dataPoints, "results_data/points");
				DataPoints.calcResults(names[0], "\t", dataPoints);
				dataPoints = DataPoints.calcNextCentroids("\t", dataPoints, 2);
				if(prevDataPoints.retainAll(dataPoints) == false) {
					flag=false;
				}
				if(flag == true){
					DataPoints.initData();
				}
			}
			
		rt.exec("gnuplot " + System.getProperty("user.dir") + "/plot2.txt");	
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
