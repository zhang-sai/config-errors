package edu.washington.cs.conf.analysis.evol.experiments;

import java.util.Collection;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfUtils;
import edu.washington.cs.conf.analysis.IRStatement;
import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.analysis.evol.CodeAnalyzer;
import edu.washington.cs.conf.analysis.evol.CodeAnalyzerRepository;
import edu.washington.cs.conf.analysis.evol.EvolConfOptionRepository;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema.TYPE;
import edu.washington.cs.conf.util.WALAUtils;
import junit.framework.TestCase;

public class TestFullSlicing extends TestCase {

	//make sure the cache file is correct, and the setting of cg construction, etc
	//OK
	public void testRandoopOld() {
		ConfEntityRepository rep = EvolConfOptionRepository.randoopOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.randoop121Path);
		rep.showAll();
		
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputsFullSlicing
		    (CodeAnalyzerRepository.randoop121Path,
				CodeAnalyzerRepository.randoopMain, 
				rep.getConfEntityList(), 
				"JavaAllExclusions.txt",
				CG.RTA,
				false,
				DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS,
				ControlDependenceOptions.NO_EXCEPTIONAL_EDGES,
				TYPE.SOURCE_PREDICATE
		    );
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
		
		String saveFileName = EvolConfOptionRepository.randoopOldVersionFullSlicing;
		TestOptionsAndSlicing.saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	//OK
	public void testRandoopNew() {
		ConfEntityRepository rep = EvolConfOptionRepository.randoopNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.randoop132Path);
		rep.showAll();
		
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputsFullSlicing
		      (CodeAnalyzerRepository.randoop132Path,
				CodeAnalyzerRepository.randoopMain, 
				rep.getConfEntityList(), 
				"JavaAllExclusions.txt",
				CG.RTA,
				false,
				DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS,
				ControlDependenceOptions.NO_EXCEPTIONAL_EDGES,
				TYPE.SOURCE_PREDICATE
				);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
		
		String saveFileName = EvolConfOptionRepository.randoopNewVersionFullSlicing;
		TestOptionsAndSlicing.saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	//OK
	public void testWekaOld() {
		ConfEntityRepository rep = EvolConfOptionRepository.wekaOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.oldWekaPath);
		rep.showAll();
		//test thin slicing
		
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputsFullSlicing
		       (CodeAnalyzerRepository.oldWekaPath,
				CodeAnalyzerRepository.wekaMainClass,
				rep.getConfEntityList(),
				"JavaAllExclusions.txt",
				CG.ZeroCFA, //CG.RTA,
				false,
				DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS,
				ControlDependenceOptions.NO_EXCEPTIONAL_EDGES,
				TYPE.SOURCE_PREDICATE
				);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
		
		String saveFileName = EvolConfOptionRepository.wekaOldVersionFullSlicing;
		TestOptionsAndSlicing.saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	//OK
	public void testWekaNew() {
		ConfEntityRepository rep = EvolConfOptionRepository.wekaNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.newWekaPath);
		rep.showAll();
		//test thin slicing
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs
		        (CodeAnalyzerRepository.newWekaPath,
				CodeAnalyzerRepository.wekaMainClass,
				rep.getConfEntityList(), 
				"JavaAllExclusions.txt",
				CG.ZeroCFA,
				false,
				DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS,
				ControlDependenceOptions.NO_EXCEPTIONAL_EDGES
				);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
//			if(output.conf.getConfName().equals("m_numFolds")) {
//				System.err.println(output.toString()); //XXX in
//			}
		}
		
		String saveFileName = EvolConfOptionRepository.wekaNewVersionFullSlicing;
		TestOptionsAndSlicing.saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	//OK
	public void testSynopticOld() {
		ConfEntityRepository rep = EvolConfOptionRepository.synopticOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.oldSynopticPath);
		rep.showAll();
		
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputsFullSlicing
		        (CodeAnalyzerRepository.oldSynopticPath,
				CodeAnalyzerRepository.synopticMainClass,
				rep.getConfEntityList(),
				"JavaAllExclusions.txt",
				CG.RTA,
				false,
				DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS,
				ControlDependenceOptions.NO_EXCEPTIONAL_EDGES,
				TYPE.SOURCE_PREDICATE
				);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
		
		String saveFileName = EvolConfOptionRepository.synopticOldVersionFullSlicing;
		TestOptionsAndSlicing.saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	//OK
	public void testSynopticNew() {
		ConfEntityRepository rep = EvolConfOptionRepository.synopticNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.newSynopticPath);
		rep.showAll();
		
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputsFullSlicing
		        (CodeAnalyzerRepository.newSynopticPath,
				CodeAnalyzerRepository.synopticMainClass,
				rep.getConfEntityList(),
				"SynopticExclusions.txt",
//				"JavaAllExclusions.txt",
				CG.RTA,
				false,
				DataDependenceOptions.NO_HEAP_NO_EXCEPTIONS,
				ControlDependenceOptions.NONE,
				TYPE.SOURCE_PREDICATE
				);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
