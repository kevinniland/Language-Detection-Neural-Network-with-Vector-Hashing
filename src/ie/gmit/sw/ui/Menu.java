package ie.gmit.sw.ui;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

import javax.swing.JFileChooser;

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
 *          Menu - Simple menu from the user can choose several different
 *          options such as specifying the n-gram size, vector size, etc.
 */
public class Menu {
	private NeuralNetwork neuralNetwork;
	private BasicNetwork basicNetwork;
	private MLDataSet mlDataSet;
	private Duration duration;
	private Instant start, stop;
	private Scanner scanner = new Scanner(System.in);
	private String languageFile = "wili-2018-Small-11750-Edited.txt", userFile, userString;
	private boolean keepAlive = true;
	private long timeElapsed;
	private int menuChoice, epochs, ngramSize, inputsVectorSize;
	private double errorRate;

	public void menu() throws Exception {
		while (keepAlive) {
			System.out.println("\nLanguage Detection Neural Network with Vector Hashing");
			System.out.println("=====================================================");
			System.out.println("Enter 1 to use cross validation to train a neural network,");
			System.out.println("Enter 2 to use resilient propagation to train a neural network,");
			System.out.println("Enter 3 to predict language of a file with cross validation,");
			System.out.println("Enter 4 to predict language of a file with resilient propagation,");
			System.out.println("Enter 5 to quit the program: ");
			menuChoice = scanner.nextInt();

			switch (menuChoice) {
			case 1:
				System.out.println("Enter n-gram size: ");
				ngramSize = scanner.nextInt();
				
				System.out.println("Enter input size: ");
				inputsVectorSize = scanner.nextInt();
				
				System.out.println("Enter number of epochs: ");
				epochs = scanner.nextInt();
				
				new VectorProcessor(ngramSize, inputsVectorSize).parse();
				neuralNetwork = new NeuralNetwork(inputsVectorSize, epochs, 0);
				
				basicNetwork = neuralNetwork.configureTopology();
				mlDataSet = neuralNetwork.generateDataSet();
				
				neuralNetwork.crossValidation(basicNetwork, mlDataSet);
				neuralNetwork.getAccuracy(basicNetwork, mlDataSet);
				break;
			case 2:
				System.out.println("Enter n-gram size: ");
				ngramSize = scanner.nextInt();
				
				System.out.println("Enter input size: ");
				inputsVectorSize = scanner.nextInt();
				
				System.out.println("Enter error rate to train to (in the format of #.####): ");
				errorRate = scanner.nextDouble();
				
				new VectorProcessor(ngramSize, inputsVectorSize).parse();
				neuralNetwork = new NeuralNetwork(inputsVectorSize, 0, errorRate);
				
				basicNetwork = neuralNetwork.configureTopology();
				mlDataSet = neuralNetwork.generateDataSet();
				
				neuralNetwork.resilientPropagation(basicNetwork, mlDataSet);
				neuralNetwork.getAccuracy(basicNetwork, mlDataSet);
				break;
			case 3:
				System.out.println("Enter file name: ");
				userFile = scanner.next();
				
				System.out.println("Enter n-gram size: ");
				ngramSize = scanner.nextInt();
				
				System.out.println("Enter input size: ");
				inputsVectorSize = scanner.nextInt();
				
				new VectorPredictor(userFile, ngramSize, inputsVectorSize).parse();
				break;
			case 4:
				
				break;
			case 5:
				System.out.println("Program terminated");
				
				keepAlive = false;
				break;
			default:
				System.out.println("ERROR: Invalid input. Please try again");
				break;
			}
		}
	}
}
