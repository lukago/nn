package iad.mlp.activationFunctions;

import java.io.Serializable;

import iad.mlp.ActivationFunction;

public class BinaryStep implements ActivationFunction, Serializable {

	private static final long serialVersionUID = -1009760446439414869L;

	@Override
	public double evalute(double value) {
		return 0;
	}

	@Override
	public double evaluteDerivate(double value) {
		return 0;
	}
	
}
