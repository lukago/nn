package iad.task1;

import java.io.IOException;


// Lukasz Golebiewski 203882
// Jakub Mielczarek 203943

public class TaskOneApp {

	public static void main(String[] args) {
		
		FileHandler.makeEmptyDir("data_img");
		String imageFile = "images/img.jpg";
		String imageData = "data_img/img.data";
		String sep = "\t";
		int frameSz = 2;
		
		try {		
			FileHandler.parseIMG(imageFile, imageData, sep, frameSz);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//KMeans k = new KMeans(20, frameSz*frameSz*3, imageData, sep, false, "1 2 49", 30, true);
		//k.calc(0);
		
		NeuralGas k = new NeuralGas(20, frameSz*frameSz*3, imageData, sep, false, "1 2 49", 
				300, 0.2, 100, 1, true);
		k.calc();
		
		//Kohonen k = new Kohonen(20, frameSz*frameSz*3, imageData, sep, false, "1 2 49", 
		//		50, 0.2, 300, 1, true);
		//k.calc();
		
		try {
			FileHandler.writeMatrixToImage(FileHandler.readPixels(k.destDir + k.imgcprFile, sep), 
					imageFile, k.destDir+"out.bmp", sep, frameSz, "bmp");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/////////
		/////////
		/////////
		
		//KMeans km = new KMeans(6, 2, "data/sample1.data", "\t", false, "1 2 3", 100, false);
		//km.calc(0);
		
		//Kohonen khn = new Kohonen(6, 2, "data/sample1.data", "\t", false, "1 2 3", 
		//		0.1, 0.05, 1000, 1, false);
		//khn.calc();

		//NeuralGas ng = new NeuralGas(6, 2, "data/sample1.data", "\t", false, "1 2 3", 
		//			5, 0.05, 1000, 1, false);
		//ng.calc();

	}
}
