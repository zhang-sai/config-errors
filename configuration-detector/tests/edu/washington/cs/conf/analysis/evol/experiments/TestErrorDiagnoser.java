package edu.washington.cs.conf.analysis.evol.experiments;

import java.util.Set;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.evol.CodeAnalysisUtils;
import edu.washington.cs.conf.analysis.evol.CodeAnalyzer;
import edu.washington.cs.conf.analysis.evol.CodeAnalyzerRepository;
import edu.washington.cs.conf.analysis.evol.ErrorDiagnoser;
import edu.washington.cs.conf.analysis.evol.EvolConfOptionRepository;
import edu.washington.cs.conf.analysis.evol.SimpleChecks;
import edu.washington.cs.conf.analysis.evol.TraceRepository;
import edu.washington.cs.conf.analysis.evol.TracesWrapper;
import edu.washington.cs.conf.util.Utils;
import junit.framework.TestCase;

public class TestErrorDiagnoser extends TestCase {
	
	boolean useFullSlicing = false;
	
	public void setUp() {
		if(useFullSlicing) {
			System.err.println("Use full slicing in error diagnosis...");
		    this.useFullSlicing();
		}
	}
	
	//just for experiment purpose
	private void useFullSlicing() {
		EvolConfOptionRepository.randoopOldCacheFile = EvolConfOptionRepository.randoopOldVersionFullSlicing;
		EvolConfOptionRepository.randoopNewCacheFile = EvolConfOptionRepository.randoopNewVersionFullSlicing;
		EvolConfOptionRepository.wekaOldCacheFile = EvolConfOptionRepository.wekaOldVersionFullSlicing;
		EvolConfOptionRepository.wekaNewCacheFile = EvolConfOptionRepository.wekaNewVersionFullSlicing;
		EvolConfOptionRepository.synopticOldCacheFile = EvolConfOptionRepository.synopticOldVersionFullSlicing;
		EvolConfOptionRepository.synopticNewCacheFile = EvolConfOptionRepository.synopticNewVersionFullSlicing;
		EvolConfOptionRepository.jchordOldCacheFile = EvolConfOptionRepository.jchordOldVersionFullSlicing;
		EvolConfOptionRepository.jchordNewCacheFile = EvolConfOptionRepository.jchordNewVersionFullSlicing;
	}
	
	public void testRandoop() {
		ConfEntityRepository oldConf = EvolConfOptionRepository.randoopOldConfs();
		ConfEntityRepository newConf = EvolConfOptionRepository.randoopNewConfs();
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getRandoop121Analyzer();
		oldCoder.buildAnalysis();
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getRandoop132Analyzer();
		newCoder.buildAnalysis();
		TracesWrapper wrapper = TraceRepository.getRandoopTraces();
		
		ErrorDiagnoser diagnoser = new ErrorDiagnoser(oldConf, newConf, oldCoder, newCoder, wrapper);
//		diagnoser.setOnlyUsePredicate(true); //just for experiments
		diagnoser.diagnoseRootCauses();
	}
	
	public void testWeka() {
		ConfEntityRepository oldConf = EvolConfOptionRepository.wekaOldConfs();
		ConfEntityRepository newConf = EvolConfOptionRepository.wekaNewConfs();
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getWekaOldAnalyzer();
		oldCoder.buildAnalysis();
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getWekaNewAnalyzer();
		newCoder.buildAnalysis();
		TracesWrapper wrapper = TraceRepository.getWekaTraces();
		
		ErrorDiagnoser diagnoser = new ErrorDiagnoser(oldConf, newConf, oldCoder, newCoder, wrapper);
//		diagnoser.setOnlyUsePredicate(true);
		diagnoser.diagnoseRootCauses();
	}
	
	public void testSynoptic() {
		ConfEntityRepository oldConf = EvolConfOptionRepository.synopticOldConfs();
		ConfEntityRepository newConf = EvolConfOptionRepository.synopticNewConfs();
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getSynopticOldAnalyzer();
		oldCoder.buildAnalysis();
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getSynopticNewAnalyzer();
		newCoder.buildAnalysis();
		TracesWrapper wrapper = TraceRepository.getSynopticTraces();
		
		ErrorDiagnoser diagnoser = new ErrorDiagnoser(oldConf, newConf, oldCoder, newCoder, wrapper);
//		diagnoser.setOnlyUsePredicate(true);
		diagnoser.diagnoseRootCauses();
	}
	
