package ie.gmit.sw.processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import ie.gmit.sw.helpers.Utilities;
import ie.gmit.sw.interfaces.PredictorInterface;
import ie.gmit.sw.neuralnetwork.NeuralNetwork;

/**
 * @author Kevin Niland
 * @category Processing
 * @version 1.0
 * 
 *          VectorPredictor - Parses and processes file containing a string of
 *          text to be predicted using a trained neural network
 */
public class VectorPredictor implements PredictorInterface {
	private BufferedReader bufferedReader;
	private String file, line, nnFile, ngram;
	private int i, ngramSize;
	private double[] vector;

	/**
	 * Constructor 
	 * 
	 * @param file       - File containing the string of text
	 * @param ngramSize  - N-gram size
	 * @param vectorSize - Input size (Input size must be the same as the input size
	 *                   chosen when training a neural network)
	 * @param nnFile     - Trained neural network that will be used to predict the
	 *                   language
	 */
	public VectorPredictor(String file, int ngramSize, int vectorSize, String nnFile) {
		this.file = file;
		this.ngramSize = ngramSize;
		vector = new double[vectorSize];
		this.nnFile = nnFile;
	}

	/**
	 * Parse file and pass it off to process()
	 * 
	 * @throws Exception
	 */
	public void parse() throws Exception {
		System.out.print("\nReading file " + file + "...");

		try {
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));

			System.out.println("Done");
			System.out.print("Processing file...");

			while ((line = bufferedReader.readLine()) != null) {
				process(line);
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	/**
	 * Process the user file and construct n-grams from it
	 * 
	 * @param line - Line in file
	 * @throws Exception
	 */
	public void process(String line) throws Exception {
		line = line.toLowerCase().replaceAll("[0-9]", "");

		// Reset vector index i to 0 on each iteration
		for (i = 0; i < vector.length; i++) {
			vector[i] = 0;
		}

		// Construct n-grams from the file
		for (i = 0; i < line.length() - ngramSize; i++) {
			ngram = line.substring(i, i + ngramSize);

			vector[ngram.hashCode() % vector.length]++;
		}

		System.out.println("Done");

		// Normalize vector values between 0 and 1
		vector = Utilities.normalize(vector, 0, 1);

		new NeuralNetwork().getPrediction(nnFile, vector);
	}
}
