package test.slice.examples;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class FieldFlowToObject {
	public static Pattern omitmethods = null;
	public List<String> useOmitMethod(Class<?> c) {
		List<String> list = new LinkedList<String>();
		Filter f = new Filter(omitmethods);
		for(Constructor<?> con : c.getConstructors()) {
			if(f.canUse(con)) {
				list.add("can use con");
			} else {
				list.add("cannot use con");
			}
		}
		return list;
	}
	
	public static void main(String[] args) {
		FieldFlowToObject o = new FieldFlowToObject();
		o.useOmitMethod(Object.class);
	}
}

class Filter {
	private Pattern omitmethods = null;
	public Filter(Pattern omitmethods) {
		this.omitmethods = omitmethods;
	}
	public boolean canUse(Constructor<?> c) {
	    if (matchesOmitMethodPattern(c.toString())) {
	      return false;
	    }
	    if (Modifier.isAbstract(c.getDeclaringClass().getModifiers()))
	      return false;
	    return c!=null;
	  }
	
	private boolean matchesOmitMethodPattern(String name) {
	     if (omitmethods == null) {
	       return false;
	     }
	     boolean result = omitmethods.matcher(name).find();
	     if (name != null) {
	       System.out.println(String.format("Comparing '%s' against pattern '%s' = %b%n", name,
	                    omitmethods, result));
	     }
	     return result;
	  }
}