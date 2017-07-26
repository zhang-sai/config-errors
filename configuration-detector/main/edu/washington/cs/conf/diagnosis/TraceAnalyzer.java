package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import edu.washington.cs.conf.instrument.AbstractInstrumenter;
import edu.washington.cs.conf.instrument.ConfInstrumenter;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

public class TraceAnalyzer {
	
	public final Collection<String> goodTraces;
	public final Collection<String> badTraces;
	
	public TraceAnalyzer(Collection<String> goodTraces, Collection<String> badTraces) {
		this.goodTraces = goodTraces;
		this.badTraces = badTraces;
	}
	
	public TraceAnalyzer(String goodTraceFileName, String badTraceFileName) {
		this.goodTraces = Files.readWholeNoExp(goodTraceFileName);
		this.badTraces = Files.readWholeNoExp(badTraceFileName);
	}
	
	public Collection<PredicateProfile> getGoodProfiles() {
		return createProfiles(this.goodTraces);
	}
	
	public Collection<PredicateProfile> getBadProfiles() {
		return createProfiles(this.badTraces);
	}
	
	public static Collection<PredicateProfile> createProfiles(String traceFileName) {
		Collection<String> traces = Files.readWholeNoExp(traceFileName);
		return createProfiles(traces);
	}
	
	public static PredicateProfileTuple createGoodProfileTuple(String traceFileName, String tupleName) {
		Collection<PredicateProfile> ps = createProfiles(traceFileName);
		return PredicateProfileTuple.createGoodRun(tupleName, ps);
	}
	
	public static PredicateProfileTuple createBadProfileTuple(String traceFileName, String tupleName) {
		Collection<PredicateProfile> ps = createProfiles(traceFileName);
		return PredicateProfileTuple.createBadRun(tupleName, ps);
	}
	
	public static Collection<PredicateProfile> createProfiles(Collection<String> traces) {
		Collection<PredicateProfile> profiles = new LinkedHashSet<PredicateProfile>();
		
		//a data structure to memorize the already processed parts
		Map<String, PredicateProfile> profileMap = new LinkedHashMap<String, PredicateProfile>();
		
		for(String trace : traces) {
			String[] splits = splitLine(trace, 4);
			//it may have recorded multiple confs, see ConfInstrumenter for details
			String confs = splits[1];
			String fullRecordedContext = splits[2];
			String point = splits[0];
			int count = Integer.parseInt(splits[3]);
			
			String[] confIds = confs.split(AbstractInstrumenter.CONF_SEP);
			Utils.checkTrue(confIds.length > 0);
			
			//check the context field
			String[] items = fullRecordedContext.split(ConfInstrumenter.SUB_SEP);
			int srcLineNum = -1;
			String context = fullRecordedContext;
			String predicateTxt = "\"N/A in creating Profiles\"";
			if(items.length > 1) {
			    Utils.checkTrue(items.length == 3);
			    srcLineNum = Integer.parseInt(items[0]);
			    predicateTxt = items[1];
			    context = items[2];
			}
			//process each configuration
			for(String confId : confIds) {
				String key = confId + context; //assume this could uniquely identify a position FIXME
				//can not check this, since is an entering and an evaluation
//				Utils.checkTrue(!profileMap.containsKey(key), "key is: " + key);
				if(!profileMap.containsKey(key)) {
					//FIXME this constructor should be replaced, since the count is setting below
					profileMap.put(key, new PredicateProfile(confId, context));
				}
				PredicateProfile p = profileMap.get(key);
				Utils.checkTrue(p.getEnteringCount() == 0 || p.getEvaluatingCount() == 0);
				p.setSourceLineNumber(srcLineNum); //set source line number of source code
				p.setPredicateInSource(predicateTxt);
				if(point.equals(ConfInstrumenter.PRE)) {
					p.setEvaluatingCount(count);
				} else if (point.equals(ConfInstrumenter.POST)) {
					p.setEnteringCount(count);
				} else {
					throw new Error(point);
				}
			}
		}
		
		profiles.addAll(profileMap.values());
		System.err.println("Number of profiles: " + profiles.size());
		
		return profiles;
	}
	
	private static String[] splitLine(String line, int expectedNum) {
		String[] splits = line.split(ConfInstrumenter.SEP);
		Utils.checkTrue(splits.length == expectedNum, "Length: " + splits.length);
		return splits;
	}
}
