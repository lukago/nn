package iad.mlp.actfun;

import java.io.Serializable;

public class Sigmoidal implements ActivationFunction, Serializable {

	@Override
	public double evalute(double value) {
		return 1.0 / (1.0 + Math.exp(-value));
	}	

	@Override
	public double evaluteDerivate(double value) {
		return value * (1 - value);
	}
}
