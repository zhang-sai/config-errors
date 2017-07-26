package edu.washington.cs.conf.mutation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;

public class MutatedConf {
	
	
	private final ConfFileParser parser;
	
	private final String mutatedConf;
	private final int mutatedLineIndex;
	private final String mutatedValue;
	
	public static String PREFIX="-";
	
	private Status intendedBehavior = Status.Pass;
	
//	public MutatedConf(ConfFileParser parser, String mutatedConf, String mutatedValue) {
//		this(parser, mutatedConf, mutatedValue, -1); //-1 means the new option
//	}
	
	public static MutatedConf createNonExistentMutatedConf(ConfFileParser parser, String mutatedConf, String mutatedValue) {
		return new MutatedConf(parser, mutatedConf, mutatedValue, -1);
	}
	
	//index is 0 ~ -1
	public MutatedConf(ConfFileParser parser, String mutatedConf, String mutatedValue, int lineIndex) {
		Utils.checkNotNull(parser);
		Utils.checkNotNull(mutatedConf);
		Utils.checkNotNull(mutatedValue);
		Utils.checkTrue(lineIndex >= -1);
		this.parser = parser;
		this.mutatedConf = mutatedConf;
		this.mutatedValue = mutatedValue;
		this.mutatedLineIndex = lineIndex;
		if(lineIndex != -1) { //-1 means a new added non-existent option
		    Utils.checkTrue(this.parser.getAllConfLines().size() > lineIndex);
		    Utils.checkTrue(this.parser.getAllConfLines().get(lineIndex).trim().startsWith(this.mutatedConf),
		    		mutatedConf + "=>" + mutatedValue + "@" + lineIndex);
		}
	}
	
	//by default, it is "-", sometimes, it might be "--"
	//or "" (empty space)
	public static void setConfPrefix(String prefix) {
		PREFIX = prefix;
	}
	
	public void setIntendedBehavior(Status behavior) {
		Utils.checkTrue(behavior != Status.Init);
		this.intendedBehavior = behavior;
	}
	
	public boolean shouldFail() {
		return this.intendedBehavior == Status.Fail;
	}
	
	public boolean shouldHang() {
		return this.intendedBehavior == Status.Hang;
	}
	
	public boolean shouldPass() {
		return this.intendedBehavior == Status.Pass;
	}
	
//	public Map<String, String> getMutatedConfOptions() {
//		Map<String, String> copy = new LinkedHashMap<String, String>();
//		copy.putAll(this.mutatedConfValues);
//		return copy;
//	}
	
	public String getMutatedConfOption() {
		return this.mutatedConf;
	}
	
	public String getMutatedConfValue() {
		return this.mutatedValue;
	}
	
	public Collection<String> getOriginalValues() {
		Utils.checkTrue(this.mutatedLineIndex != -1);
		return this.parser.getConfValues(this.mutatedConf);
	}
	
	public String getOriginalValue() {
		Utils.checkTrue(this.mutatedLineIndex != -1);
		return this.parser.getConfValues(this.mutatedConf).get(this.mutatedLineIndex);
	}
	
	public String createCmdLineForMutatedOptions() {
		return PREFIX + this.mutatedConf + "=" + this.mutatedValue;
	}
	
	//return command line like: -option1 value1 -option2 value2 ...
	//this is only used in the test code for convenience
//	@Deprecated
//	public String createCmdLine() {
//		StringBuilder sb = new StringBuilder();
//		for(String option : mutatedConfValues.keySet()) {
//			String v = mutatedConfValues.get(option); 
//			if(this.onOffOptions.contains(option)) {
//				Utils.checkTrue(v.toLowerCase().equals("true") || v.toLowerCase().equals("false"));
//				if(v.toLowerCase().equals("true")) {
//					sb.append(" ");
//					sb.append(PREFIX);
//					sb.append(option);
//				}
//			} else {
//			    //process other normal options
//				sb.append(" ");
//				sb.append(PREFIX);
//				sb.append(option);
//				sb.append(" ");
//				sb.append(v);
//			}
//		}
//		return sb.toString();
//	}
	
