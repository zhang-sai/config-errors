package edu.washington.cs.conf.diagnosis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OptionalDataException;
import java.io.PrintStream;
import java.io.StreamCorruptedException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

/**
 * Given a list of invariant files by good runs, and an invariant
 * file by a bad run. Return a map of method to its corresponding
 * scores. In the return map, the score is computed by counting the
 * number of good runs, where the method invariant differs from
 * the given bad run.
 * */
public class InvariantDiffAnalyzer {
	
	private final Collection<String> goodInvFiles = new LinkedList<String>();
	private final String badInvFile;
	
	public InvariantDiffAnalyzer(Collection<String> goodInvFiles, String badInvFile) {
		Utils.checkNotNull(goodInvFiles);
		Utils.checkNotNull(badInvFile);
		this.goodInvFiles.addAll(goodInvFiles);
		this.badInvFile = badInvFile;
	}
	
	public Map<String, Float> getMethodsWithDiffInvariants() {
		Map<String, Float> methodsAndScores = new LinkedHashMap<String, Float>();
		for(String goodInvFile : goodInvFiles) {
			System.out.println("Analyzing good inv file: " + goodInvFile);
			Set<String> diffMethods = this.getMethodsWithDiffInvariants(goodInvFile, badInvFile);
			for(String m : diffMethods) {
				if(!methodsAndScores.containsKey(m)) {
					methodsAndScores.put(m, 1.0f);
				} else {
					methodsAndScores.put(m, methodsAndScores.get(m) + 1.0f);
				}
			}
			
		}
		return methodsAndScores;
	}
	
	//call daikon to find out methods with different invariants
	Set<String> getMethodsWithDiffInvariants(String goodInvFile, String badInvFile) {
		Utils.checkTrue(Files.checkFileExistence(goodInvFile), "Not exist: " + goodInvFile);
		Utils.checkTrue(Files.checkFileExistence(badInvFile), "Not exist: " + badInvFile);
		
		try {
			return InvariantUtils.fetchMethodsWithDiffInvariants(goodInvFile, badInvFile);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Error();
		}
	}
}

