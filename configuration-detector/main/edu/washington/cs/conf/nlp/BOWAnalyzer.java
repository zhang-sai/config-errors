package edu.washington.cs.conf.nlp;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.washington.cs.conf.util.Utils;

/**
 * This implements the bag of word model.
 * */
public class BOWAnalyzer {
	
	private boolean useWordNet = false;
	
	private boolean useAugmented = false;

	//with the use of tf-idf
	public final TFIDFWeightCalculator tfidf;
	
	public final WordNetReader reader = new WordNetReader();
	
	public BOWAnalyzer(TFIDFWeightCalculator tfidf) {
		Utils.checkNotNull(tfidf);
		this.tfidf = tfidf;
		this.tfidf.computeTFIDFValues();
	}
	
	public void setWordNet(boolean wordnet) {
		this.useWordNet = wordnet;
	}
	
	public void setAugmented(boolean augmented) {
		this.useAugmented = augmented;
	}
	
	public Float computeSimilarity(String s1, String s2) {
		String[] words1 = NLPUtils.extractWords(s1);
		Set<String> set1 = new LinkedHashSet<String>(Arrays.asList(words1));
		String[] words2 = NLPUtils.extractWords(s2);
		Set<String> set2 = new LinkedHashSet<String>(Arrays.asList(words2));
		
		return 0.5f * (
				computeOneWaySimilarity(set1, words1, set2, words2)
				+
				computeOneWaySimilarity(set2, words2, set1, words1)
				);
	}
	
	//
	private Float computeOneWaySimilarity(Set<String> set1, String[] words1, Set<String> set2, String[] words2) {
		float sum = 0.0f;
		for(String w1 : words1) {
			sum += this.tfidf.getIDFValue(w1);
		}
		float overlap = 0.0f;
		for(String w1 : words1) {
			float simValue = maxSim(w1, set2);
			float idfValue = this.tfidf.getIDFValue(w1);
			overlap += simValue * idfValue;
		}
		return overlap / sum;
	}
	
	private Float maxSim(String word, Set<String> words) {
		if(words.contains(word)) {
			return 1.0f;
		}
		
		if(!this.useWordNet) {
			return 0.0f;
		}
		//check wordnet to see if any synonym of word is in the words set
		Collection<String> synonyms = WordNetReader.getSyn(word);
		
		//check if there is any intersection
		boolean overlap = false;
		for(String syn : synonyms) {
			if(words.contains(syn)) {
				overlap = true;
				break;
			}
		}
		
		if(overlap) {
			return 1.0f;
		}
		
		if(!this.useAugmented) {
			return 0.0f;
		}
		
		//then use the augmented wordnet part
		//the task specific
		//if there is some synonym, then return true, otherwise, return false;
		overlap = TaskSpecificSynonyms.hasSynonym(word, words);
		
		return overlap ? 1.0f : 0.0f;
	}
}