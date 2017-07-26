package edu.washington.cs.conf.diagnosis;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.classLoader.IBytecodeMethod;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.core.tests.callGraph.CallGraphTestUtil;
import com.ibm.wala.core.tests.util.TestConstants;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.impl.Everywhere;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAOptions;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.io.FileProvider;

import edu.washington.cs.conf.instrument.AbstractInstrumenter;
import edu.washington.cs.conf.util.Utils;

public class StmtExecuted {

	public final String line;
	
	public final String methodSig;
	
	public final String bytecodeInstruction;
	
	public final int instructionIndex;
	
	private int source_number = -1;
	
	/**
	 * Important: this parsing semantic must be kept consistent
	 * with the assembly logic in EveryStmtInstrumenter
	 * */
	public StmtExecuted(String line) {
		Utils.checkNotNull(line);
		this.line = line;
		String[] strs = line.split(AbstractInstrumenter.SEP);
		Utils.checkTrue(strs.length == 3, "It is: " + strs.length + ", in: " + line);
		this.methodSig = strs[0];
		this.bytecodeInstruction = strs[1];
		this.instructionIndex = Integer.parseInt(strs[2]);
	}
	
	public void setSourceNumber(int num) {
		Utils.checkTrue(num > 0);
		source_number = num;
	}
	
	@Override
	public String toString() {
//		return line; //
		return getStmtAsStr();
	}
	
	public String getStmtAsStr() {
		return methodSig + AbstractInstrumenter.SEP + bytecodeInstruction
	    + AbstractInstrumenter.SEP + instructionIndex
	    + AbstractInstrumenter.SEP + source_number;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof StmtExecuted)) {
			return false;
		}
		StmtExecuted stmt = (StmtExecuted)o;
		return stmt.methodSig.equals(this.methodSig)
		    && stmt.bytecodeInstruction.equals(this.bytecodeInstruction)
		    && stmt.instructionIndex == this.instructionIndex;
	}
	
	public static void addSourceNumber(String classPath, Collection<StmtExecuted>... stmtsSet) {
		AnalysisScope scope;
		try {
			List<StmtExecuted> stmts = new LinkedList<StmtExecuted>();
			for(Collection<StmtExecuted> set : stmtsSet) {
				stmts.addAll(set);
			}
			
			scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(classPath, (new FileProvider())
			          .getFile(CallGraphTestUtil.REGRESSION_EXCLUSIONS));
			ClassHierarchy cha = ClassHierarchy.make(scope);
			addSourceNumber(cha, stmts);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	    
	}
	
	/**
	 * A utility method to add source number to the executed stmts
	 * @throws InvalidClassFileException 
	 * */
	//a method signature looks like: test.slice.depfield.SeeCoverage.main([Ljava/lang/String;)V
	//
	//for each StmtExecuted object, add the line number tag to it
	public static void addSourceNumber(ClassHierarchy cha, Collection<StmtExecuted> stmts) throws InvalidClassFileException {
		Set<String> allMethodSigs = new LinkedHashSet<String>();
		for(StmtExecuted s : stmts) {
			allMethodSigs.add(s.methodSig);
		}
		//map from signature to IR
		AnalysisCache cache = new AnalysisCache();
		Map<String, IR> irMap = new LinkedHashMap<String, IR>();
		for (IClass klass : cha) {
			for(IMethod m : klass.getDeclaredMethods()) {
				String signature = m.getSignature();
				if(allMethodSigs.contains(signature)) {
					IR ir = cache.getIRFactory().makeIR(m, Everywhere.EVERYWHERE, new SSAOptions());
					irMap.put(signature, ir);
				}
			}
		}
		//initialize the line number
		for(StmtExecuted stmt : stmts) {
			IR ir = irMap.get(stmt.methodSig);
			Utils.checkNotNull(ir, "The method: " + stmt.methodSig + " does not exist! ");
			IBytecodeMethod method = (IBytecodeMethod)ir.getMethod();
		    int bytecodeIndex = method.getBytecodeIndex(stmt.instructionIndex);
		    int sourceLineNum = method.getLineNumber(bytecodeIndex);
		    if(sourceLineNum != -1) {
		    	stmt.setSourceNumber(sourceLineNum);
		    }
		}
	}
}