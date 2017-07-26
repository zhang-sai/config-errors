package edu.washington.cs.conf.analysis;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

/**
 * This class encapsulates a configuration option.
 * A configuration option is often represented by
 * a class field 
 * */
public class ConfEntity implements Serializable {
	
	private static final long serialVersionUID = 5179583036405221484L;
	
	private final String className;
	private final String confName;
	private final boolean isStatic;
	
	private String type = null;
	private String assignMethod = null; //null is the default
	
	public ConfEntity(String className, String confName, boolean isStatic) {
		this(className, confName, null, isStatic);
	}
	
	public ConfEntity(String className, String confName, String affMethod,
			boolean isStatic) {
		assert className != null;
		assert confName != null;
		this.className = className;
		this.confName = confName;
		this.assignMethod = affMethod;
		this.isStatic = isStatic;
		//get the class
//		Field field = Utils.lookupField(className, confName);
//		this.type = field.getDeclaringClass();
//		this.isStatic = Modifier.isStatic(field.getModifiers());
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isStatic() {
		return isStatic;
	}
	
	public String getAssignMethod() {
		return assignMethod;
	}

	public void setAssignMethod(String assignMethod) {
		this.assignMethod = assignMethod;
	}

	public String getClassName() {
		return className;
	}

	public String getConfName() {
		return confName;
	}
	
	public String getFullConfName() {
		return className + "." + confName;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ConfEntity) {
			ConfEntity e = (ConfEntity)obj;
			return e.className.equals(this.className)
			    && e.confName.equals(this.confName)
//			    && e.type.equals(this.type)
			    && e.isStatic == this.isStatic;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return className + " : " + confName + " @ " + assignMethod
		    + ", " + type + ", static: " + isStatic;
	}
	
	/**
	 * A utility method to read config options from file, each line is in the form of:
	 * 
	 *    class_name # conf_name #  static_or_not #  assigning_method
	 *    
	 * The last one is optional, if not specified, will use <clinit> for static field, and <init>
	 * for non-static field
	 * */
	public static Collection<ConfEntity> readConfigOptionsFromFile(String fileName) {
		List<String> options = Files.readWholeNoExp(fileName);
		Set<String> no_dup_options = new LinkedHashSet<String>();
		no_dup_options.addAll(options);
		if(options.size() != no_dup_options.size()) {
			System.err.println("Duplicate options specified in: " + fileName);
		}
		List<ConfEntity> returnOptions = new LinkedList<ConfEntity>();
		for(String str : no_dup_options) {
			String[] splits = str.trim().split("#");
			Utils.checkTrue(splits.length == 3 || splits.length == 4, "Illegal format: " + str);
			ConfEntity conf = null;
			if(splits.length == 3) {
				conf = new ConfEntity(splits[0].trim(), splits[1].trim(), Boolean.parseBoolean(splits[2].trim()));
			}
			if(splits.length == 4) {
				conf = new ConfEntity(splits[0].trim(), splits[1].trim(), splits[3].trim(), Boolean.parseBoolean(splits[2].trim()));
			}
			Utils.checkNotNull(conf);
			returnOptions.add(conf);
		}
		return returnOptions;
	}
}