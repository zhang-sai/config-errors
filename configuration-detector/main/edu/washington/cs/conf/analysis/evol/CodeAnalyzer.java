package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAArrayReferenceInstruction;
import com.ibm.wala.ssa.SSABinaryOpInstruction;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSACheckCastInstruction;
import com.ibm.wala.ssa.SSAComparisonInstruction;
import com.ibm.wala.ssa.SSAConditionalBranchInstruction;
import com.ibm.wala.ssa.SSAConversionInstruction;
import com.ibm.wala.ssa.SSAFieldAccessInstruction;
import com.ibm.wala.ssa.SSAGetCaughtExceptionInstruction;
import com.ibm.wala.ssa.SSAInstanceofInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.ssa.SSAMonitorInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import com.ibm.wala.ssa.SSAPutInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.ssa.SSAThrowInstruction;
import com.ibm.wala.ssa.SymbolTable;

import edu.washington.cs.conf.analysis.ConfigurationSlicer;
import edu.washington.cs.conf.analysis.evol.experimental.IterativeSlicer;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class CodeAnalyzer {

	public final ConfigurationSlicer slicer;
	
	public final IterativeSlicer iSlicer;
	
	private boolean cache = true;
	
	public CodeAnalyzer(String classPath, String mainClass) {
		this.slicer = new ConfigurationSlicer(classPath, mainClass);
		this.iSlicer = new IterativeSlicer(this);
	}
	
	public void buildClassHierarchy() {
		System.out.println("Using exclusion file: " + this.slicer.getExclusionFile());
		this.slicer.buildScope();
		this.slicer.buildClassHierarchy();
	}
	
	public void buildAnalysis() {
		this.slicer.buildAnalysis();
	}
	
	public CallGraph getCallGraph() {
		return this.slicer.getCallGraph();
	}
	
	public Collection<Statement> performSlicing(Statement s) {
		return this.slicer.performSlicing(s);
	}
	
	//the data structure for caching
	private Map<String, SSAInstruction> cachedMap = new HashMap<String, SSAInstruction>();
	public SSAInstruction getInstruction(String methodSig, int index) {
		String uniqueToken = methodSig + "#" + index;
		if(cache) {
			if(cachedMap.containsKey(uniqueToken)) {
				return cachedMap.get(uniqueToken);
			} else {
				SSAInstruction ssa
				    = CodeAnalysisUtils.getInstruction(this.slicer.getCallGraph(), methodSig, index);
				cachedMap.put(uniqueToken, ssa);
				return ssa;
			}
		} else {
		    return CodeAnalysisUtils.getInstruction(this.slicer.getCallGraph(), methodSig, index);
		}
	}
}
