package iad.lab3.activationFunctions;

import java.io.Serializable;

import iad.lab3.ActivationFunction;

public class Sigmoidal implements ActivationFunction, Serializable {

	private static final long serialVersionUID = -5677576066640858761L;

	@Override
	public double evalute(double value) {
		return 1.0 / (1.0 + Math.exp(-value));
	}	

	@Override
	public double evaluteDerivate(double value) {
		return value * (1 - value);
	}
}
