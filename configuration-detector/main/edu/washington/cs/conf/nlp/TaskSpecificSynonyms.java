package edu.washington.cs.conf.nlp;

import java.util.LinkedHashSet;
import java.util.Set;

import plume.Pair;

public class TaskSpecificSynonyms {
	
	private static Set<Pair<String, String>> synonymSet =
		new LinkedHashSet<Pair<String, String>>();

	static {
		//add to the synonym set
	}
	
	public static boolean hasSynonym(String aWord, Set<String> words) {
		for(String word : words) {
			Pair<String, String> p = Pair.of(aWord, word);
			if(synonymSet.contains(p)) {
				return true;
			}
			p = Pair.of(word, aWord);
			if(synonymSet.contains(p)) {
				return true;
			}
		}
		return false;
	}
}