package iad.task1;

// Lukasz Golebiewski 203882
// Jakub Mielczarek 203943

public class TaskOneApp {

	public static void main(String[] args) {
		
		images();	
		//txt();

	}
	
	public static void txt() {
		//Neural neural = new KMeans(60, 100, "data/circle1.data", "\t", false);
		//neural.calc(true, "1 2 3", "gnuplot/plot_km.gpt");
		
		//Neural neural = new Kohonen(6, 1000, "data/circle1.data", "\t", false, 0.1, 0.1, 1); 
		//neural.calc(true, "1 2 3", "gnuplot/plot_khn.gpt");

		//Neural neural = new NeuralGas(16, 1000, "data/circle1.data", "\t", false, 1, 0.1, 1); 
		//neural.calc(true, "1 2 3", "gnuplot/plot_ng.gpt");
	}
	
	public static void images() {
		FileHandler.makeEmptyDir("data_img");
		String imageFile = "images/img.jpg";
		String imageData = "data_img/img.data";
		String sep = "\t";
		int frameSz = 4;
		int n = 20;
		int iter = 300;
		double mapRadius = 100;
		double lr = 0.1;
		double lrc = 1;	
		
		FileHandler.parseIMG(imageFile, imageData, sep, frameSz);
		
		//KMeans k = new KMeans(n, iter, imageData, sep, false);
		//k.calc(false, null, null);
		
		//Kohonen k = new Kohonen(n, iter, imageData, sep, false, mapRadius, lr, lrc); 
		//k.calc(false, null, null);
		
		NeuralGas k = new NeuralGas(n, iter, imageData, sep, false, mapRadius, lr, lrc); 
		k.calc(false, null, null);
		
		FileHandler.writeMatrixToImage(FileHandler.readPixels(k.destDir + k.imgcprFile, sep), 
				imageFile, k.destDir+"out.bmp", sep, frameSz, "bmp");
	}
}
