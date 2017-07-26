package edu.washington.cs.conf.analysis.evol;

import java.util.Comparator;

import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecInfo;
import edu.washington.cs.conf.util.Utils;

public class PredicateMetrics {

	public static float computeTrueRatio(PredicateExecInfo info) {
		int freqCount = info.evalFreqCount;
		int trueCount = info.evalResultCount;
		Utils.checkTrue(freqCount != 0);
		return (float)trueCount / (float)freqCount;
	}
	
	public static float computeTrueRatioDiff(PredicateExecInfo info1, PredicateExecInfo info2) {
		return Math.abs(computeTrueRatio(info1) - computeTrueRatio(info2));
	}
	
	public static float computeBehavior(PredicateExecInfo info) {
		float trueRatio = computeTrueRatio(info);
		if(trueRatio == 0.0f) {
			trueRatio = 1 / (float)info.evalFreqCount;
		}
		float importance = 2 / ((1/trueRatio) + (1/info.evalFreqCount));
		return importance;
	}
	
	public static float computeBehaviorDiff(PredicateExecInfo info1, PredicateExecInfo info2) {
		return Math.abs(computeBehavior(info1) - computeBehavior(info2));
	}
	
	public static Comparator<PredicateExecInfo> getFreqComparator() {
		Comparator<PredicateExecInfo> comparator = new Comparator<PredicateExecInfo>() {
			@Override
			public int compare(PredicateExecInfo o1, PredicateExecInfo o2) {
				return o1.evalFreqCount > o2.evalFreqCount ? 1 : 0;
			}
			
		};
		return comparator;
	}
}