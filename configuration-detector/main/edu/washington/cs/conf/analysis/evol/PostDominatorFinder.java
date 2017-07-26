package edu.washington.cs.conf.analysis.evol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAPhiInstruction;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class PostDominatorFinder {
	//FIXME the result can be cached
	public static boolean post_dom_debug = false;
	public static ISSABasicBlock computeImmediatePostDominator(CGNode node, ISSABasicBlock block) {
		List<ISSABasicBlock> listBBs = WALAUtils.getAllBasicBlocks(node);
		Map<ISSABasicBlock, Set<ISSABasicBlock>> postDom = new HashMap<ISSABasicBlock, Set<ISSABasicBlock>>();
		Utils.checkTrue(listBBs.contains(block));
		
		//the initial set only contain the basic block itself
		for(ISSABasicBlock bb : listBBs) {
			Set<ISSABasicBlock> set = new HashSet<ISSABasicBlock>();
			set.add(bb);
			postDom.put(bb, set);
		}
		
		//iterate until no changes
		boolean changed = true;
		while(changed) {
			changed = false;
			for(ISSABasicBlock bb : listBBs) {
				if(post_dom_debug) {
				    System.out.println("processing: " + bb.getNumber());
				}
				List<ISSABasicBlock> succs = WALAUtils.getSuccBasicBlocks(node, bb);
				//must exclude exit node
				List<ISSABasicBlock> noExitSuccs = new LinkedList<ISSABasicBlock>();
				for(ISSABasicBlock succ : succs) {
					if(!succ.isExitBlock()) {
						noExitSuccs.add(succ);
					}
				}
				succs.clear();
				succs.addAll(noExitSuccs);
				if(post_dom_debug) {
				    System.out.println("  succ id, exluding exit: "
						+ WALAUtils.getAllBasicBlockIDList(noExitSuccs));
				}
				
				//compute the intersection of all BBs of succ nodes
				Set<ISSABasicBlock> intersectBBs = new HashSet<ISSABasicBlock>();
				if(!succs.isEmpty()) {
					
					for(ISSABasicBlock succ : succs) {
						Set<ISSABasicBlock> domSet = postDom.get(succ);
						Set<ISSABasicBlock> otherDom = new HashSet<ISSABasicBlock>();
						otherDom.addAll(domSet);
						otherDom.remove(succ); //remove itself
						if(domSet.contains(bb) && postDom.get(bb).containsAll(otherDom)) {
							if(post_dom_debug) {
							    System.out.println("   skip loop back edge: " + succ.getNumber()
									+ ", whose domset: " + WALAUtils.getAllBasicBlockIDList(domSet)
									+ ", the current update node: " + bb.getNumber()
									+ ", whose domset: " + WALAUtils.getAllBasicBlockIDList(postDom.get(bb)));
							}
							continue; //manage the loop
						}
						//if the intersectBBs is empty
						if(intersectBBs.isEmpty()) {
							intersectBBs.addAll(domSet);
						} else {
							//do intersection
							intersectBBs = Utils.intersect(intersectBBs, domSet);
						}
						if(post_dom_debug) {
						    System.out.println("   intersect: " + WALAUtils.getAllBasicBlockIDList(intersectBBs));
						}
					}
				}
				//check the bb size and update the dominate tree
				int originalSize = postDom.get(bb).size();
				postDom.get(bb).addAll(intersectBBs);
				if(originalSize != postDom.get(bb).size()) {
					changed = true;
					if(post_dom_debug) {
					    System.out.println("++ update: " + bb.getNumber() + ": "
							+ WALAUtils.getAllBasicBlockIDList(postDom.get(bb)));
					}
				}
			}
		}
		
		//get the post-dominate set
		Set<ISSABasicBlock> postDomSet = postDom.get(block);
		Utils.checkTrue(postDomSet.size() > 0);
		
		Set<ISSABasicBlock> excludedDomSet = new LinkedHashSet<ISSABasicBlock>();
		
		//need to exclude itself and the exit block
		for(ISSABasicBlock bb : postDomSet) {
			if(!bb.isExitBlock() && bb != block) {
				excludedDomSet.add(bb);
			}
		}
		
		//do a BFS from block, and to check which basicblock seems first
		ISSABasicBlock immediatePost = null;
		//the first traversed basic block is the immediate one
		List<ISSABasicBlock> queue = new LinkedList<ISSABasicBlock>();
		Set<ISSABasicBlock> visited = new HashSet<ISSABasicBlock>();
		queue.addAll(WALAUtils.getSuccBasicBlocks(node, block));
		while(!queue.isEmpty()) {
			ISSABasicBlock currBB = queue.remove(0);
			if(visited.contains(currBB)) {
				continue;
			}
			if(currBB != block && excludedDomSet.contains(currBB)) {
				immediatePost = currBB;
				break;
			}
			visited.add(currBB);
			for(ISSABasicBlock nextBB : WALAUtils.getSuccBasicBlocks(node, currBB)) {
				queue.add(nextBB);
			}
		}
		
		//if no immediate post basic block is available,
		//use the exit block
		if(immediatePost != null) {
			return immediatePost;
		} else {
			return node.getIR().getExitBlock();
		}
	}
	
	/**
	 * We need to skip the phi and other instruction that is not in the node
	 * Note that the return instruction can be null, since it may represent the
	 * end of a method.
	 * */
	public static SSAInstruction getImmediatePostDominatorInstruction(CGNode node, SSAInstruction ssa) {
		ISSABasicBlock bb = node.getIR().getBasicBlockForInstruction(ssa);
		if(bb == null) {
			WALAUtils.printCFG(node);
			Utils.fail("cannot be null, ssa is: " + ssa);
		}
		return getImmediatePostDominatorInstruction(node, bb);
	}
	
	public static SSAInstruction getImmediatePostDominatorInstruction(CGNode node, ISSABasicBlock block) {
		ISSABasicBlock postBlock = computeImmediatePostDominator(node, block);
		
		if(postBlock.isExitBlock()) {
			return null;
		}
		
		int size = WALAUtils.getBasicBlockSize(postBlock);
		//error checking
		if(size == 0) {
			//for such weird cases, WALA produces some empty basic blocks
			List<ISSABasicBlock> succList = WALAUtils.getSuccBasicBlocks(node, postBlock);
			if(succList.size() == 1) {
				ISSABasicBlock succBB = succList.get(0);
				SSAInstruction succSSA = getFirstNonPhiSSA(succBB, node);
				if(succSSA != null) {
					return succSSA;
				} else {
					System.out.println("The index is -1?");
				}
			} else {
			    WALAUtils.printCFG(node);
			    System.out.println("block id: " + block.getNumber());
			    System.out.println("post block id: " + postBlock.getNumber() + ", is exit? : " + postBlock.isExitBlock());
			    Utils.fail("");
			}
 		}
//		Utils.checkTrue(size > 0, "bb size: " + size + ", id: " + postBlock.getNumber() +
//				", in method: " + node.getMethod().getSignature());
		
		Iterator<SSAInstruction> iter = postBlock.iterator();
		while(iter.hasNext()) {
			SSAInstruction ssa = iter.next();
			if(ssa instanceof SSAPhiInstruction) {
				continue;
			}
			int index = WALAUtils.getInstructionIndex(node, ssa);
			if(index != -1) {
				return ssa;
			}
		}
		
		//here, all instructions in the postBlock are phis, we need to look at the succ one
		List<ISSABasicBlock> succList = WALAUtils.getSuccBasicBlocks(node, postBlock);
		if(succList.size() == 1) {
			ISSABasicBlock succBB = succList.get(0);
			SSAInstruction succSSA = getFirstNonPhiSSA(succBB, node);
			if(succSSA != null) {
				return succSSA;
			} else {
				System.out.println("The index is -1?");
			}
		}
		
		WALAUtils.printCFG(node);
		System.out.println("block id: " + block.getNumber());
		System.out.println("post block id: " + postBlock.getNumber() + ", is exit? : " + postBlock.isExitBlock());
		WALAUtils.printBasicBlock(postBlock);
		System.out.println("Number of succ nodes: " + succList.size());
		throw new Error("should not be here.");
	}
	
	private static SSAInstruction getFirstNonPhiSSA(ISSABasicBlock bb, CGNode node) {
		Iterator<SSAInstruction> iter = bb.iterator();
		while(iter.hasNext()) {
			SSAInstruction ssa = iter.next();
			if(ssa instanceof SSAPhiInstruction) {
				continue;
			}
			int index = WALAUtils.getInstructionIndex(node, ssa);
			if(index != -1) {
				return ssa;
			}
		}
		return null;
	}
	
	//it includes the start and end basic blocks
	@Deprecated
	public static Set<ISSABasicBlock> findAllBasicBlocksBetween(CGNode node, ISSABasicBlock block) {
		ISSABasicBlock startBlock = block;
		ISSABasicBlock endBlock = computeImmediatePostDominator(node, startBlock);
		
		Set<ISSABasicBlock> bbSet = new LinkedHashSet<ISSABasicBlock>();
		Set<ISSABasicBlock> visited = new HashSet<ISSABasicBlock>();
		List<ISSABasicBlock> queue = new LinkedList<ISSABasicBlock>();
		queue.addAll(WALAUtils.getSuccBasicBlocks(node, startBlock));
		while(!queue.isEmpty()) {
			ISSABasicBlock top = queue.remove(0);
			if(top == startBlock || top == endBlock || top.isExitBlock()) {
				continue;
			}
			if(visited.contains(top)) {
				continue;
			}
			visited.add(top);
			bbSet.add(top);
			queue.addAll(WALAUtils.getSuccBasicBlocks(node, top));
		}
		
		//incldue the start and end block
		bbSet.add(startBlock);
		bbSet.add(endBlock);
		
		return bbSet;
	}
}
