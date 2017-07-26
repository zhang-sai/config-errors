package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecInfo;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

//just for experiment purpose
public class SimpleChecks {
	
	public static boolean unique_matching = false;
	
	public static Set<String> uniqueMethods = null;
	
	public static Set<String> getUnmatchedOldMethods(
			Collection<PredicateExecInfo> oldSet,
			Collection<PredicateExecInfo> newSet) {
		Set<String> oldMethods = new HashSet<String>();
		for(PredicateExecInfo execInfo : oldSet) {
			oldMethods.add(execInfo.context);
		}
		Set<String> newMethods = new HashSet<String>();
		for(PredicateExecInfo execInfo : newSet) {
			newMethods.add(execInfo.context);
		}
		return Utils.minus(oldMethods, newMethods);
	}

	public static Set<String> getUnmatchedNewMethods(
			Collection<PredicateExecInfo> oldSet,
			Collection<PredicateExecInfo> newSet) {
		Set<String> oldMethods = new HashSet<String>();
		for(PredicateExecInfo execInfo : oldSet) {
			oldMethods.add(execInfo.context);
		}
		Set<String> newMethods = new HashSet<String>();
		for(PredicateExecInfo execInfo : newSet) {
			newMethods.add(execInfo.context);
		}
		return Utils.minus(newMethods, oldMethods);
	}
	
	public static Set<String> getUnmatchedOldPredicates(
			Collection<PredicateExecInfo> oldSet,
			Collection<PredicateExecInfo> newSet
	    ) {
		Set<String> oldPredicates = new HashSet<String>();
		for(PredicateExecInfo execInfo : oldSet) {
			oldPredicates.add(execInfo.getPredicateSig());
		}
		Set<String> newPredicates = new HashSet<String>();
		for(PredicateExecInfo execInfo : newSet) {
			newPredicates.add(execInfo.getPredicateSig());
		}
		return Utils.minus(oldPredicates, newPredicates);
	}
	
	public static Set<String> getUnmatchedNewPredicates(
			Collection<PredicateExecInfo> oldSet,
			Collection<PredicateExecInfo> newSet
	    ) {
		Set<String> oldPredicates = new HashSet<String>();
		for(PredicateExecInfo execInfo : oldSet) {
			oldPredicates.add(execInfo.getPredicateSig());
		}
		Set<String> newPredicates = new HashSet<String>();
		for(PredicateExecInfo execInfo : newSet) {
			newPredicates.add(execInfo.getPredicateSig());
		}
		return Utils.minus(newPredicates, oldPredicates);
	}
	
	public static Set<PredicateBehaviorAcrossVersions> rankByBehaviorChanges(
			Collection<PredicateBehaviorAcrossVersions> coll) {
		Map<PredicateBehaviorAcrossVersions, Float> map = new LinkedHashMap<PredicateBehaviorAcrossVersions, Float>();
		for(PredicateBehaviorAcrossVersions p : coll) {
			map.put(p, p.getDifferenceDegree());
		}
		map = Utils.sortByValue(map, false);
		return map.keySet();
	}
	
	public static boolean useStrictMatching = true;
	
