package edu.washington.cs.conf.mutation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.washington.cs.conf.mutation.ConfValueTypeInferrer.ConfType;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

/**
 * Generate values
 * */
public class ValueGenerator {
	
	public static Random rand = new Random();
	
	public String generateRandomValue(String originalValue, String type) {
		return null;
	}

	public String generateEmtpyValue() {
		return "";
	}
	
	public String reverseCase(String currValue) {
		return Utils.reverseCase(currValue);
	}
	
	public List<Object> generateMutatedValues(Collection<String> currValues, ConfType type) {
		Set<Object> set = new LinkedHashSet<Object>();
		
		for(String currValue : currValues) {
			set.addAll(generateMutatedValues(currValue, type));
		}
		
		List<Object> retObjs = new LinkedList<Object>(set);
		return retObjs;
	}
	
	public List<Object> generateMutatedValues(String currValue, ConfType type) {
		List<Object> list = new ArrayList<Object>();
		
		if(type.equals(ConfType.Bool)) {
			list.addAll(this.generateBoolValues(currValue));
		} else if (type.equals(ConfType.Int)) {
			list.addAll(this.generateIntValues(currValue));
		} else if (type.equals(ConfType.FilePath)) {
			list.addAll(this.generateAbsFilePaths(currValue));
			list.addAll(this.generateRelFilePaths(currValue));
			list.add(this.generateNonExistentFilePath());
			list.add(this.generateNoPermissionFilePath());
		} else if (type.equals(ConfType.Encoding)) {
			list.addAll(this.generateEncodingValues(currValue));
		} else if (type.equals(ConfType.Lang)) {
			list.addAll(this.generateLangValues(currValue));
		} else if (type.equals(ConfType.IPAddress)) {
			list.addAll(this.generateIPAddresses(currValue));
		} else if (type.equals(ConfType.URL)) {
			list.addAll(this.generateURLs(currValue));
			list.add(generateNonExistentURL());
		} else if (type.equals(ConfType.ClassName)) {
			list.addAll(this.generateClassNameValue(currValue));
			list.add(this.generateNonExistentClassName(currValue));
		} else if (type.equals(ConfType.String)) {
			list.addAll(this.generateArbitraryStrings(currValue));
			list.add(this.reverseCase(currValue));
		}
		
		//add the empty value
		list.add(this.generateEmtpyValue());
		
		return list;
	}
	
	//65-90  97-122, randomize one bit
	public String randomizeValue(String currValue) {
		char[] cs = currValue.toCharArray();
		for(int i = 0; i < cs.length; i++) {
			char c = cs[i];
			if(c >= 65 && c <=90 ) {
				cs[i] = (char)(rand.nextInt(26) + 65);
			} else if (c >= 97 && c <=122) {
				cs[i] = (char)(rand.nextInt(26) + 97);
			}
		}
		return new String(cs);
	}
	
	//for one particular position
	public String reverseCase(String currValue, int pos) {
		Utils.checkTrue(pos >= 0 && pos < currValue.length());
		char[] cs = currValue.toCharArray();
		char c = cs[pos];
        if (Character.isUpperCase(c)) {
            cs[pos] = Character.toLowerCase(c);
        } else if (Character.isLowerCase(c)) {
            cs[pos] = Character.toUpperCase(c);
        }
        return new String(cs);
	}
	
	public Boolean reverseBoolValue(String currValue) {
		String lowerCaseValue = currValue.toLowerCase();
		Utils.checkTrue(lowerCaseValue.equals("true") || lowerCaseValue.equals("false"), "invalid: " + currValue);
		return lowerCaseValue.equals("true") ? false : true;
	}
	
	//generate a number of values below
	
	public List<String> generateArbitraryStrings(String currValue) {
		return Arrays.asList("summary", "hello", "world");
	}
	
	public List<Boolean> generateBoolValues(String currValue) {
		return Arrays.asList(true, false);
	}
	
	public List<Integer> generateIntValues(String currValue) {
		Utils.checkTrue(Utils.isIntegerValue(currValue), "Invalid value: " + currValue);
		Integer v = Integer.parseInt(currValue);
		return Arrays.asList(v + 1, v - 1, 0, -1*v, -1*v + 1, -1*v - 1, rand.nextInt(Math.abs(v) + 1));
	}
	
	public List<String> generateClassNameValue(String currValue) {
		return Arrays.asList("java.lang.Object", "java.lang.String");
	}
	
	public String generateNonExistentClassName(String currValue) {
		return "java.class.foo.bar";
	}
	
	public List<String> generateEncodingValues(String  currValue) {
		return Arrays.asList("iso-8859-1", "utf-8", "utf-16", "utf-32", "windows-1250",
				"IBM855", "x-UTF-32LE-BOM");
	}
	
	public List<String> generateLangValues(String currValue) {
		return Arrays.asList("en", "cn", "kr", "jp", "fr", "ge", "ru");
	}

	public List<String> generateFileTypes(String currValue) {
		return Arrays.asList("xml", "log", "txt", "csv", "doc", "docs");
	}
	
	
	public List<String> generateIPAddresses(String currValue) {
		return Arrays.asList("192.168.1.1", "1.0.0.1", "23.0.0.32");
	}
	
	public List<String> generateURLs(String currValue) {
		return Arrays.asList("www.google.com", "http://cs.washington.edu",
				"www.cnn.com");
	}
	
	public String generateNonExistentURL() {
		return "www.foobar-foobar.com";
	}

	//see the suffix, .xml, .log, .txt
	public List<String> generateAbsFilePaths(String currValue) {
		int max_num = this.max_file_num;
		String dir = "C:\\Users\\szhang\\Documents\\my_docs\\test"; //System.getProperty("user.dir");
		File d = new File(dir);
		File[] files = d.listFiles();
		List<String> list = new ArrayList<String>();
		for(File f : files) {
			if(max_num < 0) {
				break;
			}
			max_num --;
			list.add(f.getAbsolutePath());
		}
		return list;
	}
	
	private int max_file_num = 3;
	public List<String> generateRelFilePaths(String currValue) {
		int max_num = this.max_file_num;
		File d = new File(".");
		File[] files = d.listFiles();
		List<String> list = new ArrayList<String>();
		for(File f : files) {
			if(max_num < 0) {
				break;
			}
			max_num --;
			list.add(f.getPath());
		}
		return list;
	}
	
    public String generateNonExistentFilePath() {
	    return "/foo/bar";	
	}
    
    public String generateNoPermissionFilePath() {
    	return "C:\\Users\\szhang\\Documents\\my_docs\\test\\test.py";
    }

//	
//	
//	
//	//TODO not useful now, leave for future
//	@Deprecated
//	public List<Character> generateCharValue(String currValue) {
//		return null;
//	}
//	
//	//TODO not useful enough
//	@Deprecated
//	public Float generateFloatValue(String currValue) {
//		return null;
//	}
//	
//	//TODO not useful enough
//	@Deprecated
//	public String generateClassPathValue(String currValue) {
//		return null;
//	}
//	
//	public String generateRegexValue(String currValue) {
//		return null;
//	}
//	
//	public String generateCSVValue(String currValue) {
//		return null;
//	}
//	
//	//like:%{time:yyyy/MM/dd HH:mm:ss}
//	public String generateStringFormatValue(String currValue) {
//		return null;
//	}
//	
//	//like: 1.1.1
//	public String generateVersionValue(String currValue) {
//		return null;
//	}
//	
//	public String generateURLValue(String currValue) {
//		return null;
//	}
//	
//	
//	public String generateDateTimeValue(String currValue) {
//		return null;
//	}
}
