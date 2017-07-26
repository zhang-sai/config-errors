package edu.washington.cs.conf.mutation;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.mutation.weka.TestWekaConfigExamples;
import edu.washington.cs.conf.util.Utils;

import junit.framework.TestCase;

public class TestReflectionExecutor extends TestCase {
	
	public void testToyExample() {
		Method m = Utils.getMainMethod(SampleCode.CLASS_NAME);
		List<String> args = new LinkedList<String>();
		
		for(int i = 0; i < 10; i++) {
		    Throwable error = ReflectionExecutor.executeReflectionCode(m, args);
		    System.out.println(error);
		}
		
		args.add("good");
		for(int i = 0; i < 10; i++) {
		    Throwable error = ReflectionExecutor.executeReflectionCode(m, args);
		    System.out.println(error);
		}
		
		args.clear();
		args.add("bad");
		for(int i = 0; i < 10; i++) {
		    Throwable error = ReflectionExecutor.executeReflectionCode(m, args);
		    System.out.println(error);
		}
		
		args.clear();
		args.add("sleep");
		for(int i = 0; i < 10; i++) {
		    Throwable error = ReflectionExecutor.executeReflectionCode(m, args);
		    System.out.println(error);
		}
	}
	
	public void testWeka() {
		Method m = Utils.getMainMethod(TestWekaConfigExamples.main_zeror);
		List<String> args = new LinkedList<String>();
		args.add("-t");
		args.add("./subjects/weka/weather.arff");
		
		Throwable error = ReflectionExecutor.executeReflectionCode(m, args);
	    System.out.println(error);
	}
	
}
