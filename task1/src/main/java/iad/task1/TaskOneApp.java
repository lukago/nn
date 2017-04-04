package iad.task1;

public class TaskOneApp {

	public static void main(String[] args) {
		
		//KMeans km = new KMeans(100, 2, "data/circle2.data", "\t", false, "1 2 3");
		//km.calc(0);
		
		//Kohonen khn = new Kohonen(6, 2, "data/sample1.data", "\t", false, "1 2 3", 0.4, 0.05, 1000, 1);
		//khn.calc();

		NeuralGas ng = new NeuralGas(10, 2, "data/circle2.data", "\t", false, "1 2 3", 1, 0.1, 1000, 1);
		ng.calc();

	}
}
