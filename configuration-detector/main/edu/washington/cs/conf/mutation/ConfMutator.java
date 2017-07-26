package edu.washington.cs.conf.mutation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.mutation.ConfValueTypeInferrer.ConfType;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;

/**
 * Mutate the configuration file, and write the mutated conf to the disk
 * */
public class ConfMutator {

	private final ConfFileParser parser;
	
	private final ValueGenerator valueGenerator = new ValueGenerator();
	
	private boolean addNonExistentOption = true;
	
	//mutate each part of the configuration property file, and output
	//the mutated results to disk
	
	public ConfMutator(String filePath) {
		this.parser = new ConfFileParser(filePath);
		this.parser.parse();
	}
	
	public ConfFileParser getParser() {
		return this.parser;
	}
	
	public void setNonExistentOption(boolean add) {
		this.addNonExistentOption = add;
	}
	
	//insert a non-existent configuration option
	MutatedConf createNonExistentConf() {
		String nonExistentConf = "NO_EXISTENT_CONF_OPTION";
		String nonExistentValue = "NO_EXISTENT_VALUE";
		
		return MutatedConf.createNonExistentMutatedConf(this.parser, nonExistentConf, nonExistentValue);
	}
	
	public List<MutatedConf> mutateConfFile() {
		List<MutatedConf> mutatedConfList = new LinkedList<MutatedConf>();
		
		int totalLineNum = this.parser.getAllConfLines().size();
		for(int lineIndex = 0; lineIndex < totalLineNum; lineIndex ++) {
			if(!this.parser.isConfigLine(lineIndex)) {
				continue;
			}
			String optionName = this.parser.getConfOptionName(lineIndex);
			Set<String> mutatedValues = this.createMutatedValues(optionName);
			for(String mutatedValue : mutatedValues) {
			    MutatedConf mutatedConf = new MutatedConf(this.parser, optionName, mutatedValue, lineIndex);
			    //add to the list
			    mutatedConfList.add(mutatedConf);
			}
		}
		
		//add the non-existent conf options
		if(this.addNonExistentOption) {
			MutatedConf nonExistent = this.createNonExistentConf();
			mutatedConfList.add(nonExistent);
		}
		
		return mutatedConfList;
	}
	
	//start to mutate the read conf file
	//produce a list of values
	Set<String> createMutatedValues(String optionName) {
		Set<String> valueSet = new LinkedHashSet<String>();
		
		//start to mutate
		Set<ConfType> types = this.parser.getTypes(optionName);
		Set<String> confValues = new LinkedHashSet<String>(this.parser.getConfValues(optionName));
		for(ConfType type : types) {
			List<Object> mutatedValues = this.valueGenerator.generateMutatedValues(confValues, type);
			for(Object v : mutatedValues) {
				if(confValues.contains(v.toString())) {
					continue;
				}
				valueSet.add(v.toString());
			}
		}
//		System.out.println("Mutating: " + optionName + ", type: " + type);
//		System.out.println(" Original value: " + currValue);
//		System.out.println(" Mutated values: " + mutatedValues);
//		
		return valueSet;
	}
	
	@Deprecated
	public static void writeConfFile(String filePath, Map<String, String> confValues) {
		StringBuilder sb = new StringBuilder();
		
		for(String option : confValues.keySet()) {
			sb.append(option);
			sb.append("=");
			sb.append(confValues.get(option));
			sb.append(Globals.lineSep);
		}
		
		Files.writeToFileNoExp(sb.toString(), filePath);
	}
	
	public static void main(String[] args) {
		
	}
}
