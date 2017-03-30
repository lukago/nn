package iad.task1;

public class TaskOneApp {

	public static void main(String[] args) {
		
		System.out.println("Hello world!");
		KMeans km = new KMeans(5, 2, "data/sample1.data", "\t");
		km.calc();

	}

}
