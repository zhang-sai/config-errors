package edu.washington.cs.conf.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;


import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.core.tests.callGraph.CallGraphTestUtil;
import com.ibm.wala.examples.drivers.PDFSlice;
import com.ibm.wala.examples.drivers.PDFTypeHierarchy;
import com.ibm.wala.examples.properties.WalaExamplesProperties;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.LocalPointerKey;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.ipa.callgraph.propagation.cfa.nCFABuilder;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.SDG;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.properties.WalaProperties;
import com.ibm.wala.shrikeBT.ConditionalBranchInstruction;
import com.ibm.wala.shrikeBT.MethodData;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSACFG.BasicBlock;
import com.ibm.wala.ssa.SSAConditionalBranchInstruction;
import com.ibm.wala.ssa.SSAGetInstruction;
import com.ibm.wala.ssa.SSAGotoInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.collections.CollectionFilter;
import com.ibm.wala.util.collections.Filter;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.GraphSlicer;
import com.ibm.wala.util.io.FileProvider;
import com.ibm.wala.viz.DotUtil;
import com.ibm.wala.viz.PDFViewUtil;


public class WALAUtils {

	public static SSAPropagationCallGraphBuilder makeOneCFABuilder(
			AnalysisOptions options, AnalysisCache cache, IClassHierarchy cha,
			AnalysisScope scope) {
		return makeCFABuilder(1, options, cache, cha, scope);
	}
	
	public static SSAPropagationCallGraphBuilder makeCFABuilder(int n,
			AnalysisOptions options, AnalysisCache cache, IClassHierarchy cha,
			AnalysisScope scope) {

		if (options == null) {
			throw new IllegalArgumentException("options is null");
		}
		Util.addDefaultSelectors(options, cha);
		Util.addDefaultBypassLogic(options, scope, Util.class.getClassLoader(), cha);

		return new nCFABuilder(n, cha, options, cache, null, null);
	}
	
//	public static void viewCallGraph(Graph<CGNode> g, boolean flag) {
//		if(flag) {
//			logCallGraph(g);
//		}
//	}
	
	public static void logCallGraph(Graph<CGNode> g, boolean DEBUG) {
		if(DEBUG) {
			logCallGraph(g);
		}
	}
	
	public static void logCallGraph(Graph<CGNode> g) {
		 StringBuilder sb = new StringBuilder();
		    for(CGNode node : g) {
		    	{
		    	   sb.append("node: " + node);
		    	   sb.append(Globals.lineSep);
		    	   Iterator<CGNode> cgit = g.getSuccNodes(node);
		    	   while(cgit.hasNext()) {
		    		   sb.append("  calls: " + cgit.next());
		    		   sb.append(Globals.lineSep);
		    	   }
		    	}
		    }
		    Log.log(sb.toString());
	}
	
	  //FIXME buyer aware
	  public static Graph<CGNode> copy(Graph<CGNode> g) throws WalaException {
		  return pruneGraph(g, new AcceptAllFilter());
	  }
	  private static class AcceptAllFilter implements Filter<CGNode> {
			public boolean accepts(CGNode o) {
				return true;
			}
		}

	public static Graph<CGNode> pruneForAppLoader(CallGraph g)
			throws WalaException {
		return pruneGraph(g, new ApplicationLoaderFilter());
	}
	  
	  public static <T> Graph<T> pruneGraph(Graph<T> g, Filter<T> f) throws WalaException {
		    Collection<T> slice = GraphSlicer.slice(g, f);
		    return GraphSlicer.prune(g, new CollectionFilter<T>(slice));
	  }
	  
	  private static class ApplicationLoaderFilter implements Filter<CGNode> {

	    public boolean accepts(CGNode o) {
	      if (o instanceof CGNode) {
	        CGNode n = (CGNode) o;
//	        System.out.println("processing: " + n.getMethod().getDeclaringClass());
	        return n.getMethod().getDeclaringClass().getClassLoader().getReference().equals(ClassLoaderReference.Application);
	      } else if (o instanceof LocalPointerKey) {
	        LocalPointerKey l = (LocalPointerKey) o;
	        return accepts(l.getNode());
	      } else {
	        return false;
	      }
	    }
	  }
	  
