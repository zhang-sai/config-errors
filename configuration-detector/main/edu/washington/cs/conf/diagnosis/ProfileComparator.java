package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Log;
import edu.washington.cs.conf.util.Utils;

@Deprecated
public class ProfileComparator {

	public static boolean DEBUG = true;
	
	public static int DEBUG_LIMIT = 120;
	
	public final Collection<PredicateProfile> goodProfiles;
	public final Collection<PredicateProfile> badProfiles;
	
	private int limit = Integer.MAX_VALUE;
	
	public ProfileComparator(Collection<PredicateProfile> goodPs, Collection<PredicateProfile> badPs) {
		this.goodProfiles = goodPs;
		this.badProfiles = badPs;
	}
	
	public void setReturnLimit(int limit) {
		this.limit = limit;
	}
	
	//those configurations disappear or appears may be dominated by others
	//should be assigned a value dynamically
	public List<String> findProfilesByTfIdf() {
		//how important a context is for a configuration
		// tf-counts =# context of the conf (the less the better)
		// idf-counts = number of total config that has this context (the less the better)
		Map<String, Float>  goodRatios = this.computeGoodRatios();
		Map<String, Float>  badRatios = this.computeBadRatios();
		//note it is only for those configurations appearing in both runs
		Map<String, Float> diffs = this.computeDiffRatios(goodRatios, badRatios);
		//remove all all keys that 
		this.removeNoDifferences(diffs);
        diffs = Utils.sortByValue(diffs, false);
        
        //doing the tf-idf counting
        Map<String, Integer> tfCounts = this.computeTfCounts(diffs);
        Map<String, Integer> idfCounts = this.computeIdfCounts(diffs);
        
        //recompute the diff to consider tf-idf
        Map<String, Float> refinedDiffs = this.recomputeDiffValues(diffs, tfCounts, idfCounts);
        refinedDiffs = Utils.sortByValue(refinedDiffs, false);
        
        if(DEBUG) {
        	this.showDebugInfo(diffs, refinedDiffs, tfCounts, idfCounts);
        }
        
        Map<String, Float> result = this.sumSameKeyValues(refinedDiffs);
        result = Utils.sortByValue(result, false);
        
        if(DEBUG) {
        	System.out.println(" ---- Ranked profile and values --- ");
        	for(String key : result.keySet()) {
            	System.out.println(key + "  -- " + result.get(key));
            }
        }
        
        //return the top k
        int numberToReturn = result.size() > this.limit ? this.limit : result.size();
        List<String> profiles = new LinkedList<String>();
        for(String v : result.keySet()) {
        	if(numberToReturn -- > 0) {
        		profiles.add(v);
        		Log.logln("Add profile: " + v + " with value: " + result.get(v));
        	} else {
        		break;
        	}
        }
        
        Log.logln("Num of returned profiles: " + profiles.size());
        
		return profiles;
	}
	
	private Map<String, Float> computeGoodRatios() {
		Map<String, Float> goodRatios = new LinkedHashMap<String, Float>();
		for(PredicateProfile p : goodProfiles) {
			String key = p.getUniqueKey();
			if(p.getEnteringCount() == 0) {
				continue;
			}
			goodRatios.put(key, (float)p.getEvaluatingCount()/(float)p.getEnteringCount());
		}
		return goodRatios;
	}
	
	private Map<String, Float> computeBadRatios() {
		Map<String, Float> badRatios = new LinkedHashMap<String, Float>();
		for(PredicateProfile p : badProfiles) {
        	String key = p.getUniqueKey();
			if(p.getEnteringCount() == 0) {
				continue;
			}
			badRatios.put(key, (float)p.getEvaluatingCount()/(float)p.getEnteringCount());
		}
		return badRatios;
	}
	
	private Map<String, Float> computeDiffRatios(Map<String, Float>  goodRatios, Map<String, Float>  badRatios) {
		Map<String, Float> diffs = new LinkedHashMap<String, Float>();
        for(String key : badRatios.keySet()) {
        	if(goodRatios.containsKey(key)) {
        		float goodr = goodRatios.get(key);
        		float badr = badRatios.get(key);
        		float diff = goodr > badr ? goodr/badr : badr/goodr;
        		diffs.put(key, diff);
        	}
        }
        return diffs;
	}
	
