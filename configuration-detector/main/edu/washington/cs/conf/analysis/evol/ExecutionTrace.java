package edu.washington.cs.conf.analysis.evol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecInfo;
import edu.washington.cs.conf.instrument.evol.EfficientTracer;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

/**
 * The class of recording a full execution trace
 * */
//should make it lazily-initialized, the trace file can be extremely large,
//do not read it all at once
public class ExecutionTrace {
	public static boolean enable_cache_trace = true;
	
	//for experiment use only
	private final List<InstructionExecInfo> all_exec_instr = new LinkedList<InstructionExecInfo>();
	
	private final String traceFileName;
	//the sig map file records a number to the instruction signature
	private final String sigmapFileName;
	private final String predicateFileName;
	
	//if a trace is too big, just count the instruction num
	private final String countingFileName;
	private ExecutionTraceCounter traceCounter = null;
	
	public ExecutionTrace(String traceFile, String sigmapFile, String predicateFile) {
		Utils.checkFileExistence(traceFile);
		Utils.checkFileExistence(sigmapFile);
//		Utils.checkFileExistence(predicateFile);
		this.traceFileName = traceFile;
		this.sigmapFileName = sigmapFile;
		this.predicateFileName = predicateFile;
		this.countingFileName = null;
		//check whether to read into memory
		if(enable_cache_trace) {
		    this.checkOrRead();
		}
	}
	
	public ExecutionTrace(String countingFile) {
		Utils.checkFileExistence(countingFile);
		this.countingFileName = countingFile;
		this.traceCounter = new ExecutionTraceCounter(this.countingFileName);
		this.traceFileName = null;
		this.sigmapFileName = null;
		this.predicateFileName = null;
	}
	
	/**
	 * This constructor is only for experimental testing purpose
	 * */
	ExecutionTrace(String[] lines) {
		for(String line : lines) {
			all_exec_instr.add(ExecutionTraceReader.createInstructionExecInfo(line));
		}
		this.traceFileName = null;
		this.sigmapFileName = null;
		this.predicateFileName = null;
		this.countingFileName = null;
	}
	
	//check to see whether the trace is small enough to read into the memory
	static int MAXLINE = 2000000;
	private void checkOrRead() {
		int num = Files.countLinesFast(this.traceFileName);
		if(num < MAXLINE) {
			long start = System.currentTimeMillis();
			long prevMem = Runtime.getRuntime().totalMemory()
			    - Runtime.getRuntime().freeMemory();
			System.out.println("Number of total lines: " + num + ", less than: " + MAXLINE);
			System.out.println("Read all into memory.");
			List<InstructionExecInfo> all
			    = ExecutionTraceReader.createInstructionExecInfo(this.traceFileName, this.sigmapFileName);
			this.all_exec_instr.addAll(all);
			System.out.println("Using time: " + (System.currentTimeMillis() - start) + " ms");
			System.out.println("Using memory: " +
					(Runtime.getRuntime().totalMemory() - - Runtime.getRuntime().freeMemory()
							- prevMem));
		} else {
			System.out.println("The trace file is too large. wont read into memory all. " + num);
		}
	}
	
	public Set<PredicateExecInfo> getExecutedPredicates() {
		Utils.checkFileExistence(this.predicateFileName);
		Collection<PredicateExecInfo> predColl = ExecutionTraceReader.createPredicateExecInfoList(this.predicateFileName,this.sigmapFileName);
		Set<PredicateExecInfo> predSet = new LinkedHashSet<PredicateExecInfo>(predColl);
		return predSet;
	}
	
	public static InstructionExecInfo getImmediatePostDominator(CodeAnalyzer coder, PredicateExecInfo pred) {
		String methodSig = pred.getMethodSig();
		int index = pred.getIndex();
		CGNode node = WALAUtils.lookupMatchedCGNode(coder.getCallGraph(), methodSig);
		if(node == null) {
			System.err.println("Missing node for method sig: " + methodSig);
			return null;
		}
//		Utils.checkNotNull(node.getIR(), "node sig: " + node.getMethod().getSignature()
//				+ ", is abstract method? " + node.getMethod().isAbstract());
		SSAInstruction ssa = WALAUtils.getInstruction(node, index);
		Utils.checkNotNull(ssa);
		SSAInstruction domSSA = PostDominatorFinder.getImmediatePostDominatorInstruction(node, ssa);
		
		//represent at the end of a method
		if(domSSA == null) {
			return InstructionExecInfo.createMethodEndExec(methodSig);
		}
		
		//get the index
		int domSSAIndex = WALAUtils.getInstructionIndex(node, domSSA);
		
		InstructionExecInfo info = new InstructionExecInfo(methodSig, domSSAIndex);
		info.setSSAInstruction(domSSA);
		info.setCGNode(node);
		
		//the executed instruction info
		return info;
	}
	
	public Set<InstructionExecInfo> getExecutedInstructionsInsidePredicate(CodeAnalyzer coder, PredicateExecInfo pred) {
		InstructionExecInfo postDom = getImmediatePostDominator(coder, pred);
		return this.getExecutedInstructionsBetween(pred.getMethodSig(), pred.getIndex(),
				postDom.getMethodSig(), postDom.getIndex());
	}
	