	  //give method full name like a.b.c.ClassName.method, return the CGNode
	  public static Collection<CGNode> lookupCGNode(Graph<CGNode> cg, String fullName) {
		  Collection<CGNode> nodes = new LinkedHashSet<CGNode>();
		  
		  for(CGNode node : cg) {
			  String fullMethodName = WALAUtils.getFullMethodName(node.getMethod());
			  if(fullName.equals(fullMethodName)) {
				  nodes.add(node);
			  }
		  }
		  
		  return nodes;
	  }
	  
	  public static CGNode lookupMatchedCGNode(Graph<CGNode> cg, String signature) {
		  for(CGNode node : cg) {
			  if(node.getMethod().getSignature().equals(signature)) {
				  return node;
			  }
		  }
		  return null;
	  }
	  
	  public static Set<IClass> getAllAppClasses(ClassHierarchy cha) {
		  Set<IClass> set = new LinkedHashSet<IClass>();
		  for(IClass c : cha) {
			  if(c.getClassLoader().getReference().equals(ClassLoaderReference.Application)) {
	    			set.add(c);
	    		}
		  }
		  return set;
	  }
	  
	  public static boolean isAppClass(IClass c) {
		  return c.getClassLoader().getReference().equals(ClassLoaderReference.Application);
	  }
	  
	  public static Set<String> getAllAppClassNames(ClassHierarchy cha) {
		  Set<IClass> set = getAllAppClasses(cha);
		  Set<String> names = new LinkedHashSet<String>();
		  for(IClass c : set) {
			  names.add(WALAUtils.getJavaFullClassName(c));
		  }
		  return names;
	  }
	  
	  public static Iterable<Entrypoint> createEntrypointsFromAllMethods(String className, ClassHierarchy cha) {
		  final HashSet<Entrypoint> result = HashSetFactory.make();
          for(IClass c : cha) {
                  if(c.getClassLoader().getReference().equals(ClassLoaderReference.Application)) {
                          String fullClassName = WALAUtils.getJavaFullClassName(c);
                          if(!fullClassName.equals(className)) {
                        	  continue;
                          }
                          for(IMethod m : c.getDeclaredMethods()) {
//                                  if(m.isPublic()) {
                                          Entrypoint ep = new DefaultEntrypoint(m, cha);
                                          result.add(ep);
//                                  }
                          }
                  }
          }
          
          return result;
	  }
	  
	  //a.b.c,  methodName is a token
	  public static Iterable<Entrypoint> createEntrypoints(String className, String methodName, ClassHierarchy cha) {
		  final HashSet<Entrypoint> result = HashSetFactory.make();
          for(IClass c : cha) {
                  if(c.getClassLoader().getReference().equals(ClassLoaderReference.Application)) {
                          String fullClassName = WALAUtils.getJavaFullClassName(c);
                          if(!fullClassName.equals(className)) {
                        	  continue;
                          }
                          for(IMethod m : c.getDeclaredMethods()) {
                                  if(m.getName().toString().equals(methodName)) {
                                          Entrypoint ep = new DefaultEntrypoint(m, cha);
                                          result.add(ep);
                                  }
                          }
                  }
          }
          
          return result;
	  }
	  
	  //given class name like a.b.c.d
	  public static IClass lookupClass(ClassHierarchy cha, String classFullName) {
		  for(IClass c : cha) {
				String fullName = WALAUtils.getJavaFullClassName(c);
				if(fullName.equals(classFullName)) {
					return c;
				}
			}
			return null;
	  }
	  
	  //return a.b.c.d.MethodName
	  public static String getFullMethodName(IMethod method) {
		  String className = getJavaFullClassName(method.getDeclaringClass());
		  return className + "." + method.getName().toString();
	  }
		
