//package edu.washington.cs.conf.mutation;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.LinkedHashMap;
//import java.util.LinkedHashSet;
//import java.util.Map;
//import java.util.Properties;
//import java.util.Set;
//
//import edu.washington.cs.conf.mutation.ConfValueTypeInferrer.ConfType;
//import edu.washington.cs.conf.util.Utils;
//
////parse a property file
//@Deprecated
//public class ConfParser {
//
//	private String propertyFile = null;
//	
//	//store the key-value pair, and key-type pair
//	private final Map<String, String> valueMap = new LinkedHashMap<String, String>();
//	private final Map<String, Set<ConfType>> typeMap = new LinkedHashMap<String, Set<ConfType>>();
//	
//	//an on/off option does not require a concrete value after it
//	//like: --verbose
//	private final Set<String> onOffOptions = new LinkedHashSet<String>();
//	
//	public ConfParser() {}
//	
//	public ConfParser(String propertyFile) {
//		Utils.checkNotNull(propertyFile);
//		this.propertyFile = propertyFile;
//	}
//	
//	public Map<String, String> getOptionValueMap() {
//		Map<String, String> mapCopy = new LinkedHashMap<String, String>();
//		mapCopy.putAll(valueMap);
//		return mapCopy;
//	}
//	
//	public Set<String> getOptions() {
//		return valueMap.keySet();
//	}
//	
//	public Set<String> getOnOffOptions() {
//		return this.onOffOptions;
//	}
//	
//	public Set<ConfType> getTypes(String confOption) {
//		return typeMap.get(confOption);
//	}
//	
//	@Deprecated
//	public void addConfOption(String optionName, String optionValue, boolean isOnOff) {
//		Utils.checkTrue(!valueMap.containsKey(optionName));
//		if(isOnOff) {
//			Utils.checkTrue(optionValue.toLowerCase().equals("true")
//					|| optionValue.toLowerCase().equals("false"));
//			this.onOffOptions.add(optionName);
//		}
//		this.valueMap.put(optionName, optionValue);
//		//parse the type as we did in parseFile
//		Set<ConfType> type = ConfValueTypeInferrer.inferPossibleTypes(optionValue.toString());
//		this.typeMap.put(optionName, type);
//	}
//	
//	//assuming at least one type for an option
//	public ConfType getType(String confOption) {
//		return typeMap.get(confOption).iterator().next();
//	}
//	
//	public String getValue(String confOption) {
//		return valueMap.get(confOption);
//	}
//	
//	public boolean isOnOffOption(String confOption) {
//		return this.onOffOptions.contains(confOption);
//	}
//	
//	//parse into a set key-value pairs
//	//if multiple option appear in the same configuration file
//	//only the last one counts
//	public void parseFile() {
//		Utils.checkNotNull(propertyFile);
//		Properties prop = new Properties();
//    	try {
//            //load a properties file
//    		prop.load(new FileInputStream(this.propertyFile));
// 
//            for(Object key : prop.keySet()) {
//            	Object value = prop.get(key);
//            	Set<ConfType> type = ConfValueTypeInferrer.inferPossibleTypes(value.toString());
//            	
//            	Utils.checkTrue(!valueMap.containsKey(key.toString()));
//            	valueMap.put(key.toString(), value.toString());
//            	typeMap.put(key.toString(), type);
//            	
////            	System.out.println(key + ",  " + value);
////            	System.out.println("   " + type);
//            }
// 
//    	} catch (IOException ex) {
//    		ex.printStackTrace();
//        }
//	}
//	
//	private static int count = 1;
//	public String getNextMutatedFileName() {
//		File f = new File(this.propertyFile);
//		return "mutated-" + (count++) + "-" + f.getName();
//	}
//	
//}
