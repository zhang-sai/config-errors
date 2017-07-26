package edu.washington.cs.conf.analysis.evol;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.washington.cs.conf.instrument.evol.EfficientTracer;
import edu.washington.cs.conf.instrument.evol.TestInstrumentPrograms;
import edu.washington.cs.conf.util.Files;

public class TraceRepository {
	@Deprecated
	public static String randoopOldTrace = "./evol-experiments/randoop/randoop-1.2.1.txt";
	@Deprecated
	public static String randoopNewTrace = "./evol-experiments/randoop/randoop-1.3.2.txt";
	@Deprecated
	public static String synopticOldTrace = "./evol-experiments/synoptic/synoptic-0.05.txt";
	@Deprecated
	public static String synopticNewTrace = "./evol-experiments/synoptic/synoptic-0.1.txt";
	@Deprecated
	public static String jmeterOldTrace = "./evol-experiments/jmeter/jmeter-2.8.txt";
	@Deprecated
	public static String jmeterNewTrace = "./evol-experiments/jmeter/jmeter-2.9.txt";
	@Deprecated
	public static String wekaOldTrace = "./evol-experiments/weka/weka-3.6.1.txt";
	@Deprecated
	public static String wekaNewTrace = "./evol-experiments/weka/weka-3.6.2.txt";
	@Deprecated
	public static String jchordP1OldTrace = "./evol-experiments/jchord/problem-1/jchord-2.0.txt";
	@Deprecated
	public static String jchordP1NewTrace = "./evol-experiments/jchord/problem-1/jchord-2.1.txt";
	@Deprecated
	public static String jchordP2OldTrace = "./evol-experiments/jchord/problem-2/jchord-2.0.txt";
	@Deprecated
	public static String jchordP2NewTrace = "./evol-experiments/jchord/problem-2/jchord-2.1.txt";
	
	/**
	 * The above traces are obsoleted
	 * */
	//execution trace from instrumenting all statements
	static String history_dump = "history_dump.txt";
	static String predicate_dump = "predicate_dump.txt";
	
	public static String synopticOldSig = TestInstrumentPrograms.synoptic_05_sigmap;
	public static String synopticNewSig = TestInstrumentPrograms.synoptic_10_sigmap;
	public static String synopticOldDir = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.0.5\\tmp-output-folder\\";
	public static String synopticNewDir = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.1\\tmp-output-folder\\";
	public static String synopticOldHistoryDump = synopticOldDir + history_dump;
	public static String synopticOldPredicateDump = synopticOldDir + predicate_dump;
	public static String synopticNewHistoryDump = synopticNewDir + history_dump;
	public static String synopticNewPredicateDump = synopticNewDir + predicate_dump;
	public static TracesWrapper getSynopticTraces() {
		return new TracesWrapper(synopticOldSig, synopticNewSig, 
				synopticOldPredicateDump, synopticNewPredicateDump,
				synopticOldHistoryDump, synopticNewHistoryDump,
				EvolConfOptionRepository.synopticOldCacheFile,
				EvolConfOptionRepository.synopticNewCacheFile);
	}
	
	public static String jmeterOldSig = TestInstrumentPrograms.jmeter_28_sigmap;
	public static String jmeterNewSig = TestInstrumentPrograms.jmeter_29_sigmap;
	public static String jmeterOldDir = "D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\bin\\tmp-output-folder\\";
	public static String jmeterNewDir = "D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\bin\\tmp-output-folder\\";
	public static String jmeterOldHistoryDump = jmeterOldDir + history_dump;
	public static String jmeterOldPredicateDump = jmeterOldDir + predicate_dump;
	public static String jmeterNewHistoryDump = jmeterNewDir + history_dump;
	public static String jmeterNewPredicateDump = jmeterNewDir + predicate_dump;
	public static TracesWrapper getJMeterTraces() {
		return new TracesWrapper(jmeterOldSig, jmeterNewSig, 
				jmeterOldPredicateDump, jmeterNewPredicateDump,
				jmeterOldHistoryDump, jmeterNewHistoryDump,
				EvolConfOptionRepository.jmeterOldCacheFile,
				EvolConfOptionRepository.jmeterNewCacheFile);
	}
	
