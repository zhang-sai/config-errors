package edu.washington.cs.conf.mutation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import plume.Pair;

//generate the error report for inadequate error messages
public class ReportGenerator {
	
	/**
	 * need to keep the full context
	 * */
	
	private Collection<Pair<MessageAdequacy, ExecResult>> data =
		new ArrayList<Pair<MessageAdequacy, ExecResult>>();

	public void addToReport(MessageAdequacy adequancy, ExecResult result) {
		//generate the report to describe the message inadequacy
		data.add(Pair.of(adequancy, result));
	}
	
	public void dumpReport(File file) {
		
	}
	
	public Collection<Pair<MessageAdequacy, ExecResult>> getCategorizedReports() {
		throw new RuntimeException();
	}
	
	public int size() {
		return data.size();
	}
}
