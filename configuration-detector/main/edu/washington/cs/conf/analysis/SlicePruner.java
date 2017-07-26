package edu.washington.cs.conf.analysis;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.util.Utils;

/**
 * This class prunes some likely statements in a slice which are likely to
 * be false positives.
 * */
public class SlicePruner {
	
	public static int maxsize = 50;
	
	public static int minoverlap = 5;
	
	public static List<ConfPropOutput> pruneSliceByOverlap(Collection<ConfPropOutput> outputs) {
		List<ConfPropOutput> retList = new LinkedList<ConfPropOutput>();
		
		Map<String, Set<ConfPropOutput>> stmtConfMap = createStmtConfigMap(outputs);
		
		for(ConfPropOutput output : outputs) {
			if(output.statements.size() < maxsize) {
			    retList.add(output);
			} else {
				Set<IRStatement> prunedStmts = new LinkedHashSet<IRStatement>();
				for(IRStatement s : output.statements) {
					String uniqueSig = s.getUniqueSignature();
					if(stmtConfMap.get(uniqueSig).size() < minoverlap) {
						prunedStmts.add(s);
					}
				}
				//re-create it
				ConfPropOutput newOutput = new ConfPropOutput(output.getConfEntity(), prunedStmts);
				if(newOutput.statements.isEmpty()) {
					System.err.println("It removes all, need to roll back.");
					retList.add(output); //should not remove all
				} else {
				    retList.add(newOutput);
				}
			}
		}
		
		Utils.checkTrue(retList.size() == outputs.size());
		
		return retList;
	}
	
	public static Map<String, Set<ConfPropOutput>> createStmtConfigMap(Collection<ConfPropOutput> outputs) {
		Map<String, Set<ConfPropOutput>> stmtConfMap = new LinkedHashMap<String, Set<ConfPropOutput>>();
		for(ConfPropOutput output : outputs) {
			Collection<IRStatement> stmts = output.statements;
			for(IRStatement stmt : stmts) {
				String sig = stmt.getUniqueSignature();
				if(!stmtConfMap.containsKey(sig)) {
					stmtConfMap.put(sig, new LinkedHashSet<ConfPropOutput>());
				}
				stmtConfMap.get(sig).add(output);
			}
		}
		return stmtConfMap;
	}
	
}