	public static String wekaOldSig = TestInstrumentPrograms.weka_361_sigmap;
	public static String wekaNewSig = TestInstrumentPrograms.weka_362_sigmap;
	public static String wekaDir = "D:\\research\\confevol\\subject-programs\\weka\\tmp-output-folder\\";
	public static String wekaOldHistoryDump = wekaDir + "old_" + history_dump;
	public static String wekaOldPredicateDump = wekaDir + "old_" + predicate_dump;
	public static String wekaNewHistoryDump = wekaDir + "new_" + history_dump;
	public static String wekaNewPredicateDump = wekaDir + "new_" + predicate_dump;
	public static TracesWrapper getWekaTraces() {
		return new TracesWrapper(wekaOldSig, wekaNewSig, 
				wekaOldPredicateDump, wekaNewPredicateDump,
				wekaOldHistoryDump, wekaNewHistoryDump,
				EvolConfOptionRepository.wekaOldCacheFile,
				EvolConfOptionRepository.wekaNewCacheFile);
	}
	
	public static String randoopOldSig = TestInstrumentPrograms.randoop_121_sigmap;
	public static String randoopNewSig = TestInstrumentPrograms.randoop_132_sigmap;
	public static String randoopOldDir = "D:\\research\\confevol\\subject-programs\\randoop\\tmp-output-folder\\";
	public static String randoopNewDir = randoopOldDir;
	public static String randoopOldHistoryDump = randoopOldDir + "old_" + history_dump;
	public static String randoopOldPredicateDump = randoopOldDir + "old_" + predicate_dump;
	public static String randoopNewHistoryDump = randoopNewDir + "new_" + history_dump;
	public static String randoopNewPredicateDump = randoopNewDir + "new_" + predicate_dump;
	public static TracesWrapper getRandoopTraces() {
		return new TracesWrapper(randoopOldSig, randoopNewSig, 
				randoopOldPredicateDump, randoopNewPredicateDump,
				randoopOldHistoryDump, randoopNewHistoryDump,
				EvolConfOptionRepository.randoopOldCacheFile,
				EvolConfOptionRepository.randoopNewCacheFile);
	}
	
	public static String chordOldSig = TestInstrumentPrograms.chord_20_sigmap;
	public static String chordNewSig = TestInstrumentPrograms.chord_21_sigmap;
	
	static final String chordDir = "D:\\research\\confevol\\subject-programs\\jchord\\trace-files-icse2014\\";
	@Deprecated
	public static String chordOldHistoryDump_SSA = null;
	@Deprecated
	public static String chordNewHistoryDump_SSA = null;
	public static String chordOldPredicateDump_SSA = chordDir + "predicate_dump_SSA-chord-2.0.txt";
	public static String chordNewPredicateDump_SSA = chordDir + "predicate_dump_SSA-chord-2.1.txt";
//	@Deprecated
	static boolean dummy = true;
	public static TracesWrapper getChordTraces_SSA() {
		return new TracesWrapper(chordOldSig, chordNewSig,
				chordOldPredicateDump_SSA, chordNewPredicateDump_SSA,
				counting_ssa_old, counting_ssa_new,
//				chordOldHistoryDump_SSA, chordNewHistoryDump_SSA,
				EvolConfOptionRepository.jchordOldCacheFile,
				EvolConfOptionRepository.jchordNewCacheFile, dummy);
	}
	
