package edu.washington.cs.conf.analysis;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.core.tests.callGraph.CallGraphTestUtil;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.CallGraphStats;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.slicer.NormalReturnCaller;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Slicer;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ipa.slicer.Statement.Kind;
import com.ibm.wala.ipa.slicer.thin.CISlicer;
import com.ibm.wala.ipa.slicer.StatementWithInstructionIndex;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAGetInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInvokeInstruction;
import com.ibm.wala.ssa.SSAPutInstruction;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.graph.traverse.DFSFinishTimeIterator;
import com.ibm.wala.util.io.FileProvider;

import edu.washington.cs.conf.util.Log;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class ConfigurationSlicer {
	
	public enum CG {RTA, ZeroCFA, ZeroContainerCFA, VanillaZeroOneCFA, ZeroOneCFA, ZeroOneContainerCFA, OneCFA, TwoCFA, CFA, TempZeroCFA}
	
	public final String classPath;
	public final String mainClass;
	
	private CG type = CG.ZeroOneCFA;
	private String byPassFile = null;
	private int cfaprecision = -1;
	private String exclusionFile = CallGraphTestUtil.REGRESSION_EXCLUSIONS;
	private boolean contextSensitive = true;
	private boolean backward = false;
	//if the default slice seed does not work, use
	//the return statement from getters
	private boolean addSliceSeedFromGet = false;
	private boolean useReturnSeed = false; //return the last get instruction from the affecting method
	private boolean extractAllGets = false;
	
	private DataDependenceOptions dataOption = DataDependenceOptions.NO_BASE_PTRS;
	private ControlDependenceOptions controlOption =ControlDependenceOptions.NONE;
	//the CISlicer
 	private CISlicer slicer = null;
	
	private AnalysisScope scope = null;
	private ClassHierarchy cha = null;
	private Iterable<Entrypoint> entrypoints = null;
	private CallGraphBuilder builder = null;
	private AnalysisOptions options = null;
	private CallGraph cg = null;
	private boolean addStatementDistance = false;
	
	private String targetPackageName = null;
	
	public ConfigurationSlicer(String classPath, String mainClass) {
		this.classPath = classPath;
		this.mainClass = mainClass;
	}
	
	public void useFullSlice() {
		this.dataOption = DataDependenceOptions.FULL;
		this.controlOption = ControlDependenceOptions.FULL;
	}
	
	public void buildAnalysis() {
		try {
		  System.out.println("Using exclusion file: " + this.exclusionFile);
		  System.out.println("CG type: " + this.type);
		  
//	      this.scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(this.classPath, (new FileProvider())
//	          .getFile(exclusionFile));
//	      this.cha = ClassHierarchy.make(this.scope);
	      this.buildScope(); //compute values to this.scope and this.cha
	      this.buildClassHierarchy();
	      if(this.entrypoints == null) {
	          this.entrypoints = com.ibm.wala.ipa.callgraph.impl.Util.makeMainEntrypoints(scope, cha, mainClass);
	      } else {
	    	  System.err.println("Note, use customized entry points: " + this.entrypoints);
	    	  System.err.println("Total num: " + Utils.countIterable(this.entrypoints));
	      }
	      this.options = CallGraphTestUtil.makeAnalysisOptions(scope, entrypoints);
	      this.builder = chooseCallGraphBuilder(options, new AnalysisCache(), cha, scope);
	      //build the call graph
	      System.out.println("Building call graph...");
	      System.out.println("Number of entry points: " + Utils.countIterable(this.entrypoints));
	      this.cg = this.builder.makeCallGraph(options, null);
	      System.err.println(CallGraphStats.getStats(this.cg));
		} catch (Throwable e) {
			throw new Error(e);
		}
	}
	
	//only call this after building the CG
	//Never call this
	public void setExcludeStringBuilder(boolean exclude) {
		System.err.println("+++ Do you really want to call this method to exclude the string.");
		DFSFinishTimeIterator.DIRTY_HACK = exclude;
	}
	
	public void setCGType(CG type) {
		this.type = type;
	}
	
	public void setExclusionFile(String fileName) {
		this.exclusionFile = fileName;
	}
	
	public String getExclusionFile() {
		return this.exclusionFile;
	}
	
	public void setEntrypoints(Iterable<Entrypoint> entrypoints) {
		this.entrypoints = entrypoints;
	}
	
	public void setCFAPrecision(int length) {
		this.cfaprecision = length;
	}
	
	public void setContextSensitive(boolean cs) {
		this.contextSensitive = cs;
	}
	
	public void setBackward(boolean backward) {
		this.backward = backward;
	}
	
	public void setAddSliceSeedFromGet(boolean addSliceSeed) {
		this.addSliceSeedFromGet = addSliceSeed;
	}
	
	public void setUseReturnSeed(boolean useReturnSeed) {
		this.useReturnSeed = useReturnSeed;
	}
	
	public void setExtractAllGets(boolean extractAllGets) {
		this.extractAllGets = extractAllGets;
	}
	
	public void setDataDependenceOptions(DataDependenceOptions op) {
		this.dataOption = op;
	}
	
	public void setControlDependenceOptions(ControlDependenceOptions op) {
		this.controlOption = op;
	}
	
	public void setTargetPackageName(String packageName) {
		this.targetPackageName = packageName;
	}
	
	public void buildScope() {
		try {
			this.scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(this.classPath, (new FileProvider())
			          .getFile(exclusionFile));
		} catch (IOException e) {
			throw new Error(e);
		}
	}
	
	public void buildClassHierarchy() {
		if(this.scope == null) {
			this.buildScope();
		}
		try {
			this.cha = ClassHierarchy.make(this.scope);
		} catch (ClassHierarchyException e) {
			throw new Error(e);
		}
	}
	
	public ClassHierarchy getClassHierarchy() {
		return this.cha;
	}
	
	public CallGraph getCallGraph() {
		return this.cg;
	}
	
	public PointerAnalysis getPointerAnalysis() {
		return this.builder.getPointerAnalysis();
	}
	
	public void setSlicingDistance(boolean flag) {
		this.addStatementDistance = flag;
	}
	
	public ConfPropOutput outputSliceConfOption(ConfEntity entity) {
		long startT = System.currentTimeMillis();
		Collection<Statement> stmts = sliceConfOption(entity);
		if(this.addSliceSeedFromGet) {
			//compute more affected statement, and then add to stmts
			Collection<Statement> moreStmts = sliceConfOptionFromGetter(entity);
			stmts.addAll(moreStmts);
		}
		System.out.println("Time cost: " + (System.currentTimeMillis() - startT)/1000 + " s");
		Collection<IRStatement> irs = convert(stmts);
		ConfPropOutput output = new ConfPropOutput(entity, irs, this.targetPackageName);
		//add distance or not
		if(this.addStatementDistance) {
			Statement seed = this.extractConfStatement(entity);
			for(IRStatement target : irs) {
			    int distance = this.computeDistanceInThinSlicing(seed, target.getStatement());
			    output.setSlicingDistance(target, distance);
			}
		}
		return output;
	}
	
	public List<ConfPropOutput> outputSliceConfOptionInBatch(List<ConfEntity> entities) {
		List<ConfPropOutput> outputs = new LinkedList<ConfPropOutput>();
		
		for(ConfEntity entity : entities) {
			ConfPropOutput output = outputSliceConfOption(entity);
			outputs.add(output);
		}
		
		return outputs;
	}
	
	public Collection<Statement> sliceConfOption(ConfEntity entity) {
		checkCG();
		Statement s = this.extractConfStatement(entity);
		if(s == null) {
			IClass clz = WALAUtils.lookupClass(this.getClassHierarchy(), entity.getClassName());
			if(clz != null) {
				//need to take a look at this statement
				String signature = entity.getClassName() + "." +
				    (entity.getAssignMethod() == null
				      ?(entity.isStatic() ? "<clinit>" : "<init>")
				      : entity.getAssignMethod());
				Collection<CGNode> nodes = WALAUtils.lookupCGNode(this.getCallGraph(), signature);
				for(CGNode node : nodes) {
					System.out.println("---the CGNode:" + node);
				    WALAUtils.printAllIRs(node);
				}
				if(nodes.isEmpty()) {
					//the call graph do not contain such nodes
					System.err.println(" no such nodes in CG: " + signature);
					return new LinkedList<Statement>();
				}
			}
			
			Utils.checkTrue(clz == null, "Class: " + entity.getClassName()
					+ ",  here is the entity: " + entity);
		}
		Utils.checkNotNull(s, "statement is null? " + entity);
		//compute the slice
		Collection<Statement> slice = this.performSlicing(s);
		
		if(this.extractAllGets) {
			Collection<Statement> stmtsFromGetters = this.sliceConfOptionFromEveryGetter(entity);
			slice.addAll(stmtsFromGetters);
		}
		
		return slice;
	}
	
	public Collection<Statement> sliceConfOptionFromGetter(ConfEntity entity) {
		this.checkCG();
		Statement s = this.extractConfStatementFromGetter(entity);
		if(s == null) {
			return Collections.EMPTY_LIST;
		}
		System.out.println("Add additional seed: " + s + " to: " + entity.getConfName());
		return this.performSlicing(s);
	}
	
	Collection<Statement> sliceConfOptionFromEveryGetter(ConfEntity entity) {
		this.checkCG();
		Collection<Statement> allStmts = new LinkedHashSet<Statement>();
		
		Collection<Statement> allGetStmts = this.extractAllGetStatements(entity);
		System.err.println("   all get stmts: " + allGetStmts.size());
		for(Statement getStmt : allGetStmts) {
			Collection<Statement> slicedStmts = this.performSlicing(getStmt);
			allStmts.addAll(slicedStmts);
		}
		
		return allStmts;
	}
	
	public Collection<Statement> performSlicing(Statement s) {
		this.checkCG();
		Utils.checkNotNull(s);
		try {
			if(backward) {
				System.err.println("!!! Now backward slicing -- experimental!, context: " + this.contextSensitive);
				if(this.contextSensitive) {
					return this.computeConetxtSensitiveSlice(s, true, this.dataOption, this.controlOption);
				} else {
					return this.computeConetxtInsensitiveThinSlice(s, true, this.dataOption, this.controlOption);
				}
			}
			//the usually used one, forward
			if(this.contextSensitive) {
			    return computeContextSensitiveForwardSlice(s);
			} else {
				return this.computeContextInsensitiveForwardThinSlice(s);
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
//			return new LinkedList<Statement>();
		}
	}
	
	public Collection<Statement> computeContextSensitiveForwardSlice(Statement seed) throws IllegalArgumentException, CancelException {
		return computeConetxtSensitiveSlice(seed, false, this.dataOption, this.controlOption);
	}
	
	public Collection<Statement> computeConetxtSensitiveSlice(Statement seed, boolean goBackward,
			DataDependenceOptions dOptions, ControlDependenceOptions cOptions) throws IllegalArgumentException, CancelException {
		  checkCG();
	      System.err.println("Seed statement in context-sensitive slicing: " + seed);
	      System.err.println("Data dependence option: " + dOptions);
	      System.err.println("Control dependence option: " + cOptions);
	      // compute the slice as a collection of statements
	      Collection<Statement> slice = null;
	      if (goBackward) {
	        slice = Slicer.computeBackwardSlice(seed, cg, builder.getPointerAnalysis(), dOptions, cOptions);
	      } else {
	        // for forward slices ... we actually slice from the return value of calls.
	        seed = getReturnStatementForCall(seed);
	        slice = Slicer.computeForwardSlice(seed, cg, builder.getPointerAnalysis(), dOptions, cOptions);
	      }
	      //SlicerTest.dumpSlice(slice);
	      return slice;
	}
	
	public Collection<Statement> computeContextInsensitiveForwardThinSlice(Statement seed) throws IllegalArgumentException, CancelException {
		return computeConetxtInsensitiveThinSlice(seed, false, this.dataOption, this.controlOption);
	}
	
	public Collection<Statement> computeConetxtInsensitiveThinSlice(Statement seed, boolean goBackward,
			DataDependenceOptions dOptions, ControlDependenceOptions cOptions) throws IllegalArgumentException, CancelException {
		checkCG();
		System.err.println("Seed statement in context-insensitive slicing: " + seed);
		System.err.println("Data dependence option: " + dOptions);
	    System.err.println("Control dependence option: " + cOptions);
		
		//initialize the slice
		if(slicer == null) {
		    slicer = new CISlicer(cg, builder.getPointerAnalysis(), dOptions, cOptions);
		}
		
		Collection<Statement> slice = null;
		if (goBackward) {
	        slice = slicer.computeBackwardThinSlice(seed);
	      } else {
	        // for forward slices ... we actually slice from the return value of calls.
	        seed = getReturnStatementForCall(seed);
	        slice = slicer.computeForwardThinSlice(seed);
	      }
		
		return slice;
	}
	
	public int computeDistanceInThinSlicing(Statement seed, Statement target) {
		Utils.checkNotNull(slicer);
		int distance = slicer.computeBFSDistanceInForwardSlice(seed, target);
		return distance;
	}
	
	public List<Statement> computeStatementListInThinSlicing(Statement seed, Statement target) {
		Utils.checkNotNull(slicer);
		return slicer.computeBFSPathInForwardSlice(seed, target);
	}
	
	private void checkCG() {
		if(this.cg == null) {
			  throw new RuntimeException("Please call buildAnalysis() first.");
		  }
	}
	
	public Statement extractConfStatement(ConfEntity entity) {
		String className = entity.getClassName();
		String confName = entity.getConfName();
		String assignMethod = entity.getAssignMethod(); //FIXME we may need more specific method signature
		boolean isStatic = entity.isStatic();
		
		String targetMethod = assignMethod != null
		     ? assignMethod
		     : (isStatic ? "<clinit>" : "<init>");
		
		//String irSig = isStatic ? "putstatic" : "putfield";
		//SSAPutInstruction.class;
		
		Log.logln("target method name: " + targetMethod);
//		System.out.println(targetMethod);
		
		for(CGNode node : cg) {
			String fullMethodName = WALAUtils.getFullMethodName(node.getMethod());
			//Log.logln("full method name: " + fullMethodName + ",  className: " + className);
			if(fullMethodName.equals(className + "." + targetMethod)) {
				
				if(this.useReturnSeed) {
					List<SSAInstruction> irList = WALAUtils.getAllIRs(node);
					Collections.reverse(irList);
					for(SSAInstruction ssa : irList) {
						if(ssa instanceof SSAGetInstruction) {
							SSAGetInstruction ssaGet = (SSAGetInstruction)ssa;
							if(ssaGet.toString().indexOf(confName) != -1) {
								Statement s = new NormalStatement(node, WALAUtils.getInstructionIndex(node, ssaGet));
								return s;
							}
						}
					}
				}
				
//				WALAUtils.printAllIRs(node);
				Iterator<SSAInstruction> ssaIt = node.getIR().iterateAllInstructions();
				while(ssaIt.hasNext()) {
					SSAInstruction inst = ssaIt.next();
					if(inst instanceof SSAPutInstruction) {
						//Log.logln("In method: " + fullMethodName + "put inst: " + inst);
						SSAPutInstruction putInst = (SSAPutInstruction)inst;
						if(putInst.isStatic() == isStatic) {
							//FIXME ugly below
							//Log.logln("put inst with same modifier: " + putInst);
							if(putInst.toString().indexOf(confName) != -1) {
								//find it
//								System.out.println(" ==> " + putInst);
								Statement s = new NormalStatement(node, WALAUtils.getInstructionIndex(node, inst));
								return s;
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	public Collection<Statement> extractAllGetStatements(ConfEntity entity) {
		return ConfUtils.getextractAllGetStatements(entity, this.getCallGraph());
	}
	
	public Statement extractConfStatementFromGetter(ConfEntity entity) {
		String confClassName = entity.getClassName();
		String confName = entity.getConfName();
		boolean isStatic = entity.isStatic();
		
		Set<String> skippedMethods = new HashSet<String>();
		skippedMethods.add("equals");
		skippedMethods.add("hashCode");
		skippedMethods.add("toString");
		skippedMethods.add("<init>");
		skippedMethods.add("<clinit>");
		
		for(CGNode node : cg) {
			String fullClassName = WALAUtils.getJavaFullClassName(node.getMethod().getDeclaringClass());
			if(fullClassName.equals(confClassName)) {
				String methodName = node.getMethod().getName().toString();
				if(skippedMethods.contains(methodName)) {
					continue;
				}
				if(methodName.startsWith("set")) { //a heuristic
					continue;
				}
				for(SSAInstruction ssa : WALAUtils.getAllIRs(node)) {
					if(ssa instanceof SSAGetInstruction) {
						SSAGetInstruction ssaGet = (SSAGetInstruction)ssa;
						if(ssaGet.isStatic() == isStatic && ssaGet.toString().indexOf(confName) != -1) {
							int index = WALAUtils.getInstructionIndex(node, ssa);
							Statement s = new NormalStatement(node, index);
							return s;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	private CallGraphBuilder chooseCallGraphBuilder(AnalysisOptions options, AnalysisCache cache,
	      IClassHierarchy cha, AnalysisScope scope) {
	    CallGraphBuilder builder = null;
	    if(this.type == CG.ZeroCFA) {
			System.out.println("Using 0-CFA call graph");
			builder = Util.makeZeroCFABuilder(options, cache, cha, scope);
		} else if (this.type == CG.ZeroOneCFA) {
			System.out.println("Using 0-1-CFA call graph");
			builder = Util.makeVanillaZeroOneCFABuilder(options, cache, cha, scope);
		} else if (this.type == CG.ZeroContainerCFA) {
			System.out.println("Using 0-container-CFA call graph");
			builder = Util.makeVanillaZeroOneContainerCFABuilder(options, cache, cha, scope);
		} else if (this.type == CG.RTA) {
			System.out.println("Using RTA call graph");
			builder = Util.makeRTABuilder(options, cache, cha, scope);
		} else if (this.type == CG.ZeroOneContainerCFA) {
			System.out.println("Using 0-1-container-CFA call graph");
			builder = Util.makeZeroOneContainerCFABuilder(options, cache, cha, scope);
		} else if (this.type == CG.OneCFA) {
			System.out.println("Using 1-CFA call graph");
			builder = WALAUtils.makeOneCFABuilder(options,  cache, cha, scope);
		} else if (this.type == CG.TwoCFA) {
			System.out.println("Using 2-CFA call graph");
			builder = WALAUtils.makeCFABuilder(2, options,  cache, cha, scope);
		} else if (this.type == CG.CFA) { 
			if(this.cfaprecision < 2) {
			throw new RuntimeException("Please set cfa precision first.");
			}
			System.out.println("Use CFA with precision: " + this.cfaprecision);
			builder = WALAUtils.makeCFABuilder(this.cfaprecision, options,  cache, cha, scope);
		} else if (this.type == CG.TempZeroCFA) {
			System.out.println("Use Temp-0-CFA with 0 context precision ");
			builder = WALAUtils.makeCFABuilder(0, options, cache, cha, scope);
		}else {
			throw new RuntimeException("The CG type: " + type + " is unknonw");
		}
		assert builder != null;
		
		//add the bypass file
		if(this.byPassFile != null) {
			System.err.println("Use bypass file: " + this.byPassFile);
			Util.addBypassLogic(options, scope, Utils.class.getClassLoader(), this.byPassFile, cha);
		}
		
		return builder;
	}
	
	public static Statement getReturnStatementForCall(Statement s) {
	    if (s.getKind() == Kind.NORMAL) {
	      NormalStatement n = (NormalStatement) s;
	      SSAInstruction st = n.getInstruction();
	      if (st instanceof SSAInvokeInstruction) {
	        SSAAbstractInvokeInstruction call = (SSAAbstractInvokeInstruction) st;
	        if (call.getCallSite().getDeclaredTarget().getReturnType().equals(TypeReference.Void)) {
	          throw new IllegalArgumentException("this driver computes forward slices from the return value of calls.\n" + ""
	              + "Method " + call.getCallSite().getDeclaredTarget().getSignature() + " returns void.");
	        }
	        System.err.println("Use return value as slicing seed: " + s);
	        return new NormalReturnCaller(s.getNode(), n.getInstructionIndex());
	      } else {
	        return s;
	      }
	    } else {
	      return s;
	    }
	  }
	
	static Collection<IRStatement> convert(Collection<Statement> stmts) {
		Collection<IRStatement> irs = new LinkedList<IRStatement>();
		
		for(Statement s : stmts) {
			if(s instanceof StatementWithInstructionIndex) {
				if(s.getNode().getMethod() instanceof ShrikeBTMethod) {
					try {
				    IRStatement ir = new IRStatement((StatementWithInstructionIndex)s);
				        irs.add(ir);
					} catch (Throwable e) {
						//System.err.println("Error in IR: " + s);
						continue;
					}
			    } else {
			    	//skip fake method 
			    	//Log.logln("skip stmt: " + s + " in method: " + s.getNode().getClass());
			    }
			} else {
				//Log.logln("skip non-StatementWithInstructionIndex: " + s);
			}
		}
		
		return irs;
	}
}