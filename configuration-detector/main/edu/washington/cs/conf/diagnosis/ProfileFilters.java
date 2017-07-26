package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implements some heuristic to filter unlikely (or likely not useful) profiles.
 * */
public class ProfileFilters {
	
	public static List<ConfDiagnosisEntity> filter(Collection<ConfDiagnosisEntity> entities) {
		List<ConfDiagnosisEntity> filtered = filterSingleOccurance(entities);
		filtered = filterOneOccurance(filtered);
//		filtered = filterSingleOccuranceBothRuns(filtered);
//		filtered = filterSameRatio(filtered);
//		filtered = filterSameCountDelta(filtered);
		filtered = filterLikelySliceImprecision(filtered);
		return filtered;
	}
	
	/**
	 * Each individual filters
	 * just 1 observation either in the good run, or the bad run
	 * */
	public static List<ConfDiagnosisEntity> filterSingleOccurance(Collection<ConfDiagnosisEntity> entities) {
		List<ConfDiagnosisEntity> retList = new LinkedList<ConfDiagnosisEntity>();
		for(ConfDiagnosisEntity entity : entities) {
			if(!entity.isSingleOccurance()) {
				retList.add(entity);
			}
		}
		return retList;
	}
	
	/**
	 * only 1 observaton in both runs
	 * */
	public static List<ConfDiagnosisEntity> filterSingleOccuranceBothRuns(Collection<ConfDiagnosisEntity> entities) {
		List<ConfDiagnosisEntity> retList = new LinkedList<ConfDiagnosisEntity>();
		for(ConfDiagnosisEntity entity : entities) {
			if(!entity.isSingleOccuranceInBothRuns()) {
				retList.add(entity);
			}
		}
		return retList;
	}
	
	//a predicate only occurs in good run or bad run
	public static List<ConfDiagnosisEntity> filterOneOccurance(Collection<ConfDiagnosisEntity> entities) {
		List<ConfDiagnosisEntity> retList = new LinkedList<ConfDiagnosisEntity>();
		for(ConfDiagnosisEntity entity : entities) {
			if(!entity.missedByOneRun()) {
				retList.add(entity);
			}
		}
		return retList;
	}
	
	public static List<ConfDiagnosisEntity> filterSameRatio(Collection<ConfDiagnosisEntity> entities) {
		List<ConfDiagnosisEntity> retList = new LinkedList<ConfDiagnosisEntity>();
		for(ConfDiagnosisEntity entity : entities) {
			if(!entity.hasSameRatio()) {
				retList.add(entity);
			}
		}
		return retList;
	}
	
	//like a timer count
	public static List<ConfDiagnosisEntity> filterSameCountDelta(Collection<ConfDiagnosisEntity> entities) {
		List<ConfDiagnosisEntity> retList = new LinkedList<ConfDiagnosisEntity>();
		for(ConfDiagnosisEntity entity : entities) {
			if(!entity.hasSameCountDelta()) {
				retList.add(entity);
			}
		}
		return retList;
	}
	
	//it is unlikely that a predicate is directly affected by > 3 configuration options
	public static List<ConfDiagnosisEntity> filterLikelySliceImprecision(Collection<ConfDiagnosisEntity> entities) {
		//the map of <context, set<config>>
		Map<String, Set<String>> contextMap = new LinkedHashMap<String, Set<String>>();
		for(ConfDiagnosisEntity entity : entities) {
			//if a context is shared by many configs, that context (i.e., predicate) may be
			//an imprecise output by slicing
			String context = entity.getContext();
			String config = entity.getConfigFullName();
			if(!contextMap.containsKey(context)) {
				contextMap.put(context, new LinkedHashSet<String>());
			}
			contextMap.get(context).add(config);
		}
		//see which entity should be kept
		List<ConfDiagnosisEntity> retList = new LinkedList<ConfDiagnosisEntity>();
		for(ConfDiagnosisEntity entity : entities) {
			String context = entity.getContext();
			if(contextMap.get(context).size() >= MainAnalyzer.thresholdcount) {
				continue;
			} else {
				retList.add(entity);
			}
		}
		
		return retList;
	}

}