package edu.washington.cs.conf.experiments.weka;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.diagnosis.MainAnalyzer;
import edu.washington.cs.conf.experiments.RandoopExpUtils;
import edu.washington.cs.conf.experiments.WekaExpUtils;
import junit.framework.TestCase;

public class TestWekaFullSlice extends TestCase {

	String laborRun = "./experiments/weka-database/weka-labor-full-slice.txt";
	
	String irisRun = "./experiments/weka-database/weka-iris-full-slice.txt";
	String weatherRun = "./experiments/weka-database/weka-weather-full-slice.txt";
	
	String[] db = new String[]{irisRun, weatherRun};
	
	public void testDiagnoseSimilar() {
		ConfEntityRepository repo = WekaExpUtils.getWekaRepository();
		MainAnalyzer.diagnoseConfigErrors(laborRun,
				db,
				repo,
				null, null, null);
	}
	
}
