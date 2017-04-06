package iad.task1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract class for neural network algorithms.
 */
public abstract class Neural {
	// dataset with neurons weights
	List<List<Double>> neurons;
	// dataset with points position in each dim
	List<List<Double>> data;
	// dataset with nerons closest to points
	// in same order as points in data list
	List<Integer> winnerIds;

	int iterations;
	int dimensions;
	String srcFilePath;
	String separator;

	public Neural(int neuronsNum, int iterations, String srcFilePath, String separator, boolean normalize) {
		this.data = FileHandler.readData(srcFilePath, separator);
		this.dimensions = data.get(0).size();
		this.neurons = new ArrayList<List<Double>>();
		this.winnerIds = new ArrayList<Integer>();

		this.srcFilePath = srcFilePath;
		this.separator = separator;
		this.iterations = iterations;

		if (normalize) {
			Utils.normalize(data, Utils.calcSds(data), false);
		}

		initNeurons(neuronsNum);
		calcWinnersIds();
	}

	/**
	 * Initalize neurons weights using nextGaussian.
	 * 
	 * @param neuronsNum number of neurons to initalize
	 */
	void initNeurons(int neuronsNum) {
		List<Double> avgs = new ArrayList<Double>();
		List<Double> sdevs = new ArrayList<Double>();
		List<Double> tmp = new ArrayList<Double>();
		Random r = new Random();

		for (int i = 0; i < dimensions; i++) {
			avgs.add(DataMath.arithmeticMean(Utils.getColumn(data, i)));
			sdevs.add(DataMath.standardDeviation(Utils.getColumn(data, i)));
		}

		for (int i = 0; i < neuronsNum; i++) {
			for (int j = 0; j < dimensions; j++) {
				tmp.add(r.nextGaussian() * sdevs.get(j) + avgs.get(j));
			}
			neurons.add(tmp);
			tmp = new ArrayList<Double>();
		}
	}

	/**
	 * Calc vector of indexes to closest to neurons for each data point to
	 * winnerIds list.
	 */
	void calcWinnersIds() {
		List<Double> point = new ArrayList<Double>();
		List<Double> distsToNeurons = new ArrayList<Double>();
		winnerIds = new ArrayList<Integer>();

		for (int i = 0; i < data.size(); i++) {
			point = data.get(i);

			// create array with distances to neurons for current data point
			distsToNeurons = new ArrayList<Double>();
			for (int j = 0; j < neurons.size(); j++) {
				distsToNeurons.add(Metric.euclidean(neurons.get(j), point));
			}

			// get index of min from created array
			winnerIds.add(distsToNeurons.indexOf(DataMath.min(distsToNeurons)));
		}
	}
	
	/**
	 * delete dead neurons
	 */
	void deleteDead() {
		calcWinnersIds();
		List<List<Double>> newNeurons = new ArrayList<List<Double>>();
		for (int i = 0 ; i < neurons.size(); i++) {
			if (winnerIds.contains(i)) {
				newNeurons.add(neurons.get(i));
			}
		}
		neurons = newNeurons;
	}
	
	/**
	 * reroll neurons if more dead than minDead
	 */
	void rerollDead(int numOfRerolls) {
		int minDead = Integer.MAX_VALUE;
		int dead = 0;
		
		for (int i = 0; i<numOfRerolls; i++) {
			for (int j = 0 ; j < neurons.size(); j++) {
				if (!winnerIds.contains(j)) {
					dead++;
				}
			}
			
			if (dead < minDead ) {
				initNeurons(neurons.size());
				calcWinnersIds();
				minDead = dead;
			}	
		}
	}

	/**
	 * Learn algorithm.
	 * @param epoch current epoch of algorithm.
	 */
	public abstract void learn(int epoch);

	/**
	 * Calculate results loop.
	 * 
	 * @param plot set true to run gnuplot and write files for it
	 * @param gptCols columns to plot
	 * @param relative path to plot script file
	 */
	public abstract void calc(boolean plot, String gptCols, String plotFile);
}
