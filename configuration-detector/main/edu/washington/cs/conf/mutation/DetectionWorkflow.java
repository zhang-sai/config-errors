package edu.washington.cs.conf.mutation;

import java.util.Collection;

import edu.washington.cs.conf.util.Utils;

public class DetectionWorkflow {
	
	private ProgramRunner runner = null;
	
	private ReportGenerator reporter = new ReportGenerator();
	
	private UserManual manual = null;
	
	public DetectionWorkflow() {
		//empty
	}
	
//	public DetectionWorkflow(Class<? extends ProgramRunner> clz) {
//		try {
//			runner = clz.newInstance();
//		} catch (Exception e) {
//			System.out.println("Cannot instantiate class: " + clz);
//			e.printStackTrace();
//		}
//	}
	
	public void setProgramRunner(ProgramRunner runner) {
		Utils.checkNotNull(runner);
		this.runner = runner;
	}
	
	public void setUserManual(UserManual manual) {
		Utils.checkNotNull(manual);
		this.manual = manual;
	}

	public void detect() {
		Utils.checkNotNull(runner);
		Utils.checkNotNull(reporter);
		
		//collect the results
		runner.setUpEnv();
		Collection<ExecResult> execResults = runner.execute();
		runner.clearEnv();
		
		System.out.println("number of exec results: " + execResults.size());
		for(ExecResult result : execResults) {
//			System.out.println(result);
			//analyze it and generate the report
			if(result.pass()) {
				continue; //do nothing
			}
			//get the config option and message
			String option = result.getMutatedOption();
			String message = result.getMessage();
			//check the adequancy of the error message
			MessageAdequacy adequancy = MessageAnalyzer.isMessageAdequate(result, manual);
			if(!adequancy.isAdequate()) {
				//generate a report
				reporter.addToReport(adequancy, result);
			}
		}
		
		System.out.println("Number of messages in report: " + reporter.size());
		
	}
	
	public ReportGenerator getReport() {
		return this.reporter;
	}
	
}
