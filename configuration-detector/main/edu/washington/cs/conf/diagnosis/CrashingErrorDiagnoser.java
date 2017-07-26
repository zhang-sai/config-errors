package edu.washington.cs.conf.diagnosis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Statement;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfUtils;
import edu.washington.cs.conf.analysis.IRStatement;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser.RankType;
import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

/**
 * Implements a diagnoser for configuration crashing errors.
 * 
 * Diagnosing crashing errors is significantly different than diagnosing non-crashing ones.
 * 
 * 1. it is unlikely to find a similar trace in the database for the crashing runs
 * 2. it does not need to compare the whole trace to identify the different parts, since
 *    the crashing trace is an incomplete profile over the whole execution.
 *    
 * Methodology:
 * - only compare the profiles in the stack trace?
 * - only compare the common predicates in the crashing trace
 * - no filtering, the error can be just off-by-one
 * */
public class CrashingErrorDiagnoser {
	
    public final Collection<PredicateProfileTuple> goodRuns;
	
	public final PredicateProfileTuple badRun;
	
	public final ConfEntityRepository repository;
	
	public static float default_experiment_value = -1f;
	private float similar_threshold_experiment = default_experiment_value;
	
	private String[] stackTraces = null;
	
	public CrashingErrorDiagnoser(Collection<PredicateProfileTuple> goodRuns,
			PredicateProfileTuple badRun, ConfEntityRepository repository) {
		Utils.checkNotNull(goodRuns);
		Utils.checkNotNull(badRun);
		Utils.checkNotNull(repository);
		this.goodRuns = goodRuns;
		this.badRun = badRun;
		this.repository = repository;
	}
	
	public void setStackTraces(String file) {
		String[] traces = Files.readWholeNoExp(file).toArray(new String[0]);
		this.setStackTraces(traces);
	}
	
	public void setSimilarThreshold(float threshold) {
		//Utils.checkTrue(threshold > 0.0f);
		this.similar_threshold_experiment = threshold;
	}
	
	public void setStackTraces(String[] stackTraces) {
		Utils.checkNotNull(stackTraces);
		this.stackTraces = new String[stackTraces.length];
		for(int i = 0; i < stackTraces.length; i++) {
			this.stackTraces[i] = stackTraces[i];
		}
	}
	
	//only the executed parts in the crashing trace
	public List<ConfDiagnosisOutput> computeResponsibleOptionsInCrashingTrace() {
		
		//remove some parts in the good trace
		List<PredicateProfileTuple> filteredGoodRuns = new LinkedList<PredicateProfileTuple>();
		for(PredicateProfileTuple goodRun : this.goodRuns) {
			Collection<PredicateProfile> filteredProfiles = new LinkedList<PredicateProfile>();
			Collection<PredicateProfile> profiles = goodRun.getAllProfiles();
			for(PredicateProfile p : profiles) {
				if(this.badRun.lookUpByUniqueKey(p.getUniqueKey()) != null) {
					filteredProfiles.add(p);
				}
			}
			//create the good profile tuple
			PredicateProfileTuple filteredTuple = PredicateProfileTuple.createGoodRun(goodRun.name, filteredProfiles);
			filteredGoodRuns.add(filteredTuple);
			//print the debugging information
			System.out.println(" - number of profiles in the good run before filtering: " + goodRun.getAllProfiles().size());
			System.out.println(" - number of profiles in the good run after filtering: " + filteredTuple.getAllProfiles().size());
			System.out.println();
		}
		
		if(similar_threshold_experiment > 0.0f) {
			//need to do filtering
			PredicateProfileDatabase db = new PredicateProfileDatabase("db", filteredGoodRuns);
			List<PredicateProfileTuple > similarOnes = db.findSimilarTuples(this.badRun, DistanceType.INTERPRODUCT, this.similar_threshold_experiment);
			//re-assign to filtered good runs
			System.out.println("Use similar threshold: " + this.similar_threshold_experiment + ", select: "
					+ similarOnes.size() + " out of: " + db.getAllTuples().size());
			if(!similarOnes.isEmpty()) {
			    filteredGoodRuns = similarOnes;
			} else {
				System.out.println("No similar found. use all.");
			}
			//record
			Files.writeToFileNoExp("number for comparison: " + filteredGoodRuns.size(), this.badRun.name + "_num_cmp.txt");
		}
		
		PredicateProfileBasedDiagnoser diagnoser
            = new PredicateProfileBasedDiagnoser(filteredGoodRuns, this.badRun, this.repository);
		
		System.out.println(" > number of profiles in the bad run: " + this.badRun.getAllProfiles().size());
		System.out.println();
		
        return diagnoser.computeResponsibleOptions();
	}
	