		//return like a.b.c.d
		public static String getJavaFullClassName(IClass clazz) {
			TypeName tn = clazz.getName();
			String packageName = (tn.getPackage() == null ? "" : tn.getPackage().toString() + ".");
			String clazzName = tn.getClassName().toString();
			return Utils.translateSlashToDot(packageName) + clazzName;
		}
		
		//return like a.b.c  or "" for default package
		public static String getJavaPackageName(IClass clazz) {
			TypeName tn = clazz.getName();
			String packageName = tn.getPackage() == null ? "" : tn.getPackage().toString();
			return Utils.translateSlashToDot(packageName);
		}
		
		public static boolean isClassInPackages(CGNode node, String[] packages) {
			return isClassInPackages(node.getMethod().getDeclaringClass(), packages);
		}
		
		public static boolean isClassInPackages(IClass clazz, String[] packages) {
			String packageName = getJavaPackageName(clazz);
			boolean isIn = false;
			for(String p : packages) {
				if(packageName.startsWith(p)) {
					isIn = true;
					break;
				}
			}
			return isIn;
		}
		
		//dump all classes
		public static void dumpClasses(ClassHierarchy cha, String fileName) {
			StringBuilder sb = new StringBuilder();
			int count = 0;
			for(IClass c : cha) {
				sb.append(c);
				sb.append(Globals.lineSep);
				count++;
			}
			sb.append("Number in total: " + count);
			try {
				Files.writeToFile(sb.toString(), fileName);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		//also remove repetition
		public static Collection<String> convertIClassToStrings(Collection<IClass> coll) {
			Collection<String> strs = new LinkedHashSet<String>();
			for(IClass ic : coll) {
				strs.add(WALAUtils.getJavaFullClassName(ic));
			}
			return strs;
		}
		
		public static Set<String> getUnloadedClasses(ClassHierarchy cha, Collection<String> jars) throws IOException {
			Set<String> unloadedClasses = new LinkedHashSet<String>();
			for(String jarFile : jars) {
				unloadedClasses.addAll(getUnloadedClasses(cha, jarFile));
			}
			return unloadedClasses;
		}
		
		public static Set<String> getUnloadedClasses(ClassHierarchy cha, String jarFile) throws IOException {
			assert (jarFile != null && jarFile.endsWith(".jar"));
	    	
	    	Set<String> classInJar = new LinkedHashSet<String>();
		    JarFile file = new JarFile(new File(jarFile));
		    for (Enumeration<JarEntry> e = file.entries(); e.hasMoreElements();) {
		        ZipEntry Z = (ZipEntry) e.nextElement();
		        String entryName = Z.toString();
		        if(entryName.endsWith(".class")) {
		            String classFileName = Utils.translateSlashToDot(entryName);
		            String className = classFileName.substring(0, classFileName.length() - ".class".length());
		            classInJar.add(className);
		        }
		    }
		    //all loaded class
		    Set<String> loadedClasses = new LinkedHashSet<String>();
		    for(IClass c : cha) {
		    	loadedClasses.add(iclassToClassName(c));
		    }
		    Set<String> unloadedClasses = new LinkedHashSet<String>();
		    for(String cj : classInJar) {
		    	if(!loadedClasses.contains(cj)) {
		    		unloadedClasses.add(cj);
		    	}
		    }
	        return unloadedClasses;
		}

	    //check all class in a given jar are all loaded by Wala
	    public static int getUnloadedClassNum(ClassHierarchy cha, String jarFile) throws IOException {
	    	return getUnloadedClasses(cha, jarFile).size();
	    }
	    
	    public static String iclassToClassName(IClass c) {
	    	TypeName tn = c.getName();
	    	String packageName = Utils.translateSlashToDot(tn.getPackage() == null ? "" : tn.getPackage().toString() + ".");
	    	String className = tn.getClassName().toString();
	    	return packageName + className;
	    }
	    
	    public static String javaClassToWalaClass(String javaFullClassName) {
	    	return "L" + Utils.translateDotToSlash(javaFullClassName);
	    }
	    
	    //return La/b/c.name sig
		public static String getMethodSignature(MethodData d) {
			  String sig = d.getSignature();
			  String name = d.getName();
			  String jvmClassName = d.getClassType();
			  Utils.checkTrue(jvmClassName.startsWith("L"));
			  Utils.checkTrue(jvmClassName.endsWith(";"));
			  String javaClassName =  Utils.translateSlashToDot(jvmClassName.substring(1, jvmClassName.length() - 1));
			  return javaClassName + "." + name + sig;
		  }
		
		public static String getFullMethodNameNoSig(MethodData d) {
			  String name = d.getName();
			  String jvmClassName = d.getClassType();
			  Utils.checkTrue(jvmClassName.startsWith("L"));
			  Utils.checkTrue(jvmClassName.endsWith(";"));
			  String javaClassName =  Utils.translateSlashToDot(jvmClassName.substring(1, jvmClassName.length() - 1));
			  return javaClassName + "." + name;
		  }
	    
	    //utilities for ir
	    public static String getAllIRAsString(CGNode node) {
	    	StringBuilder sb = new StringBuilder();
	    	List<SSAInstruction> list = getAllIRs(node);
	    	int count = 0;
	    	for(SSAInstruction ssa : list) {
	    		sb.append(count);
	    		sb.append(". ");
	    		sb.append(ssa);
	    		
	    		//append some explanation afterward
	    		if(ssa != null && ssa.getNumberOfUses() > 0) {
	    			SymbolTable st = node.getIR().getSymbolTable();
	    			sb.append("  ==> ");
	    		    for(int i = 0; i < ssa.getNumberOfUses(); i++) {
	    		    	int useId = ssa.getUse(i);
	    			    if(st.isConstant(useId)) {
	    			    	sb.append(useId);
		    			    sb.append(" is: ");
	    			    	sb.append("const: " + st.getValue(useId));
	    			    }
	    		    }
	    		}
	    		
	    		sb.append(Globals.lineSep);
	    		count++;
	    	}
	    	return sb.toString();
	    }
	    
	    public static List<SSAInstruction> getAllIRs(CGNode node) {
	    	List<SSAInstruction> list = new LinkedList<SSAInstruction>();
	    	SSAInstruction[] instructions = node.getIR().getInstructions();
	    	for(SSAInstruction ins : instructions) {
	    		list.add(ins);
	    	}
	    	return list;
	    }
	    
	    public static List<SSAInstruction> getAllNotNullIRs(CGNode node) {
	    	List<SSAInstruction> list = new LinkedList<SSAInstruction>();
	    	SSAInstruction[] instructions = node.getIR().getInstructions();
	    	for(SSAInstruction ins : instructions) {
	    		if(ins == null) {
	    			continue;
	    		}
	    		list.add(ins);
	    	}
	    	return list;
	    }
	    
	    public static List<ISSABasicBlock> getAllBasicBlocks(CGNode node) {
	    	List<ISSABasicBlock> bbList = new LinkedList<ISSABasicBlock>();
	    	
	    	Iterator<ISSABasicBlock> iter = node.getIR().getControlFlowGraph().iterator();
	    	while(iter.hasNext()) {
	    		bbList.add(iter.next());
	    	}
	    	
	    	return bbList;
	    }
	    
	    //only for debugging purpose
	    public static String getAllBasicBlockIDList(Collection<ISSABasicBlock> coll) {
	    	StringBuilder sb = new StringBuilder();
	    	
	    	sb.append("[");
	    	for(ISSABasicBlock bb : coll) {
	    		if(sb.length() > 1) {
	    		    sb.append(", ");
	    		}
	    		sb.append(bb.getNumber());
	    	}
	    	sb.append("]");
	    	
	    	return sb.toString();
	    }
	    
	    public static List<ISSABasicBlock> getSuccBasicBlocks(CGNode node, ISSABasicBlock bb) {
	    	Utils.checkTrue(node.getMethod().equals(bb.getMethod()));
	    	SSACFG cfg = node.getIR().getControlFlowGraph();
	    	List<ISSABasicBlock> bbList = new LinkedList<ISSABasicBlock>();
	    	
	    	Iterator<ISSABasicBlock> succIter = cfg.getSuccNodes(bb);
	    	while(succIter.hasNext()) {
	    		bbList.add(succIter.next());
	    	}
	    	
	    	return bbList;
	    }
	    
	    public static List<ISSABasicBlock> getPredBasicBlocks(CGNode node, ISSABasicBlock bb) {
	    	SSACFG cfg = node.getIR().getControlFlowGraph();
	    	List<ISSABasicBlock> predBBs = new LinkedList<ISSABasicBlock>();
	    	Iterator<ISSABasicBlock> predIter = cfg.iterator();
			while(predIter.hasNext()) {
				ISSABasicBlock currBB = predIter.next();
				Iterator<ISSABasicBlock> succIter = cfg.getSuccNodes(currBB);
				while(succIter.hasNext()) {
					if(succIter.next().equals(bb)) {
						predBBs.add(currBB);
						break;
					}
				}
			}
			return predBBs;
	    }
	    
	    public static ISSABasicBlock getHostBasicBlock(CGNode node, SSAInstruction ssa) {
	    	ISSABasicBlock bb = null;
	    	SSACFG cfg = node.getIR().getControlFlowGraph();
			Iterator<ISSABasicBlock> iter = cfg.iterator();
			while(iter.hasNext()) {
				ISSABasicBlock tmpBlock = iter.next();
				Iterator<SSAInstruction> instIter = tmpBlock.iterator();
				while(instIter.hasNext()) {
					if(instIter.next().equals(ssa)) {
						bb = tmpBlock;
						break;
					}
				}
				if(bb != null) {
					break;
				}
			}
			return bb;
	    }
	    
	    public static boolean containNode(CallGraph cg, CGNode node) {
			Utils.checkNotNull(node, "cannot be null ");
			for(CGNode n : cg) {
				if(n.equals(node)){
					return true;
				}
			}
			return false;
		}
	    
	    public static void printAllIRs(CGNode node) {
	    	for(SSAInstruction ssa : WALAUtils.getAllNotNullIRs(node)) {
	    		System.out.println(ssa + ", type: " + ssa.getClass());
//	    		if(ssa instanceof ConditionalBranchInstruction) {
//	    			System.out.println(((ConditionalBranchInstruction)ssa).getTarget());
//	    		}
	    	}
	    }
	    
	    public static void printAllIRsWithIndices(CGNode node) {
	    	List<SSAInstruction> list = WALAUtils.getAllIRs(node);
	    	for(int i = 0; i < list.size(); i++) {
	    		SSAInstruction ssa = list.get(i);
	    		System.out.println(i + ", " + ssa);
//	    		if(ssa instanceof SSAGetInstruction) {
//	    			System.out.println("   :" + ((SSAGetInstruction)ssa).getDeclaredField().getDeclaringClass().getName());
//	    		}
	    	}
	    }
	    
	    public static void printBasicBlocks(Collection<ISSABasicBlock> bbs) {
	    	for(ISSABasicBlock bb : bbs) {
	    		printBasicBlock(bb);
	    	}
	    }
	    
	    public static void printBasicBlock(ISSABasicBlock bb) {
	    	System.out.println(bb);
    		Iterator<SSAInstruction> iter_ssa = bb.iterator();
    		while(iter_ssa.hasNext()) {
    			System.out.println("    " + iter_ssa.next());
    			System.out.println();
    		}
	    }
	    
	    public static void printCFG(CGNode node) {
	    	SSACFG cfg = node.getIR().getControlFlowGraph();
	    	
	    	//first show all basic blocks
	    	BasicBlock bb = cfg.entry();
	    	Iterator<ISSABasicBlock> iter = cfg.iterator();
	    	while(iter.hasNext()) {
	    		ISSABasicBlock issabb = iter.next();
	    		System.out.println(issabb);
//	    		System.out.println(issabb.getClass());
	    		Iterator<SSAInstruction> iter_ssa = issabb.iterator();
	    		while(iter_ssa.hasNext()) {
	    			System.out.println("    " + iter_ssa.next());
	    			System.out.println();
	    		}
	    		
	    	}
//	    	System.out.println(cfg);
	    	
	    	bb = cfg.entry();
	    	List<ISSABasicBlock> list = new LinkedList<ISSABasicBlock>();
	    	Set<ISSABasicBlock> visited = new LinkedHashSet<ISSABasicBlock>();
	    	list.add(bb);
	    	while(!list.isEmpty()) {
	    		ISSABasicBlock top = list.remove(0);
	    		if(visited.contains(top)) {
	    			continue;
	    		}
	    		visited.add(top);
	    		System.out.println(top.getNumber());
	    		Iterator<ISSABasicBlock> nextIter = cfg.getSuccNodes(top);
	    		while(nextIter.hasNext()) {
	    			ISSABasicBlock nextBb = nextIter.next();
	    			System.out.println("  --> " + nextBb.getNumber());
	    			list.add(nextBb);
	    		}
	    	}
	    }
	    
	    public static int getBasicBlockSize(CGNode node, SSAInstruction ssa) {
			ISSABasicBlock bb = node.getIR().getBasicBlockForInstruction(ssa);
			return getBasicBlockSize(bb);
		}
	    
	    public static int getBasicBlockSize(ISSABasicBlock bb) {
	    	return WALAUtils.getAllIRs(bb).size();
	    }
	    
	    public static List<SSAInstruction> getAllIRs(ISSABasicBlock bb) {
	    	List<SSAInstruction> list = new LinkedList<SSAInstruction>();
	    	Iterator<SSAInstruction> iter = bb.iterator();
	    	while(iter.hasNext()) {
	    		list.add(iter.next());
	    	}
	    	return list;
	    }
	    
	    public static String getAllIRsString(ISSABasicBlock bb) {
	    	StringBuilder sb = new StringBuilder();
	    	List<SSAInstruction> list = getAllIRs(bb);
	    	for(SSAInstruction ssa : list) {
	    		sb.append(ssa.toString());
	    		sb.append(Globals.lineSep);
	    	}
	    	return sb.toString();
	    }
	    
	    public static SSAInstruction getInstruction(CGNode node, int index) {
	    	return node.getIR().getInstructions()[index];
	    }
	    
	    public static int getInstructionIndex(CGNode node, SSAInstruction instr) {
	    	int index = -1;
	    	
	    	SSAInstruction[] instructions = node.getIR().getInstructions();
	    	for(int i = 0; i < instructions.length; i++) {
	    		if(instr == instructions[i]) {
	    			index = i;
	    			break;
	    		}
	    	}
	    	
	    	return index;
	    }
		
		public static void dumpSlice(Collection<Statement> slice, PrintWriter w) {
			w.println("SLICE:\n");
			int i = 1;
			for (Statement s : slice) {
				int line_num = getStatementLineNumber(s);
				if(line_num != -1) {
				    String line = (i++) + "   " + s + ",  line num: " + line_num;
				    w.println(line);
				    w.flush();
				}
			}
		}
	    
	    public static int getStatementLineNumber(Statement s) {
			int lineNum = -1;
			if (s.getKind() == Statement.Kind.NORMAL) { // ignore special kinds of statements
				  int bcIndex, instructionIndex = ((NormalStatement) s).getInstructionIndex();
				  try {
				    bcIndex = ((ShrikeBTMethod) s.getNode().getMethod()).getBytecodeIndex(instructionIndex);
				    try {
				      int src_line_number = s.getNode().getMethod().getLineNumber(bcIndex);
				      lineNum = src_line_number;
				      //System.err.println ( "Source line number = " + src_line_number );
				    } catch (Exception e) {
				      System.err.println("Bytecode index no good");
				      System.err.println(e.getMessage());
				    }
				  } catch (Exception e ) {
				    System.err.println("it's probably not a BT method (e.g. it's a fakeroot method)");
				    System.err.println(e.getMessage());
				  }
			}
			return lineNum;
		}
	    
	    /**
	     * check if a given class is a Runnable
	     * */
	    private static IClass RUNNABLE = null;
	    public static IClass getRunnable(ClassHierarchy cha) {
	    	if(RUNNABLE == null) {
	    		RUNNABLE = WALAUtils.lookupClass(cha, "java.lang.Runnable");
	    	}
	    	if(RUNNABLE == null) {
	    		throw new Error("No runnable loaded.");
	    	}
	    	return RUNNABLE;
	    }
	    public static boolean isRunnable(ClassHierarchy cha, IClass c) {
	    	IClass runnable = getRunnable(cha);
	    	return cha.isAssignableFrom(runnable, c);
	    }
	    public static Collection<IClass> getRunnablesInApp(ClassHierarchy cha) {
	    	Collection<IClass> cs = new LinkedHashSet<IClass>();
	    	//only count client code
	    	for(IClass c : cha) {
	    		if(c.getClassLoader().getReference().equals(ClassLoaderReference.Application)) {
	    			cs.add(c);
	    		}
	    	}
	    	return cs;
	    }
	    
	    /**
	     * filter a collection node by package names
	     * */
	    public static Collection<CGNode> filterCGNodeByPackages(Collection<CGNode> nodes, String[] packages) {
	    	if(packages == null) {
	    		throw new RuntimeException("The package name can not be null.");
	    	}
	    	Collection<CGNode> filteredNodes = new LinkedHashSet<CGNode>();
	    	for(CGNode node : nodes) {
	    		IMethod method = node.getMethod();
	    		String packageName = getJavaPackageName(method.getDeclaringClass());
	    		boolean retained = false;
	    		for(String pName : packages) {
	    			if(packageName.startsWith(pName)) {
	    				retained = true;
	    				break;
	    			}
	    		}
	    		if(retained) {
	    			filteredNodes.add(node);
	    		}
	    	}
	    	
	    	return filteredNodes;
	    }
	    
	    public static void viewSDG(SDG sdg, Collection<Statement> slice,
			String pdfFile) throws WalaException {
	    	// create a view of the SDG restricted to nodes in the slice
			Graph<Statement> g = pruneSDG(sdg, slice);

			System.out.println("Number of statements: " + g.getNumberOfNodes());

			// load Properties from standard WALA and the WALA examples project
			Properties p = null;
			try {
				p = WalaExamplesProperties.loadProperties();
				p.putAll(WalaProperties.loadProperties());
			} catch (WalaException e) {
				e.printStackTrace();
				Assertions.UNREACHABLE();
			}
			// create a dot representation.
			String psFile = p.getProperty(WalaProperties.OUTPUT_DIR)
					+ File.separatorChar + pdfFile;
			String dotExe = p.getProperty(WalaExamplesProperties.DOT_EXE);
			DotUtil.dotify(g, PDFSlice.makeNodeDecorator(),
					PDFTypeHierarchy.DOT_FILE, psFile, dotExe);

			// fire off the PDF viewer
			String gvExe = p.getProperty(WalaExamplesProperties.PDFVIEW_EXE);
			PDFViewUtil.launchPDFView(psFile, gvExe);
	    }

	    public static Graph<Statement> pruneSDG(SDG sdg,
			final Collection<Statement> slice) {
	    	Filter<Statement> f = new Filter<Statement>() {
				public boolean accepts(Statement o) {
					// return true;
					return slice.contains(o);
				}
			};
			return GraphSlicer.prune(sdg, f);
	    }
	    
	    public static Collection<NormalStatement> extractNormalStatements(Collection<Statement> coll) {
	    	Collection<NormalStatement> normalStmts = new LinkedList<NormalStatement>();
	    	for(Statement s : coll) {
	    		if(s instanceof NormalStatement) {
	    			normalStmts.add((NormalStatement)s);
	    		}
	    	}
	    	return normalStmts;
	    }
}