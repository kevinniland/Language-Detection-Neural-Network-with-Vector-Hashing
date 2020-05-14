package ie.gmit.sw.neuralnetwork;

import java.io.File;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationSoftMax;
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
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;

import ie.gmit.sw.helpers.Utilities;
import ie.gmit.sw.processing.VectorProcessor;

public class NeuralNetwork {
	private BasicNetwork basicNetwork;
	private CrossValidationKFold crossValidationKFold;
	private DataSetCODEC dataSetCODEC;
	private FoldedDataSet foldedDataSet;
	private MemoryDataLoader memoryDataLoader;
	private MLData mlDataOutput, mlDataActual, mlDataIdeal;
	private MLDataSet mlDataSet;
	private MLTrain mlTrain;
	private ResilientPropagation resilientPropagation;
	private int i, counter = 0, epoch = 1, inputs = 300, outputs = 235, hiddenLayers = inputs * 3;
	private int idealIndex = 0, resultIndex = -1;
	private final double MAX_ERROR = 0.004;
	private double correctValues = 0, total = 0;
	private double[] results;

	public NeuralNetwork() {

	}

	public void trainNeuralNetwork() {
		// Configure the neural network topology.
		basicNetwork = new BasicNetwork();

		basicNetwork.addLayer(new BasicLayer(new ActivationReLU(), true, inputs));

		basicNetwork.addLayer(new BasicLayer(new ActivationReLU(), true, hiddenLayers));
		basicNetwork.addLayer(new BasicLayer(new ActivationSoftMax(), false, outputs));
//		basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), true, 10));
//		basicNetwork.addLayer(new BasicLayer(new ActivationSoftMax(), false, 4));

		basicNetwork.getStructure().finalizeStructure();
		basicNetwork.reset();

		/**
		 * Read the CSV file "data.csv" into memory. Encog expects your CSV file to have
		 * input + output number of columns.
		 */
		dataSetCODEC = new CSVDataCODEC(new File("data.csv"), CSVFormat.DECIMAL_POINT, false, inputs, outputs, false);
		memoryDataLoader = new MemoryDataLoader(dataSetCODEC);
		mlDataSet = memoryDataLoader.external2Memory();

		foldedDataSet = new FoldedDataSet(mlDataSet);
		mlTrain = new ResilientPropagation(basicNetwork, foldedDataSet);
		crossValidationKFold = new CrossValidationKFold(mlTrain, 5);

//		resilientPropagation = new ResilientPropagation(basicNetwork, mlDataSet);
//		resilientPropagation.addStrategy(new RequiredImprovementStrategy(5));

		System.out.println("INFO: Training neural network...");
//		EncogUtility.trainToError(resilientPropagation, MAX_ERROR);

		do {
			crossValidationKFold.iteration();
			
			epoch++;
			
			System.out.println("Epoch: " + epoch);
			System.out.println("Error: " + crossValidationKFold.getError());
		} while (crossValidationKFold.getError() > MAX_ERROR);

		System.out.println(
				"INFO: Training complete in " + epoch + " epochs with error = " + crossValidationKFold.getError());

		Utilities.saveNeuralNetwork(basicNetwork, "./test.nn");
		crossValidationKFold.finishTraining();
//		resilientPropagation.finishTraining();

//		BasicNetwork loadedNetwork = Utilities.loadNeuralNetwork("./test.nn");
//		MLDataSet training = mdl.external2Memory();
//		double err = loadedNetwork.calculateError(training);
//
//		System.out.println("Loaded network’s error is (should be same as above) : " + err);
//		EncogUtility.evaluate(loadedNetwork, training);

		/**
		 * 
		 */
		for (MLDataPair mlDataPair : mlDataSet) {
			total++;

			mlDataOutput = basicNetwork.compute(mlDataPair.getInput());
			mlDataActual = mlDataOutput;
			mlDataIdeal = mlDataPair.getIdeal();

			results = mlDataActual.getData();

			for (i = 0; i < results.length; i++) {
				if (results[i] > 0 && (resultIndex == -1 || results[i] > results[resultIndex])) {
					resultIndex = i;
				}
			}

			for (i = 0; i < mlDataIdeal.size(); i++) {
				if (mlDataIdeal.getData(i) == 1.0) {
					idealIndex = i;

					if (idealIndex == resultIndex) {
						correctValues++;
					}
				}
			}

			counter++;
		}

		System.out.println("INFO: Testing complete. Accuracy: " + (correctValues / total) * 100 + "%");
		Encog.getInstance().shutdown();
	}

	public static void main(String[] args) throws Exception {
		new VectorProcessor(4, 100).parse();

		for (int i = 0; i < 5; i++) {
			new NeuralNetwork().trainNeuralNetwork();
		}
	}
}