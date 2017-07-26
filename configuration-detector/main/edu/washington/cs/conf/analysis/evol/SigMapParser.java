package edu.washington.cs.conf.analysis.evol;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.washington.cs.conf.instrument.evol.PredicateInstrumenter;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

public class SigMapParser {
	
	//use cache, since the sig map can be large, from file name --> the sig map
	private static Map<String, Map<Integer, String>> sigmaps
	    = new LinkedHashMap<String, Map<Integer, String>>();
	
	private static Map<String, Map<String, Integer>> revSigMaps
	    = new LinkedHashMap<String, Map<String, Integer>>();
	
	//Parse the sig mapping files
	//from the integer to the instruction signature
	public static Map<Integer, String> parseSigNumMapping(String fileName) {
		String absolutePath = new File(fileName).getAbsolutePath();
		if(sigmaps.containsKey(absolutePath)) {
			return sigmaps.get(absolutePath);
		} else {
			Map<Integer, String> sigmap = createSigNumMapping(fileName);
			sigmaps.put(absolutePath, sigmap);
			return sigmap;
		}
	}
	
	public static Map<String, Integer> revParseSigNumMapping(String fileName) {
		String absolutePath = new File(fileName).getAbsolutePath();
		if(revSigMaps.containsKey(absolutePath)) {
			return revSigMaps.get(absolutePath);
		} else {
			Map<String, Integer> revSigMap = createReverseSigNumMapping(fileName);
			revSigMaps.put(absolutePath, revSigMap);
			return revSigMap;
		}
	}
	
	private static Map<Integer, String> createSigNumMapping(String fileName) {
		Utils.checkNotNull(fileName);
		Map<Integer, String> sigMap = new LinkedHashMap<Integer, String>();
		for(String line : Files.readWholeNoExp(fileName)) {
			String[] splits = line.split(PredicateInstrumenter.sigSep);
			Utils.checkTrue(splits.length == 2);
			Integer num = Integer.parseInt(splits[1]);
			String sig = splits[0];
			Utils.checkTrue(!sigMap.containsKey(num));
			sigMap.put(num, sig);
		}
		return sigMap;
	}
	
	//much duplicated than above
	private static Map<String, Integer> createReverseSigNumMapping(String fileName) {
		Utils.checkNotNull(fileName);
		Map<String, Integer> revSigMap = new LinkedHashMap<String, Integer>();
		for(String line : Files.readWholeNoExp(fileName)) {
			String[] splits = line.split(PredicateInstrumenter.sigSep);
			Utils.checkTrue(splits.length == 2);
			Integer num = Integer.parseInt(splits[1]);
			String sig = splits[0];
			Utils.checkTrue(!revSigMap.containsKey(sig));
//			sigMap.put(num, sig);
			revSigMap.put(sig, num);
		}
		return revSigMap;
	}
	
}