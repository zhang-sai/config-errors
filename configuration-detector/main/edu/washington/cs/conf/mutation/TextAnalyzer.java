package edu.washington.cs.conf.mutation;

import edu.washington.cs.conf.nlp.BOWAnalyzer;
import edu.washington.cs.conf.nlp.NLPUtils;
import edu.washington.cs.conf.nlp.TFIDFWeightCalculator;

public class TextAnalyzer {

	public static float similarity_threshold = 0.1f;
	
	public static void setSimilarityThreshold(float threshold) {
		similarity_threshold = threshold;
	}

	//check if the error message contains the key, which can
	//be a configuration option name or value
	//this is an initial implementation
	public static boolean containsOptionName(String msg, String option) {
		String[] words = NLPUtils.extractWords(msg);
		//use this slightly complex way for matching, since some option name can be
		//as simple as "-t"
		String optionWithPrefix = MutatedConf.PREFIX + option;
		for(String word : words) {
			if(word.equals(option) || word.equals("'" + option + "'")
					|| word.equals("\"" + option + "\"")) {
				return true;
			}
			if(word.equals(optionWithPrefix) || word.equals("'" + optionWithPrefix + "'")
					|| word.equals("\"" + optionWithPrefix + "\"")) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean containsOptionValue(String msg, String value) {
		if(value.trim().equals("")) {
			return false;
		}
		return msg.indexOf(value) != -1;
	}
	
	//check if the error message is similar to its user manual description
	public static boolean isMessageCloseEnough(String errMsg, String manualDesc, UserManual manual) {
		TFIDFWeightCalculator cal = new TFIDFWeightCalculator(manual.getAllTextDesc());
		cal.computeTFIDFValues();
		BOWAnalyzer analyzer = new BOWAnalyzer(cal);
		float similarity = analyzer.computeSimilarity(errMsg, manualDesc);
		return similarity >= similarity_threshold;
	}
	
	public static boolean isClosestInManual(String errMsg, String mutatedOption, UserManual manual) {
		TFIDFWeightCalculator cal = new TFIDFWeightCalculator(manual.getAllTextDesc());
		cal.computeTFIDFValues();
		BOWAnalyzer analyzer = new BOWAnalyzer(cal);
		
		float similarity = analyzer.computeSimilarity(errMsg, manual.getDescription(mutatedOption));
		
		float restSimilarity = 0.0f;
		for(String optionName : manual.getAllOptions()) {
			if(optionName.equals(mutatedOption)) {
				continue;
			}
			String optionDesc = manual.getDescription(optionName);
			float sim = analyzer.computeSimilarity(errMsg, optionDesc);
			restSimilarity = Math.max(restSimilarity, sim);
		}
		
		return similarity >= restSimilarity;
	}
	
}
