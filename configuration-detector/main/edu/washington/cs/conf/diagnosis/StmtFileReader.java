package edu.washington.cs.conf.diagnosis;

import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

public class StmtFileReader {

	private final String fileName;
	
	public StmtFileReader(String fileName) {
		this.fileName = fileName;
	}
	
	public List<StmtExecuted> readStmts(String[] retainedPrefix) {
        List<StmtExecuted> retStmts = new LinkedList<StmtExecuted>();
		
		List<String> content = Files.readWholeNoExp(fileName);
		for(String line : content) {
			if(line.trim().isEmpty()) {
				continue;
			}
			if(retainedPrefix != null && retainedPrefix.length > 0) {
				if(!Utils.startWith(line.trim(), retainedPrefix)) {
					continue;
				}
			}
			try {
			    StmtExecuted stmt = new StmtExecuted(line);
			    retStmts.add(stmt);
			} catch (RuntimeException e) {
				e.printStackTrace();
				continue;
			}
		}
		
		return retStmts;
	}
	
	public List<StmtExecuted> readStmts() {
		return readStmts(new String[]{});
	}
	
	public static List<StmtExecuted> readStmts(String fileName) {
		return new StmtFileReader(fileName).readStmts();
	}
	
	public static List<StmtExecuted> readStmts(String fileName, String[] retainedPrefix) {
		return new StmtFileReader(fileName).readStmts(retainedPrefix);
	}
}