	public void testJMeter() {
		ConfEntityRepository oldConf = EvolConfOptionRepository.jmeterOldConfs();
		ConfEntityRepository newConf = EvolConfOptionRepository.jmeterNewConfs();
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getJMeterOldAnalyzer();
		oldCoder.buildAnalysis();
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getJMeterNewAnalyzer();
		newCoder.buildAnalysis();
		TracesWrapper wrapper = TraceRepository.getJMeterTraces();
		
		ErrorDiagnoser diagnoser = new ErrorDiagnoser(oldConf, newConf, oldCoder, newCoder, wrapper);
//		diagnoser.setOnlyUsePredicate(true);
		diagnoser.diagnoseRootCauses();
	}
	
	public void testJChord_SSA() {
		ConfEntityRepository oldConf = EvolConfOptionRepository.jchordOldConfs();
		ConfEntityRepository newConf = EvolConfOptionRepository.jchordNewConfs();
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getJChordOldAnalyzer();
		oldCoder.buildAnalysis();
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getJChordNewAnalyzer();
		newCoder.buildAnalysis();
		
		//set up for jchord
		SimpleChecks.unique_matching = true;
		String[] pkgs = new String[]{"chord."};
		Set<String> uniqueSet1 = CodeAnalysisUtils.findUniquelyInvokedMethods(oldCoder, pkgs);
		Set<String> uniqueSet2 = CodeAnalysisUtils.findUniquelyInvokedMethods(newCoder, pkgs);
		Set<String> uniqueIntersect = Utils.intersect(uniqueSet1, uniqueSet2);
		SimpleChecks.uniqueMethods = uniqueIntersect;
		//end of set up
		
		//the different part
		TracesWrapper wrapper = TraceRepository.getChordTraces_SSA();
		
		ErrorDiagnoser diagnoser = new ErrorDiagnoser(oldConf, newConf, oldCoder, newCoder, wrapper);
//		diagnoser.setOnlyUsePredicate(true);
		diagnoser.diagnoseRootCauses();
	}
	
	public void testJChord_Print() {
		ConfEntityRepository oldConf = EvolConfOptionRepository.jchordOldConfs();
		ConfEntityRepository newConf = EvolConfOptionRepository.jchordNewConfs();
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getJChordOldAnalyzer();
		oldCoder.buildAnalysis();
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getJChordNewAnalyzer();
		newCoder.buildAnalysis();
		
		//set up for jchord
		SimpleChecks.unique_matching = true;
		String[] pkgs = new String[]{"chord."};
		Set<String> uniqueSet1 = CodeAnalysisUtils.findUniquelyInvokedMethods(oldCoder, pkgs);
		Set<String> uniqueSet2 = CodeAnalysisUtils.findUniquelyInvokedMethods(newCoder, pkgs);
		Set<String> uniqueIntersect = Utils.intersect(uniqueSet1, uniqueSet2);
		SimpleChecks.uniqueMethods = uniqueIntersect;
		//end of set up
		
		TracesWrapper wrapper = TraceRepository.getChordTraces_Print();
		
		ErrorDiagnoser diagnoser = new ErrorDiagnoser(oldConf, newConf, oldCoder, newCoder, wrapper);
//		diagnoser.setOnlyUsePredicate(true);
		diagnoser.diagnoseRootCauses();
	}
	
	
	public void testJavalanche() {
		ConfEntityRepository oldConf = EvolConfOptionRepository.javalancheOldConfs();
		ConfEntityRepository newConf = EvolConfOptionRepository.javalancheNewConfs();
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getJavalancheOldAnalyzer();
		oldCoder.buildAnalysis();
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getJavalancheNewAnalyzer();
		newCoder.buildAnalysis();
		TracesWrapper wrapper = TraceRepository.getJavalancheTraces();
		
		ErrorDiagnoser diagnoser = new ErrorDiagnoser(oldConf, newConf, oldCoder, newCoder, wrapper);
		diagnoser.setFuzzMatching(true);
//		diagnoser.setOnlyUsePredicate(true);
		diagnoser.diagnoseRootCauses();
	}

}
