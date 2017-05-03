package iad.lab3.activationFunctions;

import java.io.Serializable;

import iad.lab3.ActivationFunction;

public class Sigmoidal implements ActivationFunction, Serializable{

	private static final long serialVersionUID = -5677576066640858761L;

	@Override
	public double evalute(double value) {
		return 1 / (1 + Math.exp(-value));
	}

	@Override
	public double evaluteDerivate(double value) {
		double fx = evalute(value);
		return fx * (1.0 - fx);
	}
}
