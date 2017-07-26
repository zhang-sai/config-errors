package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecInfo;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class ErrorDiagnoser {
	
	public final ExecutionTrace oldTrace;
	public final ExecutionTrace newTrace;
	
	public final CodeAnalyzer oldCoder;
	public final CodeAnalyzer newCoder;
	
	private final ConfEntityRepository oldRep;
	private final ConfEntityRepository newRep;
	
	public final TracesWrapper traceWrapper;
	
	private float pruneThreshold = 0.1f;
	private boolean debug = true;
	
	//just for comparison experiments
	private boolean onlyUsePredicate = false;
	
	public final InstrumentSchema oldSliceOutput;
	public final InstrumentSchema newSliceOutput;
	
//	private Collection<ConfPropOutput> oldSliceOutputs = Collections.EMPTY_SET;
//	private Collection<ConfPropOutput> newSliceOutputs = Collections.EMPTY_SET;
	
//	public final IterativeSlicer oldSlicer;
//	public final IterativeSlicer newSlicer;
//	private final TraceComparator comparator;
	
	public void setOnlyUsePredicate(boolean onlyUse) {
		this.onlyUsePredicate = onlyUse;
	}
	
	public ErrorDiagnoser(ConfEntityRepository oldConfs, ConfEntityRepository newConfs,
			CodeAnalyzer oldCoder, CodeAnalyzer newCoder, TracesWrapper wrapper) {
		Utils.checkNotNull(oldConfs);
		Utils.checkNotNull(newConfs);
		Utils.checkNotNull(oldCoder);
		Utils.checkNotNull(oldCoder.getCallGraph());
		Utils.checkNotNull(newCoder);
		Utils.checkNotNull(newCoder.getCallGraph());
		Utils.checkNotNull(wrapper);
		this.oldRep = oldConfs;
		this.newRep = newConfs;
		this.traceWrapper = wrapper;
		this.oldTrace = this.createOldTrace(); 
			//new ExecutionTrace(this.traceWrapper.oldHistoryFile, this.traceWrapper.oldSigFile, this.traceWrapper.oldPredicateFile);
		this.newTrace = this.createNewTrace(); 
			//new ExecutionTrace(this.traceWrapper.newHistoryFile, this.traceWrapper.newSigFile, this.traceWrapper.newPredicateFile);
		this.oldCoder = oldCoder;
		this.newCoder = newCoder;
		//read the slice rsult back
		this.oldSliceOutput = ConfOutputSerializer.deserializeAsSchema(this.traceWrapper.oldSliceCache);
		this.newSliceOutput = ConfOutputSerializer.deserializeAsSchema(this.traceWrapper.newSliceCache);
	}
	
	/**a ranked list of suspicious configuration options
	//TODO the main entry
	//1. take the predicate execution delta into account
	//   predicate p1, execute 10 times, in which 3 times evaluate to true
	//   compute a value from this and multiple with the cost
	//2. for a predicate only executed in the old version, inform users that
	//   it may not take effect any more.
	//3. consider nested branches?
	 * */
	public List<ConfEntity> diagnoseRootCauses() {
		
		//get predicates executed in the old version
		Collection<PredicateExecInfo>  oldPredExecs = createOldPredExecInfo();
	    Collection<PredicateExecInfo> newPredExecs = createNewPredExecInfo();
	    
	    //the matched predicates
	    Set<PredicateBehaviorAcrossVersions> matchedPreds
	        = SimpleChecks.getMatchedPredicateExecutions(oldPredExecs, newPredExecs, this.oldCoder, this.newCoder);
	    //rank by the behavior changes
	    matchedPreds = SimpleChecks.rankByBehaviorChanges(matchedPreds);
	    
	    //store the likelihood of each configuration option in a map
	    Map<ConfEntity, Float> oldConfMap = new LinkedHashMap<ConfEntity, Float>();
	    Map<ConfEntity, Float> newConfMap = new LinkedHashMap<ConfEntity, Float>();
	    
		for(PredicateBehaviorAcrossVersions predBehavior : matchedPreds) {
			float behaviorDiff = predBehavior.getDifferenceDegree();
			if(behaviorDiff < pruneThreshold) {
				continue;
			}
			int instrNumOnOldVersion = 0;
			int instrNumOnNewVersion = 0;
			if(predBehavior.isExecutedOnOldVersion()) {
				instrNumOnOldVersion = this.getExecutedInstructionNumInOldVersion(predBehavior.createOldPredicateExecInfo());
//				Set<InstructionExecInfo> set = oldTrace.getExecutedInstructionsInsidePredicate(oldCoder, predBehavior.createOldPredicateExecInfo());
//				instrNumOnOldVersion = set.size();
			}
			if(predBehavior.isExecutedOnNewVersion()) {
				instrNumOnNewVersion = this.getExecutedInstructionNumInNewVersion(predBehavior.createNewPredicateExecInfo());
//				Set<InstructionExecInfo> set = newTrace.getExecutedInstructionsInsidePredicate(newCoder, predBehavior.createNewPredicateExecInfo());
//				instrNumOnNewVersion = set.size();
			}
			
			int instrDelta = Math.abs(instrNumOnNewVersion - instrNumOnOldVersion);
			if(this.onlyUsePredicate) {
				instrDelta = 1;
			}
			float behaviorDelta = instrDelta*behaviorDiff;
			
			Set<ConfEntity> oldConfs = this.getAffectingOptionsInOldVersion(predBehavior.oldMethodSig, predBehavior.oldIndex);
			Set<ConfEntity> newConfs = this.getAffectingOptionsInNewVersion(predBehavior.newMethodSig, predBehavior.newIndex);

			//update the likelihood
			for(ConfEntity oldConf : oldConfs) {
				if(!oldConfMap.containsKey(oldConf)) {
					oldConfMap.put(oldConf, behaviorDelta);
				} else {
					oldConfMap.put(oldConf, oldConfMap.get(oldConf) + behaviorDelta);
				}
			}
			for(ConfEntity newConf : newConfs) {
				if(!newConfMap.containsKey(newConf)) {
					newConfMap.put(newConf, behaviorDelta);
				} else {
					newConfMap.put(newConf, newConfMap.get(newConf) + behaviorDelta);
				}
			}
			
			
			if(debug) {
			    System.out.println(predBehavior);
			    System.out.println("      diff: " + behaviorDiff);
			    System.out.println("      executed ssa on old: " + instrNumOnOldVersion);
			    System.out.println("      executed ssa on new: " + instrNumOnNewVersion);
			    System.out.println("      behavior delta: " + behaviorDelta);
			    System.out.println();
			}
		}
		
		System.out.println("new conf maps: " + newConfMap);
		
		int threshold = 10000000;
		for(ConfEntity e : oldConfMap.keySet()) {
			if(oldConfMap.get(e) == null) {
				continue;
			}
			if(oldConfMap.get(e) > threshold) {
				oldConfMap.put(e, 0.0f);
			} else {
			    oldConfMap.put(e, oldConfMap.get(e)
			    		+ (this.newRep.lookupConfEntity(e.getFullConfName()) == null ? 1.0f : 0.0f));
			}
		}
		for(ConfEntity e : newConfMap.keySet()) {
			if(newConfMap.get(e) == null) {
				continue;
			}
			if(newConfMap.get(e) > threshold) {
				newConfMap.put(e, 0.0f);
			} else {
				newConfMap.put(e, newConfMap.get(e) 
						+ (this.oldRep.lookupConfEntity(e.getFullConfName()) == null ? 1.0f : 0.0f));
			}
		}
		
//		System.out.println("new conf maps: " + newConfMap);
		
		oldConfMap = Utils.sortByValue(oldConfMap, false);
		newConfMap = Utils.sortByValue(newConfMap, false);
		
//		System.out.println("new conf maps after ranking: " + newConfMap);
		
		System.out.println(" ========= Dump the results =========");
		System.out.println(" ========= old version =========");
		for(ConfEntity e : oldConfMap.keySet()) {
			System.out.println(e.getConfName() + " => " + oldConfMap.get(e));
		}
		System.out.println(" ========= new version =========");
		for(ConfEntity e : newConfMap.keySet()) {
			System.out.println(e.getConfName() + " => " + newConfMap.get(e));
		}
		
		List<ConfEntity> list = new LinkedList<ConfEntity>();
		return list;
	}
	
	public void setFuzzMatching(boolean match) {
		this.oldSliceOutput.setFuzzMatching(match);
		this.newSliceOutput.setFuzzMatching(match);
	}
	
	private ExecutionTrace createOldTrace() {
		ExecutionTrace oldTrace = null;
		if(this.traceWrapper.useCountingFile()) {
			oldTrace = new ExecutionTrace(this.traceWrapper.oldCountingFile);
		} else {
		    oldTrace = new ExecutionTrace(this.traceWrapper.oldHistoryFile, this.traceWrapper.oldSigFile, this.traceWrapper.oldPredicateFile);
		}
		return oldTrace;
	}
	
	private ExecutionTrace createNewTrace() {
		ExecutionTrace newTrace = null;
		if(this.traceWrapper.useCountingFile()) {
			newTrace = new ExecutionTrace(this.traceWrapper.newCountingFile);
		} else {
			newTrace = new ExecutionTrace(this.traceWrapper.newHistoryFile, this.traceWrapper.newSigFile, this.traceWrapper.newPredicateFile);
		}
		return newTrace;
	}
	
	private Collection<PredicateExecInfo> createOldPredExecInfo() {
		Collection<PredicateExecInfo>  oldPredExecs
            = ExecutionTraceReader.createPredicateExecInfoList(this.traceWrapper.oldPredicateFile,
 		      this.traceWrapper.oldSigFile);
		return oldPredExecs;
	}
	
	private Collection<PredicateExecInfo> createNewPredExecInfo() {
		Collection<PredicateExecInfo> newPredExecs
            = ExecutionTraceReader.createPredicateExecInfoList(this.traceWrapper.newPredicateFile,
    		  this.traceWrapper.newSigFile);
		return newPredExecs;
	}
	
	private int getExecutedInstructionNumInOldVersion(PredicateExecInfo pred) {
		String methodSig = pred.getMethodSig();
		int index = pred.getIndex();
		if(oldTrace.useCountFile()) {
			return oldTrace.getExecutedInstructions(methodSig, index);
		}
		Set<InstructionExecInfo> set = oldTrace.getExecutedInstructionsInsidePredicate(oldCoder, pred);
		return set.size();
	}
	
	private int getExecutedInstructionNumInNewVersion(PredicateExecInfo pred) {
		String methodSig = pred.getMethodSig();
		int index = pred.getIndex();
		if(newTrace.useCountFile()) {
			return newTrace.getExecutedInstructions(methodSig, index);
		}
		Set<InstructionExecInfo> set = newTrace.getExecutedInstructionsInsidePredicate(newCoder, pred);
		return set.size();
	}
	
	private Set<ConfEntity> getAffectingOptionsInOldVersion(String methodSig, int instructionIndex) {
		Set<ConfEntity> set = new LinkedHashSet<ConfEntity>();
		Collection<ConfEntity> coll = this.oldSliceOutput.getAffectingConfOptions(methodSig, instructionIndex);
		set.addAll(coll);
		return set;
	}
	
	private Set<ConfEntity> getAffectingOptionsInNewVersion(String methodSig, int instructionIndex) {
		Set<ConfEntity> set = new LinkedHashSet<ConfEntity>();
		Collection<ConfEntity> coll = this.newSliceOutput.getAffectingConfOptions(methodSig, instructionIndex);
		set.addAll(coll);
		return set;
	}
}

