package edu.washington.cs.conf.diagnosis;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfUtils;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser.CrossRunRank;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser.RankType;
import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.experiments.WekaExpUtils;
import edu.washington.cs.conf.experiments.weka.TestComparingWekaTraces;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;

/**
 * The main entry of the predicate profile based diagnosis.
 * Input: 
 *     - a number of good runs (files)
 *     - a bad run (files)
 *     - a threashold of selecting similar runs
 *     - a selection methodology
 *     - a ranking methodology
 *     - a cross run ranking methodology
 * Output:
 *     - a list of ranked configuration options
 * */
public class MainAnalyzer {
	
	public enum SelectionStrategy {RandomK, OneMostSimilar, OneLeastSimilar, ALL}
	
	public static boolean doFiltering = false;
	public static boolean amortizeNoise = false;
	public static int thresholdcount = 3;
	public static float default_threshold = 0.1f;
	public static String result_output_file = "./diagnosis_results.txt";
	public static boolean globalcount = true;
	
	private float distanceThreshold = default_threshold; //distance
//	private DistanceType distanceType = DistanceType.INTERPRODUCT;
	private DistanceType distanceType = DistanceType.COSINE;
	private RankType rankType = RankType.SINGLE_IMPORT;
//	private CrossRunRank crossRank = CrossRunRank.HIGHEST_RANK_AVG;
	private final ConfEntityRepository repository;
	
	private String sourceDir = null;
	private Collection<ConfPropOutput> outputs = null;
	
	private SelectionStrategy strategy = null;

	public final PredicateProfileTuple badRun;
	private final List<PredicateProfileTuple> goodRuns
	    = new LinkedList<PredicateProfileTuple>();
	public final PredicateProfileDatabase goodRunDb;
	
	public MainAnalyzer(PredicateProfileTuple badRun, Collection<PredicateProfileTuple> goodRuns,
			ConfEntityRepository repository) {
		Utils.checkNotNull(badRun);
		Utils.checkNotNull(goodRuns);
		Utils.checkTrue(goodRuns.size() > 0);
		this.badRun = badRun;
		this.goodRuns.addAll(goodRuns);
		this.goodRunDb = new PredicateProfileDatabase(this.goodRuns);
		this.repository = repository;
	}
	
	public MainAnalyzer(String badRunTraceFile, Collection<String> goodRunTraceFiles,
			ConfEntityRepository repository) {
		this(badRunTraceFile, goodRunTraceFiles, repository, null, null);
	}
	
	public MainAnalyzer(String badRunTraceFile, Collection<String> goodRunTraceFiles,
			ConfEntityRepository repository, String srcDir, Collection<ConfPropOutput> propOutputs) {
		Utils.checkNotNull(badRunTraceFile);
		Utils.checkNotNull(goodRunTraceFiles);
		Utils.checkTrue(goodRunTraceFiles.size() > 0);
		
		//set the dir
		this.sourceDir = srcDir;
		this.outputs = propOutputs;
		
		//create the bad run
		Collection<PredicateProfile> badProfiles = TraceAnalyzer.createProfiles(badRunTraceFile);
		this.setSrcLineAndText(badProfiles);
		this.badRun = PredicateProfileTuple.createBadRun("badrun", badProfiles);
		//create the good runs
		int index = 0;
		for(String goodRunTraceFile : goodRunTraceFiles) {
			Collection<PredicateProfile> goodProfiles = TraceAnalyzer.createProfiles(goodRunTraceFile);
			this.setSrcLineAndText(goodProfiles);
			PredicateProfileTuple goodProfile = PredicateProfileTuple.createGoodRun("goodrun-" + (index++), goodProfiles);
			this.goodRuns.add(goodProfile);
		}
		this.goodRunDb = new PredicateProfileDatabase(this.goodRuns);
		this.repository = repository;
	}
	
	private void setSrcLineAndText(Collection<PredicateProfile> profiles) {
		if(this.sourceDir != null && this.outputs != null) {
			ConfUtils.setUpLineNumberAndSource(this.sourceDir, this.outputs, profiles);
		}
	}
	
