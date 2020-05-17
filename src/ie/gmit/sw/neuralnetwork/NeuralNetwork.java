package ie.gmit.sw.neuralnetwork;

import java.io.File;
import java.io.IOException;

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
	private FoldedDataSet foldedDataSet;
	private MemoryDataLoader memoryDataLoader;
	private MLData mlDataOutput, mlDataActual, mlDataIdeal;
	private MLDataSet mlDataSet;
	private MLTrain mlTrain;
	private ResilientPropagation resilientPropagation;
	private String csvFile = "data.csv";
	private File nnFile = new File("test.nn");
	private int i, counter = 0, epoch = 1, inputs = 0, outputs = 235;
	private int idealIndex = 0, resultIndex = -1;
	private final double MAX_ERROR = 0.00426;
	private int correctValues = 0, total = 0;
	private double alpha = 0.01;
//	private double hiddenLayers = Math.sqrt(inputs * outputs); // Geometric Pyramid Rule
	private double hiddenLayers = inputs / (alpha * (inputs + outputs));
	private double[] results;

	public NeuralNetwork(int inputs) {
		this.inputs = inputs;
	}

	// Trains a neural network using 5-fold cross validation
	public void fiveFoldNeuralNetwork() {
		// Configures the neural network topology
		basicNetwork = new BasicNetwork();

		basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), true, inputs));

		basicNetwork.addLayer(new BasicLayer(new ActivationReLU(), true, (int) hiddenLayers));
		basicNetwork.addLayer(new BasicLayer(new ActivationBipolarSteepenedSigmoid(), false, (int) hiddenLayers));
		basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), true, (int) hiddenLayers));
		basicNetwork.addLayer(new BasicLayer(new ActivationClippedLinear(), true, (int) hiddenLayers));
		basicNetwork.addLayer(new BasicLayer(new ActivationTANH(), true, outputs));
		basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), true, outputs));

		basicNetwork.addLayer(new BasicLayer(new ActivationReLU(), false, outputs));

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
//		mlTrain = new Backpropagation(basicNetwork, mlDataSet);
//		mlTrain = new Backpropagation(basicNetwork, foldedDataSet);
		mlTrain = new ResilientPropagation(basicNetwork, foldedDataSet);
		crossValidationKFold = new CrossValidationKFold(mlTrain, 5);

//		resilientPropagation = new ResilientPropagation(basicNetwork, mlDataSet);
//		resilientPropagation.addStrategy(new RequiredImprovementStrategy(5));

		System.out.println("\nINFO: Training neural network...");
//		EncogUtility.trainToError(resilientPropagation, MAX_ERROR);

		do {
			crossValidationKFold.iteration();

			System.out.println("Epoch: " + epoch);
			System.out.println("Error: " + crossValidationKFold.getError());

			epoch++;
		} while (crossValidationKFold.getError() > MAX_ERROR);

		System.out.println(
				"INFO: Training complete in " + epoch + " epochs with error = " + crossValidationKFold.getError());

		Utilities.saveNeuralNetwork(basicNetwork, "./test.nn");
		crossValidationKFold.finishTraining();
//		resilientPropagation.finishTraining();

		savedNetwork = Utilities.loadNeuralNetwork("./test.nn");
//		MLDataSet training = mdl.external2Memory();
//		double err = loadedNetwork.calculateError(training);
//
//		System.out.println("Loaded network’s error is (should be same as above) : " + err);
//		EncogUtility.evaluate(loadedNetwork, training);

		for (MLDataPair mlDataPair : mlDataSet) {
			MLData inputData = mlDataPair.getInput();
			MLData actualData = mlDataPair.getIdeal();
			MLData predictData = basicNetwork.compute(inputData);

			double actual = actualData.getData(0);
			double predict = predictData.getData(0);
			double diff = Math.abs(actual - predict);

			// Doesn't work - ignore
//			Direction actualDirection = Direction.determineDirection(actual);
//			Direction predictDirection = Direction.determineDirection(predict);

			// Gives 44% accuracy
			if (mlDataPair.getInput().getData(0) == mlDataPair.getInput().getData(1)) {
				correctValues++;
			}

			// Gives 99% accuracy
//			if (actual == predict) {
//				correctValues++;
//			}

			total++;
		}

		double percent = (double) correctValues / (double) total;

		System.out.println("\nINFO: Testing complete.");
		System.out.println("Correct: " + correctValues + "/" + total);
		System.out.println("Accuracy: " + (percent * 100) + "%\n");

		Encog.getInstance().shutdown();
	}
}