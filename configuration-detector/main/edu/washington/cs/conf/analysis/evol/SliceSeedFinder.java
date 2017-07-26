package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.PhiStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAAbstractThrowInstruction;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSAGetInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAPhiInstruction;
import com.ibm.wala.ssa.SSAPutInstruction;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class SliceSeedFinder {

	/**
	 * Given a predicate in a node, find all possible seeds.
	 * */
	public Collection<Statement> createSliceSeedsBySSAs(CGNode node, int predIndex,
			Set<SSAInstruction> executedSSAs) {
		Set<ISSABasicBlock> executedBlocks = new LinkedHashSet<ISSABasicBlock>();
		
		for(SSAInstruction ssa : executedSSAs) {
			ISSABasicBlock bb = node.getIR().getBasicBlockForInstruction(ssa);
			if(bb != null) {
				executedBlocks.add(bb);
			}
		}
		
		return createSliceSeedsByBBs(node, predIndex, executedBlocks);
	}
	
	//for each variable, check whether it escapes the block
	public Collection<Statement> createSliceSeedsByBBs(CGNode node, int predIndex,
			Set<ISSABasicBlock> executedBlocks) {
		SSAInstruction predSSA = node.getIR().getInstructions()[predIndex];
		Utils.checkTrue(CodeAnalysisUtils.isPredicateInstruction(predSSA));
		//get every instructions in the branch
		
		ISSABasicBlock predBlock = node.getIR().getBasicBlockForInstruction(predSSA);
		
		ISSABasicBlock postDomBlock = PostDominatorFinder.computeImmediatePostDominator(node, predBlock);
		
		SSAInstruction postDomInstr = PostDominatorFinder.getImmediatePostDominatorInstruction(node, postDomBlock);
		
		Utils.checkNotNull(postDomInstr, "fix the code, the instruction might be null.");
		
		System.out.println(postDomBlock);
//		WALAUtils.printBasicBlock(postDomBlock);
		System.out.println(postDomInstr);
		System.out.println("Index: " + WALAUtils.getInstructionIndex(node, postDomInstr));
		
		//only care about basic blocks between the starting block and the ending block
		//first all basic blocks between them
	    
		ISSABasicBlock startBlock = predBlock;
		ISSABasicBlock endBlock = postDomBlock;
		
		Set<ISSABasicBlock> betweenBlocks = PostDominatorFinder.findAllBasicBlocksBetween(node, startBlock);
		System.out.println(WALAUtils.getAllBasicBlockIDList(betweenBlocks));
		
		//FIXME may affect some test cases
		Set<ISSABasicBlock> intersectBlocks = Utils.intersect(betweenBlocks, executedBlocks); 
        // was = betweenBlocks;
		intersectBlocks.remove(startBlock);
		intersectBlocks.remove(endBlock);
		
		//find out instructions that is not in other predicate
		Set<ISSABasicBlock> predicateBlocks = new LinkedHashSet<ISSABasicBlock>();
		for(ISSABasicBlock bb : intersectBlocks) {
			if(bb == startBlock || bb == endBlock) {
				continue;
			}
			//skip empty block
			if(!bb.iterator().hasNext()) {
				continue;
			}
			if(CodeAnalysisUtils.isPredicateInstruction(bb.getLastInstruction())) {
				predicateBlocks.add(bb);
			}
		}
		
		for(ISSABasicBlock bb : predicateBlocks) {
			Set<ISSABasicBlock> bbSet = PostDominatorFinder.findAllBasicBlocksBetween(node, bb);
			intersectBlocks.removeAll(bbSet);
		}
		
		System.out.println("After removing nested");
		System.out.println(WALAUtils.getAllBasicBlockIDList(intersectBlocks));
		
		//first get all instructions, then remove those the value will be used again later
		//in the same basic block
		Set<SSAInstruction> allSSA = new LinkedHashSet<SSAInstruction>();
		for(ISSABasicBlock bb : intersectBlocks) {
			Iterator<SSAInstruction> iter = bb.iterator();
			while(iter.hasNext()) {
				SSAInstruction instr = iter.next();
				allSSA.add(instr);
			}
		}
		//we also need to return the phi statements in the end block
		Iterator<SSAPhiInstruction> iter = endBlock.iteratePhis();
		while(iter.hasNext()) {
			SSAPhiInstruction ssaPhiInstr = iter.next();
			allSSA.add(ssaPhiInstr);
		}
		
		//extract the seed statements
		Set<SSAInstruction> seedSSA = this.extractSeedInstructions(allSSA);
		
		//create statement for it
		Collection<Statement> stmts = new LinkedHashSet<Statement>();
		for(SSAInstruction ssa : seedSSA) {
			int index = WALAUtils.getInstructionIndex(node, ssa);
			if(index != -1) {
				Statement s = new NormalStatement(node, index);
				stmts.add(s);
			} else {
				if(ssa instanceof SSAPhiInstruction) {
					PhiStatement phiStmt = new PhiStatement(node, (SSAPhiInstruction)ssa);
					stmts.add(phiStmt);
				}
			}
		}
		
		return stmts;
		
	}
	
//	//FIXME
//	//Rewrite this part
//	public boolean valueMayFlowOut(CGNode node, SSAInstruction ssa) {
//		//test whether the
//		if(ssa instanceof SSAPutInstruction) {
//			return true;
//		}
//		if(ssa instanceof SSAGetInstruction) {
//			return false;
//		}
//		if(ssa instanceof SSAAbstractThrowInstruction) {
//			return false;
//		}
//		if(!ssa.hasDef()) {
//			return false;
//		}
//		return true;
//	}
	
	
	public Set<SSAInstruction> extractSeedInstructions(Set<SSAInstruction> ssaSet) {
		Set<SSAInstruction> seedSSAs = new LinkedHashSet<SSAInstruction>();
		
		for(SSAInstruction ssa : ssaSet) {
			if(!ssa.hasDef()) {
				continue;
			}
			if(SSAFilter.filterSSAForSlicing(ssa)) {
				continue;
			}
			//only focus on statement that has def
			int def = ssa.getDef();
			boolean isDefUsedLater = false;
			for(SSAInstruction anSSA : ssaSet) {
				if(!anSSA.hasDef()) {
					continue;
				}
				int useNum = anSSA.getNumberOfUses();
				for(int i = 0; i < useNum; i++) {
					if(anSSA.getUse(i) == def) {
						isDefUsedLater = true;
						break;
					}
				}
				if(isDefUsedLater) {
					break;
				}
			}
			//if the output of this instruction is used later, ignore it
			//if the output of this instruction is never used later
			if(!isDefUsedLater) {
				seedSSAs.add(ssa);
			}
		}
		
		return seedSSAs;
	}
}