package edu.washington.cs.conf.analysis.evol.experimental;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.analysis.evol.ExecutionTraceReader;
import edu.washington.cs.conf.util.Utils;

/**
 * A list of predicate exec info. Not really necessary
 * */
@Deprecated
public class PredicateExecPool {

	public final List<PredicateExecInfo> predicates;
	
	public PredicateExecPool(String fileName) {
		Collection<PredicateExecInfo> infos = ExecutionTraceReader.createPredicateExecInfoList(fileName);
		this.predicates = new LinkedList<PredicateExecInfo>();
		this.predicates.addAll(infos);
	}
	
	public PredicateExecPool(Collection<PredicateExecInfo> predicates) {
		Utils.checkNotNull(predicates);
		this.predicates = new LinkedList<PredicateExecInfo>();
		this.predicates.addAll(predicates);
	}
	
	public PredicateExecInfo getMostFrequentlyExecuted() {
		PredicateExecInfo p = null;
		for(PredicateExecInfo info : this.predicates) {
			if(p == null) {
				p = info;
			} else {
				if(info.evalFreqCount > p.evalFreqCount) {
					p = info;
				}
			}
		}
		return p;
	}
	
//	public PredicateExecInfo getMostEvaluated() {
//		
//	}
}
