package edu.washington.cs.conf.instrument.evol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.util.Utils;

public class CountingTracer {
	
	public static String COUNT_SEP = "==>";

	//an instruction to its post-dominant instruction
//	public Map<String, String> postDomMaps = new HashMap<String, String>();
	public Map<String, Set<String>> reversePostDom = new HashMap<String, Set<String>>();
	
	public Map<String, Integer> indexMaps = new HashMap<String, Integer>();
	public boolean[] flagArray = null;
	public int[] countingArray = null;
	
	private Set<Integer> currTrueIndex = new HashSet<Integer>();
	
	public CountingTracer(String filePath) throws IOException {
		File f = new File(filePath);
//		System.out.println("Reading file: " + f.getAbsolutePath());
		Set<String> allPreds = new HashSet<String>();
		BufferedReader reader = new BufferedReader(new FileReader(f));
	    String line= reader.readLine();
	    while(line != null) {
	      if(!line.trim().equals("")) {
	    	  String[] splits = line.split(COUNT_SEP);
			  if(splits.length != 2) {
				  throw new Error(line);
			  }
			  String pred = splits[0].trim();
			  String postDom = splits[1].trim();
			  if(!this.reversePostDom.containsKey(postDom)) {
				  this.reversePostDom.put(postDom, new HashSet<String>());
			  }
			  this.reversePostDom.get(postDom).add(pred);
			  allPreds.add(pred);
		  }
	      line= reader.readLine();
	    }
	    //all predicate
	    int size = allPreds.size();
	    this.countingArray = new int[size];
	    int index = 0;
	    for(String pred : allPreds) {
	    	this.countingArray[index] = 0;
	    	this.indexMaps.put(pred.trim(), index);
	    	index++;
	    }
	    //set the flag array
	    this.flagArray = new boolean[size];
	    for(int i = 0; i < flagArray.length; i++) {
	    	this.flagArray[i] = false;
	    }
	}
	
	public void tracePredicateFrequency(String str) {
		//do nothing
	}
	
	public void tracePredicateResult(String str) {
		//turn the flag to be true
//		System.out.println("->" + str);
//		System.out.println("-->" + this.indexMaps.size());
		str = str.trim();
		if(this.indexMaps.containsKey(str)) {
		    int index = this.indexMaps.get(str);
		    if(index >= 0) {
			    this.flagArray[index] = true;
			    this.currTrueIndex.add(index);
//			    System.out.println("==> index: " + index + ", size: " + this.currTrueIndex.size());
		    }
		}
	}
	
	public void traceNormalInstruction(String str) {
		//first update the count maps
		str = str.trim();
		for(Integer index : this.currTrueIndex) {
			this.countingArray[index] = this.countingArray[index] + 1;
		}
		//then checks whether need to turn off some counting
		if(this.reversePostDom.containsKey(str)) {
			for(String predStr : this.reversePostDom.get(str)) {
				int index = this.indexMaps.get(predStr);
//				if(index >= 0) {
				    this.currTrueIndex.remove(index);
				    this.flagArray[index] = false;
//				}
			}
		}
	}
	
	public void writeCountsToFile(String fileName) throws IOException {
		Collection<String> results = new LinkedList<String>();
		for(String pred : this.indexMaps.keySet()) {
			results.add(pred + COUNT_SEP + this.countingArray[this.indexMaps.get(pred)]);
		}
		System.out.println("Writing counting to: " + new File(fileName).toString());
		EfficientTracer.directWriteToFile(results, new File(fileName));
	}
}