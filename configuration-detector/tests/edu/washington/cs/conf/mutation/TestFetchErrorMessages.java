package edu.washington.cs.conf.mutation;

import java.util.Collection;

import edu.washington.cs.conf.instrument.evol.TestInstrumentPrograms;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;

import junit.framework.TestCase;

public class TestFetchErrorMessages extends TestCase {
	
	public void testJMeter() {
		String jmeterJar = TestInstrumentPrograms.jmeter29InputJar;
		String outputFile = "./jmeter_msg.txt";
		writeErrorMessageToFile(jmeterJar, outputFile);
	}

	public void writeErrorMessageToFile(String inputJar, String outputFileName) {
		ConstantMsgFetcher fetcher = new ConstantMsgFetcher();
		try {
			Collection<String> msgs = fetcher.findAllStringMsg(inputJar);
			StringBuilder sb = new StringBuilder();
			for(String msg : msgs) {
				sb.append(msg);
				sb.append(Globals.lineSep);
			}
			Files.writeToFile(sb.toString(), outputFileName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Error(e);
		}
	}
	
}
