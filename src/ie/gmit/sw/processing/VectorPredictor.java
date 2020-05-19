package ie.gmit.sw.processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.encog.neural.networks.BasicNetwork;
import org.encog.util.simple.EncogUtility;

import ie.gmit.sw.helpers.Utilities;
import ie.gmit.sw.neuralnetwork.NeuralNetwork;

/**
 * @author Kevin Niland
 * @category Processing
 * @version 1.0
 *
 */
public class VectorPredictor {
	private BasicNetwork basicNetwork;
	private BufferedReader bufferedReader;
	private String file, line, ngram;
	private int i, ngramSize;
	private double[] vector;

	public VectorPredictor(String file, int ngramSize, int vectorSize) {
		this.file = file;
		this.ngramSize = ngramSize;
		vector = new double[vectorSize];
	}

	/**
	 * Parse file and pass it of to process()
	 * 
	 * @throws Exception
	 */
	public void parse() throws Exception {
		System.out.print("\nReading file " + file + "...");
//		System.out.println(System.getProperty("user.dir"));

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
	 * Process file
	 * 
	 * @param line
	 * @throws Exception
	 */
	public void process(String line) throws Exception {
		line = line.toLowerCase().replaceAll("[0-9]", "");

		for (i = 0; i < vector.length; i++) {
			vector[i] = 0;
		}

		for (i = 0; i < line.length() - ngramSize; i++) {
			ngram = line.substring(i, i + ngramSize);

			vector[ngram.hashCode() % vector.length]++;
		}
		
		System.out.println("Done");

		vector = Utilities.normalize(vector, 0, 1);

		new NeuralNetwork().getPrediction(vector);
	}
}
