package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.util.Utils;

public class StmtExecutedDiffer {

	//find out those stmt executed most in a failed run
	private Map<String, Float> stmtScores
	    = new LinkedHashMap<String, Float>();
	
	private Collection<Collection<StmtExecuted>> goodRuns
	    = new LinkedList<Collection<StmtExecuted>>();
	private Collection<Collection<StmtExecuted>> badRuns
        = new LinkedList<Collection<StmtExecuted>>();
	
	public StmtExecutedDiffer(Collection<Collection<StmtExecuted>> goodRuns,
			Collection<Collection<StmtExecuted>> badRuns) {
		this.goodRuns.addAll(goodRuns);
		this.badRuns.addAll(badRuns);
	}
	
	public Map<String, Float> getRankedStmts() {
		Map<String, Float> retMap = Utils.sortByValue(stmtScores, false); //in decreasing order
		return retMap;
	}
	
	//fill in the stmtScores map
	public void computeStmtScores() {
		Map<String, Integer> goodCounts = new LinkedHashMap<String, Integer>();
		Map<String, Integer> badCounts = new LinkedHashMap<String, Integer>();
		
		for(Collection<StmtExecuted> aGoodRun : goodRuns) {
			Set<String> stmts = new LinkedHashSet<String>();
			for(StmtExecuted s : aGoodRun) {
				stmts.add(s.toString());
			}
			for(String stmt : stmts) {
				if(goodCounts.containsKey(stmt)) {
					goodCounts.put(stmt, goodCounts.get(stmt) + 1);
				} else {
					goodCounts.put(stmt, 1);
				}
			}
		}
        for(Collection<StmtExecuted> aBadRun : badRuns) {
        	Set<String> stmts = new LinkedHashSet<String>();
        	for(StmtExecuted s : aBadRun) {
				stmts.add(s.toString());
			}
        	for(String stmt : stmts) {
        		if(badCounts.containsKey(stmt)) {
        			badCounts.put(stmt, badCounts.get(stmt) + 1);
        		} else {
        			badCounts.put(stmt, 1);
        		}
        	}
		}
        
        //do count
        Set<String> allStmtStrs = new LinkedHashSet<String>();
        allStmtStrs.addAll(goodCounts.keySet());
        allStmtStrs.addAll(badCounts.keySet());
        int totalGoodRun = goodRuns.size();
        int totalBadRun = badRuns.size();
        //compute the score
        for(String stmt : allStmtStrs) {
        	int goodRunNum = goodCounts.containsKey(stmt) ? goodCounts.get(stmt) : 0;
        	int badRunNum = badCounts.containsKey(stmt) ? badCounts.get(stmt) : 0;
        	Utils.checkTrue(goodRunNum != 0 || badRunNum != 0);
        	Float score
        	    = ((float)badRunNum / (float)totalBadRun)
        	      /
        	      (((float)badRunNum / (float)totalBadRun) +  ((float)goodRunNum/(float)totalGoodRun));
        	Utils.checkTrue(!stmtScores.containsKey(stmt));
        	stmtScores.put(stmt, score);
        }
	}
}