	public List<ConfDiagnosisOutput> computeResponsibleOptions() {
		this.showParameters(); //for debugging purpose
		List<PredicateProfileTuple> cmpProfiles
		   = selectSimilarProfileTuples(this.goodRunDb, this.badRun, this.distanceType, this.distanceThreshold);
		System.err.println("Number of similar profiles: " + cmpProfiles.size());
		
		//this if branch is only for experiments
		//do experiment here
		if(this.strategy != null) {
			if(this.strategy.equals(SelectionStrategy.RandomK)) {
				cmpProfiles = randomSelectProfileTuples(this.goodRunDb, cmpProfiles.size());
			} else if (this.strategy.equals(SelectionStrategy.OneMostSimilar)) {
				cmpProfiles = selectOneMostSimilarProfileTuples(this.goodRunDb, this.badRun, this.distanceType);
			} else if (this.strategy.equals(SelectionStrategy.OneLeastSimilar)) {
				cmpProfiles = selectOneLeastSimilarProfileTuples(this.goodRunDb, this.badRun, this.distanceType);
			} else if (this.strategy.equals(SelectionStrategy.ALL)) {
				cmpProfiles = this.goodRunDb.getAllTuples();
			}
			System.err.println("In experiment, using strategy: " + this.strategy);
			System.err.println("re-select: " + cmpProfiles.size() + " profiles for comparison.");
		}
		
		//the default setting below
		PredicateProfileBasedDiagnoser diagnoser = createDiagnoser(cmpProfiles, this.badRun, this.repository);
		List<ConfDiagnosisOutput> rankedOutput = diagnoser.computeResponsibleOptions(this.rankType);
		return rankedOutput;
	}
	
	private void showParameters() {
		StringBuilder sb = new StringBuilder();
		sb.append("Basic info:");
		sb.append(Globals.lineSep);
		sb.append("  number of good runs: " + this.goodRunDb.getAllTuples().size());
		sb.append("Selecting similar traces: ");
		sb.append(Globals.lineSep);
		sb.append("  distance type: " + this.distanceType);
		sb.append(Globals.lineSep);
		sb.append("  threshold: " + this.distanceThreshold);
		sb.append(Globals.lineSep);
		sb.append("For configuration option ranking: ");
		sb.append(Globals.lineSep);
		sb.append("  rank type: " + this.rankType);
		sb.append(Globals.lineSep);
//		sb.append("  cross run ranking: " + this.crossRank);
//		sb.append(Globals.lineSep);
		System.err.println(sb.toString());
	}
	
	static PredicateProfileBasedDiagnoser createDiagnoser(Collection<PredicateProfileTuple> similarProfiles,
			PredicateProfileTuple target, ConfEntityRepository repo) {
		PredicateProfileBasedDiagnoser diagnoser = new PredicateProfileBasedDiagnoser(similarProfiles, target, repo);
		return diagnoser;
	}
	
	static List<PredicateProfileTuple> selectSimilarProfileTuples(PredicateProfileDatabase db, PredicateProfileTuple target,
			DistanceType distanceType, Float threshold) {
		List<PredicateProfileTuple> similarTuples = db.findSimilarTuples(target, distanceType, threshold);
		return similarTuples;
	}
	
	static Random randomGenerator = new Random();
	
	static List<PredicateProfileTuple> randomSelectProfileTuples(PredicateProfileDatabase db, int num) {
		List<PredicateProfileTuple> allTuples = db.getAllTuples();
		Utils.checkTrue(num > 0 && num <= allTuples.size(), "Incorrect num.");
		
		Set<Integer> indices = new LinkedHashSet<Integer>();
		while(indices.size() < num) {
			int index = randomGenerator.nextInt(allTuples.size());
			indices.add(index);
		}
		
		List<PredicateProfileTuple> randomTuples = new LinkedList<PredicateProfileTuple>();
		for(Integer i : indices) {
			randomTuples.add(allTuples.get(i));
		}
		
		Utils.checkTrue(num == randomTuples.size());
		
		return randomTuples;
	}
	
	static List<PredicateProfileTuple> selectOneMostSimilarProfileTuples(PredicateProfileDatabase db, PredicateProfileTuple target,
			DistanceType distanceType) {
		List<PredicateProfileTuple> mostSimilarTuples = Collections.singletonList(db.findTheMostSimilarTuple(target, distanceType));
		Utils.checkTrue(mostSimilarTuples.size() == 1);
		return mostSimilarTuples;
	}
	
	static List<PredicateProfileTuple> selectOneLeastSimilarProfileTuples(PredicateProfileDatabase db, PredicateProfileTuple target,
			DistanceType distanceType) {
		List<PredicateProfileTuple> leastSimilarTuples = Collections.singletonList(db.findTheLeastSimilarTuple(target, distanceType));
		Utils.checkTrue(leastSimilarTuples.size() == 1);
		return leastSimilarTuples;
	}

	public float getThreshold() {
		return distanceThreshold;
	}

	public void setThreshold(float threshold) {
		this.distanceThreshold = threshold;
	}

	public DistanceType getDistanceType() {
		return distanceType;
	}

