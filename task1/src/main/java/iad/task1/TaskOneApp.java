package iad.task1;

public class TaskOneApp {

	public static void main(String[] args) {
		
		/*KMeans km = new KMeans(2, 10, "data/generated3.data", " ", false, "8 9 11");
		km.calc(0);*/
		
		// zbyt male mapRadius - nie porusza sasiednimi
		Kohonen khn = new Kohonen(4, 2, "data/sample2.data", "\t", false, "1 2 3",
				100, 0.02, 1000);
		khn.calc();
	}

}
