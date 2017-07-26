package edu.washington.cs.conf.mutation;

import junit.framework.TestCase;

public class TestConfParser extends TestCase {

	public void testParseJettyConfig() {
		//if multiple option appear in the same configuration file
		//only the last one counts
		String filePath = "E:\\conf-vul\\programs\\jetty\\jetty-distribution-9.2.1.v20140609\\demo-base\\start.ini";
		ConfFileParser parser = new ConfFileParser(filePath);
		parser.parse();
		parser.dumpFile();
		System.out.println(parser.getConfOptionNames());
		System.out.println(parser.getConfOptionValues());
	}
	
	public void testParseJMeterConfig() {
		String filePath = "./sample-config-files/jmeter.properties";
		ConfFileParser reader = new ConfFileParser(filePath);
		reader.parse();
		reader.dumpFile();
	}
}
