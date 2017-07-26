package edu.washington.cs.conf.mutation;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.washington.cs.conf.nlp.WordNetReader;

import junit.framework.TestCase;

public class TestWordNet extends TestCase {

	public void testInstall() throws IOException {
//		IDictionary dict = WordNetReader.getDict();
//		
//		// look up first sense of the word "dog "
//		IIndexWord idxWord = dict . getIndexWord ("dog", POS.NOUN );
//		IWordID wordID = idxWord . getWordIDs ().get (0) ;
//		IWord word = dict . getWord ( wordID );
//		System .out . println ("Id = " + wordID );
//		System .out . println (" Lemma = " + word . getLemma ());
//		System .out . println (" Gloss = " + word . getSynset (). getGloss ());
//		
//		idxWord = dict.getIndexWord("technique", POS.NOUN );
//		wordID = idxWord.getWordIDs().get(0) ; // 1st meaning
//		word = dict.getWord (wordID);
//		ISynset synset = word.getSynset ();
//		
//		// iterate over words associated with the synset
//		for( IWord w : synset.getWords ())
//		   System.out.println( "=>" + w.getLemma ());
		
		System.out.println(WordNetReader.getSyn("software"));
	}
	
}
