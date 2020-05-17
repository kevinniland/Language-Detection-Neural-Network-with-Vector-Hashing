package ie.gmit.sw.gui;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

import javax.swing.JFileChooser;

import ie.gmit.sw.neuralnetwork.NeuralNetwork;
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
	private Duration duration;
	private Instant start, stop;
	private Scanner scanner = new Scanner(System.in);
	private String languageFile = "wili-2018-Small-11750-Edited.txt", userFile, userString;
	private boolean keepAlive = true;
	private long timeElapsed;
	private int menuChoice, ngramSize, inputsVectorSize;

	public void menu() throws Exception {
		while (keepAlive) {
			System.out.println("\nLanguage Detection Neural Network with Vector Hashing");
			System.out.println("=====================================================");
			System.out.println("Enter 1 to train neural network against WiLI dataset,");
			System.out.println("Enter 2 to predict langauge of a file,");
			System.out.println("Enter 3 to predict langauge of a string,");
			System.out.println("Enter 4 to quit the program:");
			menuChoice = scanner.nextInt();

			// Menu
			switch (menuChoice) {
			case 1:
				/**
				 * Trains the neural network using the WiLI Language Dataset and outputs the
				 * error and accuracy
				 */
				System.out.println("\nWiLI Dataset\n============");
				System.out.println("Enter n-gram size: ");
				ngramSize = scanner.nextInt();

				System.out.println("Enter the number of inputs and vector size (must be the same value): ");
				inputsVectorSize = scanner.nextInt();

//				new VectorProcessor(languageFile, ngramSize, inputsVectorSize).parse();
				new VectorProcessor(ngramSize, inputsVectorSize).parse();

				start = Instant.now();

				new NeuralNetwork(inputsVectorSize).fiveFoldNeuralNetwork();

				stop = Instant.now();

				duration = Duration.between(start, stop);
				timeElapsed = duration.toSeconds();

				System.out.println("Time taken: " + timeElapsed + " seconds");
				break;
			case 2:
				/**
				 * Predicts the language of a file using the trained neural network
				 */
				System.out.println("\nFile\n====");
				System.out.println("Enter file name: ");
				userFile = scanner.next();

				System.out.println("Enter n-gram size: ");
				ngramSize = scanner.nextInt();

				System.out.println("Enter the number of inputs and vector size (must be the same value): ");
				inputsVectorSize = scanner.nextInt();

//				new VectorProcessor(userFile, ngramSize, inputsVectorSize).parse();
				new VectorProcessor(ngramSize, inputsVectorSize).parse();

				start = Instant.now();

				stop = Instant.now();

				duration = Duration.between(start, stop);
				timeElapsed = duration.toSeconds();

				System.out.println("Time taken: " + timeElapsed + " seconds");
				break;
//			case 3:
//				System.out.println("\nString\n======");
//				System.out.println("Enter n-gram size: ");
//				ngramSize = scanner.nextInt();
//
//				System.out.println("Enter the number of inputs and vector size (must be the same value): ");
//				inputsVectorSize = scanner.nextInt();
//
//				new VectorProcessor(ngramSize, inputsVectorSize).parse();
//
//				start = Instant.now();
//				
//				stop = Instant.now();
//
//				duration = Duration.between(start, stop);
//				timeElapsed = duration.toSeconds();
//				
//				System.out.println("Time taken: " + timeElapsed + " seconds");
//				break;
			case 4:
				keepAlive = false;
				break;
			case 5:
				System.out.println("Enter n-gram size: ");
				ngramSize = scanner.nextInt();

				System.out.println("Enter the number of inputs and vector size (must be the same value): ");
				inputsVectorSize = scanner.nextInt();

//				new VectorProcessor(languageFile, ngramSize, inputsVectorSize).parse();
				new VectorProcessor(ngramSize, inputsVectorSize).parse();
			default:
				System.out.println("ERROR: Invalid input");
			}
		}
	}
}
