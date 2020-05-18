package ie.gmit.sw.neuralnetwork;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
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

import ie.gmit.sw.helpers.Utilities;

public class NeuralNetwork {
	private BasicNetwork basicNetwork, savedNetwork;
	private CrossValidationKFold crossValidationKFold;
	private DataSetCODEC dataSetCODEC;
	private FoldedDataSet foldedDataSet;
	private MemoryDataLoader memoryDataLoader;
	private MLData mlDataActual, mlDataIdeal;
	private MLDataSet mlDataSet;
	private MLTrain mlTrain;
	private ResilientPropagation resilientPropagation;
	private DecimalFormat decimalFormat;
	private static int inputs = 510;
	private static final int outputs = 235;
	private int i, actual, correctValues = 0, epoch = 0, epochs, ideal, inputSize, result = -1, totalValues = 0;
	private int hiddenLayers = inputs / 4;
	private static final double MAX_ERROR = 0.0023;
	private double error, percent, limit = -1, errorRate;

	/**
	 * 
	 * @param inputSize
	 */
	public NeuralNetwork(int inputSize, int epochs, double errorRate) {
		this.inputSize = inputSize;
		this.epochs = epochs;
		this.errorRate = errorRate;
	}

	/**
	 * Configures the network topology
	 * 
	 * @return
	 */
	public BasicNetwork configureTopology() {
		basicNetwork = new BasicNetwork();

		basicNetwork.addLayer(new BasicLayer(new ActivationReLU(), true, inputSize));
		basicNetwork.addLayer(new BasicLayer(new ActivationReLU(), true, hiddenLayers, 400));
		basicNetwork.addLayer(new BasicLayer(new ActivationSoftMax(), false, outputs));

		basicNetwork.getStructure().finalizeStructure();
		basicNetwork.reset();

		return basicNetwork;
	}

	/**
	 * 
	 * @return
	 */
	public MLDataSet generateDataSet() {
		dataSetCODEC = new CSVDataCODEC(new File("data.csv"), CSVFormat.DECIMAL_POINT, false, inputSize, outputs,
				false);
		memoryDataLoader = new MemoryDataLoader(dataSetCODEC);
		mlDataSet = memoryDataLoader.external2Memory();

		return mlDataSet;
	}

	/**
	 * 
	 * @param basicNetwork
	 * @param mlDataSet
	 */
	public void crossValidation(BasicNetwork basicNetwork, MLDataSet mlDataSet) {
		foldedDataSet = new FoldedDataSet(mlDataSet);
		mlTrain = new Backpropagation(basicNetwork, foldedDataSet);
		crossValidationKFold = new CrossValidationKFold(mlTrain, 5);

		// Format crossValidationKFold output
		decimalFormat = new DecimalFormat("#.######");
		decimalFormat.setRoundingMode(RoundingMode.CEILING);

		do {
			crossValidationKFold.iteration();

			epoch++;

			System.out.println("Epoch: " + epoch);
			System.out.println("Error: " + decimalFormat.format(crossValidationKFold.getError()));
		} while (epoch < epochs);

		System.out.println("\nINFO: Training complete in " + epoch + " epochs with error = "
				+ decimalFormat.format(crossValidationKFold.getError()));

		Utilities.saveNeuralNetwork(basicNetwork, "./test.nn");
		crossValidationKFold.finishTraining();
	}

	/**
	 * 
	 * @param basicNetwork
	 * @param mlDataSet
	 */
	public void resilientPropagation(BasicNetwork basicNetwork, MLDataSet mlDataSet) {
		resilientPropagation = new ResilientPropagation(basicNetwork, mlDataSet);
		resilientPropagation.addStrategy(new RequiredImprovementStrategy(5));

		EncogUtility.trainToError(resilientPropagation, errorRate);

		Utilities.saveNeuralNetwork(basicNetwork, "./test.nn");
		resilientPropagation.finishTraining();
	}

//	public void evaluate(MLDataSet mlDataSet) {
//		savedNetwork = Utilities.loadNeuralNetwork("./test.nn");
//		error = savedNetwork.calculateError(mlDataSet);
//
//		System.out.println("Saved network's error rate: " + error);
//		EncogUtility.evaluate(savedNetwork, mlDataSet);
//	}

	public void getAccuracy(BasicNetwork basicNetwork, MLDataSet mlDataSet) {
		for (MLDataPair mlDataPair : mlDataSet) {
			mlDataActual = basicNetwork.compute(mlDataPair.getInput());
			mlDataIdeal = mlDataPair.getIdeal();

			for (i = 0; i < mlDataActual.getData().length; i++) {
				if (mlDataActual.getData(i) > 0
						&& (result == -1 || (mlDataActual.getData(i) > mlDataActual.getData(result)))) {
					result = i;
				}
			}

			for (i = 0; i < mlDataIdeal.size(); i++) {
				if (mlDataIdeal.getData(i) == 1) {
					ideal = i;

					if (result == ideal) {
						correctValues++;
					}
				}
			}

			totalValues++;
		}

//		for (MLDataPair mlDataPair : mlDataSet) {
//			mlDataActual = basicNetwork.compute(mlDataPair.getInput());
//
//			for (i = 0; i < mlDataActual.size(); i++) {
//				if (mlDataActual.getData(i) > limit) {
//					limit = mlDataActual.getData(i);
//
//					actual = i;
//				}
//			}
//
//			for (i = 0; i < mlDataActual.size(); i++) {
//				if (mlDataPair.getIdeal().getData(i) > limit) {
//					limit = mlDataActual.getData(i);
//
//					ideal = i;
//
//					if (actual == ideal) {
//						correctValues++;
//					}
//				}
//			}
//
//			totalValues++;
//		}

		// Format accuracy
		decimalFormat = new DecimalFormat("##.##");
		decimalFormat.setRoundingMode(RoundingMode.CEILING);

		percent = (double) correctValues / (double) totalValues;

		System.out.println("\nINFO: Testing complete.");
		System.out.println("Correct: " + correctValues + "/" + totalValues);
		System.out.println("Accuracy: " + decimalFormat.format(percent * 100) + "%");
	}
}