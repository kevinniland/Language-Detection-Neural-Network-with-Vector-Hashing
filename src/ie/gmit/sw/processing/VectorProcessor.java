package ie.gmit.sw.processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import ie.gmit.sw.Utilities;
import ie.gmit.sw.language.Language;

public class VectorProcessor {
	private CharSequence kmer;
	private Language[] languages = Language.values();
	private DecimalFormat decimalFormat = new DecimalFormat("###.###");
	private int i, index, n = 4;
	private double[] vector = new double[100];

	public VectorProcessor() {

	}

	public void readFile() {
		System.out.println("Reading WiLI language dataset...");

		// Read in the language dataset
		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream("wili-2018-Small-11750-Edited.txt")));
			String line = null;

			System.out.println("Done");
			System.out.println("Parsing file...");

			// Split the file and trim it
			while ((line = bufferedReader.readLine()) != null) {
				String[] fileRecord = line.trim().split("@");

				if (fileRecord.length != 2) {
					continue;
				}

				parse(fileRecord[0], fileRecord[1]);
			}

			System.out.println("Done");
			bufferedReader.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Parses the text and languages of the subject file. Creates kmers from the
	 * subject text and add the kmer and language the kmer is in to the database
	 * 
	 * @param text - Subject text from the data set file
	 * @param lang - Language of subject text
	 */
	private void parse(String text, String lang, int... ks) {
		Language language = Language.valueOf(lang);

		for (i = 0; i < vector.length; i++) {
			vector[i] = 0;
		}

		/**
		 * Create the k-mers
		 * 
		 * Add the k-mer and language to the proxy database
		 */
		// Loop over text, for each n-gram compute:
		for (i = 0; i <= text.length() - n; i++) {
			kmer = text.substring(i, i + n);

			index = kmer.hashCode() % vector.length;

			vector[index]++;
		}

		Utilities.normalize(vector, -1, 1);

		// Write out vector to csv file using decimalFormat for each vector index
		// Write out the language numbers to same row in csv file

		// 0, 0, 0, 0, 0, ...., 1

		// vector.length + #labels
	}

	public static void main(String[] args) {
		new VectorProcessor().readFile();
	}
}
