package iad.mlp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import iad.mlp.activationFunctions.Sigmoidal;
import iad.mlp.utlis.MLPUtils;

public class MlpCli {

	public static void main(String args[]) {
		Options options = createOptions();
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;
		

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e1) {
			String usage = "-b True/False -d int -e int -l n0-n1-..-nN -lr double -m double -i String -o String";
			formatter.printHelp(usage,options);
			return;
		}

		// initalize mlp
		int[] layers = Arrays.asList(cmd.getOptionValue("l").split("-"))
				.stream()
				.mapToInt(Integer::parseInt)
				.toArray();
		double learningRate = Double.parseDouble(cmd.getOptionValue("lr"));
		double momentum = Double.parseDouble(cmd.getOptionValue("m"));
		boolean useBias = Boolean.parseBoolean(cmd.getOptionValue("b"));
		int epochs = Integer.parseInt(cmd.getOptionValue("e"));
		double div = Integer.parseInt(cmd.getOptionValue("d"));
		String in = cmd.getOptionValue("i");
		String out = cmd.getOptionValue("o");

		ActivationFunction f = new Sigmoidal();
		String cmd1 = "gnuplot -c " + System.getProperty("user.dir") + "/gnuplot/plot.gpt " + out;

		MultiLayerPerceptron mlp = new MultiLayerPerceptron(layers, learningRate, momentum, useBias, f, div);

		// local
		double[] epochOut;
		double[][] inputs = MLPUtils.readMatrix(in, "\t");
		double[][] outputs = MLPUtils.readMatrix(out, "\t");
		double[] quadFunVals = new double[epochs];
		List<Integer> indexes = new ArrayList<>();
		for (int i = 0; i < outputs.length; i++) {
			indexes.add(i);
		}

		for (int i = 0; i < epochs; i++) {
			Collections.shuffle(indexes);
			for (int j = 0; j < inputs.length; j++) {
				epochOut = mlp.backPropagate(inputs[indexes.get(j)], outputs[indexes.get(j)]);
				quadFunVals[i] += MLPUtils.quadraticCostFun(epochOut, outputs[indexes.get(j)]);
			}
		}

		new File("results").mkdir();
		MLPUtils.serialize(mlp, "results/mlp.bin");
		mlp = MLPUtils.deserialize("results/mlp.bin");

		double[][] outFinal = new double[inputs.length][];
		for (int i = 0; i < inputs.length; i++) {
			outFinal[i] = mlp.execute(inputs[i]);
			MLPUtils.writeMLPData("results/mlp" + i + ".data", mlp);
		}

		MLPUtils.writeQuadFun("results/quad.data", quadFunVals);
		MLPUtils.writeResults("results/out.data", outFinal);
		MLPUtils.writeError("results/error.data", outFinal, outputs);
		MLPUtils.rumCmd(cmd1);
	}

	private static Options createOptions() {
		Options options = new Options();

		Option l = new Option("l", "layers", true, "mlp layers");
		l.setRequired(true);
		options.addOption(l);

		Option lr = new Option("lr", "learning", true, "learning rate");
		lr.setRequired(true);
		options.addOption(lr);

		Option m = new Option("m", "momentum", true, "momentum");
		m.setRequired(true);
		options.addOption(m);

		Option b = new Option("b", "bias", true, "bias");
		b.setRequired(true);
		options.addOption(b);

		Option e = new Option("e", "epochs", true, "mlp epochs");
		e.setRequired(true);
		options.addOption(e);

		Option d = new Option("d", "div", true, "gaussian rand divide");
		d.setRequired(true);
		options.addOption(d);

		Option in = new Option("i", "input", true, "input data");
		in.setRequired(true);
		options.addOption(in);

		Option out = new Option("o", "output", true, "output expected data");
		out.setRequired(true);
		options.addOption(out);
		

		return options;
	}

}
