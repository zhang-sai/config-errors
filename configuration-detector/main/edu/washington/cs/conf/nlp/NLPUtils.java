package edu.washington.cs.conf.nlp;

public class NLPUtils {

	public static String[] extractWords(String line) {
		String[] words = line.split("\\s+");
		for(int i = 0; i < words.length; i++) {
			words[i] = words[i].toLowerCase();
		}
		return words;
	}

	public static int countFreq(String w, String[] words) {
		int c = 0;
		for(String word : words) {
			if(w.equals(word)) {
				c++;
			}
		}
		return c;
	}
	
}
