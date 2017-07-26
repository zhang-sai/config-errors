package edu.washington.cs.conf.diagnosis;

import java.util.Arrays;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.TestConfEntityRepository;
import junit.framework.TestCase;

public class TestMainAnalyzer extends TestCase {

	public void testMainExamples() {
		String goodRunTrace = "./tests/edu/washington/cs/conf/diagnosis/main-examples/goodrun.txt";
		String badRunTrace = "./tests/edu/washington/cs/conf/diagnosis/main-examples/badrun.txt";
		
		ConfEntityRepository repository = TestConfEntityRepository.getSampleConfEntityRepository();
		
		MainAnalyzer analyzer = new MainAnalyzer(badRunTrace, Arrays.asList(goodRunTrace), repository);
		analyzer.setThreshold(1.0f);
		
		List<ConfDiagnosisOutput> outputs = analyzer.computeResponsibleOptions();
		
		for(ConfDiagnosisOutput output : outputs) {
		    System.out.println(output);
		}
		
		assertEquals(outputs.size(), 6);
	}
}