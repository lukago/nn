package iad.mlp;

import iad.mlp.actfun.ActivationFunction;
import iad.mlp.actfun.Sigmoidal;
import iad.mlp.mlp.MultiLayerPerceptron;
import iad.mlp.utils.IOUtils;
import iad.mlp.utils.MLPUtils;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.Arrays;

class AppMlpCLI {

    public static void main(String[] args) {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = initCmd(parser, options, formatter, args);

        String in = cmd.getOptionValue("i");
        String out = cmd.getOptionValue("o");
        int epochs = Integer.parseInt(cmd.getOptionValue("e"));
        String gptCmd = "gnuplot -c " + System.getProperty("user.dir")
                + "/gnuplot/plotIDX.gpt ";

        // local
        double[][] inputs = IOUtils.readMatrix(in, "\t");
        double[][] outputs = IOUtils.readMatrix(out, "\t");
        MultiLayerPerceptron mlp = initMlp(cmd);

        // learn
        mlp.learn(epochs, inputs, outputs);

        new File("results").mkdir();
        IOUtils.serialize(mlp, "results/mlp.bin");
        mlp = IOUtils.deserialize("results/mlp.bin");

        double[][] outFinal = new double[inputs.length][];
        for (int i = 0; i < inputs.length; i++) {
            outFinal[i] = mlp.execute(inputs[i]);
            IOUtils.writeMLPData("results/mlp" + i + ".data", mlp);
        }
        double[] error = MLPUtils.calcError(outFinal, outputs);

        IOUtils.writeVector("results/quad.data", mlp.getQuadFunVals());
        IOUtils.writeMatrix("results/out.data", outFinal);
        IOUtils.writeVector("results/error.data", error);
        IOUtils.rumCmd(gptCmd);
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

    private static CommandLine initCmd(CommandLineParser parser, Options options,
                                       HelpFormatter formatter, String[] args) {
        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            String usage = "-b True/False -d int -e int -l n0-n1-..-nN -lr double" +
                    " -m double -i String -o String";
            formatter.printHelp(usage, options);
            return null;
        }
    }

    private static MultiLayerPerceptron initMlp(CommandLine cmd) {
        int[] layers = Arrays.stream(cmd.getOptionValue("l").split("-"))
                .mapToInt(Integer::parseInt)
                .toArray();
        double learningRate = Double.parseDouble(cmd.getOptionValue("lr"));
        double momentum = Double.parseDouble(cmd.getOptionValue("m"));
        double div = Integer.parseInt(cmd.getOptionValue("d"));

        boolean useBias = Boolean.parseBoolean(cmd.getOptionValue("b"));

        ActivationFunction f = new Sigmoidal();

        return new MultiLayerPerceptron(
                layers, learningRate, momentum, useBias, f, div);
    }
}
