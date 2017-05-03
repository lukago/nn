package iad.lab3.activationFunctions;

import iad.lab3.ActivationFunction;

public class Sigmoidal implements ActivationFunction {

	@Override
	public double evalute(double value) 
	{
		return 1 / (1 + Math.pow(Math.E, - value));
	}

	@Override
	public double evaluteDerivate(double value) 
	{
		return (value - Math.pow(value, 2));
	}
}