	@Deprecated
	public static String chordOldHistoryDump_Print = null;
	@Deprecated
	public static String chordNewHistoryDump_Print = null;
	public static String chordOldPredicateDump_Print = chordDir + "predicate_dump_print-chord-2.0.txt";
	public static String chordNewPredicateDump_Print = chordDir + "predicate_dump_print-chord-2.1.txt";
//	@Deprecated
	public static TracesWrapper getChordTraces_Print() {
		return new TracesWrapper(chordOldSig, chordNewSig,
				chordOldPredicateDump_Print, chordNewPredicateDump_Print,
				counting_print_old, counting_print_new,
//				chordOldHistoryDump_Print, chordNewHistoryDump_Print,
				EvolConfOptionRepository.jchordOldCacheFile,
				EvolConfOptionRepository.jchordNewCacheFile, dummy);
	}
	private static String countingTraceDir = "D:\\research\\configurations\\";
	public static String counting_ssa_new = countingTraceDir + "instr_counting-new-ssa.txt";
	public static String counting_ssa_old = countingTraceDir + "instr_counting-old-ssa.txt";
	public static String counting_print_new = countingTraceDir + "instr_counting-new-print.txt";
	public static String counting_print_old = countingTraceDir + "instr_counting-old-print.txt";
	
	//the trace files below is for Javalanche
	static String traceDir = "D:\\research\\confevol\\subject-programs\\javalanche\\traces\\";
	public static String traceDir36 = traceDir + "0.36";
	public static String traceDir40 = traceDir + "0.40";
	public static String javalancheOldSig = TestInstrumentPrograms.javalanche_36;
	public static String javalancheNewSig = TestInstrumentPrograms.javalanche_40;
	public static Collection<String> getJavalancheOldPredicateFiles() {
		return getPredicateFiles(traceDir36);
	}
	public static Collection<String> getJavalancheOldTraceFiles() {
		return getTraceFiles(traceDir36);
	}
	
    public static Collection<String> getJavalancheNewPredicateFiles() {
    	return getPredicateFiles(traceDir40);
	}
    
    public static Collection<String> getJavalancheNewTraceFiles() {
    	return getTraceFiles(traceDir40);
    }
    
    static String oldMerged = traceDir36 + "\\merged_history.dump.txt";
    public static String getJavalancheMergedOldTraceFile() {
    	Collection<String> oldTraces = getJavalancheOldTraceFiles();
    	return Files.mergeFilesNoExp(oldTraces.toArray(new String[0]), oldMerged);
    }
    
    static String oldPredicateMerged = traceDir36 + "\\merged_predicate.dump.txt";
    
    static String newMerged = traceDir40 + "\\merged_history.dump.txt";
    public static String getJavalancheMergedNewTraceFile() {
    	Collection<String> newTraces = getJavalancheNewTraceFiles();
    	return Files.mergeFilesNoExp(newTraces.toArray(new String[0]), newMerged);
    }
    
    static String newPredicateMerged = traceDir40 + "\\merged_predicate.dump.txt";
    
    public static TracesWrapper getJavalancheTraces() {
		return new TracesWrapper(javalancheOldSig, javalancheNewSig, 
				oldPredicateMerged, newPredicateMerged,
				oldMerged, newMerged,
				EvolConfOptionRepository.javalancheOldCacheFile,
				EvolConfOptionRepository.javalancheNewCacheFile);
	}
    
    static Collection<String> getPredicateFiles(String dir) {
		try {
			List<File> files = Files.getFileListing(new File(dir));
			Collection<String> predicates = new HashSet<String>();
			for(File f : files) {
				if(f.getName().startsWith(EfficientTracer.PREDICATE_DUMP_FILE)) {
					predicates.add(f.getAbsolutePath());
				}
			}
			return predicates; 
		} catch (FileNotFoundException e) {
			throw new Error(e);
		}
	}
    static Collection<String> getTraceFiles(String dir) {
		try {
			List<File> files = Files.getFileListing(new File(dir));
			Collection<String> predicates = new HashSet<String>();
			for(File f : files) {
				if(f.getName().startsWith(EfficientTracer.HISTORY_DUMP_FILE)) {
					predicates.add(f.getAbsolutePath());
				}
			}
			return predicates; 
		} catch (FileNotFoundException e) {
			throw new Error(e);
		}
	}
}