	public int getExecutedInstructions(String methodSig, int index) {
		Utils.checkNotNull(this.traceCounter);
		if(!this.traceCounter.hasPredicate(methodSig, index)) {
			return 0;
		}
		return this.traceCounter.getCount(methodSig, index);
	}
	
	public boolean useCountFile() {
		return this.traceCounter != null;
	}
	
	//FIXME
	//given an execute predicate, and its execution frequency.
	//return the delta instructions that have been executed
	/**
	 * avoid double count in the recursive case,
	 * foo() {
	 *   if(x) {
	 *      foo();
	 *   }
	 * }
	 * the trace would be like:  enter-x enter-x exit-x exit-x
	 * 
	 * Also a problem for loops:
	 * 
	 * while(predicate) {  //this predicate may execute  lots of times
	 *  ...
	 * }
	 * postImmediateDominator();
	 * 
	 * */
	public Set<SSAInstruction> getExecutedInstructions(
			String startMethodSig, int startIndex,
			String endMethodSig, int endIndex,
			CodeAnalyzer coder) {
		Set<InstructionExecInfo> execSet
		    = this.getExecutedInstructionsBetween(startMethodSig, startIndex, endMethodSig, endIndex);
		Set<SSAInstruction> ssaSet = new LinkedHashSet<SSAInstruction>();
		for(InstructionExecInfo exec : execSet) {
			ssaSet.add(coder.getInstruction(exec.getMethodSig(), exec.getIndex()));
		}
		return ssaSet;
	}
	
	//FIXME did not consider the nested case now. may be needed in the future
	//the trace file cotent looks like: NORMAL:a_number
	//the number correspond to the sig map
	public Set<InstructionExecInfo> getExecutedInstructionsBetween(
			String startMethodSig, int startIndex,
			String endMethodSig, int endIndex) {
		//if the history has already been processed, then use the full processed history
		if(!this.all_exec_instr.isEmpty()) {
			return this.getExecutedInstructionsBetween_internal(startMethodSig, startIndex,
					endMethodSig, endIndex);
		}
		//the return set
		Set<InstructionExecInfo> returnSet = new LinkedHashSet<InstructionExecInfo>();
		//read the file directly to save memory
		boolean start = false;
		Map<Integer, String> sigMap = SigMapParser.parseSigNumMapping(this.sigmapFileName);
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(this.traceFileName)));  
			String line = null; 
		    while ((line = br.readLine()) != null) {  
		       line = line.trim();
		       if(line.equals("null")) {
		    	   continue;
		       }
		       String[] splits = line.split(EfficientTracer.EVAL_SEP);
		       Utils.checkTrue(splits.length == 2, "error: " + line);
		       String instrSig = sigMap.get(Integer.parseInt(splits[1]));
		       Utils.checkNotNull(instrSig);
		       line = splits[0] + EfficientTracer.EVAL_SEP + instrSig;
//		       System.out.println(line);
		       if(ExecutionTraceReader.checkInstruction(line, startMethodSig, startIndex)) {
		    	   if(start) {
//		    		   Utils.fail("unsupported, already started."); //FIXME
		    	   } else {
		    		   start = true;
		    	   }
		       } else if (ExecutionTraceReader.checkInstruction(line, endMethodSig, endIndex)) {
		    	   if(start) {
		    		   start = false;
		    	   } else {
//		    		   Utils.fail("unsupported, not started yet.");
		    	   }
		       } else {
		    	   if(start) {
		    		   InstructionExecInfo execInfo = ExecutionTraceReader.createInstructionExecInfo(line);
		    		   returnSet.add(execInfo);
		    	   }
		       }
		    }
		} catch (Throwable e) {
			throw new Error(e);
		}
		return returnSet;
	}
	
	/**
	 * This is just for testing purpose
	 * */
	private Set<InstructionExecInfo> getExecutedInstructionsBetween_internal(
			String startMethodSig, int startIndex,
			String endMethodSig, int endIndex) {
		Utils.checkNotNull(startMethodSig);
		Utils.checkNotNull(endMethodSig);
		Utils.checkTrue(startIndex >= 0 && endIndex >= 0);
		//the return set
		Set<InstructionExecInfo> execSet = new LinkedHashSet<InstructionExecInfo>();
		
		int startNum = 0;
		for(InstructionExecInfo execInfo : this.all_exec_instr) {
			String methodSig = execInfo.getMethodSig();
			int index = execInfo.getIndex();
			if(methodSig.equals(startMethodSig) && index == startIndex) {
				startNum++;
//				if(start) {
//					Utils.fail("ERROR: meet nested cases.");
//				} else {
//					start = true;
//				}
			} else if (methodSig.equals(endMethodSig) && index == endIndex) {
				if(startNum <= 0) {
//					System.err.println("Starting index: " + startMethodSig + ", index: "
//							+ startIndex);
//					Utils.fail("Error: did not start yet. For: " + endMethodSig
//							+ ", with index: " + endIndex);
				} else {
					startNum = 0; //FIXME -- or = 0?
				}
			} else {
				//depends on the flag
				if(startNum > 0) {
					execSet.add(execInfo);
				}
			}
		}
		
		return execSet;
	}
}
