package edu.washington.cs.conf.mutation.jetty;

import java.util.List;

import edu.washington.cs.conf.mutation.ConfFileParser;
import edu.washington.cs.conf.mutation.ConfMutator;
import edu.washington.cs.conf.mutation.MutatedConf;
import junit.framework.TestCase;

public class TestConfMutation extends TestCase {

	public void testParseConfig() {
		String filePath = "E:\\conf-vul\\programs\\jetty\\jetty-distribution-9.2.1.v20140609\\demo-base\\start.ini";
		ConfFileParser parser = new ConfFileParser(filePath);
		parser.parse();
		parser.dumpFile();
		System.out.println(parser.getConfOptionNames());
		System.out.println(parser.getConfOptionValues());
	}
	
	public void testConfMutator() {
		String filePath = "E:\\conf-vul\\programs\\jetty\\jetty-distribution-9.2.1.v20140609\\demo-base\\start.ini";
		ConfMutator mutator = new ConfMutator(filePath);
		List<MutatedConf> mutates = mutator.mutateConfFile();
		for(int i = 0; i < mutates.size(); i++) {
			MutatedConf conf = mutates.get(i);
			System.out.println(i + ". " + conf.getMutatedConfOption() + ", " + conf.getMutatedConfValue());
			conf.writeToFile("./sample-config-files-after-mutated/jetty-base-demo-" + i + ".txt");
		}
	}
	
}
