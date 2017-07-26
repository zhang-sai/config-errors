package edu.washington.cs.conf.experiments;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfUtils;
import edu.washington.cs.conf.analysis.ConfigurationSlicer;
import edu.washington.cs.conf.analysis.IRStatement;
import edu.washington.cs.conf.analysis.SlicePruner;
import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.diagnosis.ConfDiagnosisOutput;
import edu.washington.cs.conf.diagnosis.MainAnalyzer;
import edu.washington.cs.conf.diagnosis.PredicateProfileTuple;
import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator;
import edu.washington.cs.conf.diagnosis.TraceAnalyzer;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser.RankType;
import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.instrument.InstrumentSchema.TYPE;
import edu.washington.cs.conf.util.Log;
import edu.washington.cs.conf.util.Utils;

public class CommonUtils {
	
	public static Collection<ConfPropOutput> getConfPropOutputs(String path, String mainClass, List<ConfEntity> confList, String exclusionFile, boolean doPruning) {
		return getConfPropOutputs(path, mainClass, confList, exclusionFile, CG.OneCFA, doPruning);
	}
	
	public static Collection<ConfPropOutput> getConfPropOutputs(String path, String mainClass, List<ConfEntity> confList, String exclusionFile, CG type, boolean doPruning) {
		return getConfPropOutputs(path, mainClass, confList, exclusionFile, type, doPruning, DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS, ControlDependenceOptions.NONE);
	}
	
	public static Collection<ConfPropOutput> getConfPropOutputsFullSlice(String path, String mainClass, List<ConfEntity> confList, String exclusionFile, CG type, boolean doPruning) {
		return getConfPropOutputs(path, mainClass, confList, exclusionFile, type, doPruning, DataDependenceOptions.FULL, ControlDependenceOptions.FULL);
	}
	
	public static Collection<ConfPropOutput> getConfPropOutputs(String path, String mainClass, List<ConfEntity> confList, String exclusionFile, CG type, boolean doPruning,
			DataDependenceOptions dataDep, ControlDependenceOptions controlDep) {
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
		helper.setCGType(type);
		helper.setExclusionFile(exclusionFile);
		helper.setDataDependenceOptions(dataDep);
		helper.setControlDependenceOptions(controlDep);
		helper.setContextSensitive(false); //context-insensitive
		helper.buildAnalysis();
		
		//set computing distance
//		helper.setSlicingDistance(true);
		
		//get all type info
		ConfEntityRepository repo = new ConfEntityRepository(confList);
		repo.initializeTypesInConfEntities(path);
		
		Collection<ConfPropOutput> outputs = new LinkedList<ConfPropOutput>();
		for(ConfEntity entity : confList) {
			Log.logln("entity: " + entity);
			ConfPropOutput output = helper.outputSliceConfOption(entity);
			
			outputs.add(output);
			
			System.out.println("  statement in slice: " + output.statements.size());
			Log.logln("  statement in slice: " + output.statements.size());
			Set<IRStatement> filtered = ConfPropOutput.excludeIgnorableStatements(output.statements);
			System.out.println("  statements after filtering: " + filtered.size());
			Log.logln("  statements after filtering: " + filtered.size());
			
			Set<IRStatement> sameStmts = ConfUtils.removeSameStmtsInDiffContexts(filtered);// filterSameStatements(filtered);
			System.out.println("  filtered statements: " + sameStmts.size());
			Log.logln("  filtered statements: " + sameStmts.size());
			
			for(IRStatement s : sameStmts) {
				Log.logln("       statement: " + s);
			}
			
			Set<IRStatement> branchStmts = ConfPropOutput.extractBranchStatements(sameStmts);
			System.out.println("  branching statements: " + branchStmts.size());
			Log.logln("  branching statements: " + branchStmts.size());
			
			dumpStatements(branchStmts);
		}

		Utils.checkTrue(confList.size() == outputs.size());
		
		if(doPruning) {
			System.out.println("pruning slices by overalp...");
			outputs = SlicePruner.pruneSliceByOverlap(outputs);
			for(ConfPropOutput output : outputs) {
				Log.logln("entity: " + output.getConfEntity());
				Set<IRStatement> filtered = ConfPropOutput.excludeIgnorableStatements(output.statements);
				Set<IRStatement> sameStmts = ConfUtils.removeSameStmtsInDiffContexts(filtered);
				Set<IRStatement> branchStmts = ConfPropOutput.extractBranchStatements(sameStmts);
				Log.logln("  statement in the pruned slice: " + branchStmts.size());
			}
		}
		
		//save the slicer
		for(ConfPropOutput output : outputs) {
			output.setConfigurationSlicer(helper);
		}
		
		return outputs;
	}
	
