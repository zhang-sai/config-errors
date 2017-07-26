package edu.washington.cs.conf.experiments.randoop;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfUtils;
import edu.washington.cs.conf.analysis.IRStatement;
import edu.washington.cs.conf.analysis.SlicePruner;
import edu.washington.cs.conf.analysis.ConfigurationSlicer;
import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.experiments.RandoopExpUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import junit.framework.TestCase;

public class TestSliceRandoopConfigOptions extends TestCase {
	
	public static String randoop_instrument_file = "./randoop_option_instr_ser.dat";
	public static String randoop_instrument_txt = "./randoop_option_instr.txt";
	
	public static String randoop_instrument_file_full_slice = "./randoop_option_instr_ser_full_slice.dat";
	public static String randoop_instrument_txt_full_slice = "./randoop_option_instr_full_slice.txt";
	
	public static boolean doPruning = false;
	
	@Override
	public void tearDown() {
		doPruning = false;
	}
	
	public void testInitRandoopOptionTypes() {
		String path = "./subjects/randoop-jamie-no-trace.jar;./subjects/plume.jar";
		List<ConfEntity> randoopConfList = RandoopExpUtils.getRandoopConfList();
		ConfEntityRepository repo = new ConfEntityRepository(randoopConfList);
		repo.initializeTypesInConfEntities(path);
		for(ConfEntity conf : randoopConfList) {
			System.out.println(conf);
		}
	}
	
	public void testPruneRandoopSlices() {
		doPruning = true;
		
		String path = "./subjects/randoop-jamie-no-trace.jar;./subjects/plume.jar";
		Collection<ConfPropOutput> confOutputs = getConfPropOutputs(path, RandoopExpUtils.getRandoopConfList());
		for(ConfPropOutput o : confOutputs) {
			System.out.println(o.getConfEntity());
			System.out.println("    size: " + o.statements.size());
		}
		if(doPruning) {
		    System.out.println("------------");
		    confOutputs = SlicePruner.pruneSliceByOverlap(confOutputs);
		    for(ConfPropOutput o : confOutputs) {
			    System.out.println(o.getConfEntity());
//			System.out.println(o.toString());
			    System.out.println("    size: " + o.statements.size());
			
			    Set<IRStatement> filtered = ConfPropOutput.excludeIgnorableStatements(o.statements);
			    System.out.println("      statements after filtering: " + filtered.size());
			
			    Set<IRStatement> sameStmts = ConfUtils.removeSameStmtsInDiffContexts(filtered);// filterSameStatements(filtered);
			    System.out.println("      filtered statements: " + sameStmts.size());
			
			    Set<IRStatement> branchStmts = ConfPropOutput.extractBranchStatements(sameStmts);
			    System.out.println("      branching statements: " + branchStmts.size());
			
			    CommonUtils.dumpStatements(branchStmts);
		    }
		}
	}
	
	public void testSliceRandoopCheaply() {
		long start = System.currentTimeMillis();
		String path = "./subjects/randoop-jamie-no-trace.jar;./subjects/plume.jar";
		getConfPropOutputs(path, RandoopExpUtils.getRandoopConfList());
		long end = System.currentTimeMillis();
		System.out.println("Eclapsed time: " + (end - start)/1000);
	}
	
	public void testLargeSliceRandoopOptions() {
		String randoopBin = "D:\\research\\configurations\\workspace\\nanoxml-jamie\\bin";
		String plumeJar = "./subjects/plume.jar";
		String path = "./subjects/randoop-jamie-no-trace.jar;./subjects/plume.jar";
		path = randoopBin + ";" + plumeJar;
		getConfPropOutputs(path, RandoopExpUtils.getLargeSliceConfList());
//		getConfPropOutputs(path, RandoopExpUtils.getFakeOptions());
	}
	
	public void testCreateInstrumentSchema() {
		doPruning = true;
		String path = "./subjects/randoop-jamie-no-trace.jar;./subjects/plume.jar";
       Collection<ConfPropOutput> outputs = getConfPropOutputs(path, RandoopExpUtils.getRandoopConfList());
		
		//save as configuration schema
		InstrumentSchema schema = new InstrumentSchema();
		schema.addInstrumentationPoint(outputs);
		
		ConfOutputSerializer.serializeSchema(schema, randoop_instrument_file);
		ConfOutputSerializer.writeToFileAsText(schema, randoop_instrument_txt);
		
		//recover from the file
		InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(randoop_instrument_file);
		assertEquals(schema.toString(), newSchema.toString());
	}
	
