package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

public class VectorProcessor {
	private CharSequence kmer;
	private Language[] languages = Language.values();
	private DecimalFormat decimalFormat = new DecimalFormat("###.###");
	private int i, index, n = 4;
	private double[] vector = new double[100];
	
	public void readFile() throws Exception {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("wili-2018-Small-11750-Edited.txt")));
			String line = null;
			
			while((line = bufferedReader.readLine()) != null) {
				process(line);
			} 
			
			bufferedReader.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	public void process(String line) throws Exception {
		String[] record = line.split("@");
		String text, lang;
		
		// Handles any invalid lines of text
		if (record.length > 2) {
			return;
		}
		
		text = record[0].toUpperCase();
		lang = record[1];
		
		
		for (i = 0; i < vector.length; i++) {
			vector[i] = 0; 
		}
		
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
	
	public static void main(String[] args) throws Exception {
		new VectorProcessor().readFile();
	}
}