	public void setDistanceType(DistanceType distanceType) {
		this.distanceType = distanceType;
	}

	public RankType getRankType() {
		return rankType;
	}

	public void setRankType(RankType rankType) {
		this.rankType = rankType;
	}

//	public CrossRunRank getCrossRank() {
//		return crossRank;
//	}
//
//	public void setCrossRank(CrossRunRank crossRank) {
//		this.crossRank = crossRank;
//	}

	public PredicateProfileTuple getBadRun() {
		return badRun;
	}

	public List<PredicateProfileTuple> getGoodRuns() {
		return goodRuns;
	}
	
	public ConfEntityRepository getConfEntityRepository() {
		return this.repository;
	}
	

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	public Collection<ConfPropOutput> getOutputs() {
		return outputs;
	}

	public void setOutputs(Collection<ConfPropOutput> outputs) {
		this.outputs = outputs;
	}
	
	public void setSelectionStrategy(SelectionStrategy strategy) {
		this.strategy = strategy;
	}
	
	/**
	 * A short cut for diagnosis
	 * */
	public static void diagnoseConfigErrors(String badRunTrace, String[] goodRunTraceArray,
			ConfEntityRepository repo, String srcDir, Collection<ConfPropOutput> confSlices,
			SelectionStrategy strategy) {
		diagnoseConfigErrors(badRunTrace, goodRunTraceArray, repo, srcDir, confSlices, strategy, MainAnalyzer.default_threshold);
	}
	
	public static void diagnoseConfigErrors(String badRunTrace, String[] goodRunTraceArray,
			ConfEntityRepository repo, String srcDir, Collection<ConfPropOutput> confSlices,
			SelectionStrategy strategy, float threshold) {
		Collection<String> goodRunTraces = Arrays.asList(goodRunTraceArray);
		
		MainAnalyzer analyzer = new MainAnalyzer(badRunTrace, goodRunTraces, repo, srcDir, confSlices);
		analyzer.setSelectionStrategy(strategy);
		analyzer.setThreshold(threshold);
		
		List<ConfDiagnosisOutput> outputs = analyzer.computeResponsibleOptions();
		int rank = 1;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < outputs.size(); i++) {
			ConfDiagnosisOutput o = outputs.get(i);
			if(filterOutput(o)) {
				continue;
			}
			System.out.println((i + 1) + ".");
			System.out.println(o.getConfEntity());
			System.out.println(o.getBriefExplanation());
			System.out.println();
			System.out.println(o.getErrorReport());
			System.out.println();
			System.out.println("Report size: " + o.getReports().size());
			System.out.println();
			System.out.println(o.getTotalEnter() + " / " + o.getTotalEval());
			System.out.println();
			
			sb.append((rank ++) + ".");
			sb.append(Globals.lineSep);
			sb.append(o.getConfEntity());
//			sb.append(Globals.lineSep);
//			sb.append(o.getBriefExplanation());
			sb.append(Globals.lineSep);
			sb.append(Globals.lineSep);
			sb.append(o.getErrorReport());
			sb.append(Globals.lineSep);
			sb.append(Globals.lineSep);
//			sb.append("Report size: " + o.getReports().size());
//			sb.append(Globals.lineSep);
			
//			int j = 0;
//			for(String  r : o.getReports()) {
//				j++;
//				sb.append(j + ". " + Globals.lineSep);
//				sb.append(r);
//				sb.append(Globals.lineSep);
//			}
			
//			sb.append(Globals.lineSep);
//			sb.append(o.getTotalEnter() + " / " + o.getTotalEval());
//			sb.append(Globals.lineSep);
//			sb.append(Globals.lineSep);
//			sb.append("=================");
//			sb.append(Globals.lineSep);
			
			System.out.println("Current report num: " + i);
		}
		
		Files.writeToFileNoExp(sb.toString(), MainAnalyzer.result_output_file);
	}
	
	private static boolean filterOutput(ConfDiagnosisOutput o) {
		String report = o.getErrorReport();
		String[] array = report.split(Globals.lineSep);
		List<String> list = new LinkedList<String>();
		for(String s : array) {
			if(s.indexOf("classpath") != -1) {
				return true;
			}
			if(s.indexOf(ExplanationGenerator.TOKEN) != -1) {
				String number = s.substring(0, s.indexOf("%"));
				list.add(number);
			}
		}
		Utils.checkTrue(list.size() == 2);
		
		if(list.get(0).equals(list.get(1))) {
			return true;
		}
		
//		if(list.get(0).trim().startsWith("NaN") || list.get(1).trim().startsWith("NaN")) {
//			return true;
//		}
		
		return false;
	}
}