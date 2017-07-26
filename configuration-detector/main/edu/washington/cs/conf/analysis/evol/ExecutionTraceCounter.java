package edu.washington.cs.conf.analysis.evol;

import java.util.Map;

import edu.washington.cs.conf.instrument.evol.EfficientTracer;
import edu.washington.cs.conf.util.Utils;

public class ExecutionTraceCounter {

	private final Map<String, Integer> map;
	
	public ExecutionTraceCounter(String countingFile) {
		this.map = ExecutionTraceReader.parseCountingFile(countingFile);
	}
	
	public boolean hasPredicate(String methodSig, int index) {
		String key = this.constructPredicateSig(methodSig, index);
		return map.containsKey(key);
	}
	
	public Integer getAbsCount(String methodSig, int index) {
		return this.hasPredicate(methodSig, index) ? Math.abs(this.getCount(methodSig, index)) : 0;
	}
	
	public Integer getCount(String methodSig, int index) {
		String key = this.constructPredicateSig(methodSig, index);
		Utils.checkTrue(map.containsKey(key));
		return map.get(key);
	}
	
	private String constructPredicateSig(String methodSig, int index) {
		return methodSig + EfficientTracer.SEP  + index;
	}
}