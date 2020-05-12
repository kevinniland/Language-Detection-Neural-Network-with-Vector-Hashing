package ie.gmit.sw.neuralnetwork;

import java.io.File;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;

import ie.gmit.sw.Utilities;

public class NeuralNetwork {

	/*
	 * *************************************************************************************
	 * NB: READ THE FOLLOWING CAREFULLY AFTER COMPLETING THE TWO LABS ON ENCOG AND REVIEWING
	 * THE LECTURES ON BACKPROPAGATION AND MULTI-LAYER NEURAL NETWORKS! YOUR SHOULD ALSO 
	 * RESTRUCTURE THIS CLASS AS IT IS ONLY INTENDED TO DEMO THE ESSENTIALS TO YOU. 
	 * *************************************************************************************
	 * 
	 * The following demonstrates how to configure an Encog Neural Network and train
	 * it using backpropagation from data read from a CSV file. The CSV file should
	 * be structured like a 2D array of doubles with input + output number of columns.
	 * Assuming that the NN has two input neurons and two output neurons, then the CSV file
	 * should be structured like the following:
	 *
	 *			-0.385,-0.231,0.0,1.0
	 *			-0.538,-0.538,1.0,0.0
	 *			-0.63,-0.259,1.0,0.0
	 *			-0.091,-0.636,0.0,1.0
	 * 
	 * The each row consists of four columns. The first two columns will map to the input
	 * neurons and the last two columns to the output neurons. In the above example, rows 
	 * 1 an 4 train the network with features to identify a category 2. Rows 2 and 3 contain
	 * features relating to category 1.
	 * 
	 * You can normalize the data using the Utils class either before or after writing to 
	 * or reading from the CSV file. 
	 */
	public NeuralNetwork() {
		int inputs = 2; //Change this to the number of input neurons
		int outputs = 2; //Change this to the number of output neurons
		
		// Configure the neural network topology. 
		BasicNetwork basicNetwork = new BasicNetwork();
		basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), true, inputs)); //You need to figure out the activation function
		// basicNetwork.addLayer(....); //You need to figure out the number of hidden layers and their neurons
		// basicNetwork.addLayer(....);
		basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), true, outputs));
		basicNetwork.getStructure().finalizeStructure();
		basicNetwork.reset();

		//Read the CSV file "data.csv" into memory. Encog expects your CSV file to have input + output number of columns.
		DataSetCODEC dataSetCODEC = new CSVDataCODEC(new File("data.csv"), CSVFormat.ENGLISH, false, inputs, outputs, false);
		MemoryDataLoader memoryDataLoader = new MemoryDataLoader(dataSetCODEC);
		MLDataSet mlDataSet = memoryDataLoader.external2Memory();
		
		FoldedDataSet foldedDataSet = new FoldedDataSet(mlDataSet);
		MLTrain mlTrain = new ResilientPropagation(basicNetwork, foldedDataSet);
		CrossValidationKFold crossValidationKFold = new CrossValidationKFold(mlTrain, 5);

		// Use backpropagation training with alpha = 0.1 and momentum = 0.2
		Backpropagation backpropagation = new Backpropagation(basicNetwork, mlDataSet, 0.1, 0.2);

		// Train the neural network
		int epoch = 1; // Use this to track the number of epochs
		
		do { 
			crossValidationKFold.iteration(); 
			epoch++;
		} while(crossValidationKFold.getError() > 0.01);
		
		Utilities.saveNeuralNetwork(basicNetwork, "./test.nn");
	}
}