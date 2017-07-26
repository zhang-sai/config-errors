package edu.washington.cs.conf.mutation;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

public class TestMutatedConfig extends TestCase {

	public void testSampleConfig() {
		ConfFileParser p = new ConfFileParser(Arrays.asList("p=2", "t=./subjects/weka/weather.arff",
				"foo=foo_original"));
		p.parse();
		
		Map<String, String> baseOptions = new LinkedHashMap<String, String>();
		//"-p", "2",  "-t", "./subjects/weka/weather.arff"
		baseOptions.put("p", "222");
		
		String mutatedConf = "foo";
		String mutatedValue = "foo_mutated";
		
		MutatedConf conf = new MutatedConf(p, mutatedConf, mutatedValue, 2);
		
		System.out.println(Arrays.asList(conf.createCmdLineAsArgs()));
		assertEquals(Arrays.asList(conf.createCmdLineAsArgs()).toString(), "[-p, 2, -t, ./subjects/weka/weather.arff, -foo, foo_mutated]");
		
		System.out.println(Arrays.asList(conf.createCmdLinesAsArgs(baseOptions)));
		assertEquals(Arrays.asList(conf.createCmdLinesAsArgs(baseOptions)).toString(), "[-foo, foo_mutated, -p, 222]");
		
		baseOptions.clear();
		baseOptions.put("bar", "bar_value");
		System.out.println(Arrays.asList(conf.createCmdLinesAsArgs(baseOptions)));
		assertEquals(Arrays.asList(conf.createCmdLinesAsArgs(baseOptions)).toString(), "[-foo, foo_mutated, -bar, bar_value]");
		
		baseOptions.clear();
		baseOptions.put("foo", "new_mutated_foo_value");
		System.out.println(Arrays.asList(conf.createCmdLinesAsArgs(baseOptions)));
		assertEquals(Arrays.asList(conf.createCmdLinesAsArgs(baseOptions)).toString(), "[-foo, new_mutated_foo_value]");
	}
	
	public void testConfMutator() {
		String filePath = "./sample-config-files/jmeter.properties";
		ConfMutator mutator = new ConfMutator(filePath);
		List<MutatedConf> mutatedConfList = mutator.mutateConfFile();
		System.out.println(mutatedConfList.size());
//		for(String optionName : mutator.parser.getOptions()) {
//			mutator.createMutatedValues(optionName);
//		}
		String outputDir = "./sample-config-files-after-mutated";
		int i = 0;
		for(MutatedConf mConf : mutatedConfList) {
			String outputFileName = outputDir + "/" + mutator.getParser().getNextMutatedFileName();
			mConf.writeToFile(outputFileName);
			i++;
			if(i > 10) {
				break;
			}
		}
	}
	
}
