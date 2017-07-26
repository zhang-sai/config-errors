package edu.washington.cs.conf.mutation;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import edu.washington.cs.conf.util.Globals;

public class ConfValueTypeInferrer {
	
	enum ConfType {Bool, Int, Char, Float, 
		ClassName, FilePath, ClassPath, Regex, IPAddress,
		CSV, StringFormat, Version, URL, Encoding,
		Lang, DateTime, String};
		
    public static void main(String[] args) {
    	System.out.println(ConfValueTypeInferrer.inferPossibleTypes("127.0.0.1"));
    }

	public static Set<ConfType> inferPossibleTypes(String value) {
		
		Set<ConfType> types = new LinkedHashSet<ConfType>();
		
		if(value.trim().length() == 0) {
			types.add(ConfType.String); //know nothing from the current value
			return types;
		}
		
		//use a latttice, to infer the most precise type first
		if(value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
			types.add(ConfType.Bool);
			return types;
		}
		
		//check if it is an integer
		try {
		    Integer.parseInt(value);
		    types.add(ConfType.Int);
		    return types;
		} catch (NumberFormatException e) {
			//do nothing;
		}
		
		//check if it is a float
		try {
		    Float.parseFloat(value);
		    types.add(ConfType.Float);
		    return types;
		} catch (NumberFormatException e) {
			//do nothing;
		}
		
		//check if it is a single char
		if(value.length() == 1) {
			char c = value.charAt(0); //check the boundary here?
			types.add(ConfType.Char);
		    return types;
		}
		
		//check if it is a lang, such as "en", "fr"
		if(langs.contains(value)) {
			types.add(ConfType.Lang);
			return types;
		}
		
		//check if it is an encoding
		if(encodings.contains(value)) {
			types.add(ConfType.Encoding);
			return types;
		}
		
		//check if it is an IP address
		if(value.split("\\.").length == 4) {
			try {
				String[] splits = value.split("\\.");
				int i1 = Integer.parseInt(splits[0]);
				int i2 = Integer.parseInt(splits[1]);
				int i3 = Integer.parseInt(splits[2]);
				int i4 = Integer.parseInt(splits[3]);
				if(i1 >= 0 && i1 <= 255 && i2 >= 0 && i2 <= 255
					&& i3 >= 0 && i3 <= 255
					&& i4 >= 0 && i4 <= 255) {
					types.add(ConfType.IPAddress);
					return types;
				}
			} catch (NumberFormatException e) {
				//do nothing
			}
		}
		
		
		//check if it is FilePath
		//check no ; or :
		if(value.indexOf(Globals.pathSep) == -1
				&& value.indexOf(".") != -1) {
			for(String suffix : fileSuffix) {
				if(value.endsWith(suffix)) {
					types.add(ConfType.FilePath);
					return types;
				}
			}
		}
		
		//ClassPath,
		
		//StringFormat, URL,
		
		//DateTime
		
		//check software version, like 1.1.1
		//or class name
		if(value.split("\\.").length > 1) {
			String[] splits = value.split("\\.");
			boolean allInteger = true;
			boolean allPackName = true;
			boolean hasEmpty = false;
			for(String s : splits) {
				try {
					Integer.parseInt(s);
				} catch (NumberFormatException e) {
					allInteger = false;
				}
				if(!isJavaIDs(s)) {
					allPackName = false;
				}
				if(s.length() == 0) {
					hasEmpty = true;
				}
			}
			if(allInteger && !hasEmpty) {
			    types.add(ConfType.Version);
			    return types;
			}
			if(!allPackName && !hasEmpty) {
				types.add(ConfType.ClassName);
				return types;
			}
		}
				
		//check if it is regex
		if(value.indexOf("*") != -1
			|| value.indexOf("^") != -1
			|| value.indexOf("$") != -1) {
		    try {
			    Pattern.compile(value);
			    types.add(ConfType.Regex);
			    return types;
		    } catch (PatternSyntaxException e) {
			    //do nothing
		    }
		}
		
		//check CSV like aa,aa
		if(value.split(",").length > 1) {
			types.add(ConfType.CSV);
			return types;
		}
		
		//just ordinary string
		types.add(ConfType.String);
		
		return types;
	}
	
	//initialize all tokens
	
	private static Set<String> langs = null;
	private static Set<String> encodings = null;
	private static Set<String> fileSuffix = null;
	
	static {
		langs = new HashSet<String>();
		//add all langs
		langs.add("en");
		langs.add("fr");
		langs.add("ge");
		langs.add("cn");
		langs.add("ch");
		langs.add("ca");
		
		encodings = new HashSet<String>();
		//add all encodings
		encodings.add("utf-8");
		encodings.add("utf-16");
		encodings.add("utf-32");
		encodings.add("US-ASCII");
		encodings.add("EBCDIC");
		
		fileSuffix = new HashSet<String>();
		fileSuffix.add(".txt");
		fileSuffix.add(".sh");
		fileSuffix.add(".txt");
		fileSuffix.add(".xml");
		fileSuffix.add(".jar");
		fileSuffix.add(".log");
		fileSuffix.add(".properties");
		
	}
	
	private static boolean isJavaIDs(String v) {
		v = v.trim();
		if(v.length() == 0) {
			return false;
		}
		int count = 0;
		for(char c : v.toCharArray()) {
			if(count == 0) {
				if(c >= '0' && c <= '9') {
					return false;
				}
			}
			if(!((c>='0' && c <='9') || (c >= 'a' && c <='z')
					|| (c >= 'A' && c <='Z') || (c == '_'))) {
				return false;
			}
			count++;
		}
		return true;
	}
	
}