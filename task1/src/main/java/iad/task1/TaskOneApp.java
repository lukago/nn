package iad.task1;

public class TaskOneApp {

	public static void main(String[] args) {
		
		KMeans km = new KMeans(4, 2, "data/sample2.data", "\t");
		km.calc(300);

	}

}