	/**
	 * TODO pretty much redundant than above
	 * */
	public static Collection<ConfPropOutput> getConfPropOutputs
	    (ConfigurationSlicer helper, ConfEntityRepository repo, boolean doPruning) {
		Collection<ConfPropOutput> outputs = new LinkedList<ConfPropOutput>();
		for(ConfEntity entity : repo.getConfEntityList()) {
			Log.logln("entity: " + entity);
			ConfPropOutput output = helper.outputSliceConfOption(entity);
			
			outputs.add(output);
			
			System.out.println("  statement in slice: " + output.statements.size());
			Log.logln("  statement in slice: " + output.statements.size());
			Set<IRStatement> filtered = ConfPropOutput.excludeIgnorableStatements(output.statements);
			System.out.println("  statements after filtering: " + filtered.size());
			Log.logln("  statements after filtering: " + filtered.size());
			
			Set<IRStatement> sameStmts = ConfUtils.removeSameStmtsInDiffContexts(filtered);// filterSameStatements(filtered);
			System.out.println("  filtered statements: " + sameStmts.size());
			Log.logln("  filtered statements: " + sameStmts.size());
			
			for(IRStatement s : sameStmts) {
				Log.logln("       statement: " + s);
			}
			
			Set<IRStatement> branchStmts = ConfPropOutput.extractBranchStatements(sameStmts);
			System.out.println("  branching statements: " + branchStmts.size());
			Log.logln("  branching statements: " + branchStmts.size());
			
			dumpStatements(branchStmts);
		}

		Utils.checkTrue(repo.getConfEntityList().size() == outputs.size());
		
		if(doPruning) {
			System.out.println("pruning slices by overalp...");
			outputs = SlicePruner.pruneSliceByOverlap(outputs);
			for(ConfPropOutput output : outputs) {
				Log.logln("entity: " + output.getConfEntity());
				Set<IRStatement> filtered = ConfPropOutput.excludeIgnorableStatements(output.statements);
				Set<IRStatement> sameStmts = ConfUtils.removeSameStmtsInDiffContexts(filtered);
				Set<IRStatement> branchStmts = ConfPropOutput.extractBranchStatements(sameStmts);
				Log.logln("  statement in the pruned slice: " + branchStmts.size());
			}
		}
		
		//save the slicer
		for(ConfPropOutput output : outputs) {
			output.setConfigurationSlicer(helper);
		}
		
		return outputs;
	}
	
	public static Collection<ConfPropOutput> getConfPropOutputs(String path, String mainClass, List<ConfEntity> confList, boolean doPruning) {
		return getConfPropOutputs(path, mainClass, confList, "JavaAllExclusions.txt", doPruning);
	}
	
	
	public static void dumpStatements(Collection<IRStatement> stmts) {
		for(IRStatement stmt : stmts) {
			Log.logln("     >> " + stmt.toString());
		}
	}
	