	//only the executed parts in the stack trace
	public List<ConfDiagnosisOutput> computeResponsibleOptionsWithStackTrace() {
		return computeResponsibleOptionsWithStackTrace(RankType.SINGLE_IMPORT);
	}
	public List<ConfDiagnosisOutput> computeResponsibleOptionsWithStackTrace(RankType type) {
		System.out.println("Compute responsible options only covered by the stack trace... ");
		Utils.checkNotNull(this.stackTraces);
		String[] methods = fetchMethodFromStackTrace(this.stackTraces);
		
		//recreate the bad profiles
		Collection<PredicateProfile> filteredBadProfiles = new LinkedList<PredicateProfile>();
		for(PredicateProfile p : this.badRun.getAllProfiles()) {
//			System.out.println(p.getContext());
			if(Utils.startWith(p.getContext(), methods)) {
				//System.out.println("add");
				filteredBadProfiles.add(p);
			}
		}
		PredicateProfileTuple filteredBadTuple = PredicateProfileTuple.createBadRun(this.badRun.name, filteredBadProfiles);
		System.out.println(" > number of profiles in the bad run before filtering: " + this.badRun.getAllProfiles().size());
		System.out.println(" > number of profiles in the bad run after filtering: " + filteredBadTuple.getAllProfiles().size());
		System.out.println();
		//recreate the good profiles
		Collection<PredicateProfileTuple> filteredGoodTuples = new LinkedList<PredicateProfileTuple>();
		for(PredicateProfileTuple goodTuple : this.goodRuns) {
			Collection<PredicateProfile> filteredGoodProfiles = new LinkedList<PredicateProfile>();
			for(PredicateProfile p : goodTuple.getAllProfiles()) {
				if(Utils.startWith(p.getContext(), methods)) {
					filteredGoodProfiles.add(p);
				}
			}
			PredicateProfileTuple filteredGoodTuple = PredicateProfileTuple.createGoodRun(goodTuple.name, filteredGoodProfiles);
			filteredGoodTuples.add(filteredGoodTuple);
			//see the filtered num
			System.out.println(" - number of profiles in the good run before filtering: " + goodTuple.getAllProfiles().size());
			System.out.println(" - number of profiles in the good run before filtering: " + filteredGoodTuple.getAllProfiles().size());
		}
		System.out.println();
		
		//start diagnosis
		PredicateProfileBasedDiagnoser diagnoser
            = new PredicateProfileBasedDiagnoser(filteredGoodTuples, filteredBadTuple, this.repository);
	
        return diagnoser.computeResponsibleOptions(type);
	}
	
	//a single stack trace looks like: at chord.project.Main.main(Main.java: 19)
	private static String[] fetchMethodFromStackTrace(String[] traces) {
		String[] methods = new String[traces.length];
		int count = 0;
		for(String trace : traces) {
			methods[count++] = fetchMethodFromStackTrace(trace);
//			System.out.println(method);
		}
		return methods;
	}
	
	private static String fetchMethodFromStackTrace(String trace) {
		String at = "at ";
		trace = trace.trim();
		int startIndex = trace.indexOf(at);
		int endIndex = trace.indexOf("(");
		Utils.checkTrue(startIndex != -1 && endIndex != -1 && endIndex > startIndex, trace);
		String method = trace.substring(startIndex + at.length(), endIndex);
		method = method.trim();
		return method;
	}
	
	private static int fetchLineNumberFromStackTrace(String trace) {
		trace = trace.trim();
		int startIndex = trace.lastIndexOf(":");
		int endIndex = trace.lastIndexOf(")");
		if(startIndex == -1 || endIndex == -1) {
			return -1;
		}
		Utils.checkTrue(endIndex > startIndex);
		String str = trace.substring(startIndex + 1, endIndex).trim();
		return Integer.parseInt(str);
	}
	
