package edu.washington.cs.conf.analysis;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;

import com.ibm.wala.core.tests.slicer.SlicerTest;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.slicer.SDG;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.StatementWithInstructionIndex;
import com.ibm.wala.ipa.slicer.thin.CISlicer;
import com.ibm.wala.ipa.slicer.thin.ThinSlicer;
import com.ibm.wala.util.graph.traverse.DFSFinishTimeIterator;

import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.WALAUtils;
import junit.framework.TestCase;

public class TestCISlicerOnSmallExamples extends TestCase {

	public void testCode() {
		String path = "D:\\research\\configurations\\workspace\\configuration-detector\\bin\\test\\code";
		String mainClass = "Ltest/code/EqthConf";
		String definedClass = "test.code.EqthConf";
		String fieldName = "eqth";
		boolean isStatic = true;
		forwardSliceOnSingleField(path, mainClass, definedClass, fieldName, isStatic);
	}
	
	public void testFieldFlowToObject() {
		String path = "D:\\research\\configurations\\workspace\\configuration-detector\\bin\\test\\slice\\examples";
		String mainClass = "Ltest/slice/examples/FieldFlowToObject";
		String definedClass = "test.slice.examples.FieldFlowToObject";
		String fieldName = "omitmethods";
		boolean isStatic = true;
		forwardSliceOnSingleField(path, mainClass, definedClass, fieldName, isStatic);
	}
	
	public void testAddObjectToList() {
		String path = "D:\\research\\configurations\\workspace\\configuration-detector\\bin\\test\\slice\\examples";
		String mainClass = "Ltest/slice/examples/AddObjectToList";
		String definedClass = "test.slice.examples.AddObjectToList";
		String fieldName = "init_routine";
		boolean isStatic = true;
		forwardSliceOnSingleField(path, mainClass, definedClass, fieldName, isStatic);
	}
	
	public void testAddObjectToList2() {
		String path = "D:\\research\\configurations\\workspace\\configuration-detector\\bin\\test\\slice\\examples";
		String mainClass = "Ltest/slice/examples/AddObjectToList";
		String definedClass = "test.slice.examples.AddObjectToList";
		String fieldName = "mem_megabytes";
		boolean isStatic = true;
		forwardSliceOnSingleField(path, mainClass, definedClass, fieldName, isStatic);
	}
	
	public void testSimpleListExample() {		
//		type = CG.OneCFA;
		
		String path = "D:\\research\\configurations\\workspace\\configuration-detector\\bin\\test\\slice\\examples";
		String mainClass = "Ltest/slice/examples/SimpleListExample";
		String definedClass = "test.slice.examples.SimpleListExample";
		String fieldName = "str1";
		boolean isStatic = true;
		forwardSliceOnSingleField(path, mainClass, definedClass, fieldName, isStatic);
	}
	
	public void testObjectAccess() {		
//		type = CG.OneCFA;
		
		String path = "D:\\research\\configurations\\workspace\\configuration-detector\\bin\\test\\slice\\examples";
		String mainClass = "Ltest/slice/examples/ObjectAccess";
		String definedClass = "test.slice.examples.ObjectAccess";
		String fieldName = "v";
		boolean isStatic = true;
		forwardSliceOnSingleField(path, mainClass, definedClass, fieldName, isStatic);
	}
	
	public void testSharingStringEx() {		
		String path = "D:\\research\\configurations\\workspace\\configuration-detector\\bin\\test\\slice\\examples";
		String mainClass = "Ltest/slice/examples/SharingStringExample";
		String definedClass = "test.slice.examples.SharingStringExample";
		String fieldName = "str";
		boolean isStatic = true;
//		SDG.DEBUG_LAZY = true;
		forwardSliceOnSingleField(path, mainClass, definedClass, fieldName, isStatic);
	}
	
	public void testRandoopInitRoutine() {
		limit = 20;
		type = CG.OneCFA;
		
		String path = "./subjects/randoop-rmv-init-routine-str.jar;./subjects/plume.jar";
		String mainClass = "Lrandoop/main/Main";
		String definedClass = "randoop.main.GenInputsAbstract";
		String fieldName = "init_routine";
		boolean isStatic = true;
		forwardSliceOnSingleField(path, mainClass, definedClass, fieldName, isStatic);
	}
	
	private static int limit = Integer.MAX_VALUE;
	private static CG type = null;
	
	private void forwardSliceOnSingleField(String path, String mainClass, String definedClass, String fieldName,
			boolean isStatic) {
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
		helper.setExclusionFile("JavaAllExclusions.txt");
		helper.setCGType(CG.ZeroCFA);
		if(type != null) {
			helper.setCGType(type);
		}
		helper.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
		helper.setControlDependenceOptions(ControlDependenceOptions.NONE);
		helper.buildAnalysis();
		
		CISlicer slicer = new ThinSlicer(helper.getCallGraph(), helper.getPointerAnalysis());
		ConfEntity entity = new ConfEntity(definedClass, fieldName, isStatic);
		Statement seed = helper.extractConfStatement(entity);
		
		System.out.println("--- forward ---");
		
//		DFSFinishTimeIterator.DIRTY_HACK = true;
		
		Collection<Statement> slice = slicer.computeForwardThinSlice(seed);
		
		//WALAUtils.dumpSlice(slice, new PrintWriter(System.out));
		
		Collection<IRStatement> stmts = new LinkedList<IRStatement>();
		for(Statement s : slice) {
			if(s instanceof StatementWithInstructionIndex) {
				if(WALAUtils.getStatementLineNumber(s) == -1 ) {
					continue;
				}
			    IRStatement irs = new IRStatement((StatementWithInstructionIndex)s);
			    if(!irs.shouldIgnore() && irs.hasLineNumber()) {
			    	stmts.add(irs);
			    }
			    
			    CGNode node = s.getNode();
			    String ss = WALAUtils.getAllIRAsString(node);
			    System.out.println(ss);
			    
			}
		}
		
		dumpIRStatements(stmts, new PrintWriter(System.out), limit);
		
		System.out.println("slice size: " + stmts.size());
		
		limit = Integer.MAX_VALUE;
		type = null;
	}
	
	 public static void dumpIRStatements(Collection<IRStatement> slice, PrintWriter w, int limit) {
			w.println("SLICE:\n");
			int i = 1;
			for (IRStatement irs : slice) {
				Statement  s = irs.getStatement();
				int line_num = WALAUtils.getStatementLineNumber(s);
				String line = (i++) + "   " + s
				    + Globals.lineSep
				    + "\t" + WALAUtils.getFullMethodName(s.getNode().getMethod()) + ",  line num: " + line_num;
				w.println(line);
				w.flush();
				if(i > limit) {
					break;
				}
			}
		}
	
}