/**
 * 
 * algorithm sketch:
 * 
 * two execution traces, t_old, t_new
 * 
 * for each predicate in t_old, and t_new, classify it as "only executed in t_old",
 * "only executed in t_new", or "both but differently".
 * 
 * for predicates only executed in one version, merging nested predicates
 * 
 * for each different predicate, compute its cost by counting the number of instructions
 * (what about the cases of nested predicates, e.g., recursive case)
 * - nest case: if nested, just count the first level
 * - diff: instructions executed (|true branch - false branch|)
 *   remove the nested instructions. such as:
 *   
 *   if(x) {  //only count i1, and i2
 *      i1
 *      i2
 *      if(y) {
 *      }
 *   }
 *   
 *   //count both side?
 *   
 * - new: num  (true - false)
 * 
 *   //count both side
 * 
 * - old: num   (true - false)
 * 
 *   //count both side
 * 
 * for each predicate not executed in both versions, find the variables inside,
 * and repeatedly do the slicing
 * - just account for variables inside the basic block  (IGNORED) since both are executed
 * - new: OK natural
 * 
 *   (not both side, only for the executed part)
 * 
 * - old: OK natural
 * 
 * what about "changing the predicate"?? e.g.,
 * 
 * changing  if(pred)   to  if(!pred)
 * 
 * */


/**
 * if there is a lot of redundant computation, which causes the differences,
 * but it is unlikely
 * 
 * */