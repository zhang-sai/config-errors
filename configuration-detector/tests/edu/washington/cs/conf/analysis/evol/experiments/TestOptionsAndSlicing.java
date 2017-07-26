package edu.washington.cs.conf.analysis.evol.experiments;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.SSAGetInstruction;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfUtils;
import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.analysis.IRStatement;
import edu.washington.cs.conf.analysis.ShrikePoint;
import edu.washington.cs.conf.analysis.evol.CodeAnalyzer;
import edu.washington.cs.conf.analysis.evol.CodeAnalyzerRepository;
import edu.washington.cs.conf.analysis.evol.EvolConfOptionRepository;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentSchema.TYPE;
import edu.washington.cs.conf.util.WALAUtils;
import junit.framework.TestCase;

public class TestOptionsAndSlicing extends TestCase {

	public void testWekaOldOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.wekaOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.oldWekaPath);
		rep.showAll();
		//test thin slicing
		
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(CodeAnalyzerRepository.oldWekaPath,
				CodeAnalyzerRepository.wekaMainClass, rep.getConfEntityList(), false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
		
		String saveFileName = EvolConfOptionRepository.wekaOldCacheFile;
		saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	public void testWekaNewOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.wekaNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.newWekaPath);
		rep.showAll();
		//test thin slicing
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(CodeAnalyzerRepository.newWekaPath,
				CodeAnalyzerRepository.wekaMainClass, rep.getConfEntityList(), false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
			if(output.conf.getConfName().equals("m_numFolds")) {
				System.err.println(output.toString()); //XXX in
			}
		}
		
		String saveFileName = EvolConfOptionRepository.wekaNewCacheFile;
		saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	//some unreachable options are exluded in the file
	public void testRandoopOldOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.randoopOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.randoop121Path);
		rep.showAll();
		
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(CodeAnalyzerRepository.randoop121Path,
				CodeAnalyzerRepository.randoopMain, rep.getConfEntityList(), false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
		
		String saveFileName = EvolConfOptionRepository.randoopOldCacheFile;
		saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	public void testRandoopNewOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.randoopNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.randoop132Path);
		rep.showAll();
		
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(CodeAnalyzerRepository.randoop132Path,
				CodeAnalyzerRepository.randoopMain, rep.getConfEntityList(), false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
			if(output.conf.getConfName().equals("usethreads")) {
				System.err.println(output.toString());
				//contains the
			}
		}
		
		String saveFileName = EvolConfOptionRepository.randoopNewCacheFile;
		saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	public void testSynopticOldOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.synopticOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.oldSynopticPath);
		rep.showAll();
		
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(CodeAnalyzerRepository.oldSynopticPath,
				CodeAnalyzerRepository.synopticMainClass, rep.getConfEntityList(), false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
		
		String saveFileName = EvolConfOptionRepository.synopticOldCacheFile;
		saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	public void testSynopticNewOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.synopticNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.newSynopticPath);
		rep.showAll();
		
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(CodeAnalyzerRepository.newSynopticPath,
				CodeAnalyzerRepository.synopticMainClass, rep.getConfEntityList(), false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
			if(output.conf.getConfName().equals("dumpInitialGraphDotFile")) {
				System.err.println(output);
			}
			if(output.conf.getConfName().equals("dumpInitialGraphPngFile")) {
				System.err.println(output);
			}
		}
		
		String saveFileName = EvolConfOptionRepository.synopticNewCacheFile;
		saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	public void testJMeterOldOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.jmeterOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.getOldJMeterPath());
		rep.showAll();
		
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getJMeterOldAnalyzer();
		oldCoder.buildAnalysis();
		//use additional seeds
		oldCoder.slicer.setAddSliceSeedFromGet(true);
		//memorize the output
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(oldCoder.slicer, rep, false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
		
		String saveFileName = EvolConfOptionRepository.jmeterOldCacheFile;
		saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	public void testJMeterNewOptions() {
		Collection<ConfPropOutput> outputs = getJMeterNewConfPropOutput();
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
			for(IRStatement irs : output.statements) {
			    System.out.println("   " + irs);
			}
		}
		
		String saveFileName = EvolConfOptionRepository.jmeterNewCacheFile;
		saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	public static Collection<ConfPropOutput>  getJMeterNewConfPropOutput() {
		ConfEntityRepository rep = EvolConfOptionRepository.jmeterNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.getNewJMeterPath());
		rep.showAll();
		
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getJMeterNewAnalyzer();
		newCoder.buildAnalysis();
		//use additional seeds
		newCoder.slicer.setAddSliceSeedFromGet(true);
		//memorize the output
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(newCoder.slicer, rep, false);
		return outputs;
	}
	
	public void testJChordOldOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.jchordOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.jChordOldPath);
		rep.showAll();

		//must use chord exclusion files!
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(
				CodeAnalyzerRepository.jChordOldPath,
				CodeAnalyzerRepository.chordMainClass,
				rep.getConfEntityList(),
				CodeAnalyzerRepository.chordExclusions,
				CG.ZeroCFA,
				false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
		
		String saveFileName = EvolConfOptionRepository.jchordOldCacheFile;
		saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	public void testJChordNewOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.jchordNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.jChordNewPath);
		rep.showAll();
		
		//must use chord exclusion files!
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(
				CodeAnalyzerRepository.jChordNewPath,
				CodeAnalyzerRepository.chordMainClass,
				rep.getConfEntityList(),
				CodeAnalyzerRepository.chordExclusions,
				CG.ZeroCFA,
				false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
		
		String saveFileName = EvolConfOptionRepository.jchordNewCacheFile;
		saveAndCheckSlicingResult(saveFileName, outputs);
	}

	public void testJavalancheOldOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.javalancheOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.getJavalancheOldPath());
		rep.showAll();
		
		CodeAnalyzer coder = CodeAnalyzerRepository.getJavalancheOldAnalyzer();
		coder.slicer.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
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
		