	//diagnose like diagnosing non-crashing errors
	public List<ConfDiagnosisOutput> computeResponsibleOptionsAsNonCrashingErrors() {
        PredicateProfileBasedDiagnoser diagnoser
            = new PredicateProfileBasedDiagnoser(this.goodRuns, this.badRun, this.repository);
        return diagnoser.computeResponsibleOptions();
	}
	
	public List<ConfDiagnosisOutput> computeResponsibleOptionsAsNonCrashingErrors(RankType type) {
        PredicateProfileBasedDiagnoser diagnoser
            = new PredicateProfileBasedDiagnoser(this.goodRuns, this.badRun, this.repository);
        return diagnoser.computeResponsibleOptions(type);
	}
	
	public static List<ConfDiagnosisOutput> rankConfigurationOptions(Map<ConfDiagnosisOutput, Integer> stackCoverage,
			Collection<ConfDiagnosisOutput> rankedList) {
		//first sort by rankedList's final value
		//if there is a tie with the final value, then compare the stack coverage
		//the more the better
		
		//the output with the number of beat
		Map<ConfDiagnosisOutput, Integer> outputScoreMap = new LinkedHashMap<ConfDiagnosisOutput, Integer>();
		for(ConfDiagnosisOutput o : rankedList) {
			int numberOfBeat = 0;
			for(ConfDiagnosisOutput cmp : rankedList) {
				if(cmp.equals(o)) {
					continue;
				}
				//which one should ranked high o, or cmp
				if(o.getFinalScore() > cmp.getFinalScore()) {
					numberOfBeat++;
				} else {
					if(!(o.getFinalScore() < cmp.getFinalScore())) {
//						System.out.println("comparing o: " + o.getConfEntity().getFullConfName()
//								+ ", with cmp: " + o.getConfEntity().getFullConfName());
//						System.out.println("    final score: " + o.getFinalScore()
//								+ ", v.s., " + cmp.getFinalScore());
//						System.out.println("    stack coverage: " + stackCoverage.get(o)
//								+ ", v.s., " + stackCoverage.get(cmp));
						if(stackCoverage.get(o) > stackCoverage.get(cmp)) {
							numberOfBeat++;
						}
					}
				}
			}
			outputScoreMap.put(o, numberOfBeat);
			
			System.out.println(o.getConfEntity().getFullConfName() + ", final score: " + o.getFinalScore()
					+ ", stack coverage: " + stackCoverage.get(o) + ",  beat num: " + numberOfBeat);
			
		}
		return Utils.sortByValueAndReturnKeys(outputScoreMap, false);
		
		
		//original implementation, not obsoleted
//		Map<ConfDiagnosisOutput, Float> scores = new LinkedHashMap<ConfDiagnosisOutput, Float>();
//		for(ConfDiagnosisOutput o : rankedList) {
//			scores.put(o, o.getFinalScore() + stackCoverage.get(o));
//			System.out.println("result: " + o.getConfEntity().getFullConfName()
//					+ ",  final score: " + o.getFinalScore() + ",   coverage: " + stackCoverage.get(o));
//		}
//		scores = Utils.sortByValue(scores, false);
//		System.out.println("-----------------intermediate results----------------");
//		for(ConfDiagnosisOutput o : scores.keySet()) {
//			System.out.println(o.getConfEntity().getFullConfName() + ",   " + scores.get(o) );
//		}
//		List<ConfDiagnosisOutput> finalRankedList = Utils.sortByValueAndReturnKeys(scores, false);
//		
//		return finalRankedList;
	}
	
	//check when the configuration can affect the line number in the stack trace
	public static Map<ConfDiagnosisOutput, Integer> computeMatchedStacktraceNum(Collection<ConfPropOutput> confSlices,
			Collection<ConfDiagnosisOutput> outputs, String[] stackTraces) {
		List<String> methods = new ArrayList<String>();
		List<Integer> lines = new ArrayList<Integer>();
		for(String trace : stackTraces) {
			String method = fetchMethodFromStackTrace(trace);
			Integer lineNum = fetchLineNumberFromStackTrace(trace);
			if(lineNum == -1) {
				System.err.println("no line number: " + trace);
				continue;
			}
			methods.add(method);
			lines.add(lineNum);
		}
		Utils.checkTrue(methods.size() == lines.size());
		//then rank each ConfDiagnosisOutput based on the match
		Map<ConfDiagnosisOutput, Integer> map = new LinkedHashMap<ConfDiagnosisOutput, Integer>();
		for(ConfDiagnosisOutput output : outputs) {
			//first get the ConfPropOutput
			ConfPropOutput confSlice = findConfDiagnosisOutput(confSlices, output); 
			Utils.checkNotNull(confSlice);
			//count the num
			Integer matchedStackTraceNum = 0;
			for(int i = 0; i < methods.size(); i++) {
				String method = methods.get(i);
				int lineNum = lines.get(i);
				if(confSlice.includeStatement(method, lineNum)) {
					matchedStackTraceNum ++;
//					matchedStackTraceNum += (methods.size() - i + 1);
				}
			}
			//put to the map
			map.put(output, matchedStackTraceNum);
		}
		
		return map;
	}
	
