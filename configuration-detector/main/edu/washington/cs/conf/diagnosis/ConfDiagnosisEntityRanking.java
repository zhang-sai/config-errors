package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.diagnosis.ConfDiagnosisEntity.ScoreType;
import edu.washington.cs.conf.util.Utils;

public class ConfDiagnosisEntityRanking {
    
    /**
     * A utility method for sorting config by its score
     * */
    public static List<ConfDiagnosisEntity> rankByCriteria(Collection<ConfDiagnosisEntity> results, ScoreType scoreType,
    		boolean increase) {
    	//check the existence of the scoreType
    	for(ConfDiagnosisEntity result : results) {
    		if(!result.hasScore(scoreType)) {
    			result.computeScore(scoreType);
    			Utils.checkTrue(result.hasScore(scoreType));
    		}
    	}
    	//do the ranking here
    	Map<ConfDiagnosisEntity, Float> scoreMap = new LinkedHashMap<ConfDiagnosisEntity, Float>();
    	for(ConfDiagnosisEntity result : results) {
    		scoreMap.put(result, result.getScore(scoreType));
    	}
    	List<ConfDiagnosisEntity> rankedList = Utils.sortByValueAndReturnKeys(scoreMap, increase);
    	
    	//for experiment only, default is false
    	if(MainAnalyzer.amortizeNoise) {
    		Map<Float, List<ConfDiagnosisEntity>> revMap = new LinkedHashMap<Float, List<ConfDiagnosisEntity>>();
    		for(ConfDiagnosisEntity e : scoreMap.keySet()) {
    			Float f = scoreMap.get(e);
    			if(!revMap.containsKey(f)) {
    				revMap.put(f, new LinkedList<ConfDiagnosisEntity>());
    			}
    			revMap.get(f).add(e);
    		}
    		Map<ConfDiagnosisEntity, Float> reweightMap = new LinkedHashMap<ConfDiagnosisEntity, Float>();
    		for(Float score : revMap.keySet()) {
    			Float amortize = score;
    			if(revMap.get(score).size() >= MainAnalyzer.thresholdcount) {
    			    amortize = score / revMap.get(score).size();
    			}
    			for(ConfDiagnosisEntity e : revMap.get(score)) {
    				reweightMap.put(e, amortize);
    			}
    		}
    		Utils.checkTrue(reweightMap.size() == scoreMap.size());
    		//resort
    		rankedList = Utils.sortByValueAndReturnKeys(reweightMap, increase);
    	}
    	
    	return rankedList;
    }
}