	public static void diagnoseOptions(String goodRunTrace, String badRunTrace,
			boolean doFiltering, ConfEntityRepository repo, RankType t, float similarThreshold) {
//		MainAnalyzer.amortizeNoise = false;
		MainAnalyzer.doFiltering = doFiltering;
		
		
		MainAnalyzer analyzer = new MainAnalyzer(badRunTrace, Arrays.asList(goodRunTrace), repo);//, sourceDir, configOutputs);
		analyzer.setRankType(t); //use single import is OK
		analyzer.setThreshold(similarThreshold);
		
		List<ConfDiagnosisOutput> outputs = analyzer.computeResponsibleOptions();
		
		for(ConfDiagnosisOutput output : outputs) {
		    System.out.println(output);
		    System.out.println("   " + output.getBriefExplanation());
		    System.out.println();
		    
		    Log.logln(output.toString());
		    Log.logln("exp num: " + output.getExplanations().size());
		    for(String exp : output.getExplanations()) {
		    	Log.logln("    " + exp);
		    }
		    Log.logln("");
		}
		
		Log.removeLogging();
		
		MainAnalyzer.doFiltering = false;
	}
	
	public static void compareTraceDistance(String goodRunTrace, String badRunTrace, DistanceType t, Float expected, boolean check) {
		PredicateProfileTuple good = TraceAnalyzer.createGoodProfileTuple(goodRunTrace, "good-run");
		PredicateProfileTuple bad = TraceAnalyzer.createBadProfileTuple(badRunTrace, "bad-run");
		
		if(check) {
		    ProfileDistanceCalculator.showAlignedVectors(good, bad);
		}
		
		float distance = ProfileDistanceCalculator.computeDistance(good, bad, t);
		System.out.println(t + " distance: " + distance);
		if(check) {
	        Utils.checkTrue(expected == distance);
		}
	}
	
	public static void compareTraceDistance(String goodRunTrace, String badRunTrace, DistanceType t, Float expected) {
		compareTraceDistance(goodRunTrace, badRunTrace, t, expected, true);
	}
	
	//for full slicing, with special speedup
	public static Collection<ConfPropOutput> getConfPropOutputsFullSlicing(String path,
			String mainClass, List<ConfEntity> confList, 
			String exclusionFile, CG type, boolean doPruning,
			DataDependenceOptions dataDep, ControlDependenceOptions controlDep, TYPE ty /*not used?*/) {
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
		helper.setCGType(type);
		helper.setExclusionFile(exclusionFile);
		helper.setDataDependenceOptions(dataDep);
		helper.setControlDependenceOptions(controlDep);
		helper.setContextSensitive(false); //context-insensitive
		helper.buildAnalysis();
		
		//set computing distance
//		helper.setSlicingDistance(true);
		
		//get all type info
		ConfEntityRepository repo = new ConfEntityRepository(confList);
		repo.initializeTypesInConfEntities(path);
		
		Collection<ConfPropOutput> outputs = new LinkedList<ConfPropOutput>();
		for(ConfEntity entity : confList) {
			Log.logln("entity: " + entity);
			ConfPropOutput output = helper.outputSliceConfOption(entity);
			System.out.println("slice num: " + output.statements.size());
			
			Set<IRStatement> filtered = ConfPropOutput.filterStatementsForFullSliceResult(output.statements);
			output.statements.clear();
			output.statements.addAll(filtered);
			System.err.println("   after filtering: " + output.statements.size());
			
			outputs.add(output);
		}

		Utils.checkTrue(confList.size() == outputs.size());
		
		if(doPruning) {
			System.out.println("pruning slices by overalp...");
			outputs = SlicePruner.pruneSliceByOverlap(outputs);
			for(ConfPropOutput output : outputs) {
				Log.logln("entity: " + output.getConfEntity());
				Set<IRStatement> filtered = ConfPropOutput.excludeIgnorableStatements(output.statements);
				Set<IRStatement> sameStmts = ConfUtils.removeSameStmtsInDiffContexts(filtered);
				Set<IRStatement> branchStmts = ConfPropOutput.extractBranchStatements(sameStmts);
				Log.logln("  statement in the pruned slice: " + branchStmts.size());
			}
		}
		
		//save the slicer
		for(ConfPropOutput output : outputs) {
			output.setConfigurationSlicer(helper);
		}
		
		return outputs;
	}
}