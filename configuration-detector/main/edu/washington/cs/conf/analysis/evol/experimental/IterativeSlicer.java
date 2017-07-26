package edu.washington.cs.conf.analysis.evol.experimental;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAConditionalBranchInstruction;

import edu.washington.cs.conf.analysis.evol.CodeAnalyzer;
import edu.washington.cs.conf.analysis.evol.PostDominatorFinder;
import edu.washington.cs.conf.analysis.evol.SSAFilter;
import edu.washington.cs.conf.analysis.evol.SliceSeedFinder;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

@Deprecated
public class IterativeSlicer {
	
	public static boolean debug = true;
	
	public final CodeAnalyzer coder;
	
	public final SliceSeedFinder finder;
	
	public IterativeSlicer(CodeAnalyzer coder) {
		Utils.checkNotNull(coder);
		this.coder = coder;
		this.finder = new SliceSeedFinder();
	}
	
	public int compute_cost_by_slice(CGNode node, SSAInstruction seed,
			Set<SSAInstruction> executedDiffSSAs) {
		return this.iterate_slice(node, seed, executedDiffSSAs).size();
	}
	
	/**
	 * Iteratively find all instructions affected.
	 * The seed must be a conditional branch instruction.
	 * Collecting the affected statements
	 * */
	//the executed diff ssa should be all ssas that reveals differences
	//rather than just the newly executed, or disappeared ssas
	public Collection<NormalStatement> iterate_slice(CGNode node, SSAInstruction seed,
			Set<SSAInstruction> executedDiffSSAs) {
		Utils.checkTrue(seed instanceof SSAConditionalBranchInstruction);
		int seedIndex = WALAUtils.getInstructionIndex(node, seed);
		Utils.checkTrue(seedIndex > -1);
		
		debugln(seed + ", index: " + seedIndex);
		
		//the branch been analyzed
		Collection<SSAInstruction> analyzedBranchSet = new HashSet<SSAInstruction>();
		analyzedBranchSet.add(seed);
		
		//get the seed block and its immediate post dominator block
		ISSABasicBlock seedBlock = node.getIR().getBasicBlockForInstruction(seed);
		
		//add the executed SSAs between seedBlock and postdomBlock
		Set<ISSABasicBlock> executedBlocks = PostDominatorFinder.findAllBasicBlocksBetween(node, seedBlock);
		
		debugln("Executed blocks: " + WALAUtils.getAllBasicBlockIDList(executedBlocks));

		//the return statement collection
		Collection<NormalStatement> stmtColl = new LinkedHashSet<NormalStatement>();
		//check if an ssa has been executed or not
		for(ISSABasicBlock bb : executedBlocks) {
			Iterator<SSAInstruction> iter = bb.iterator();
			while(iter.hasNext()) {
				SSAInstruction ssa = iter.next();
				//the static analysis may return both branches, only need to
				//retain the executed ssa
				if(executedDiffSSAs.contains(ssa)) {
					int ssaIndex = WALAUtils.getInstructionIndex(node, ssa);
					if(ssaIndex != -1) {
						stmtColl.add(new NormalStatement(node, ssaIndex));
					}
				}
			}
		}
		
		debugln("Stmt collection size: " + stmtColl.size());
		
		//get the seed statements and do the slicing repeated
		Collection<Statement> seedStmts = 
			this.finder.createSliceSeedsBySSAs(node, seedIndex, executedDiffSSAs);
		
		debugln("Number of seed stmts before filtering: " + seedStmts.size());
		
		//remove string related
		seedStmts = this.filterStringRelatedStatements(seedStmts);
		
		debugln("Number of seed stmts after filtering: " + seedStmts.size());
		for(Statement stmt : seedStmts) {
			debugln(" seed stmt: " + stmt);
		}
		
		List<Statement> seedList = new LinkedList<Statement>(seedStmts);
		Collection<SSAInstruction> visitedSliceSeedSet = new HashSet<SSAInstruction>(); 
		while(!seedList.isEmpty()) {
			debugln("Seedlist size: " + seedList.size());
			
			List<Statement> nextLevelSeeds = new LinkedList<Statement>();
			
			//perform slicing for each seed statement
			while(!seedList.isEmpty()) {
				
				debugln(" inner loop, seed list size: " + seedList.size());
				
				Statement top = seedList.remove(0);
				//skip the already sliced instruction
				if(top instanceof NormalStatement && 
						visitedSliceSeedSet.contains(((NormalStatement)top).getInstruction())) {
					continue;
				} else {
					visitedSliceSeedSet.add(((NormalStatement)top).getInstruction());
				}
				
				debugln("Seed stmt for slicing: " + top);
				
				Collection<Statement> affectedStmts = this.coder.performSlicing(top);
				
				debugln("Number of affected stmts: " + affectedStmts.size());
				
				//only extract the affected conditional branch statements
				Collection<NormalStatement> affectedBranches = new LinkedHashSet<NormalStatement>();
				for(Statement stmt : affectedStmts) {
					if(stmt instanceof NormalStatement) {
						NormalStatement nstmt = (NormalStatement)stmt;
						//check if it is a branch, but also been executed differently
						//XXX is this branch been analyzed before?
						SSAInstruction nstmtSSA = nstmt.getInstruction();
						if(nstmtSSA instanceof SSAConditionalBranchInstruction
								&& executedDiffSSAs.contains(nstmtSSA)) {
							if(!analyzedBranchSet.contains(nstmtSSA)) {
							    affectedBranches.add(nstmt);
							}
						}
					}
				}
				
				debugln("Number of affected branches: " + affectedBranches.size());
				
				//FIXME duplication below
				for(NormalStatement nstmt : affectedBranches) {
					
					debugln("Analyzing affected branch: " + nstmt);
					
					CGNode stmtNode = nstmt.getNode();
					SSAInstruction stmtSSA = nstmt.getInstruction();
					ISSABasicBlock stmtBlock = node.getIR().getBasicBlockForInstruction(stmtSSA);
					
					int stmtIndex = WALAUtils.getInstructionIndex(stmtNode, stmtSSA);
					
					//add the executed SSAs between seedBlock and postdomBlock
					Set<ISSABasicBlock> codeBasicBlocks = PostDominatorFinder.findAllBasicBlocksBetween(stmtNode, stmtBlock);
					//check if an ssa has been executed or not
					for(ISSABasicBlock bb : codeBasicBlocks) {
						Iterator<SSAInstruction> iter = bb.iterator();
						while(iter.hasNext()) {
							SSAInstruction ssa = iter.next();
							if(executedDiffSSAs.contains(ssa)) { //has been executed
								int ssaIndex = WALAUtils.getInstructionIndex(node, ssa);
								if(ssaIndex != -1) {
									stmtColl.add(new NormalStatement(node, ssaIndex));
								}
							}
						}
					}
					
					//get the seed statements and do the slicing repeated
					Collection<Statement> nextLevelSeedStmts = 
						this.finder.createSliceSeedsBySSAs(stmtNode, stmtIndex, executedDiffSSAs);
					nextLevelSeeds.addAll(nextLevelSeedStmts);
					
					debugln("Next level seed stmts size: " + nextLevelSeedStmts.size());
				}
				//FIXME end
			}
			
			//add to the seed list
			seedList.clear();
			seedList.addAll(nextLevelSeeds);
		}
		
		return stmtColl;
	}
	
	/**
	 * to avoid explosion
	 * */
	private Collection<Statement> filterStringRelatedStatements(Collection<Statement> stmts) {
		Collection<Statement> filteredStmts = new LinkedHashSet<Statement>();
		for(Statement stmt : stmts) {
			if(stmt instanceof NormalStatement) {
				SSAInstruction ssa = ((NormalStatement)stmt).getInstruction();
				if(SSAFilter.filterSSAForSlicing(ssa)) {
					continue;
				}
			}
			filteredStmts.add(stmt);
		}
		return filteredStmts;
	}
	
	private void debugln(Object o) {
		if(debug) {
		    System.out.println(o);
		}
	}
}