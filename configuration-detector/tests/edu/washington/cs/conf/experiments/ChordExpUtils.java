package edu.washington.cs.conf.experiments;

import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;

public class ChordExpUtils {
	
	public static List<ConfEntity> getSampleConfList() {
		ConfEntity entity10 = new ConfEntity("chord.project.Config", "scopeKind", true);
		List<ConfEntity> list = new LinkedList<ConfEntity>();
		list.add(entity10);
		return list;
	}
	
	public static ConfEntityRepository getChordRepository() {
		List<ConfEntity> jchordConfList = ChordExpUtils.getChordConfList();
		ConfEntityRepository repo = new ConfEntityRepository(jchordConfList);
		return repo;
	}
	
	public static List<ConfEntity> getChordConfList() {
		ConfEntity entity1 = new ConfEntity("chord.project.Config", "maxHeap", true);
		ConfEntity entity2 = new ConfEntity("chord.project.Config", "maxStack", true);
		ConfEntity entity3 = new ConfEntity("chord.project.Config", "jvmargs", true);
		ConfEntity entity4 = new ConfEntity("chord.project.Config", "workDirName", true);
		ConfEntity entity5 = new ConfEntity("chord.project.Config", "mainClassName", true);
		ConfEntity entity6 = new ConfEntity("chord.project.Config", "traceFileName", true);
		ConfEntity entity7 = new ConfEntity("chord.project.Config", "srcPathName", true);
		ConfEntity entity8 = new ConfEntity("chord.project.Config", "runIDs", true);
		ConfEntity entity9 = new ConfEntity("chord.project.Config", "runtimeJvmargs", true);
		ConfEntity entity10 = new ConfEntity("chord.project.Config", "scopeKind", true);
		ConfEntity entity11 = new ConfEntity("chord.project.Config", "reflectKind", true);
		ConfEntity entity12 = new ConfEntity("chord.project.Config", "CHkind", true);
		ConfEntity entity13 = new ConfEntity("chord.project.Config", "doSSA", true);
		ConfEntity entity14 = new ConfEntity("chord.project.Config", "scopeStdExcludeStr", true);
		ConfEntity entity15 = new ConfEntity("chord.project.Config", "scopeExtExcludeStr", true);
		ConfEntity entity16 = new ConfEntity("chord.project.Config", "scopeExcludeStr", true);
		ConfEntity entity17 = new ConfEntity("chord.project.Config", "checkStdExcludeStr", true);
		ConfEntity entity18 = new ConfEntity("chord.project.Config", "checkExtExcludeStr", true);
		ConfEntity entity19 = new ConfEntity("chord.project.Config", "checkExcludeStr", true);
		ConfEntity entity20 = new ConfEntity("chord.project.Config", "buildScope", true);
		ConfEntity entity21 = new ConfEntity("chord.project.Config", "useJvmti", true);
		ConfEntity entity22 = new ConfEntity("chord.project.Config", "printClasses", true);
		ConfEntity entity23 = new ConfEntity("chord.project.Config", "printAllClasses", true);
		ConfEntity entity24 = new ConfEntity("chord.project.Config", "userClassesDirName", true);
		ConfEntity entity25 = new ConfEntity("chord.project.Config", "instrSchemeFileName", true);
		ConfEntity entity26 = new ConfEntity("chord.project.Config", "printResults", true);
		ConfEntity entity27 = new ConfEntity("chord.project.Config", "saveDomMaps", true);
		ConfEntity entity28 = new ConfEntity("chord.project.Config", "verbose", true);
		ConfEntity entity29 = new ConfEntity("chord.project.Config", "classic", true);
		ConfEntity entity30 = new ConfEntity("chord.project.Config", "stdJavaAnalysisPathName", true);
		ConfEntity entity31 = new ConfEntity("chord.project.Config", "extJavaAnalysisPathName", true);
		ConfEntity entity32 = new ConfEntity("chord.project.Config", "javaAnalysisPathName", true);
		ConfEntity entity33 = new ConfEntity("chord.project.Config", "stdDlogAnalysisPathName", true);
		ConfEntity entity34 = new ConfEntity("chord.project.Config", "extDlogAnalysisPathName", true);
		ConfEntity entity35 = new ConfEntity("chord.project.Config", "dlogAnalysisPathName", true);
		ConfEntity entity36 = new ConfEntity("chord.project.Config", "runAnalyses", true);
		ConfEntity entity37 = new ConfEntity("chord.project.Config", "instrKind", true);
		ConfEntity entity38 = new ConfEntity("chord.project.Config", "traceKind", true);
		ConfEntity entity39 = new ConfEntity("chord.project.Config", "traceBlockSize", true);
		ConfEntity entity40 = new ConfEntity("chord.project.Config", "dynamicHaltOnErr", true);
		ConfEntity entity41 = new ConfEntity("chord.project.Config", "dynamicTimeout", true);
		ConfEntity entity42 = new ConfEntity("chord.project.Config", "maxConsSize", true);
		ConfEntity entity43 = new ConfEntity("chord.project.Config", "reuseScope", true);
		ConfEntity entity44 = new ConfEntity("chord.project.Config", "reuseRels", true);
		ConfEntity entity45 = new ConfEntity("chord.project.Config", "reuseTraces", true);
		ConfEntity entity46 = new ConfEntity("chord.project.Config", "useBuddy", true);
		ConfEntity entity47 = new ConfEntity("chord.project.Config", "bddbddbMaxHeap", true);
		ConfEntity entity48 = new ConfEntity("chord.project.Config", "outDirName", true);
		ConfEntity entity49 = new ConfEntity("chord.project.Config", "outFileName", true);
		ConfEntity entity50 = new ConfEntity("chord.project.Config", "errFileName", true);
		ConfEntity entity51 = new ConfEntity("chord.project.Config", "reflectFileName", true);
		ConfEntity entity52 = new ConfEntity("chord.project.Config", "methodsFileName", true);
		ConfEntity entity53 = new ConfEntity("chord.project.Config", "classesFileName", true);
		ConfEntity entity54 = new ConfEntity("chord.project.Config", "bddbddbWorkDirName", true);
		ConfEntity entity55 = new ConfEntity("chord.project.Config", "bootClassesDirName", true);
		ConfEntity entity56 = new ConfEntity("chord.project.Config", "printRels", true);
		ConfEntity entity57 = new ConfEntity("chord.project.Config", "printProject", true);
		ConfEntity entity58 = new ConfEntity("chord.project.Config", "userClassPathName", true);
		
		//for datarace detection
		ConfEntity entity59 = new ConfEntity("chord.analyses.datarace.RelExcludeInitMethods", "init", true);
		ConfEntity entity60 = new ConfEntity("chord.analyses.datarace.RelExcludeSameThread", "eqth", true);
		ConfEntity entity61 = new ConfEntity("chord.analyses.datarace.DataraceAnalysis", "excludeParallel", true);
		ConfEntity entity62 = new ConfEntity("chord.analyses.datarace.DataraceAnalysis", "excludeEscaping", true);
		ConfEntity entity63 = new ConfEntity("chord.analyses.datarace.DataraceAnalysis", "excludeNongrded", true);
		
		//for deadlock detection
		ConfEntity entity64 = new ConfEntity("chord.analyses.deadlock.DeadlockAnalysis", "excludeParallel", true);
		ConfEntity entity65 = new ConfEntity("chord.analyses.deadlock.DeadlockAnalysis", "excludeEscaping", true);
		ConfEntity entity66 = new ConfEntity("chord.analyses.deadlock.DeadlockAnalysis", "excludeNonreent", true);
		ConfEntity entity67 = new ConfEntity("chord.analyses.deadlock.DeadlockAnalysis", "excludeNongrded", true);

		//from RelExtraEntryPoints
		ConfEntity entity68 = new ConfEntity("chord.analyses.method.RelExtraEntryPoints", "extraMethodsFile", true);
		ConfEntity entity69 = new ConfEntity("chord.analyses.method.RelExtraEntryPoints", "extraMethodsList", true);
		
		//from Chord program
		ConfEntity entity70 = new ConfEntity("chord.program.Program", "runBefore", true);
		ConfEntity entity71 = new ConfEntity("chord.program.Program", "reflectexclude",  true);
		
		//from basic dynamic analysis
		ConfEntity entity72 = new ConfEntity("chord.project.analyses.BasicDynamicAnalysis", "runBefore", true);
		ConfEntity entity73 = new ConfEntity("chord.util.Execution", "execName", true);
		
		ConfEntity entity74 = new ConfEntity("chord.analyses.alloc.DomH", "phanton_classes", true);
		
		ConfEntity entity75 = new ConfEntity("chord.project.analyses.rhs.RHSAnalysis", "timeout", true);
		
		ConfEntity entity76 = new ConfEntity("chord.analyses.argret.DomK", "MAXZ", true);
		
		ConfEntity entity77 = new ConfEntity("chord.analyses.alias.CtxtsAnalysis", "kobj_K", true);
		ConfEntity entity78 = new ConfEntity("chord.analyses.alias.CtxtsAnalysis", "kcfa_K", true);
		ConfEntity entity79 = new ConfEntity("chord.analyses.alias.CtxtsAnalysis", "ctxtKind", true);
		
		
		List<ConfEntity> list = new LinkedList<ConfEntity>();
		
		list.add(entity1);
		list.add(entity2);
		list.add(entity3);
		list.add(entity4);
		list.add(entity5);
		list.add(entity6);
		list.add(entity7);
		list.add(entity8);
		list.add(entity9);
		list.add(entity10);
		list.add(entity11);
		list.add(entity12);
		list.add(entity13);
		list.add(entity14);
		list.add(entity15);
		list.add(entity16);
		list.add(entity17);
		list.add(entity18);
		list.add(entity19);
		list.add(entity20);
		list.add(entity21);
		list.add(entity22);
		list.add(entity23);
		list.add(entity24);
		list.add(entity25);
		list.add(entity26);
		list.add(entity27);
		list.add(entity28);
		list.add(entity29);
		list.add(entity30);
		list.add(entity31);
		list.add(entity32);
		list.add(entity33);
		list.add(entity34);
		list.add(entity35);
		list.add(entity36);
		list.add(entity37);
		list.add(entity38);
		list.add(entity39);
		list.add(entity40);
		list.add(entity41);
		list.add(entity42);
		list.add(entity43);
		list.add(entity44);
		list.add(entity45);
		list.add(entity46);
		list.add(entity47);
		list.add(entity48);
		list.add(entity49);
		list.add(entity50);
		list.add(entity51);
		list.add(entity52);
		list.add(entity53);
		list.add(entity54);
		list.add(entity55);
		list.add(entity56);
		list.add(entity57);
		list.add(entity58);
		list.add(entity59);
		list.add(entity60);
		list.add(entity61);
		list.add(entity62);
		list.add(entity63);
		list.add(entity64);
		list.add(entity65);
		list.add(entity66);
		list.add(entity67);
		list.add(entity68);
		list.add(entity69);
		list.add(entity70);
		list.add(entity71);
		list.add(entity72);
//		list.add(entity73);
		list.add(entity74);
//		list.add(entity75);
		list.add(entity76);
		list.add(entity77);
		list.add(entity78);
		list.add(entity79);
		
		return list;
	}
}
