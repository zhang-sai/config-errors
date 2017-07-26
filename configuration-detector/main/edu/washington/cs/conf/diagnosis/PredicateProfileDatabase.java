package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.util.Utils;

/**
 * Store a list of predicate profile tuples
 * */
public class PredicateProfileDatabase {
	
	public final String databaseName;
	
	private final List<PredicateProfileTuple> tuples
	    = new LinkedList<PredicateProfileTuple>();
	
	public PredicateProfileDatabase(String dbName, Collection<PredicateProfileTuple> coll) {
		this.databaseName = dbName;
		this.tuples.addAll(coll);
	}
	
	public PredicateProfileDatabase(Collection<PredicateProfileTuple> coll) {
		this("profile-db", coll);
	}
	
	public void addTuple(PredicateProfileTuple tuple) {
		this.tuples.add(tuple);
	}
	
	public List<PredicateProfileTuple> getAllTuples() {
		return this.tuples;
	}
	
	/**
	 * This returns a list of similar tuples
	 * */
	public List<PredicateProfileTuple> findSimilarTuples(PredicateProfileTuple target, DistanceType t,
			float threashold) {
		List<PredicateProfileTuple> retList = new LinkedList<PredicateProfileTuple>();
		
		for(PredicateProfileTuple tuple : tuples) {
			float distance = ProfileDistanceCalculator.computeDistance(tuple, target, t);
			System.err.println("distance: " + distance + ", for tuple: " + tuple.name);
			if(distance <= threashold) {
				retList.add(tuple);
			}
		}
		
		return retList;
	}
	
	/**
	 * return only 1 most similar tuple
	 * */
	public PredicateProfileTuple findTheMostSimilarTuple(PredicateProfileTuple target, DistanceType t) {
		Utils.checkTrue(tuples.size() > 0);
		PredicateProfileTuple ret = null;
		Float minDist = Float.MAX_VALUE;
		for(PredicateProfileTuple tuple : tuples) {
			float distance = ProfileDistanceCalculator.computeDistance(tuple, target, t);
			System.err.println("distance: " + distance);
			if(distance < minDist) {
				ret = tuple;
				minDist = distance;
			}
		}
		Utils.checkNotNull(ret);
		System.err.println("The closest distance: " + minDist);
		return ret;
	}
	
	/**
	 * return only 1 least similar tuple
	 * */
	public PredicateProfileTuple findTheLeastSimilarTuple(PredicateProfileTuple target, DistanceType t) {
		Utils.checkTrue(tuples.size() > 0);
		PredicateProfileTuple ret = null;
		Float maxDist = Float.MIN_VALUE;
		for(PredicateProfileTuple tuple : tuples) {
			float distance = ProfileDistanceCalculator.computeDistance(tuple, target, t);
			System.err.println("distance: " + distance);
			if(distance > maxDist) {
				ret = tuple;
				maxDist = distance;
			}
		}
		Utils.checkNotNull(ret);
		System.err.println("The farthest distance: " + maxDist);
		return ret;
	}
}