package ie.gmit.sw.neuralnetwork;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationBiPolar;
import org.encog.engine.network.activation.ActivationBipolarSteepenedSigmoid;
import org.encog.engine.network.activation.ActivationClippedLinear;
import org.encog.engine.network.activation.ActivationCompetitive;
import org.encog.engine.network.activation.ActivationLOG;
import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.RequiredImprovementStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;

//import deepnetts.data.DataSets;
//import deepnetts.net.FeedForwardNetwork;
//import deepnetts.net.layers.activation.ActivationType;
//import deepnetts.net.loss.LossType;
import ie.gmit.sw.helpers.Utilities;
import ie.gmit.sw.processing.VectorProcessor;

public class NeuralNetwork {
	private BasicNetwork basicNetwork, savedNetwork;
	private CrossValidationKFold crossValidationKFold;
	private DataSetCODEC dataSetCODEC;
	private DecimalFormat decimalFormat;
	private FoldedDataSet foldedDataSet;
	private MemoryDataLoader memoryDataLoader;
	private MLData mlDataOutput;
	private MLDataSet mlDataSet;
	private MLTrain mlTrain;
	private String csvFile = "data.csv";
	private File nnFile = new File("test.nn");
	private int i, counter = 0, epoch = 0, inputs = 0, outputs = 235;
	private int actual = 0, check = 0;
	private final double MAX_ERROR = 0.00425;
	private int correctValues = 0, total = 0;
	private double alpha = 0.01;
	private double limit = -1;
	private double nnError, percent;
	private double hiddenLayers = Math.sqrt(inputs * outputs); // Geometric Pyramid Rule
//	private double hiddenLayers = inputs / (alpha * (inputs + outputs));
	private double[] results;

	public NeuralNetwork(int inputs) {
		this.inputs = inputs;
	}

	// Trains a neural network using 5-fold cross validation
	public void fiveFoldNeuralNetwork() {
		// Configures the neural network topology
		basicNetwork = new BasicNetwork();

		basicNetwork.addLayer(new BasicLayer(new ActivationReLU(), true, inputs));

//		basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), true, inputs));
		basicNetwork.addLayer(new BasicLayer(new ActivationSoftMax(), true, (int) hiddenLayers));

//		basicNetwork.addLayer(new BasicLayer(new ActivationReLU(), true, (int) hiddenLayers));
//		basicNetwork.addLayer(new BasicLayer(new ActivationBipolarSteepenedSigmoid(), false, (int) hiddenLayers));
//		basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), true, 500));
//		basicNetwork.addLayer(new BasicLayer(new ActivationClippedLinear(), false, 500));

		basicNetwork.addLayer(new BasicLayer(new ActivationReLU(), true, outputs));

		basicNetwork.getStructure().finalizeStructure();
		basicNetwork.reset();

		/**
		 * Read the CSV file "data.csv" into memory. Encog expects your CSV file to have
		 * input + output number of columns.
		 */
		dataSetCODEC = new CSVDataCODEC(new File(csvFile), CSVFormat.ENGLISH, false, inputs, outputs, false);
		memoryDataLoader = new MemoryDataLoader(dataSetCODEC);
		mlDataSet = memoryDataLoader.external2Memory();

		foldedDataSet = new FoldedDataSet(mlDataSet);
		mlTrain = new Backpropagation(basicNetwork, foldedDataSet);
		crossValidationKFold = new CrossValidationKFold(mlTrain, 5);

		System.out.println("\nINFO: Training neural network...");

		// Format crossValidationKFold output
		decimalFormat = new DecimalFormat("#.######");
		decimalFormat.setRoundingMode(RoundingMode.CEILING);

		do {
			crossValidationKFold.iteration();

			epoch++;

			System.out.println("Epoch: " + epoch);
			System.out.println("Error: " + decimalFormat.format(crossValidationKFold.getError()));
		} while (crossValidationKFold.getError() > MAX_ERROR);

		System.out.println("\nINFO: Training complete in " + epoch + " epochs with error = "
				+ decimalFormat.format(crossValidationKFold.getError()));

		Utilities.saveNeuralNetwork(basicNetwork, "./test.nn");
		crossValidationKFold.finishTraining();

		savedNetwork = Utilities.loadNeuralNetwork("./test.nn");
		mlDataSet = memoryDataLoader.external2Memory();
		nnError = savedNetwork.calculateError(mlDataSet);

		System.out.println("Saved network’s error: " + decimalFormat.format(nnError));

		for (MLDataPair mlDataPair : mlDataSet) {
			mlDataOutput = basicNetwork.compute(mlDataPair.getInput());

			for (i = 0; i < mlDataOutput.size(); i++) {
				if (mlDataOutput.getData(i) > limit) {
					limit = mlDataOutput.getData(i);

					actual = i;
				}
			}

			for (i = 0; i < mlDataOutput.size(); i++) {
				if (mlDataPair.getIdeal().getData(i) > limit) {
					limit = mlDataOutput.getData(i);

					check = i;
				}
			}

			if (actual == check) {
				correctValues++;
			}

			total++;
		}

		// Format accuracy
		decimalFormat = new DecimalFormat("##.##");
		decimalFormat.setRoundingMode(RoundingMode.CEILING);

		percent = (double) correctValues / (double) total;

		System.out.println("\nINFO: Testing complete.");
		System.out.println("Correct: " + correctValues + "/" + total);
		System.out.println("Accuracy: " + decimalFormat.format(percent * 100) + "%");

		// Shutdown
		Encog.getInstance().shutdown();
	}
}