	/**
	 * Create command line arguments for reflection execution
	 * */
	public String[] createCmdLineAsArgs() {
		List<String> list = new ArrayList<String>();
		
		List<String> optionList = this.parser.getConfOptionNames();
		for(String option : optionList) {
			//PREFIX here is like: -, --, or nothing, user-settable
			String v = option.equals(this.mutatedConf) ? this.mutatedValue : this.getOriginalValue(); 
			
			if(this.parser.getOnOffOptions().contains(option)) {
				Utils.checkTrue(v.toLowerCase().equals("true") || v.toLowerCase().equals("false"));
				if(v.toLowerCase().equals("true")) {
					list.add(PREFIX + option);
				}
			} else {
			    //process other normal options
			    list.add(PREFIX + option);
			    //empty value
			    if(!v.equals("")) {
				    list.add(v);
			    }
			}
			
		}
		
		return list.toArray(new String[0]);
	}
	
	/**
	 * The baseCmds that must appear in each execution.
	 * */
	public String[] createCmdLinesAsArgs(Map<String, String> baseOptions) {
       List<String> list = new ArrayList<String>();
	
       String mutatedValue = this.mutatedValue;
       boolean isBaseOptionMutated = baseOptions.keySet().contains(this.mutatedConf);
       
       //if base option is not mutated, we need to add that mutated option
       //otherwise, ignore the mutated option, and we add each base option value later
       if(!isBaseOptionMutated) {
    	   boolean isOnOff = this.parser.getOnOffOptions().contains(this.mutatedConf);
    	   if(isOnOff) {
    		   Utils.checkTrue(mutatedValue.toLowerCase().equals("true")
    				   || mutatedValue.toLowerCase().equals("false"));
				if(mutatedValue.toLowerCase().equals("true")) {
					list.add(PREFIX + mutatedValue);
				}
    	   } else {
			    list.add(PREFIX + mutatedConf);
			    if(!mutatedValue.equals("")) {
				    list.add(mutatedValue);
			    }
    	   }
       }
       
       //add all base options
	   for(String baseOption : baseOptions.keySet()) {
			boolean isOnOff = this.parser.getOnOffOptions().contains(baseOption);
			String baseOptionValue = baseOptions.get(baseOption);
			//continue to process
			if(isOnOff) {
				Utils.checkTrue(baseOptionValue.toLowerCase().equals("true")
						|| baseOptionValue.toLowerCase().equals("false"));
				if(baseOptionValue.toLowerCase().equals("true")) {
					list.add(PREFIX + baseOption);
				}
			} else {
				list.add(PREFIX + baseOption);
			    //empty value
			    if(!baseOptionValue.equals("")) {
				    list.add(baseOptionValue);
			    }
			}
			
		}
		
		return list.toArray(new String[0]);
	}
	
	public String getMutatedContent() {
		StringBuilder sb = new StringBuilder();
        List<String> lines = this.parser.getAllConfLines();
        
        for(int index = 0; index < lines.size(); index++) {
        	String line = lines.get(index);
        	if(index == this.mutatedLineIndex) {
        		Utils.checkTrue(line.trim().startsWith(this.mutatedConf));
        		sb.append("## originally: " + line);
        		sb.append(Globals.lineSep);
        		sb.append(this.mutatedConf);
        		sb.append("=");
        		sb.append(this.mutatedValue);
        	} else {
        		sb.append(line);
        	}
        	sb.append(Globals.lineSep);
		}
		return sb.toString();
	}
	
	public void writeToFile(String filePath) {
        String content = this.getMutatedContent();
		Files.writeToFileNoExp(content, filePath);
	}
	
	@Override
	public String toString() {
		return "mutate: " + this.mutatedConf + " with value: " + this.mutatedValue;
	}
}
