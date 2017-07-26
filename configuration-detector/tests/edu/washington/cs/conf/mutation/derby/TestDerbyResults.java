package edu.washington.cs.conf.mutation.derby;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.mutation.ConfMutator;
import edu.washington.cs.conf.mutation.MutatedConf;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;
import junit.framework.TestCase;

public class TestDerbyResults extends TestCase {

	public List<MutatedConf> getMutatedConfs() {
		String dir = "E:\\conf-vul\\programs\\derby\\db-derby-10.10.1.1-bin\\derbytutor";
		String confFilePath = dir + "\\example-good-derby.properties";
		ConfMutator mutator = new ConfMutator(confFilePath);
		List<MutatedConf> mutates = mutator.mutateConfFile();
		return mutates;
	}
	
	public void testAnalyzeLaunchQuery() throws FileNotFoundException {
		List<MutatedConf> mutates = this.getMutatedConfs();
		System.out.println("Number of mutates: " + mutates.size());
		String dir = TestDerbyExamples.queryFolder;
		List<File> files = Files.getFileListing(new File(dir));
		System.out.println("Number of files: " + files.size());
		Map<String, File> fileMap = new LinkedHashMap<String, File>();
		for(File f : files) {
			fileMap.put(f.getName(), f);
		}
		//analyze the content
		for(int i = 0; i < mutates.size(); i++) {
			int num = i * 2;
			String name = "DerbyOutput-Stream-" + num + ".txt";
			File f = fileMap.get(name);
			MutatedConf conf = mutates.get(i);
			Utils.checkNotNull(f,  "Number i: " + i + ", " + conf);
			
			
			//check the results
			List<String> lines = Files.readWholeNoExp(f.getAbsolutePath());
			if(lines.size() != 122) {
				System.out.println(f + ", " + lines.size());
				System.out.println("  " + conf);
			} else {
				System.out.println("=== correct: " + conf);
			}
		}
	}
	
	public void testInsert() throws FileNotFoundException {
		List<MutatedConf> mutates = this.getMutatedConfs();
		System.out.println("Number of mutates: " + mutates.size());
		String dir = TestDerbyExamples.insertFolder;
		List<File> files = Files.getFileListing(new File(dir));
		System.out.println("Number of files: " + files.size());
		Map<String, File> fileMap = new LinkedHashMap<String, File>();
		for(File f : files) {
			fileMap.put(f.getName(), f);
		}
		//analyze the content
		for(int i = 0; i < mutates.size(); i++) {
			int num = i * 2;
			String name = "DerbyOutput-Stream-" + num + ".txt";
			File f = fileMap.get(name);
			MutatedConf conf = mutates.get(i);
			Utils.checkNotNull(f,  "Number i: " + i + ", " + conf);
			
			
			//check the results
			List<String> lines = Files.readWholeNoExp(f.getAbsolutePath());
			if(lines.size() != 2657) {
				System.out.println(f + ", " + lines.size());
				System.out.println("  " + conf);
			} else {
				System.out.println("=== correct: " + conf);
			}
		}
	}
	
	public void testShowTable() throws FileNotFoundException {
		List<MutatedConf> mutates = this.getMutatedConfs();
		System.out.println("Number of mutates: " + mutates.size());
		String dir = TestDerbyExamples.showTableFolder;
		List<File> files = Files.getFileListing(new File(dir));
		System.out.println("Number of files: " + files.size());
		Map<String, File> fileMap = new LinkedHashMap<String, File>();
		for(File f : files) {
			fileMap.put(f.getName(), f);
		}
		//analyze the content
		for(int i = 0; i < mutates.size(); i++) {
			int num = i * 2;
			String name = "DerbyOutput-Stream-" + num + ".txt";
			File f = fileMap.get(name);
			MutatedConf conf = mutates.get(i);
			Utils.checkNotNull(f,  "Number i: " + i + ", " + conf);
			
			
			//check the results
			List<String> lines = Files.readWholeNoExp(f.getAbsolutePath());
			if(lines.size() != 39) {
				System.out.println(f + ", " + lines.size());
				System.out.println("  " + conf);
			} else {
				System.out.println("=== correct: " + conf);
			}
		}
	}
	
	public void testLaunchServer() throws FileNotFoundException {
		List<MutatedConf> mutates = this.getMutatedConfs();
		System.out.println("Number of mutates: " + mutates.size());
		String dir = TestDerbyExamples.shutDownFolder;
		List<File> files = Files.getFileListing(new File(dir));
		System.out.println("Number of files: " + files.size());
		Map<String, File> fileMap = new LinkedHashMap<String, File>();
		for(File f : files) {
			fileMap.put(f.getName(), f);
		}
		//analyze the content
		for(int i = 0; i < mutates.size(); i++) {
			int num = i * 4;
			String name = "DerbyOutput-Stream-" + num + ".txt";
			File f = fileMap.get(name);
			MutatedConf conf = mutates.get(i);
			Utils.checkNotNull(f,  "Number i: " + i + ", " + conf);
			
			
			//check the results
			List<String> lines = Files.readWholeNoExp(f.getAbsolutePath());
			if(lines.size() != 3) {
				System.out.println(f + ", " + lines.size());
				System.out.println("  " + conf);
			} else {
				System.out.println("=== correct: " + conf);
			}
		}
	}
	
}
