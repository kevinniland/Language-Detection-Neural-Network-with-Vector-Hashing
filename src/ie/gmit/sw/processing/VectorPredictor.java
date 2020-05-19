package ie.gmit.sw.processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import ie.gmit.sw.helpers.Utilities;

public class VectorPredictor {
	private BufferedReader bufferedReader;
	private String line, ngram;
	private int i, ngramSize;
	private double[] vector;
	
	public VectorPredictor(int ngramSize, int vectorSize) {
		this.ngramSize = ngramSize;
		vector = new double[vectorSize];
	}
	
	/**
	 * Parse WiLI dataset and pass it of to process()
	 * 
	 * @throws Exception
	 */
	public double[] parse(String file) throws Exception {
		System.out.print("\nReading WiLI language dataset...");

		for (i = 0; i < vector.length; i++) {
			vector[i] = 0; 
		}
		
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));

			System.out.println("Done");
			System.out.print("Processing file...");

			while ((line = bufferedReader.readLine()) != null) {
				process(line);
			}

			System.out.println("Done");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		
		return Utilities.normalize(vector, 0, 1);
	}
	
	/**
	 * Process file
	 * 
	 * @param line
	 * @throws Exception
	 */
	public void process(String line) throws Exception {
		line = line.toLowerCase().replaceAll("[0-9]", "");
		
		for (i = 0; i < line.length() - ngramSize; i++) {
			ngram = line.substring(i, i + ngramSize);
			
			vector[ngram.hashCode() % vector.length]++;
		}
	}
}
