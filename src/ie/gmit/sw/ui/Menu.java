package ie.gmit.sw.ui;

import java.text.DecimalFormat;
import java.util.Scanner;

import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;

import ie.gmit.sw.neuralnetwork.NeuralNetwork;
import ie.gmit.sw.processing.VectorPredictor;
import ie.gmit.sw.processing.VectorProcessor;

/**
 * @author Kevin Niland
 * @category GUI
 * @version 1.0
 * 
 *          Menu - Simple menu from which the user can choose several different
 *          options such as specifying the n-gram size, vector size, number of
 *          epochs to perform, and error rate to train the neural network to
 */
public class Menu {
	private NeuralNetwork neuralNetwork;
	private BasicNetwork basicNetwork;
	private MLDataSet mlDataSet;
	private DecimalFormat decimalFormat;
	private Scanner scanner = new Scanner(System.in);
	private String userFile, trainedNN;
	private boolean keepAlive = true;
	private int menuChoice, epochs, ngramSize, inputsVectorSize;
	private double errorRate;
	private long start, time;

	/**
	 * Menu - User can train the neural network using K-Fold Cross Validation,
	 * Resilient Propagation, and predict a language using the trained K-Fold Cross
	 * Validation or Resilient Propagation neural network
	 * 
	 * @throws Exception
	 */
	public void menu() throws Exception {
		decimalFormat = new DecimalFormat("##.##");

		while (keepAlive) {
			System.out.println("\nLanguage Detection Neural Network with Vector Hashing");
			System.out.println("=====================================================");
			System.out.println("Enter 1 to use cross validation to train a neural network,");
			System.out.println("Enter 2 to use resilient propagation to train a neural network,");
			System.out.println("Enter 3 to predict language of a file using one of the neural networks or,");
			System.out.println("Enter 4 to quit the program: ");
			menuChoice = scanner.nextInt();

			switch (menuChoice) {
			case 1:
				/**
				 * Case 1 deals with cross validation with a mix of resilient propagation and
				 * will train the neural network using K-Fold Cross Validation. The user is
				 * prompted to enter an n-gram size, input size, and the number of epochs to
				 * train the neural network for
				 * 
				 * From testing, the ideal inputs are as follows: 
				 * N-gram size - 3 
				 * Input size - 400 
				 * Number of epochs - 10
				 * 
				 * This yields an error rate of 0.001664 and an accuracy of 78.79%, which takes
				 * approx. 1 minute to train
				 */
				System.out.println("Enter n-gram size: ");
				ngramSize = scanner.nextInt();

				System.out.println("Enter input size: ");
				inputsVectorSize = scanner.nextInt();

				System.out.println("Enter number of epochs: ");
				epochs = scanner.nextInt();

				start = System.currentTimeMillis();

				new VectorProcessor(ngramSize, inputsVectorSize).parse();
				neuralNetwork = new NeuralNetwork(inputsVectorSize, epochs, 0);

				// Configure the network topology and generate the dataset
				basicNetwork = neuralNetwork.configureTopology(inputsVectorSize);
				mlDataSet = neuralNetwork.generateDataSet();

				neuralNetwork.crossValidation(basicNetwork, mlDataSet);
				neuralNetwork.getAccuracy(basicNetwork, mlDataSet);

				// Calculate time taken to train the neural network
				time = (System.currentTimeMillis() - start) / 1000;

				if (time < 60) {
					System.out.println("Time taken: " + decimalFormat.format(time) + " second(s)");
				} else {
					System.out.println("Time taken: " + decimalFormat.format(time / 60) + " minute(s)");
				}
				break;
			case 2:
				/**
				 * Case 2 deals with resilient propagation solely and will train the neural
				 * network using Resilient Propagation. The user is prompted to enter an n-gram
				 * size, input size, and the number of epochs to train the neural network for
				 * 
				 * From testing, the ideal inputs are as follows: 
				 * N-gram size - 3
				 * Input size - 400
				 * Error rate - 0.0002
				 * 
				 * This yields an accuracy of 97.69%, which takes 51 iterations and approx. 43 
				 * seconds
				 */
				System.out.println("Enter n-gram size: ");
				ngramSize = scanner.nextInt();

				System.out.println("Enter input size: ");
				inputsVectorSize = scanner.nextInt();

				System.out.println("Enter error rate to train to (in the format of #.####): ");
				errorRate = scanner.nextDouble();

				start = System.currentTimeMillis();
				
				new VectorProcessor(ngramSize, inputsVectorSize).parse();
				neuralNetwork = new NeuralNetwork(inputsVectorSize, 0, errorRate);

				// Configure the network topology and generate the dataset
				basicNetwork = neuralNetwork.configureTopology(inputsVectorSize);
				mlDataSet = neuralNetwork.generateDataSet();

				neuralNetwork.resilientPropagation(basicNetwork, mlDataSet);
				neuralNetwork.getAccuracy(basicNetwork, mlDataSet);
				
				// Calculate time taken to train the neural network
				time = (System.currentTimeMillis() - start) / 1000;
				
				if (time < 60) {
					System.out.println("Time taken: " + decimalFormat.format(time) + " second(s)");
				} else {
					System.out.println("Time taken: " + decimalFormat.format(time / 60) + " minute(s)");
				}
				break;
			case 3:
				/**
				 * Case 3 deals with predicting the language of a file using one of the trained
				 * neural networks
				 * 
				 * N.B: To use one of the trained neural networks, make sure the input size you
				 * enter here is the same input size you entered to train the neural networks
				 * previously
				 */	
				System.out.println("Enter file name: ");
				userFile = scanner.next();

				System.out.println("Enter n-gram size: ");
				ngramSize = scanner.nextInt();

				System.out.println("Enter input size: ");
				inputsVectorSize = scanner.nextInt();

				System.out.println("Enter neural network to use: ");
				trainedNN = scanner.next();

				new VectorPredictor(userFile, ngramSize, inputsVectorSize, trainedNN).parse();
				break;
			case 4:
				// Case 4 terminates the program
				System.out.println("\nTerminating program...");

				keepAlive = false;
				System.exit(0);
				return;
			default:
				System.out.println("ERROR: Invalid input. Please try again");
				break;
			}
		}
	}
}
