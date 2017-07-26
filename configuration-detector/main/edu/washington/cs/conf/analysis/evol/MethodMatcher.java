package edu.washington.cs.conf.analysis.evol;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import plume.Pair;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class MethodMatcher {
	
	public static int default_la = 5;
	public static float default_threshold = 0.6f;
	
	public final CallGraph cgOld;
	public final CallGraph cgNew;
	public final AnalysisScope scope;
	public final AnalysisCache cache;
	
	public MethodMatcher(CallGraph cgOld, CallGraph cgNew, AnalysisScope scope, AnalysisCache cache) {
		Utils.checkNotNull(cgOld);
		Utils.checkNotNull(cgNew);
		Utils.checkNotNull(scope);
		Utils.checkNotNull(cache);
		this.cgOld = cgOld;
		this.cgNew = cgNew;
		this.scope = scope;
		this.cache = cache;
	}
	
	public CGNode getMethodInOldCG(String methodSig) {
		return WALAUtils.lookupMatchedCGNode(cgOld, methodSig);
	}
	
	public CGNode getMethodInNewCG(String methodSig) {
		return WALAUtils.lookupMatchedCGNode(cgNew, methodSig);
	}
	
	public List<CGNode> getMethodInNewCG(CGNode oldNode) {
		List<CGNode> retNodes = new LinkedList<CGNode>();
		Utils.checkNotNull(oldNode);
		//first check the full qualified names
		CGNode newNode = WALAUtils.lookupMatchedCGNode(this.cgNew, oldNode.getMethod().getSignature());
		if(newNode != null) {
			System.out.println("Use the exact matching rule.");
			retNodes.add(newNode);
			return retNodes;
		}
		
		//check the class name + method name
		String oldNodeClassMethodName = oldNode.getMethod().getDeclaringClass().getName().getClassName().toString()
		     + oldNode.getMethod().getDescriptor().toString();
		for(CGNode node : this.cgNew) {
			String classMethodName = node.getMethod().getDeclaringClass().getName().getClassName().toString()
			    + node.getMethod().getDescriptor().toString();
			if(oldNodeClassMethodName.equals(classMethodName)) {
				System.out.println("Use the class + method matching");
				retNodes.add(node);
				return retNodes;
			}
		}
		
		//use invoking methods
		
		
		//use clone detection
		
		//use jdiff matching algorithm?
		
		//use shingle
		return retNodes;
	}
	
	public List<CGNode> getFuzzMatchedNodes(CGNode oldNode, float threshold, int lh) {
		List<CGNode> matchedNodes = new LinkedList<CGNode>();
		int debugCount = 0;
		for(CGNode newNode : this.cgNew) {
			//skip methods that are in the same scope
			if(!this.scope.isInScope(newNode.getMethod().getDeclaringClass())) {
				continue;
			}
			//skip methods with empty bodies
			if(newNode.getIR() == null || newNode.getIR().getControlFlowGraph() == null) {
				continue;
			}
			//if it is not unmatched
			if(!this.cache.isUnmatchedInNewVersion(newNode.getMethod().getSignature())) {
				continue;
			}
			debugCount++;
			if(this.fuzzMatchNodes(oldNode, newNode, threshold, lh)) {
				matchedNodes.add(newNode);
			}
		}
		System.out.println("Matching: " + oldNode);
		System.out.println("   count: " + debugCount + ", matched: " + matchedNodes.size());
		return matchedNodes;
	}
	
	public boolean fuzzMatchNodes(CGNode oldNode, CGNode newNode, float threshold, int lh) {
		Utils.checkTrue(threshold >= 0 && threshold <= 1);
		Set<Pair<ISSABasicBlock, ISSABasicBlock>> matched = this.createMatchedBlocks(oldNode, newNode, threshold, lh);
		int totalSize = WALAUtils.getAllBasicBlocks(oldNode).size();
		float ratio = (float)matched.size() / (float)totalSize;
		return ratio >= threshold;
	}
	
	static boolean debug = true;
	public Set<Pair<ISSABasicBlock, ISSABasicBlock>> createMatchedBlocks(CGNode oldNode, CGNode newNode, float threshold, int lh) {
		System.out.println("Matching node: " + oldNode + ", with: " + newNode);
		
		Utils.checkTrue(lh >= 0);
		SSACFG oldcfg = oldNode.getIR().getControlFlowGraph();
		SSACFG newcfg = newNode.getIR().getControlFlowGraph();
		
		ISSABasicBlock oldBB = oldcfg.entry();
		ISSABasicBlock newBB = newcfg.entry();
		
		Set<Pair<ISSABasicBlock, ISSABasicBlock>> matched =
			new LinkedHashSet<Pair<ISSABasicBlock, ISSABasicBlock>>();
		
		//keep the nodes that has already been matched
		Set<ISSABasicBlock> oldMatchedBBs = new LinkedHashSet<ISSABasicBlock>();
		Set<ISSABasicBlock> newMatchedBBs = new LinkedHashSet<ISSABasicBlock>();
		
		//keep the node pair that has not been matched yet
		Set<Pair<ISSABasicBlock, ISSABasicBlock>> unMatchedBBs = new LinkedHashSet<Pair<ISSABasicBlock, ISSABasicBlock>>();
		
		Stack<Pair<ISSABasicBlock, ISSABasicBlock>> stack
		    = new Stack<Pair<ISSABasicBlock, ISSABasicBlock>>();
		stack.push(new Pair<ISSABasicBlock, ISSABasicBlock>(oldBB, newBB));
		
		while(!stack.isEmpty()) {
			Pair<ISSABasicBlock, ISSABasicBlock> pair = stack.pop();
			if(debug) {
			    System.out.println("Popup pair: " + pair.a.getNumber() + ", " + pair.b.getNumber());
			    System.out.println("  current old set: " + WALAUtils.getAllBasicBlockIDList(oldMatchedBBs));
			    System.out.println("  current new set: " + WALAUtils.getAllBasicBlockIDList(newMatchedBBs));
			}
			if(oldMatchedBBs.contains(pair.a)) {
				if(debug) {
				    System.out.println("Skip already matched in old version: " + pair.a.getNumber());
				}
				continue;
			}
			if(newMatchedBBs.contains(pair.b)) {
				if(debug) {
				    System.out.println("Skip already matched in new version: " + pair.b.getNumber());
				}
				continue;
			}
			if(unMatchedBBs.contains(Pair.of(pair.a, pair.b))) {
				if(debug) {
				    System.out.println("Skip already unmatched pairs: (" + pair.a.getNumber() + ", "
				    		+ pair.b.getNumber() + ")");
				}
				continue;
			}
			Pair<ISSABasicBlock, ISSABasicBlock> matchedPair = null;
			if(comp(pair.a, pair.b, threshold)) {
				if(debug) {
				    System.out.println("Matched: " + pair.a.getNumber() + ", " + pair.b.getNumber());
				}
				matchedPair = pair;
			} else {
				//the pair is not matched
				unMatchedBBs.add(pair);
				
				//initialize two lists
				Set<ISSABasicBlock> set1 = new LinkedHashSet<ISSABasicBlock>();
				set1.add(pair.a);
				Set<ISSABasicBlock> set2 = new LinkedHashSet<ISSABasicBlock>();
				set2.add(pair.b);
				for(int i = 0; i < lh; i++) {
					//expand both set1 and set2 for i steps
					Set<ISSABasicBlock> succSet1 = new LinkedHashSet<ISSABasicBlock>();
					for(ISSABasicBlock bb : set1) {
						Iterator<ISSABasicBlock> iter = oldcfg.getSuccNodes(bb);
						while(iter.hasNext()) {
							succSet1.add(iter.next());
						}
					}
					//add to the set
					set1.addAll(succSet1);
					
					Set<ISSABasicBlock> succSet2 = new LinkedHashSet<ISSABasicBlock>();
					for(ISSABasicBlock bb : set2) {
						Iterator<ISSABasicBlock> iter = newcfg.getSuccNodes(bb);
						while(iter.hasNext()) {
							succSet2.add(iter.next());
						}
					}
					set2.addAll(succSet2);
					
					//then start to match
					for(ISSABasicBlock bb2 : succSet2) {
						if(comp(pair.a, bb2, threshold)) {
							matchedPair = new Pair<ISSABasicBlock, ISSABasicBlock>(pair.a, bb2);
							break;
						} else {
							unMatchedBBs.add(Pair.of(pair.a, bb2));
						}
					}
					
					if(matchedPair == null) {
						for(ISSABasicBlock bb1 : succSet1) {
							if(comp(bb1, pair.b, threshold)) {
								matchedPair = new Pair<ISSABasicBlock, ISSABasicBlock>(bb1, pair.b);
								break;
							} else {
								unMatchedBBs.add(Pair.of(bb1, pair.b));
							}
						}
					}
					
					if(matchedPair != null) {
						break;
					} //other wise increase the look ahead length
				}
			}

			//what is the next node, if no matches, the next nodes
			//should be the descends of pair.a, and pair.b
			//otherwise, use the matched pair
			ISSABasicBlock nextBB1 = pair.a;
			ISSABasicBlock nextBB2 = pair.b;
			
			//record the matched pair
			if(matchedPair != null) {
				if(!oldMatchedBBs.contains(matchedPair.a) && !newMatchedBBs.contains(matchedPair.b)) {
					if(debug) {
				        System.out.println("ADD to matched set: " + matchedPair.a.getNumber() + ", " + matchedPair.b.getNumber());
					}
				    matched.add(matchedPair); //add to the matched list
				    oldMatchedBBs.add(matchedPair.a);
				    newMatchedBBs.add(matchedPair.b);
				    //change the next basic blocks
				    nextBB1 = matchedPair.a;
				    nextBB2 = matchedPair.b;
				} else {
					if(debug) {
					    System.out.println("Skip already matched non-null pairs: " + matchedPair.a.getNumber() + ", "
							+ matchedPair.b.getNumber());
					}
				}
			}
			
			//add the following nodes of the matched node to the stack
			List<ISSABasicBlock> succBB1 = WALAUtils.getSuccBasicBlocks(oldNode, nextBB1);
			//remove the exit basic blocks
			//XXX fix me, remove the catch block
			if(succBB1.size() > 2) {
				List<ISSABasicBlock> filteredBB = new LinkedList<ISSABasicBlock>();
			    for(ISSABasicBlock bb : succBB1) {
				    if(bb.isExitBlock() || bb.isCatchBlock()) {
				    	continue;
				    }
				    filteredBB.add(bb);
			    }
			    succBB1.clear();
			    succBB1.addAll(filteredBB);
			}
			
			List<ISSABasicBlock> succBB2 = WALAUtils.getSuccBasicBlocks(newNode, nextBB2);
			if(succBB2.size() > 2) {
				List<ISSABasicBlock> filteredBB = new LinkedList<ISSABasicBlock>();
			    for(ISSABasicBlock bb : succBB2) {
				    if(bb.isExitBlock() || bb.isCatchBlock()) {
				    	continue;
				    }
				    filteredBB.add(bb);
			    }
			    succBB2.clear();
			    succBB2.addAll(filteredBB);
			}
			
			if(debug) {
			    System.out.println("Number of succ blocks in old: "
					+ pair.a.getNumber() + " : " + succBB1.size()
					+ ", are: " + WALAUtils.getAllBasicBlockIDList(succBB1));
			    System.out.println("Number of succ blocks in new: "
					+ pair.b.getNumber() + " : " + succBB2.size()
					+ ", are: " + WALAUtils.getAllBasicBlockIDList(succBB2));
			}
			
			//Hhow to follow the matched edge is not clear in (at least to me) in the JDiff paper.
			//I exhaustively enumerate all possibilities in matching edges
			int num = 0; //for debugging only
			if(succBB1.isEmpty() || succBB2.isEmpty()) {
				continue;
			} else {
				for(ISSABasicBlock bb1 : succBB1) {
					for(ISSABasicBlock bb2 : succBB2) {
						Pair<ISSABasicBlock, ISSABasicBlock> newPair = Pair.of(bb1, bb2);
						stack.push(newPair);
						num++;
					}
				}
			}
			
			if(debug) {
				System.out.println("Adding: " + num + " to the stack.");
			    System.out.println("Stack size: " + stack.size());
			}
		}
		
		if(debug) {
		    System.out.println("all matched pairs: ");
		    for(Pair<ISSABasicBlock, ISSABasicBlock> pair : matched) {
		        System.out.println(pair.a.getNumber() + ",  " +  pair.b.getNumber());
		    }
		}
		
		//reclaim memory
		oldMatchedBBs.clear();
		newMatchedBBs.clear();
		unMatchedBBs.clear();
		
		return matched;
	}
	
	public static boolean comp(ISSABasicBlock c1, ISSABasicBlock c2, float threshold) {
		if(debug) {
		    System.out.println("starting compare: " + c1.getNumber() + ", with: " + c2.getNumber());
		}
		if((c1.isEntryBlock() && c2.isEntryBlock()) || (c1.isExitBlock() && c2.isExitBlock())) {
			return true;
		}
		if( (c1.isEntryBlock() != c2.isEntryBlock()) || (c1.isExitBlock() != c2.isExitBlock())) {
			return false;
		}
		
		if(debug) {
		    System.out.println("  -- c1: " + c1.getNumber() + " : " + WALAUtils.getAllIRsString(c1));
		    System.out.println("  -- c2: " + c2.getNumber() + " : " + WALAUtils.getAllIRsString(c2));
		}
		
		//check each instructions
		List<SSAInstruction> ssalist1 = WALAUtils.getAllIRs(c1);
		int matchedCount = 0;
		for(SSAInstruction ssa : ssalist1) {
			if(CodeAnalysisUtils.approxContainInstruction(c2, ssa)) {
				matchedCount++;
			}
		}
		
		float percentage = 0.0f;
		if(ssalist1.isEmpty()) {
			percentage = 1.0f;
		} else {
			percentage = (float)matchedCount/(float)ssalist1.size();
		}
		
		if(debug) {
		    System.out.println("matching: " + c1.getNumber() + ", with : " + c2.getNumber()
				+ ",  matchedCount: " + matchedCount
				+ ",  total ssa in c1: " + ssalist1.size());
		}
		
		return percentage >= threshold;
	}
	
	public CGNode getExactNameMatchedNodeInNewCG(CGNode node) {
		return WALAUtils.lookupMatchedCGNode(cgNew, node.getMethod().getSignature());
	}
}
