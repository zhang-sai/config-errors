package edu.washington.cs.conf.analysis;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.washington.cs.conf.util.Log;
import edu.washington.cs.conf.util.Utils;

import junit.framework.TestCase;

public class TestConfPropagationAnalyzer extends TestCase {
	
	public void testFields() {
		Log.logConfig("./log.txt");
		
		List<String> options = new LinkedList<String>();
		options.add("test.slice.depfield.FieldDeps.field_value");
		options.add("test.slice.depfield.FieldDeps.f_value");
		
		List<Boolean> isStatics = new LinkedList<Boolean>();
		isStatics.add(true);
		isStatics.add(false);
		
		String path = "D:\\research\\configurations\\workspace\\configuration-detector\\bin\\test\\slice\\depfield";
		String mainClass = "Ltest/slice/depfield/FieldDeps";
		
		ConfPropagationAnalyzer analyzer = new ConfPropagationAnalyzer(options, null, isStatics, path, mainClass);
		
		List<ConfPropOutput> outputs = analyzer.doAnalysis();
		
		for(ConfPropOutput output : outputs) {
			System.out.println(output);
			System.out.println("See shrikepoints:");
			System.out.println("+ all shrike points");
			Set<ShrikePoint> pts = output.getAllShrikePoints();
			System.out.println(Utils.dumpCollection(pts));
			System.out.println("+ all numbered points");
			pts = output.getNumberedShrikePoints();
			System.out.println(Utils.dumpCollection(pts));
			System.out.println("+ all branch points");
			pts = output.getNumberedBranchShrikePoints();
			System.out.println(Utils.dumpCollection(pts));
			System.out.println("-----");
		}
	}

}
