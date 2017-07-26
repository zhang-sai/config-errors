package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.experiments.ChordExpUtils;
import edu.washington.cs.conf.experiments.jchord.TestSliceJChordConfigOptions;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

public class RankingTieResolver {
	
	public static List<ConfDiagnosisOutput> resolveTiesInRanking(String stackTraceFile, Collection<ConfDiagnosisOutput> outputs,
    		Collection<ConfPropOutput> slices) {
		String[] stackTraces = Files.readWholeNoExp(stackTraceFile).toArray(new String[0]);
    	Map<ConfDiagnosisOutput, Integer> stackCovMap = CrashingErrorDiagnoser.computeMatchedStacktraceNum(slices, outputs, stackTraces);
    	
    	System.out.println("----------- number of affected methods in the stack trace ----------");
    	for(ConfDiagnosisOutput o : stackCovMap.keySet()) {
    		System.out.println(o.getConfEntity().getFullConfName());
    		System.out.println("     " + stackCovMap.get(o));
    	}
    	System.out.println("----------- end of affected methods in the stack trace ----------");
    	
    	//create the initial ranking bucket
    	//remove the impossible ones
    	Collection<ConfDiagnosisOutput> filteredOutput = new LinkedList<ConfDiagnosisOutput>();
    	for(ConfDiagnosisOutput o : outputs) {
    		if(stackCovMap.get(o) == 0) {
    			continue;
    		}
    		filteredOutput.add(o);
    	}
    	outputs.clear();
    	outputs.addAll(filteredOutput);
    	//end
    	
    	Map<Float, List<ConfDiagnosisOutput>> initRankBuckets = createInitRankingBuckets(outputs);
    	
    	//sort by the initial ranking
    	Map<Float, List<ConfDiagnosisOutput>> sortedInitBuckets = Utils.sortByKey(initRankBuckets, false);
    	
    	List<ConfDiagnosisOutput> rankedOutputs = new LinkedList<ConfDiagnosisOutput>();
    	for(Float initScore : sortedInitBuckets.keySet()) {
    		List<ConfDiagnosisOutput> list = sortedInitBuckets.get(initScore);
    		if(list.size() == 1) {
    			System.out.println("Add: " + list);
    			rankedOutputs.addAll(list);
    		} else {
    			//resolve ties by stack trace coverage
    			 Map<Float, List<ConfDiagnosisOutput>> rankedMapByStackTrace = resolveTiesByStackCoverage(list, stackCovMap);
    			 for(Float f : rankedMapByStackTrace.keySet()) {
    				 List<ConfDiagnosisOutput> l = rankedMapByStackTrace.get(f);
    				 System.out.println("resolve by stack: " + l);
    				 //resolve again
    				 Map<Float, List<ConfDiagnosisOutput>> rankedMapByDistance = resolveTiesBySliceDistance(l, slices, stackTraceFile);
    				 for(List<ConfDiagnosisOutput> listByDistance : rankedMapByDistance.values()) {
    					 System.out.println("resolve by distance: " + listByDistance);
    					 //resolve by name similarity
    					 Map<Float, List<ConfDiagnosisOutput>> rankedByNameSimilarity = resolveTiesByNameSimilarity(listByDistance, stackTraceFile);
    					 for(List<ConfDiagnosisOutput> listByName : rankedByNameSimilarity.values()) {
    						 System.out.println("Add: " + listByName);
    						 rankedOutputs.addAll(listByName);
    					 }
    				 }
    			 }
    		}
    	}
    	
    	return rankedOutputs;
		
	}
	
	static Map<Float, List<ConfDiagnosisOutput>> createInitRankingBuckets(Collection<ConfDiagnosisOutput> outputs) {
		Map<Float, List<ConfDiagnosisOutput>> bucket = new LinkedHashMap<Float, List<ConfDiagnosisOutput>>();
		
		for(ConfDiagnosisOutput output : outputs) {
			Float finalScore = output.getFinalScore();
			if(!bucket.containsKey(finalScore)) {
				bucket.put(finalScore, new LinkedList<ConfDiagnosisOutput>());
			}
			bucket.get(finalScore).add(output);
		}
		
		return bucket;
	}
	
	static Map<Float, List<ConfDiagnosisOutput>> resolveTiesByStackCoverage(Collection<ConfDiagnosisOutput> ties,
			Map<ConfDiagnosisOutput, Integer> stackCoverage) {
		 System.out.println("resolve ties by stack coverage: ");
		 Map<Float, List<ConfDiagnosisOutput>> scoreMap = new LinkedHashMap<Float, List<ConfDiagnosisOutput>>();
		 
		 for(ConfDiagnosisOutput o : ties) {
			 int stackCovNum = stackCoverage.get(o);
			 float score = (float)stackCovNum;
			 if(!scoreMap.containsKey(score)) {
				 scoreMap.put(score, new LinkedList<ConfDiagnosisOutput>());
			 }
			 scoreMap.get(score).add(o);
		 }
		 
		 Map<Float, List<ConfDiagnosisOutput>> rankedScoreMap = Utils.sortByKey(scoreMap, false);
		 
		 return rankedScoreMap;
	}
	
	static Map<Float, List<ConfDiagnosisOutput>> resolveTiesBySliceDistance(Collection<ConfDiagnosisOutput> ties,
			Collection<ConfPropOutput> slices, String stackTraceFile) {

    	Map<Float, List<ConfDiagnosisOutput>> scoreMap = new LinkedHashMap<Float, List<ConfDiagnosisOutput>>();
    	
    	String[] stackTraces = Files.readWholeNoExp(stackTraceFile).toArray(new String[0]);
    	for(ConfDiagnosisOutput output : ties) {
    		Map<String, Integer> distance = CrashingErrorDiagnoser.computeStackTraceDistance(slices, output, stackTraces, false);
    		for(String m : distance.keySet()) {
				Integer d = distance.get(m);
				if(d != Integer.MAX_VALUE) {
					Float score = (float)d;
					if(!scoreMap.containsKey(score)) {
						scoreMap.put(score, new LinkedList<ConfDiagnosisOutput>());
					}
					scoreMap.get(score).add(output);
					System.out.println(" >> distance: " + output.getConfEntity().getFullConfName() + ", : " + d);
					break; //there is a break here
				}
			}
    	}
    	
    	Map<Float, List<ConfDiagnosisOutput>> rankedScoreMap = Utils.sortByKey(scoreMap, true); //note it is true here
    	return rankedScoreMap;
	}
	
	static Map<Float, List<ConfDiagnosisOutput>> resolveTiesByNameSimilarity(Collection<ConfDiagnosisOutput> ties,
			String stackTraceFile) {
		Map<Float, List<ConfDiagnosisOutput>> scoreMap = new LinkedHashMap<Float, List<ConfDiagnosisOutput>>();
		
		String[] stackTraces = Files.readWholeNoExp(stackTraceFile).toArray(new String[0]);
		String flatTrace = Utils.concatenate(stackTraces, " ");
		for(ConfDiagnosisOutput o : ties) {
			String confName = o.getConfEntity().getConfName();
			int index = flatTrace.indexOf(confName);
			float score = (float)index;
			if(index == -1) {
				score = Float.MAX_VALUE;
			}
			if(!scoreMap.containsKey(score)) {
				scoreMap.put(score, new LinkedList<ConfDiagnosisOutput>());
			}
			scoreMap.get(score).add(o);
		}
		
		Map<Float, List<ConfDiagnosisOutput>> rankedScoreMap = Utils.sortByKey(scoreMap, true); //the close the better
		return rankedScoreMap;
	}
}