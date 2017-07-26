package edu.washington.cs.conf.mutation.jmeter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.mutation.MutatedConf;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;
import junit.framework.TestCase;

public class TestJMeterResults extends TestCase {
	
	public boolean isCSVFile(String file) {
		List<String> lines = Files.readWholeNoExp(file);
		for(int i = 0; i < lines.size() - 1; i++) {
			String l1 = lines.get(i);
			if(l1.trim().length() == 0) {
				continue;
			}
			String l2 = lines.get(i + 1);
			if(! (l1.split(",").length == l2.split(",").length)) {
				return false;
			}
		}
		return true;
	}
	
	public void testWebTestPlan() throws FileNotFoundException {
		List<MutatedConf> mutates = TestJMeterOptions.getMutatedJMeterOptions();
		System.out.println("Number of mutates: " + mutates.size());
		
		String dir = TestJMeterExamples.webtestfolder;
		String outputDir = dir + "/output";
		
		List<File> files = Files.getFileListingNoRecursion(new File(dir));
		System.out.println("Number of files: " + files.size());
		Map<String, File> fileMap = new LinkedHashMap<String, File>();
		for(File f : files) {
			fileMap.put(f.getName(), f);
		}
		
		List<File> outputFiles = Files.getFileListingNoRecursion(new File(outputDir));
		System.out.println("Number of output files: " + outputFiles.size());
		Map<String, File> outputFileMap = new LinkedHashMap<String, File>();
		for(File f : outputFiles) {
			outputFileMap.put(f.getName(), f);
		}
		
		for(int i = 0; i < mutates.size(); i++) {
			int num = i * 2;
			String name = "JMeterOutput-Stream-" + num + ".txt";
			
			File f = fileMap.get(name);
			MutatedConf conf = mutates.get(i);
			Utils.checkNotNull(f,  "Number i: " + i + ", " + conf);
			
			String outputFileName = "output_" + i + ".txt";
			File outputFile = outputFileMap.get(outputFileName);
			Utils.checkNotNull(outputFile, outputFileName);
			
			//look inside
			List<String> lines = Files.readWholeNoExp(f.getAbsolutePath());
			if(lines.size() != 5) {
//				System.out.println(f + ", " + lines.size());
//				System.out.println("  " + conf);
			} else {
				
			}
			
			if(!this.isCSVFile(outputFile.getAbsolutePath())) {
				System.out.println("XXX " + outputFile);
				System.out.println("XXX " + conf);
			}
			
			if(conf.getMutatedConfOption().indexOf("output") != -1) {
				System.out.println(conf + "  " + i);
			}
		}
	}

}