	public void testCreateInstrumentSchemaFullSlice() {
		doPruning = true;
		String path = "./subjects/randoop-jamie-no-trace.jar;./subjects/plume.jar";
       Collection<ConfPropOutput> outputs = getConfPropOutputsFullSlice(path, RandoopExpUtils.getRandoopConfList());
		
		//save as configuration schema
		InstrumentSchema schema = new InstrumentSchema();
		schema.addInstrumentationPoint(outputs);
		
		ConfOutputSerializer.serializeSchema(schema, randoop_instrument_file_full_slice);
		ConfOutputSerializer.writeToFileAsText(schema, randoop_instrument_txt_full_slice);
		
		//recover from the file
		InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(randoop_instrument_file_full_slice);
		assertEquals(schema.toString(), newSchema.toString());
	}
	
	public static Collection<ConfPropOutput> getConfPropOutputs() {
		String path = "./subjects/randoop-jamie-no-trace.jar;./subjects/plume.jar";
	    Collection<ConfPropOutput> outputs = getConfPropOutputs(path, RandoopExpUtils.getRandoopConfList());
	    return outputs;
	}
	
	public static Collection<ConfPropOutput> getConfPropOutputs(String path, List<ConfEntity> confList) {
		return getConfPropOutputs(path, confList, DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS,
				ControlDependenceOptions.NONE);
	}
	
	public static Collection<ConfPropOutput> getConfPropOutputsFullSlice(String path, List<ConfEntity> confList) {
		return getConfPropOutputs(path, confList,
				CG.RTA,
				DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS,
				ControlDependenceOptions.NO_EXCEPTIONAL_EDGES);
	}
	
	public static Collection<ConfPropOutput> getConfPropOutputs(String path, List<ConfEntity> confList,
			CG type, DataDependenceOptions dataDep, ControlDependenceOptions controlDep) {
		String mainClass = "Lrandoop/main/Main";
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
		helper.setCGType(type);
		helper.setExclusionFile("JavaAllExclusions.txt");
		helper.setDataDependenceOptions(dataDep);
		helper.setControlDependenceOptions(controlDep);
		helper.setContextSensitive(false); //context-insensitive
		helper.buildAnalysis();
		
		List<ConfEntity> randoopConfList = confList;
		
		//get all type info
		ConfEntityRepository repo = new ConfEntityRepository(randoopConfList);
		repo.initializeTypesInConfEntities(path);
		
		Collection<ConfPropOutput> outputs = new LinkedList<ConfPropOutput>();
		for(ConfEntity entity : randoopConfList) {
//		  helper.setExcludeStringBuilder(true); //FIXME
			ConfPropOutput output = helper.outputSliceConfOption(entity);
			outputs.add(output);
			System.err.println("  statement in slice: " + output.statements.size());
			
			Set<IRStatement> filtered = ConfPropOutput.filterStatementsForFullSliceResult(output.statements);
			output.statements.clear();
			output.statements.addAll(filtered);
			System.err.println("   after filtering: " + output.statements.size());
			
//			Set<IRStatement> filtered = ConfPropOutput.excludeIgnorableStatements(output.statements);
//			System.err.println("  statements after filtering: " + filtered.size());
//			
//			Set<IRStatement> sameStmts = ConfUtils.removeSameStmtsInDiffContexts(filtered);// filterSameStatements(filtered);
//			System.err.println("  filtered statements: " + sameStmts.size());
//			
//			Set<IRStatement> branchStmts = ConfPropOutput.extractBranchStatements(sameStmts);
//			System.err.println("  branching statements: " + branchStmts.size());
			
//			System.err.println("   numbered statements: " + output.getNumberedBranches().size());
//			System.err.println("   number of src branching statements: " + output.getNumberedBranchesInSource().size());
			
//			CommonUtils.dumpStatements(branchStmts);
		}

		assertEquals(randoopConfList.size(), outputs.size());
		
		if(doPruning) {
			System.out.println("pruning slices by overalp...");
			outputs = SlicePruner.pruneSliceByOverlap(outputs);
		}
		
		return outputs;
	}
	
	public static Collection<ConfPropOutput> getConfPropOutputs(String path, List<ConfEntity> confList,
			DataDependenceOptions dataDep, ControlDependenceOptions controlDep) {
		return getConfPropOutputs(path, confList, CG.OneCFA, dataDep, controlDep);
	}
}