	public static Map<String, Integer> computeStackTraceDistance(Collection<ConfPropOutput> confSlices,
			ConfDiagnosisOutput output, String[] stackTraces) {
		return computeStackTraceDistance(confSlices, output, stackTraces, false);
	}
	
	public static Map<String, Integer> computeStackTraceDistance(Collection<ConfPropOutput> confSlices,
			ConfDiagnosisOutput output, String[] stackTraces, boolean noLib) {
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		//in the output slice, the distance between the stack trace methods to the configuration entry
		for(String stackTrace : stackTraces) {
			String method = fetchMethodFromStackTrace(stackTrace);
			Integer lineNum = fetchLineNumberFromStackTrace(stackTrace);
			if(lineNum == -1) {
				continue;
			}
			ConfPropOutput confSlice = findConfDiagnosisOutput(confSlices, output); 
			Utils.checkNotNull(confSlice);
			
			Set<IRStatement> filtered = ConfPropOutput.excludeIgnorableStatements(confSlice.statements);
			Set<IRStatement> statements = ConfUtils.removeSameStmtsInDiffContexts(filtered);// filterSameStatements(filtered);
			
			//compute the distance
			IRStatement stmt = null;
			int pathLength = 0;
			for(IRStatement irs : statements) {
				if(irs.getMethodSig().startsWith(method) && irs.getLineNumber() == lineNum) {
					pathLength++;
					stmt = irs;
					break;
				} else {
					if(!shouldPrune(irs.getStatement())) {
						pathLength++;
					}
				}
			}
			int distance = Integer.MAX_VALUE;
			if(stmt != null) {
				if(confSlice.getConfigurationSlicer() != null) {
				    Statement seed = confSlice.getConfigurationSlicer().extractConfStatement(confSlice.getConfEntity());
				    Statement target = stmt.getStatement();
				    if(noLib) {
				      //prune out all lib calls
				      List<Statement> list = confSlice.getConfigurationSlicer().computeStatementListInThinSlicing(seed, target);
				      List<Statement> pruned = pruneAllLibCalls(list);
				      distance = pruned.size();
				    } else {
				       distance = confSlice.getConfigurationSlicer().computeDistanceInThinSlicing(seed, target);
				    }
				}
			}
			
			if(distance != Integer.MAX_VALUE) {
				distance = Math.min(distance, pathLength);
			}
			
			map.put(stackTrace, distance);
		}
		return map;
	}
	
	private static ConfPropOutput findConfDiagnosisOutput(Collection<ConfPropOutput> confSlices, ConfDiagnosisOutput output) {
		ConfPropOutput confSlice = null;
		for(ConfPropOutput slice : confSlices) {
			if(slice.getConfEntity().getFullConfName().equals(output.getConfEntity().getFullConfName())) {
				confSlice = slice;
				break;
			}
		}
		return confSlice;
//		Utils.checkNotNull(confSlice);
		
	}
	
	static String[] skipped = new String[]{"java.", "javax.", "sun."};
	
	private static List<Statement> pruneAllLibCalls(Collection<Statement> coll) {
		List<Statement> pruned = new LinkedList<Statement>();
		for(Statement s : coll) {
			if(shouldPrune(s)) {
				continue;
			} else {
				pruned.add(s);
			}
		}
		return pruned;
	}
	
	private static boolean shouldPrune(Statement s) {
		if(s instanceof NormalStatement) {
			NormalStatement ns = (NormalStatement)s;
			String methodCall = WALAUtils.getFullMethodName(ns.getNode().getMethod());
			if(Utils.startWith(methodCall, skipped)) {
				return true;
			}
		}
		return false;
	}
}