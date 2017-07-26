package edu.washington.cs.conf.nlp;

//need to parse the wordnet data manually

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;

//get the synonym for some words
public class WordNetReader {
	
	private static IDictionary dict = null;
	
	public static IDictionary getDict() {
		if(dict == null) {
			String wnhome = "D:\\develop-tools\\wordnet\\";
			String path = wnhome + File.separator + "dict";
			try {
			    URL url = new URL("file", null , path );
			    //construct the dictionary object and open it
			    dict = new Dictionary ( url);
			    dict.open();
			} catch (Throwable e) {
				throw new Error(e);
			}
		}
		return dict;
	}
	
	private static POS[] getPos() {
		return new POS[]{POS.NOUN, POS.ADJECTIVE, POS.ADVERB, POS.VERB};
	}
	
	//cach the info to speed up
	static private Map<String, Collection<String>> cache = new LinkedHashMap<String, Collection<String>>();
	
	public static Collection<String> getSyn(String aWord) {
		if(cache.containsKey(aWord)) {
			return cache.get(aWord);
		}
		Set<String> words = new HashSet<String>();
		//get the dictionary
		IDictionary dict = getDict();
		for(POS pos : getPos()) {
		    IIndexWord idxWord = dict.getIndexWord (aWord, pos );
		    if(idxWord == null) {
		    	continue;
		    }
		    for(int index = 0; index < idxWord.getWordIDs().size(); index++) {
		    	IWordID wordID = idxWord.getWordIDs().get(index) ; // 1st meaning
			    IWord word = dict.getWord (wordID);
			    ISynset synset = word.getSynset ();
			    // iterate over words associated with the synset
			    for( IWord w : synset.getWords ()) {
				    if(w.getLemma().equals(aWord)) {
					    continue;
				    }
				    String synWord = w.getLemma();
				    //replace _ by a space
				    synWord = synWord.replace('_', ' ');
				    words.add(synWord);
			    }
		    }
		    
		}
		
		//put in the cache
		cache.put(aWord, words);
		
		return words;
	}

}