	//get behaviorally changed predicates
	public static Set<PredicateBehaviorAcrossVersions> getMatchedPredicateExecutions(
			Collection<PredicateExecInfo> oldSet,
			Collection<PredicateExecInfo> newSet,
			CodeAnalyzer oldCoder, CodeAnalyzer newCoder
			) {
		
		PredicateMatcher matcher = null;
		SimplePredicateMatcher simpleMatcher = null;
		if(oldCoder != null && newCoder != null) {
		    matcher = new PredicateMatcher(oldCoder.getCallGraph(), newCoder.getCallGraph());
		    simpleMatcher = new SimplePredicateMatcher(oldCoder.getCallGraph(), newCoder.getCallGraph());
		}
		
		Map<String, PredicateExecInfo> oldPredMap = TraceComparator.buildPredicateSigMap(oldSet);
		Map<String, PredicateExecInfo> newPredMap = TraceComparator.buildPredicateSigMap(newSet);
		
		Set<String> oldMethodSet = TraceComparator.getExecutedMethods(oldSet);
		Set<String> newMethodSet = TraceComparator.getExecutedMethods(newSet);
		
		Set<PredicateBehaviorAcrossVersions> predSet = new HashSet<PredicateBehaviorAcrossVersions>();
		for(String oldPredSig : oldPredMap.keySet()) {
			PredicateExecInfo oldPred = oldPredMap.get(oldPredSig);
			
			//get the corresponding newNode
			SSAInstruction strictMatchedSSA = null;
			if(simpleMatcher != null) {
				strictMatchedSSA = simpleMatcher.getMatchedSSA(oldPred.getMethodSig(), oldPred.getIndex());
			}
			
			if(newPredMap.containsKey(oldPredSig)
					&& (strictMatchedSSA != null || !useStrictMatching)
					/* TODO FIXME this may violate existing matches	*/) {
				PredicateExecInfo newExecInfo = newPredMap.get(oldPredSig);
				//create a predicate execution object
				PredicateBehaviorAcrossVersions execObj
				    = new PredicateBehaviorAcrossVersions(oldPred.getMethodSig(), oldPred.getIndex(), newExecInfo.getMethodSig(), newExecInfo.getIndex());
				execObj.setOldExecutionInfo(oldPred.evalFreqCount, oldPred.evalResultCount);
				execObj.setNewExecutionInfo(newExecInfo.evalFreqCount, newExecInfo.evalResultCount);
				predSet.add(execObj);
			} else if(strictMatchedSSA != null) {
				//the same instruction exists, but not executed
				PredicateBehaviorAcrossVersions execObj
			        = new PredicateBehaviorAcrossVersions(oldPred.getMethodSig(), oldPred.getIndex(), null, -1);
			    execObj.setOldExecutionInfo(oldPred.evalFreqCount, oldPred.evalResultCount);
			    execObj.setNewExecutionInfo(0, 0);
			    predSet.add(execObj);
			} else {
				//if the new pred map does not contain old predicate signature
				//look at the matched predicate
				String oldMethodSig = oldPred.getMethodSig();
				
				if(newMethodSet.contains(oldMethodSig)) {
					if(newCoder != null) {
						CGNode oldNode = WALAUtils.lookupMatchedCGNode(oldCoder.getCallGraph(), oldPred.getMethodSig());
						if(oldNode == null) { //likely not reached
							System.err.println(oldPred.getMethodSig() + ", " + oldNode + ",  index: " + oldPred.getIndex());
							continue;
						}
						SSAInstruction oldSSA = WALAUtils.getInstruction(oldNode, oldPred.getIndex());
						CGNode newNode = WALAUtils.lookupMatchedCGNode(newCoder.getCallGraph(), oldPred.getMethodSig());
						if(newNode == null) {
							System.err.println("The static call graph is not complete...");
							continue;
						}
						//matching instruction by instruction using a JDiff-like algorithm
						List<SSAInstruction> ssalist = matcher.matchPredicateInNewCG(oldNode, newNode, oldSSA);

						if(ssalist.size() == 1) {
							SSAInstruction matchedSSA = ssalist.get(0);
							int matchedIndex = WALAUtils.getInstructionIndex(newNode, matchedSSA);
							
							String matchedPredSig = PredicateExecInfo.createPredicateSig(oldMethodSig, matchedIndex);
							PredicateExecInfo newPredExec = newPredMap.get(matchedPredSig);
							
							if(newPredExec != null) {
								PredicateBehaviorAcrossVersions execObj
								    = new PredicateBehaviorAcrossVersions(oldPred.getMethodSig(), oldPred.getIndex(),
								    		newPredExec.getMethodSig(), newPredExec.getIndex());
								execObj.setOldExecutionInfo(oldPred.evalFreqCount, oldPred.evalResultCount);
								execObj.setNewExecutionInfo(newPredExec.evalFreqCount, newPredExec.evalResultCount);
								predSet.add(execObj); //XXX the matched part
								System.err.println("new matched: \n" + execObj);
							} else {
								System.out.println("  ==> not executed: " + matchedPredSig);
							}
							
						} else if(ssalist.isEmpty() && unique_matching && uniqueMethods != null) {
							ssalist = matcher.matchPredicateInNewCG(oldNode, newNode, oldSSA, uniqueMethods);
							System.out.println("use uniqueness: ");
							System.out.println(ssalist);
							if(ssalist.size() == 1) {
								//XXX duplicate code
								SSAInstruction matchedSSA = ssalist.get(0);
								int matchedIndex = WALAUtils.getInstructionIndex(newNode, matchedSSA);
								
								String matchedPredSig = PredicateExecInfo.createPredicateSig(oldMethodSig, matchedIndex);
								PredicateExecInfo newPredExec = newPredMap.get(matchedPredSig);
								
								if(newPredExec != null) {
									PredicateBehaviorAcrossVersions execObj
									    = new PredicateBehaviorAcrossVersions(oldPred.getMethodSig(), oldPred.getIndex(),
									    		newPredExec.getMethodSig(), newPredExec.getIndex());
									execObj.setOldExecutionInfo(oldPred.evalFreqCount, oldPred.evalResultCount);
									execObj.setNewExecutionInfo(newPredExec.evalFreqCount, newPredExec.evalResultCount);
									predSet.add(execObj); //XXX the matched part
									System.err.println("new matched: \n" + execObj);
								} else {
									System.out.println("  ==> not executed in uniquenes: " + matchedPredSig);
								}
							} else {
								System.out.println("more than 1 match.");
							}
						} else  {
							System.out.println(" --> more than 1 item: " + ssalist);
						}
						
//						if(oldMethodSig.indexOf("chord.program.Program.<init>()") != - 1
//								&& oldPred.getIndex() == 8) {
//							throw new Error("list: " + ssalist);
//						}
						
					}
				}
			}
		}
		
		return predSet;
	}
}