//			if(output.conf.getConfName().equals("dumpInitialGraphDotFile")) {
//				System.err.println(output);
//			}
//			if(output.conf.getConfName().equals("dumpInitialGraphPngFile")) {
//				System.err.println(output);
//			}
		}
		
		String saveFileName = EvolConfOptionRepository.synopticNewVersionFullSlicing;
		TestOptionsAndSlicing.saveAndCheckSlicingResult(saveFileName, outputs);
	}

	public void testJMeterOld() {
		ConfEntityRepository rep = EvolConfOptionRepository.jmeterOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.getOldJMeterPath());
		rep.showAll();
		
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getJMeterOldAnalyzer();
		oldCoder.buildAnalysis();
		//use additional seeds
		oldCoder.slicer.setAddSliceSeedFromGet(true);
		oldCoder.slicer.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
		oldCoder.slicer.setControlDependenceOptions(ControlDependenceOptions.NO_EXCEPTIONAL_EDGES);
		//memorize the output
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs
		        (oldCoder.slicer,
		         rep,
		         false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
		
		String saveFileName = EvolConfOptionRepository.jmeterOldVersionFullSlicing;
		TestOptionsAndSlicing.saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	public void testJMeterNew() {
		ConfEntityRepository rep = EvolConfOptionRepository.jmeterNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.getNewJMeterPath());
		rep.showAll();
		
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getJMeterNewAnalyzer();
		newCoder.buildAnalysis();
		//use additional seeds
		newCoder.slicer.setAddSliceSeedFromGet(true);
		newCoder.slicer.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
		newCoder.slicer.setControlDependenceOptions(ControlDependenceOptions.NO_EXCEPTIONAL_EDGES);
		//memorize the output
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(newCoder.slicer, rep, false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
			for(IRStatement irs : output.statements) {
			    System.out.println("   " + irs);
			}
		}
		
		String saveFileName = EvolConfOptionRepository.jmeterNewVersionFullSlicing;
		TestOptionsAndSlicing.saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	//OK
	public void testJChordOld() {
		ConfEntityRepository rep = EvolConfOptionRepository.jchordOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.jChordOldPath);
		rep.showAll();

		//must use chord exclusion files!
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputsFullSlicing(
				CodeAnalyzerRepository.jChordOldPath,
				CodeAnalyzerRepository.chordMainClass,
				rep.getConfEntityList(),
				CodeAnalyzerRepository.chordExclusions,
				CG.RTA, //XX need change
				false,
				DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS,
				ControlDependenceOptions.NO_EXCEPTIONAL_EDGES,
				TYPE.SOURCE_PREDICATE
		        );
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
		
		String saveFileName = EvolConfOptionRepository.jchordOldVersionFullSlicing;
		TestOptionsAndSlicing.saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	public void testJChordNew() {
		ConfEntityRepository rep = EvolConfOptionRepository.jchordNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.jChordNewPath);
		rep.showAll();
		
		//must use chord exclusion files!
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputsFullSlicing(
				CodeAnalyzerRepository.jChordNewPath,
				CodeAnalyzerRepository.chordMainClass,
				rep.getConfEntityList(),
				CodeAnalyzerRepository.chordExclusions,
				CG.RTA, //XXX need change
				false,
				DataDependenceOptions.NO_HEAP_NO_EXCEPTIONS,
				ControlDependenceOptions.NONE,
				TYPE.SOURCE_PREDICATE
				);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
		
		String saveFileName = EvolConfOptionRepository.jchordNewVersionFullSlicing;
		TestOptionsAndSlicing.saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	public void testJavalancheOld() {
		ConfEntityRepository rep = EvolConfOptionRepository.javalancheOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.getJavalancheOldPath());
		rep.showAll();
		
		CodeAnalyzer coder = CodeAnalyzerRepository.getJavalancheOldAnalyzer();
		coder.slicer.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
		coder.slicer.setContextSensitive(false);
		coder.slicer.setControlDependenceOptions(ControlDependenceOptions.NO_EXCEPTIONAL_EDGES);
		coder.buildAnalysis();
//		coder.slicer.setContextSensitive(false);
		
		//need to build the cache
		System.out.println("Building cache for seed statements.");
		long start = System.currentTimeMillis();
		ConfUtils.buildCachedStatements(rep.getConfEntityList(), coder.getCallGraph(),
				new String[]{"de.unisb.cs.st.javalanche"});
		System.out.println("Time cost in building cache: "
				+ (System.currentTimeMillis() - start)/1000);
		
		//use all gets
		coder.slicer.setExtractAllGets(true);
		coder.slicer.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
		coder.slicer.setControlDependenceOptions(ControlDependenceOptions.NO_EXCEPTIONAL_EDGES);
		
		Collection<ConfPropOutput> outputs = 
			CommonUtils.getConfPropOutputs(coder.slicer,
				rep, false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
		
		String saveFileName = EvolConfOptionRepository.javalancheOldVersionFullSlicing;
		TestOptionsAndSlicing.saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	public void testJavalancheNew() {
		ConfEntityRepository rep = EvolConfOptionRepository.javalancheNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.getJavalancheNewPath());
		rep.showAll();
		
		CodeAnalyzer coder = CodeAnalyzerRepository.getJavalancheNewAnalyzer();
		coder.slicer.setCGType(CG.RTA);
		coder.slicer.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
		//use return statement
		coder.slicer.setUseReturnSeed(true);
		
		coder.buildAnalysis();
		coder.slicer.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
		coder.slicer.setControlDependenceOptions(ControlDependenceOptions.NO_EXCEPTIONAL_EDGES);
		
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(coder.slicer, rep, false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
		
		String saveFileName = EvolConfOptionRepository.javalancheNewVersionFullSlicing;
		TestOptionsAndSlicing.saveAndCheckSlicingResult(saveFileName, outputs);
	}
}
