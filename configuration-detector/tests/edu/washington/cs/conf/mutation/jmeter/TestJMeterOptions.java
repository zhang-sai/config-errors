package edu.washington.cs.conf.mutation.jmeter;

import java.util.List;

import edu.washington.cs.conf.mutation.ConfMutator;
import edu.washington.cs.conf.mutation.MutatedConf;
import junit.framework.TestCase;

public class TestJMeterOptions extends TestCase {

	public static String jmeterProp = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin\\good-jmeter.properties";
	
	public static String sysProp = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin\\good-system.properties";
	
	static List<MutatedConf> getMutatedJMeterOptions() {
		ConfMutator mutator = new ConfMutator(jmeterProp);
		List<MutatedConf> mutates = mutator.mutateConfFile();
		return mutates;
	}
	
	static List<MutatedConf> getMutatedSystemOptions() {
		ConfMutator mutator = new ConfMutator(sysProp);
		List<MutatedConf> mutates = mutator.mutateConfFile();
		return mutates;
	}
	
	public void testJMeterProperties() {
		List<MutatedConf> mutates = getMutatedJMeterOptions();
		System.out.println(mutates.size());
		mutates = getMutatedSystemOptions();
		System.out.println(mutates.size());
	}
	
}
