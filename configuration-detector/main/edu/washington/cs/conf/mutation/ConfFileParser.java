package edu.washington.cs.conf.mutation;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.mutation.ConfValueTypeInferrer.ConfType;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

public class ConfFileParser {

	//the re-implementation of Java property parser
	private String confFile = null;
	//the file content
	private final List<String> lines;
	
	//the configuration keys
	private List<String> confNameList = new ArrayList<String>();
	//a configuration name --> a list of line index
	//a configuration may appear multiple times in a file
	private Map<String, List<Integer>> lineIndexMap = new LinkedHashMap<String, List<Integer>>();
	private List<String> confValueList = new ArrayList<String>();
	
	//for fast query
	//line number to conf name
	private Map<Integer, String> confNameMap = new LinkedHashMap<Integer, String>();
	private Map<Integer, String> confValueMap = new LinkedHashMap<Integer, String>();
	
	//types of each option
	//a configuration name --> a set of possible types
	final Map<String, Set<ConfType>> typeMap = new LinkedHashMap<String, Set<ConfType>>();
	
	//an on/off option does not require a concrete value after it
	//like: --verbose
	private final Set<String> onOffOptions = new LinkedHashSet<String>();
	
	public ConfFileParser(String confFile) {
		this.confFile = confFile;
		lines = Files.readWholeNoExp(confFile);
	}
	
	public ConfFileParser(List<String> lines) {
		Utils.checkNotNull(lines);
		this.lines = lines;
	}
	
	public List<String> getAllConfLines() {
		return this.lines;
	}
	
	public boolean isConfigLine(int lineIndex) {
		boolean isConfig = this.confNameMap.containsKey(lineIndex);
		if(isConfig) {
			Utils.checkTrue(this.confValueMap.containsKey(lineIndex));
		} else {
			Utils.checkTrue(!this.confValueMap.containsKey(lineIndex));
		}
		return isConfig;
	}
	
	@Deprecated
	public List<String> getConfOptionNames() {
		return this.confNameList;
	}
	
	@Deprecated
	public List<String> getConfOptionValues() {
		return this.confValueList;
	}
	
	public String getConfOptionName(int lineIndex) {
		Utils.checkTrue(this.confNameMap.containsKey(lineIndex));
		return this.confNameMap.get(lineIndex);
	}

	
	public Set<String> getOnOffOptions() {
		return this.onOffOptions;
	}
	
	public Set<ConfType> getTypes(String optionName) {
		return typeMap.get(optionName);
	}
	
	public List<String> getConfValues(String optionName) {
		List<String> values = new LinkedList<String>();
		List<Integer> indices = this.lineIndexMap.get(optionName);
		for(Integer lineIndex : indices) {
			String confValue = this.getConfOptionValue(optionName, lineIndex);
			values.add(confValue);
		}
		return values;
	}
	
	public String getConfOptionValue(String optionName, int lineIndex) {
		Utils.checkTrue(optionName.equals(this.confNameMap.get(lineIndex)));
		return this.confValueMap.get(lineIndex);
	}
	
	public List<Integer> getConfOptionLineIndices(String optionName) {
		return this.lineIndexMap.get(optionName);
	}
	
	public boolean isOnOffOption(String optionName) {
		return this.onOffOptions.contains(optionName);
	}
	
	public void parse() {
		//parse each line
		for(int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
			String line = lines.get(lineIndex).trim();
			//skip empty line
			if(line.equals("") || line.startsWith("#")) {
				continue;
			}
			//parse it
			int splitIndex = line.indexOf("=");
			String confName = splitIndex == -1 ? line : line.substring(0, splitIndex).trim();
			String confValue = splitIndex == -1 ? "" : line.substring(splitIndex + 1).trim();
			//add to the internal data structure
			if(!lineIndexMap.containsKey(confName)) {
				lineIndexMap.put(confName, new ArrayList<Integer>());
			}
			lineIndexMap.get(confName).add(lineIndex);
			confNameList.add(confName);
			confValueList.add(confValue);
			
			//add to the conf map
			this.confNameMap.put(lineIndex, confName);
			this.confValueMap.put(lineIndex, confValue);
			
			//infer the possible types
			Set<ConfType> types = ConfValueTypeInferrer.inferPossibleTypes(confValue);
			if(!this.typeMap.containsKey(confName)) {
				this.typeMap.put(confName, new LinkedHashSet<ConfType>());
			}
			this.typeMap.get(confName).addAll(types);
		}
	}
	
	public void dumpFile() {
		for(String line : lines) {
			System.out.println(line);
		}
	}
	
	private static int count = 1;
	public String getNextMutatedFileName() {
		File f = new File(this.confFile);
		return "mutated-" + (count++) + "-" + f.getName();
	}
	
}
