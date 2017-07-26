package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.evol.experimental.IterativeSlicer;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;
import junit.framework.TestCase;

public class TestIterativeSlicing extends TestCase {

	public void testSlice() {
		String path = "./bin/test/evol/slicing/";
		String mainMethod = "Ltest/evol/slicing/SliceExamples";
		CodeAnalyzer coder = new CodeAnalyzer(path, mainMethod);
		coder.buildAnalysis();
		ConfEntity entity = new ConfEntity("test.evol.slicing.SliceExamples", "conf1", false);
		Collection<Statement> stmts = coder.slicer.sliceConfOption(entity);
		Collection<NormalStatement> normals  = WALAUtils.extractNormalStatements(stmts);
		for(NormalStatement s : normals) {
			System.out.println(s);
//			System.out.println(s.getInstruction());
		}
	}
	
	public void testCreateSliceSeeds() {
		String path = "./bin/test/evol/slicing/";
		String mainMethod = "Ltest/evol/slicing/SliceExamples";
		CodeAnalyzer coder = new CodeAnalyzer(path, mainMethod);
		coder.buildAnalysis();
		CallGraph cg = coder.getCallGraph();
		
		String fooSig = "test.evol.slicing.SliceExamples.foo(I)V";
		CGNode node = WALAUtils.lookupMatchedCGNode(cg, fooSig);
		
//		WALAUtils.printAllIRs(node);
		WALAUtils.printCFG(node);
//		WALAUtils.printAllIRsWithIndices(node);
		
		this.checkSeedStatements(node, 7);
		this.checkSeedStatements(node, 14);
		this.checkSeedStatements(node, 21);
		this.checkSeedStatements(node, 39);
		this.checkSeedStatements(node, 45);
	}
	
	private Collection<Statement> checkSeedStatements(CGNode node, int predIndex) {
		System.out.println("------");
		SliceSeedFinder finder = new SliceSeedFinder();
		Set<ISSABasicBlock> executedBlocks = new HashSet<ISSABasicBlock>(WALAUtils.getAllBasicBlocks(node));
		Collection<Statement> stmts = finder.createSliceSeedsByBBs(node, predIndex, executedBlocks);
		System.out.println("slicing seeds: ");
		for(Statement stmt : stmts) {
			System.out.println(stmt);
			
		}
		return stmts;
	}
	
	/**
	 * FIXME
	 * The result does not include bar()
	 * */
//	xxx
//	modify the code of identifying instructions between, use a dynamic point of view
	public void testSimpleIterativeSlice() {
		String path = "./bin/test/evol/slicing/";
		String mainMethod = "Ltest/evol/slicing/IterateSliceExample";
		CodeAnalyzer coder = new CodeAnalyzer(path, mainMethod);
		coder.buildAnalysis();
		
		CGNode nodeFoo = WALAUtils.lookupCGNode(coder.getCallGraph(), "test.evol.slicing.IterateSliceExample.foo").iterator().next();
		
		System.out.println(nodeFoo);
		WALAUtils.printAllIRsWithIndices(nodeFoo);
		
		//a sequence of executed ssa instructions
		Set<SSAInstruction> executedDiffSSAs = new LinkedHashSet<SSAInstruction>();
		//index 5 -- 27
		for(int i = 5; i < 27; i++) {
			SSAInstruction ssa = nodeFoo.getIR().getInstructions()[i];
			if(ssa == null) {
				continue;
			}
			executedDiffSSAs.add(ssa);
//			System.out.println(ssa);
		}
		CGNode nodeBar = WALAUtils.lookupCGNode(coder.getCallGraph(), "test.evol.slicing.IterateSliceExample.bar").iterator().next();
		for(SSAInstruction ssa : nodeBar.getIR().getInstructions()) {
			if(ssa != null) {
				executedDiffSSAs.add(ssa);
			}
		}
		
		SSAInstruction seedSSA = nodeFoo.getIR().getInstructions()[5];
		System.out.println("Seed: " + seedSSA);
		
		//The following seems to be OK
//		ConfEntity entity = new ConfEntity("test.evol.slicing.IterateSliceExample", "conf", false);
//		for(Statement s : coder.slicer.sliceConfOption(entity)) {
//			System.out.println("   " + s);
//		}
		//end
		
		IterativeSlicer iSlicer = new IterativeSlicer(coder);
		Collection<NormalStatement> stmts = iSlicer.iterate_slice(nodeFoo, seedSSA, executedDiffSSAs);
		
		for(NormalStatement stmt : stmts) {
			System.out.println("   index: " + stmt.getInstructionIndex()  + ", " + stmt.getInstruction());
		}
	}
	
}
