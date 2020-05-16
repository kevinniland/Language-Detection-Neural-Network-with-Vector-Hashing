package ie.gmit.sw.gui;

import java.util.Scanner;

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
	private Scanner scanner = new Scanner(System.in);
	private boolean keepAlive = true;
	private int menuChoice, ngramSize, inputsVectorSize;

	public void menu() throws Exception {
		while (keepAlive) {
			System.out.println("Language Detection Neural Network with Vector Hashing");
			System.out.println("=====================================================");
			System.out.println("Enter 1 to specify ngram, inputs, and vector size,");
			System.out.println("Enter 3 to quit the program:");
			menuChoice = scanner.nextInt();

			// Menu
			switch (menuChoice) {
			case 1:
				System.out.println("Enter n-gram size: ");
				ngramSize = scanner.nextInt();
				
				System.out.println("Enter the number of inputs and vector size (must be the same value): ");
				inputsVectorSize = scanner.nextInt();
				
				new VectorProcessor(ngramSize, inputsVectorSize).parse();
				new NeuralNetwork(inputsVectorSize).fiveFoldNeuralNetwork();
				break;
			case 2: 
				break;
			case 3:
				keepAlive = false;
				break;
			default:
				System.out.println("ERROR: Invalid input");
			}
		}
	}
}