//		Collection<CGNode> nodes = WALAUtils.lookupCGNode(coder.getCallGraph(),
//				"de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.<clinit>");
//		
//		for(CGNode node : nodes) {
//			WALAUtils.printAllIRs(node);
//		}
		
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(coder.slicer, rep, false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
			for(IRStatement irs : output.statements) {
			    System.out.println("   " + irs);
			}
		}
		
		String saveFileName = EvolConfOptionRepository.javalancheOldCacheFile;
		saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	public void testJavalancheNewOptions() {
		Collection<ConfPropOutput> outputs = getJavalancheNewPropOutputs();
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
			for(IRStatement irs : output.statements) {
			    System.out.println("   " + irs);
			}
		}
		
		String saveFileName = EvolConfOptionRepository.javalancheNewCacheFile;
		saveAndCheckSlicingResult(saveFileName, outputs);
	}
	
	
	public static Collection<ConfPropOutput> getJavalancheNewPropOutputs() {
		ConfEntityRepository rep = EvolConfOptionRepository.javalancheNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.getJavalancheNewPath());
		rep.showAll();
		
		CodeAnalyzer coder = CodeAnalyzerRepository.getJavalancheNewAnalyzer();
		coder.slicer.setCGType(CG.RTA);
		coder.slicer.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
		//use return statement
		coder.slicer.setUseReturnSeed(true);
		
		coder.buildAnalysis();
		
		for(CGNode node : coder.getCallGraph()) {
			if(node.getMethod().getSignature().indexOf("PropertyConfiguration") != -1) {
				System.err.println(node);
			}
		}
		IClass ic = WALAUtils.lookupClass(coder.slicer.getClassHierarchy(),
				"de.unisb.cs.st.javalanche.mutation.properties.PropertyConfiguration");
		System.out.println(ic);
		
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(coder.slicer, rep, false);
		return outputs;
	}
	
	public void testCheckJavalancheSlicing() {
//		String saveFileName = EvolConfOptionRepository.javalancheNewCacheFile;
		String saveFileName = EvolConfOptionRepository.javalancheOldCacheFile;
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(saveFileName);
		Map<ConfEntity, Collection<ShrikePoint>> locations = schema.getLocations();
		//see the conf entity
		for(ConfEntity e : locations.keySet()) {
			boolean has = false;
//			if(!e.getConfName().startsWith("test")) {
//				continue;
//			}
			System.out.println(e);
			for(ShrikePoint p : locations.get(e)) {
				if(p.getMethodSig().startsWith("de.unisb")) {
				    System.out.println("   " + p);
				}
			}
//			if(has) {
//				System.out.println(e);
//			}
		}
	}
	
	static void saveAndCheckSlicingResult(String saveFileName, Collection<ConfPropOutput> outputs) {
		InstrumentSchema schema = new InstrumentSchema();
		schema.setType(TYPE.ALL_PRED_STMT);
		schema.addInstrumentationPoint(outputs);
		System.out.println("Before serializing, check outputs inside: ");
		System.out.println("Map size: " + schema.getLocations().size());
		for(ConfEntity e : schema.getLocations().keySet()) {
			System.out.println(e + ", number of statements: " + schema.getLocations().get(e).size());
		}
		ConfOutputSerializer.serializeSchema(schema, saveFileName);
		
		System.out.println("Read it back..");
		
		schema = ConfOutputSerializer.deserializeAsSchema(saveFileName);
		Map<ConfEntity, Collection<ShrikePoint>> locations = schema.getLocations();
		for(ConfEntity e : locations.keySet()) {
			System.out.println(e + ", number of statements: " + locations.get(e).size());
//			for(ShrikePoint p : locations.get(e)) {
//				System.out.println("   " + p);
//			}
			if(e.getConfName().indexOf("testNames") != -1) {
				for(ShrikePoint p : locations.get(e)) {
//					if(p.getMethodSig().indexOf("collectTests") != -1) {
					if(p.getMethodSig().startsWith("de.unisb")) {
						System.out.println("  -> " + p);
					}
						
//					}
				}
			}
		}
	}
}