package edu.washington.cs.conf.analysis.evol.experimental;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.instrument.evol.EfficientTracer;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

//@Deprecated
//this is for a single predicate
public class PredicateExecInfo {
	
	public static String SEP = "#";

	public final String context; //the outside method
	public final String predicateIndex; //the predicate index
	public final int evalFreqCount;
	public final int evalResultCount;
	
	public PredicateExecInfo(String context, String predicate, int freq, int result) {
		Utils.checkNotNull(context);
		Utils.checkNotNull(predicate);
		Utils.checkTrue(freq > 0);
		Utils.checkTrue(result >= 0);
		Utils.checkTrue(freq >= result);
		this.context = context;
		this.predicateIndex = predicate;
		this.evalFreqCount = freq;
		this.evalResultCount = result;
	}
	
	public String getPredicateSig() {
		return createPredicateSig(this.context, this.getIndex());
	}
	
	//Ugly, just do not want to break existing code
	public String getPredicateSigInstr() {
		return this.context + EfficientTracer.SEP + this.getIndex();
	}
	
	public static String createPredicateSig(String method, int index) {
		return method + SEP + index;
	}
	
	public static String[] parsePredicateSig(String sig) {
		String[] splits = sig.split(SEP);
		Utils.checkTrue(splits.length == 2);
		return splits;
	}
	
	public static String paseMethodSig(String predSig) {
		return parsePredicateSig(predSig)[0];
	}
	
	public static Integer parseInstructionIndex(String predSig) {
		return Integer.parseInt(parsePredicateSig(predSig)[1]);
	}
	
	public String getMethodSig() {
		return this.context;
	}
	
	public int getIndex() {
		return Integer.parseInt(this.predicateIndex);
	}	
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof PredicateExecInfo)) {
			return false;
		}
		PredicateExecInfo info = (PredicateExecInfo)o;
		return this.context.equals(info.context) && this.predicateIndex.equals(info.predicateIndex)
		    && this.evalFreqCount == info.evalFreqCount && this.evalResultCount == info.evalResultCount;
	}
	
	@Override
	public int hashCode() {
		return this.evalFreqCount + 7*this.evalResultCount + 17*this.context.hashCode()
		    + 29*this.predicateIndex.hashCode();
	}
	
	@Override
	public String toString() {
		return predicateIndex + "@" + context + " -> " + evalFreqCount + ":" + evalResultCount;
	}
	
    /**
     * The following methods are for debugging purpose.
     **/
	public void showContext(CallGraph cg) {
		this.showContext(WALAUtils.lookupMatchedCGNode(cg, this.getMethodSig()));
	}
	public void showContext(CGNode node) {
		Utils.checkNotNull(node);
		Utils.checkTrue(this.getMethodSig().equals(node.getMethod().getSignature()));
		WALAUtils.printCFG(node);
		SSAInstruction ssa = node.getIR().getInstructions()[this.getIndex()];
		System.out.println("The " + this.getIndex() + "-th instruction is: " + ssa);
	}
}