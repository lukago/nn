package iad.task1;

// Lukasz Golebiewski 203882
// Jakub Mielczarek 203943

public class TaskOneApp {

	public static void main(String[] args) {
		
		//images();	
		//txt();
		//float p = FileHandler.compareImage(new File("images/lenakolor3.jpg"), new File("results_km/out.bmp") );
		//System.out.println(p);
		
		//n = 20, iter = 20 fs =3
		//FileHandler.compareImg("images/lenakolor3.jpg", "results_km/out.png");
		
		//int n = 20; iter = 2000; mapRadius = 50; lr = 0.05; double lrc = 1;
		//FileHandler.compareImg("images/lenakolor3.jpg", "results_ng/out.png");
		
		//int n = 100; iter = 2000; mapRadius = 1; lr = 0.05; double lrc = 1;	
		FileHandler.compareImg("images/lenakolor3.jpg", "results_khn/out.png");
	}
	
	public static void images() {
		FileHandler.makeEmptyDir("data_img");
		String imageFile = "images/lenakolor3.jpg";
		String imageData = "data_img/img.data";
		String sep = "\t";
		int frameSz = 3;
		int n = 20;
		int iter = 1000;
		double mapRadius = 1;
		double lr = 0.1;
		double lrc = 1;	
		
		FileHandler.parseIMG(imageFile, imageData, sep, frameSz);
		
		//KMeans k = new KMeans(n, iter, imageData, sep, false);
		//k.calc(false, null, null);
		
		Kohonen k = new Kohonen(n, iter, imageData, sep, false, mapRadius, lr, lrc); 
		k.calc(false, null, null);
		
		//NeuralGas k = new NeuralGas(n, iter, imageData, sep, false, mapRadius, lr, lrc); 
		//k.calc(false, null, null);
		
		FileHandler.writeMatrixToImage(FileHandler.readPixels(k.destDir + k.imgcprFile, sep), 
				imageFile, k.destDir+"out.png", sep, frameSz, "png");
	}
	
	public static void txt() {
		//Neural neural = new KMeans(6, 10, "data/circle1.data", "\t", false);
		//neural.calc(true, "1 2 3", "gnuplot/plot_km.gpt");
		
		//Neural neural = new Kohonen(6, 1000, "data/sample1.data", "\t", false, 1, 0.1, 1); 
		//neural.calc(true, "1 2 3", "gnuplot/plot_khn.gpt");

		//Neural neural = new NeuralGas(6, 1000, "data/sample1.data", "\t", false, 1, 0.1, 1); 
		//neural.calc(true, "1 2 3", "gnuplot/plot_ng.gpt");
	}
}
