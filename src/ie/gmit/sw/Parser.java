package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Parser implements Runnable {
//	private Database database = null;
	private String file;
	private int k;
	
	public Parser(String file, int k) {
		this.file = file;
		this.k = k;
	}
	
//	public void setDatabase(Database database) {
//		this.database = database;
//	}
	
	@Override
	public void run() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			
			while((line = bufferedReader.readLine()) != null) {
				String[] fileRecord = line.trim().split("@");
				
				if (fileRecord.length != 2) continue;
				parse(fileRecord[0], fileRecord[1]);
			} 
			
			bufferedReader.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	private void parse(String text, String lang, int... ks) {
		Language language = Language.valueOf(lang);
		
		for (int i = 0; i <= text.length() - k; i++) {
			CharSequence kmer = text.substring(i, i + k);
//			database.add(kmer, language);
		}
	}
	
	public static void main(String[] args) throws Throwable {
		Parser parser = new Parser("wili-2018-Small-11750-Edited.txt", 1);
		
//		Database database = new Database();
//		parser.setDatabase(database);
		
		Thread thread = new Thread(parser);
		thread.start();
		thread.join();
		
//		database.resize(300);
		
		String s = "The scene was bare, stark, unanimous dome of a school room, and the pre-square speaker of the speakers.";
		//String s = "Ba é an radharc cruinneachán lom, lom, aontónach de sheomra scoile, agus chuir réamhcheoltóir.";
		
		System.out.println(s);
		
//		System.out.println(database);
	}
}
