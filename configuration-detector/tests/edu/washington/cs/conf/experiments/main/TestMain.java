package edu.washington.cs.conf.experiments.main;

import java.io.File;
import java.io.FileNotFoundException;

import junit.framework.TestCase;

import edu.washington.cs.conf.diagnosis.DiagnoserMain;
import edu.washington.cs.conf.experiments.jchord.TestCrashingErrorDiagnosisExperimental;
import edu.washington.cs.conf.util.Utils;

public class TestMain extends TestCase {

	public void testRandoopNonCrashing() throws FileNotFoundException {
		String[] args = new String[]{
				"--config_options=./tests/edu/washington/cs/conf/experiments/main/randoop.options.txt",
				"--source_dir=./subjects/randoop/randoop-src",
				"--classpath_for_slicing=./subjects/randoop-jamie-no-trace.jar;./subjects/plume.jar",
				"--main_for_slicing=Lrandoop/main/Main",
				"--db_dir=./experiments/randoop-database/main",
				"--bad_run_trace=./experiments/randoop-database/bad-nano-xml-100s-pruned.txt"
		};
		DiagnoserMain.main(args);
	}
	
	public void testWekaNonCrashing() throws FileNotFoundException {
		String[] args = new String[]{
				"--config_options=./tests/edu/washington/cs/conf/experiments/main/weka.options.txt",
				"--source_dir=D:\\research\\configurations\\workspace\\weka-3.6\\main\\java",
				"--classpath_for_slicing=./subjects/weka/weka-no-trace.jar;./subjects/weka/JFlex.jar;./subjects/weka/java-cup.jar",
				"--main_for_slicing=Lweka/classifiers/trees/J48",
				"--db_dir=./experiments/weka-database/main",
				"--bad_run_trace=./experiments/weka-database/bad-labor.txt"
		};
		DiagnoserMain.main(args);
	}
	
	public void testSootNonCrashing() throws FileNotFoundException {
		String dir = "./subjects/soot-2.5/";
		String path = dir + "soot.jar;" +
		        dir + "libs/coffer.jar;" +
		        dir + "libs/jasminclasses-2.5.0.jar;" +
		        dir + "libs/java_cup.jar;" +
		        dir + "libs/JFlex.jar;" +
		        dir + "libs/pao.jar;" +
		        dir + "libs/polyglot.jar;" +
		        dir + "libs/pth.jar";
		
		String[] args = new String[]{
				"--config_options=./tests/edu/washington/cs/conf/experiments/main/soot.options.txt",
				"--source_dir=D:\\research\\configurations\\workspace\\soot-2.5\\src",
				"--classpath_for_slicing=" + path,
				"--main_for_slicing=Lsoot/Main",
				"--db_dir=./experiments/soot-database/main",
				"--bad_run_trace=./experiments/soot-database/soot_helloworld_no_keepline.txt",
				"--cg_type=ZeroCFA",
				"--ingorable_class_file=SootExclusions.txt"
		};
		DiagnoserMain.main(args);
	}
	
	public void testJChordNonCrashing() throws FileNotFoundException {
		String[] args = new String[]{
				"--config_options=./tests/edu/washington/cs/conf/experiments/main/jchord.options.txt",
				"--source_dir=D:\\research\\configurations\\workspace\\main\\src",
				"--classpath_for_slicing=./subjects/jchord/chord-no-trace.jar",
				"--main_for_slicing=Lchord/project/Main",
				"--db_dir=./experiments/jchord-database/main",
				"--bad_run_trace=./experiments/jchord-database/simpletest-no-race.txt",
				"--ingorable_class_file=ChordExclusions.txt"
		};
		DiagnoserMain.main(args);
	}
	
	public void testSynopticNonCrashing() throws FileNotFoundException {
		String path = "./subjects/synoptic/synoptic.jar;"
			+ "./subjects/synoptic/libs/plume.jar;"
			+ "./subjects/synoptic/libs/commons-io-2.0.1.jar;"
			+ "./subjects/synoptic/libs/commons-fileupload-1.2.2.jar;"
			+ "./subjects/synoptic/libs/junit-4.9b2.jar";
		
		String[] args = new String[]{
				"--config_options=./tests/edu/washington/cs/conf/experiments/main/synoptic.options.txt",
				"--source_dir=D:\\research\\configurations\\workspace\\synoptic\\src",
				"--classpath_for_slicing=" + path,
				"--main_for_slicing=Lsynoptic/main/Main",
				"--db_dir=./experiments/synoptic-database/main",
				"--bad_run_trace=./experiments/synoptic-database/2pc_3nodes_100tx_bad-injected.txt"
		};
		DiagnoserMain.main(args);
	}
	
	public void testNineCrashingErrors() throws FileNotFoundException {
		String[] crashingFiles = TestCrashingErrorDiagnosisExperimental.allCrashingTraces;
		String[] crashingStackTraces = TestCrashingErrorDiagnosisExperimental.allStackTraces;
		Utils.checkTrue(crashingFiles.length == crashingStackTraces.length && crashingFiles.length == 9);
		
		//diagnose one by one
		for(int i = 0; i < crashingFiles.length; i++) {
			String crashingFile = crashingFiles[i];
			String crashingStackTrace = crashingStackTraces[i];
			String outputFile = "./" + (new File(crashingFile).getName() + "_diagnose_results.txt");
			
			String[] args = new String[]{
					"--config_options=./tests/edu/washington/cs/conf/experiments/main/jchord.options.txt",
					"--source_dir=D:\\research\\configurations\\workspace\\main\\src",
					"--classpath_for_slicing=./subjects/jchord/chord-no-trace.jar",
					"--main_for_slicing=Lchord/project/Main",
					"--db_dir=./experiments/jchord-database/main",
					"--bad_run_trace=" + crashingFile,
					"--ingorable_class_file=ChordExclusions.txt",
					"--stacktrace_file=" + crashingStackTrace,
					"--cache_slice=true",
					"--diagnose_result_file=" + outputFile,
					"--noncrashing=false"
			};
			DiagnoserMain.main(args);
		}
	}
}