	public List<PredicateProfile> findDeviatedProfiles() {
		//need to design a better structure here FIXME
		Map<String, Float>  goodRatios = this.computeGoodRatios();
		Map<String, Float>  badRatios = this.computeBadRatios();
        
		Map<String, Float> diffs = this.computeDiffRatios(goodRatios, badRatios);
        diffs = Utils.sortByValue(diffs, false);
        
        int count = 0;
        for(String key : diffs.keySet()) {
        	System.out.println(key + "  -   " + diffs.get(key));
        	count++;
        	if(count > 120) {
        		break;
        	}
        }
		
		return null;
	}
	
	private void removeNoDifferences(Map<String, Float> diffs) {
		Collection<String> removes = new LinkedHashSet<String>();
		for(String str : diffs.keySet()) {
			if(diffs.get(str) == 1.0f) {
				removes.add(str);
			}
		}
		for(String str : removes) {
			diffs.remove(str);
			Log.logln("Config: " + str + " has no change, removed.");
		}
	}
	
	private Map<String, Integer>  computeTfCounts(Map<String, Float> diffs) {
		Map<String, Integer> tfCounts = new LinkedHashMap<String, Integer>();
        for(String key : diffs.keySet()) {
        	String confId = PredicateProfile.getConfig(key);
        	if(!tfCounts.containsKey(confId)) {
        		tfCounts.put(confId, 1);
        	} else {
        		tfCounts.put(confId, tfCounts.get(confId) + 1);
        	}
        }
        return tfCounts;
	}
	
	private Map<String, Integer> computeIdfCounts(Map<String, Float> diffs) {
		Map<String, Integer> idfCounts = new LinkedHashMap<String, Integer>();
        for(String key : diffs.keySet()) {
        	String context = PredicateProfile.getContext(key);
        	if(!idfCounts.containsKey(context)) {
        		idfCounts.put(context, 1);
        	} else {
        		idfCounts.put(context, idfCounts.get(context) + 1);
        	}
        }
        return idfCounts;
	}
	
	private Map<String, Float> recomputeDiffValues(Map<String, Float> diffs, Map<String, Integer> tfCounts,
			Map<String, Integer> idfCounts) {
		Map<String, Float> recomputedDiffs = new LinkedHashMap<String, Float>();
        for(String key : diffs.keySet()) {
        	String[] items = PredicateProfile.parseKey(key);
        	String confId = items[0];
        	String context = items[1];
        	recomputedDiffs.put(key, diffs.get(key)*(1/(float)tfCounts.get(confId))*(1/(float)idfCounts.get(context)));
        }
        return recomputedDiffs;
	}
	
	private Map<String, Float> sumSameKeyValues(Map<String, Float> keyValueMap) {
		Map<String, Float> sum = new LinkedHashMap<String, Float>();
        for(String key : keyValueMap.keySet()) {
        	String confId = PredicateProfile.getConfig(key);
        	if(!sum.containsKey(confId)) {
        		sum.put(confId, 0.0f);
        	}
        	sum.put(confId, sum.get(confId) + keyValueMap.get(key));
        }
        return sum;
	}
	
	private void showDebugInfo(Map<String, Float> diffs, Map<String, Float> refinedDiffs,
			Map<String, Integer> tfCounts, Map<String, Integer> idfCounts) {
		int count = 0;
        for(String key : refinedDiffs.keySet()) {
        	String confId = PredicateProfile.getConfig(key);
        	String context = PredicateProfile.getContext(key);
        	System.out.println(key
        			+ Globals.lineSep
        			+ "\trefined value: " + refinedDiffs.get(key)
        			+ Globals.lineSep
        			+ "\ttf: " + tfCounts.get(confId) + ",  idf: " + idfCounts.get(context)
        			+ Globals.lineSep
        			+ "\toriginal diff value: " + diffs.get(key));
        	if(count++ > DEBUG_LIMIT) {
        		break;
        	}
        }
	}
}