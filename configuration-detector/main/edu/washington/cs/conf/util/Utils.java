package edu.washington.cs.conf.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import com.ibm.wala.ipa.callgraph.CGNode;

public class Utils {
	
	public static void fail(String message) {
		checkNotNull(null, message);
	}
	
	public static void unimplemented() {
		throw new Error();
	}
	
	public static void checkNotNull(Object o) {
		checkNotNull(o, null);
	}
	
	public static void checkNotNull(Object o, String msg) {
		if(o == null) {
			System.err.println(msg);
			throw new RuntimeException(msg);
		}
	}
	
	public static void checkTrue(boolean cond) {
		checkTrue(cond, "");
	}
	
	public static void checkTrue(boolean cond, String msg) {
		if(!cond) {
			System.err.println(msg);
			throw new RuntimeException(msg);
		}
	}
	
	public static String translateSlashToDot(String str) {
		assert str != null;
		return str.replace('/', '.');
	}
	
	public static String translateDotToSlash(String str) {
		assert str != null;
		return str.replace('.', '/');
	}
	
	public static boolean isPrimitiveType(String type) {
		try {
			getJVMDescriptorForPrimitiveType(type);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}
	
	public static void setField(Class<?> clz, String fieldName, Object value) {
		try {
			Field f = clz.getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(null, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isIntegerValue(String value) {
		try {
			Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	public static String getJVMDescriptorForPrimitiveType(String type) {
		if (type.equals("boolean")) {
			return "Z";
		} else if (type.equals("char")) {
			return "C";
		} else if (type.equals("byte")) {
			return "B";
		} else if (type.equals("short")) {
			return "S";
		} else if (type.equals("int")) {
			return "I";
		} else if (type.equals("float")) {
			return "F";
		} else if (type.equals("long")) {
			return "J";
		} else if (type.equals("double")) {
			return "D";
		} else {
			throw new RuntimeException("Unexpected primitive type: " + type);
		}
	}
	
	public static void checkDirExistence(String dir) {
		File f = new File(dir);
		if(!f.isDirectory()) {
			throw new RuntimeException("File: " + f + " is not a dir");
		}
		if(!f.exists()) {
			throw new RuntimeException("Dir: " + f + " does not exist");
		}
	}
	
	public static void checkFileExistence(String dir) {
		File f = new File(dir);
		if(f.isDirectory()) {
			throw new RuntimeException("File: " + f + " is  a dir");
		}
		if(!f.exists()) {
			throw new RuntimeException("File: " + f + " does not exist");
		}
	}
	
	public static <T> void checkNoNull(T[] ts) {
		for(int i = 0; i < ts.length; i++) {
			if(ts[i] == null) {
				throw new RuntimeException("The " + i + "-th element is null.");
			}
		}
	}
	
	public static void checkPathEntryExistence(String path) {
		String[] entries = path.split(Globals.pathSep);
		for(String entry : entries) {
			File f = new File(entry);
			if(!f.exists()) {
				throw new RuntimeException("The entry: " + entry + " does not exist.");
			}
		}
	}
	
	public static Method getMainMethod(String className) {
		try {
			Class<?> clz = Class.forName(className);
			return getMainMethod(clz);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static Method getMainMethod(Class<?> clz) {
		try {
			return clz.getMethod("main", String[].class);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	//must wrap in a try - catch, since this will be used in a field initializer
	public static List<String> getClassesRecursive(String dir) {
		try {
			List<String> fileNames = new LinkedList<String>();
			for (File f : Files.getFileListing(new File(dir), ".class")) {
				fileNames.add(f.getAbsolutePath());
			}
			return fileNames;
		} catch (Throwable e) {
			throw new Error(e);
		}
	}
	
	//find all class files
	public static List<String> getJars(String dir, boolean recursive) throws FileNotFoundException {
		if(!recursive) {
			return getJars(dir);
		} else {
			List<String> fileNames = new LinkedList<String>();
			for(File f : Files.getFileListing(new File(dir), ".jar") ) {
				fileNames.add(f.getAbsolutePath());
			}
			return fileNames;
		}
	}
	
	//find all jar files, not this is not recursive
	public static List<String> getJars(String dir) {
		List<String> files = Files.findFilesInDir(dir, null, ".jar");
		List<String> fullPaths = new LinkedList<String>();
		for(String file : files) {
			fullPaths.add(dir + Globals.fileSep + file);
		}
		//System.out.println(fullPaths);
		return fullPaths;
	}
	
	public static Collection<String> extractClassFromPlugXMLFiles(String...fileNames) {
		Collection<String> classNames = new LinkedHashSet<String>();
		
		for(String fileName : fileNames) {
			if(!fileName.endsWith(".xml")) {
				throw new RuntimeException("The file is not an XML file: " + fileName);
			}
			String content = Files.readWholeAsString(fileName);
			Collection<String> classes = extractClasses(content);
			classNames.addAll(classes);
		}
		
		return classNames;
	}
	
	public static Collection<String> extractClassFromPluginXML(String pluginJarFile) throws IOException {
		if(!pluginJarFile.endsWith(".jar")) {
			throw new RuntimeException("The input file: " + pluginJarFile + " is not a jar file.");
		}
		String content = getPluginXMLContent(pluginJarFile);
		if(content != null) {
			return extractClasses(content);
		} else {
		    return Collections.<String>emptySet(); 
		}
	}
	
	//be aware, this can return null
	public static String getPluginXMLContent(String jarFilePath) throws IOException {
		ZipFile jarFile = new ZipFile(jarFilePath);
		ZipEntry entry = jarFile.getEntry("plugin.xml");
		if(entry == null) {
			return null;
		}
		BufferedReader in = new BufferedReader(
				new InputStreamReader(jarFile.getInputStream(entry)));
		StringBuilder sb = new StringBuilder();
		String line = in.readLine();
		while(line != null) {
		    sb.append(line);
		    sb.append(Globals.lineSep);
		    line = in.readLine();
		}
		return sb.toString();
	}
	
	public static Collection<String> extractClasses(String xmlContent) {
		final Set<String> classList = new LinkedHashSet<String>();
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			DefaultHandler handler = new DefaultHandler() {
				public void startElement(String uri, String localName,
						String qName, Attributes attributes) throws SAXException {
					if(attributes != null) {
					    for(int i = 0; i < attributes.getLength(); i++) {
						    if(attributes.getQName(i).equals("class")) {
							    classList.add(attributes.getValue(i));
						    }
					    }
					}
				}
			};
			byte[] bytes = xmlContent.getBytes("UTF8");
			InputStream inputStream = new ByteArrayInputStream(bytes);
			InputSource source = new InputSource(inputStream);
			saxParser.parse(source, handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classList;
	}
	
	public static String concatenate(Iterable<String> strs, String sep) {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for(String str : strs) {
			if(count != 0) {
				sb.append(sep);
			}
			sb.append(str);
			count++;
		}
		return sb.toString();
	}
	
	public static String concatenate(String[] strs, String sep) {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for(String str : strs) {
			if(count != 0) {
				sb.append(sep);
			}
			sb.append(str);
			count++;
		}
		return sb.toString();
	}
	
	public static String conToPath(List<String> strs) {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for(String str : strs) {
			if(count != 0) {
				sb.append(Globals.pathSep);
			}
			sb.append(str);
			count++;
		}
		return sb.toString();
	}
	
	//cannot contain null
	public static <T> Set<T> intersect(Set<T> set1, Set<T> set2) {
		Set<T> intersect = new HashSet<T>();
		for(T t : set1) {
			checkNotNull(t);
			if(set2.contains(t)) {
				intersect.add(t);
			}
		}
		return intersect;
	}
	
	//set1 - set2
	public static <T> Set<T> minus(Set<T> set1, Set<T> set2) {
		Set<T> minus = new HashSet<T>();
		for(T t : set1) {
			checkNotNull(t);
			if(!set2.contains(t)) {
				minus.add(t);
			}
		}
		return minus;
	}
	
	public static <T> boolean includedIn(T target, T[] array) {
		if(target == null) {
			throw new RuntimeException("target can not be null.");
		}
		for(T elem : array) {
			if(elem != null && elem.equals(target)) {
				return true;
			}
		}
		return false;
 	}
	
	public static boolean startWith(String t, String[] prefix) {
		for(String p : prefix) {
			if(t.startsWith(p)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean matchStacktrace(Collection<String> logs) {
		String regex = "^.+Exception[^\\n]+[[\\s]+at .+]+";
		StringBuilder sb = new StringBuilder();
		for(String line : logs) {
			sb.append(line);
//			sb.append("\n");
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(sb.toString());
		return matcher.find();
	}
	
	public static String reverseCase(String text) {
	    char[] chars = text.toCharArray();
	    for (int i = 0; i < chars.length; i++) {
	        char c = chars[i];
	        if (Character.isUpperCase(c)) {
	            chars[i] = Character.toLowerCase(c);
	        }
	        else if (Character.isLowerCase(c)) {
	            chars[i] = Character.toUpperCase(c);
	        }
	    }
	    return new String(chars);
	}
	
	public static <T> Collection<T> iterableToCollection(Iterable<T> ts) {
		Collection<T> collection = new LinkedList<T>();
		for(T t : ts) {
			collection.add(t);
		}
		return collection;
 	}
	
	public static <T> void removeRedundant(Collection<T> coll) {
		Set<T> set = new LinkedHashSet<T>();
		set.addAll(coll);
		coll.clear();
		coll.addAll(set);
	}
	
	public static <T> Iterable<T> returnUniqueIterable(Iterable<T> coll) {
		Set<T> set = new LinkedHashSet<T>();
		for(T t : coll) {
			set.add(t);
		}
		return set;
	}
	
	public static <T> Iterable<T> combine(Iterable<T> c1, Iterable<T> c2) {
		Set<T> set = new LinkedHashSet<T>();
		for(T t : c1) {
			set.add(t);
		}
		for(T t : c2) {
			set.add(t);
		}
		return set;
	}
	
	//check if every element of its is included in all
	public static <T> boolean includedIn(Iterable<T> its, Iterable<T> all) {
		Collection<T> collection_its = iterableToCollection(its);
		Collection<T> collection_all = iterableToCollection(its);
		return collection_all.containsAll(collection_its);
	}
	
	/** This project-specific methods */
	public static <T> int countIterable(Iterable<T> c) {
		int count = 0;
		for(T t: c) {
			count++;
		}
		return count;
	}
	
	public static boolean debug = true;
	public static void debugPrintln(String str) {
		if(debug) {
			System.out.println(str);
		}
	}
	
	public static <T> void logCollection(Iterable<T> c) {
		Log.logln(dumpCollection(c));
	}
	
	public static <T> void dumpCollection(Iterable<T> c, PrintStream out) {
		out.println(dumpCollection(c));
	}
	public static <T> void dumpCollection(Iterable<T> c, String fileName) {
		try {
			Files.writeToFile(dumpCollection(c), fileName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static <T> String dumpCollection(Iterable<T> c) {
		StringBuilder sb = new StringBuilder();
		int num = 0;
		for(T t : c) {
			sb.append(t);
			sb.append(Globals.lineSep);
			num ++;
		}
		sb.append("Num in total: " + num);
		return sb.toString();
	}
	
	public static void flushToStd(String[] msgs) {
        for(String msg : msgs) {
          System.out.println(msg);
        }
    }
	
	static Random random = new Random();
	
	public static int nextRandomInt(int range) {
		return random.nextInt(range);
	}
	
	public static <T> Object[] randomSubArray(T[] array) {
		Utils.checkTrue(array.length > 0);
		int length = nextRandomInt(array.length) + 1;
		if(length == array.length) {
			return array;
		}
		Set<Integer> indexSet = new LinkedHashSet<Integer>();
		while(indexSet.size() != length) {
			indexSet.add(nextRandomInt(array.length));
		}
		List<T> elements = new LinkedList<T>();
		for(Integer index : indexSet) {
			elements.add(array[index]);
		}
		return elements.toArray();
	}
	
	public static <T> String dumpArray(T[] ts) {
		if(ts == null) {
			return "NULL ARRAY";
		}
		StringBuilder sb = new StringBuilder();
		int num = 0;
		for(T t : ts) {
			sb.append(t);
			if(num != ts.length - 1) {
			    sb.append(", ");
			}
			num ++;
		}
		return sb.toString();
	}
	
	public static Float average(Collection<Integer> ts) {
		Utils.checkTrue(ts.size() > 0);
		Float sum = 0.0f;
		for(Integer t : ts) {
			sum += (float)t;
		}
		return sum/ts.size();
	}
	
	public static Integer sum(Collection<Integer> ts) {
		Utils.checkTrue(ts.size() > 0);
		Integer sum = 0;
		for(Integer t : ts) {
			sum = sum + t;
		}
		return sum;
	}
	
//	public static <T> Set<T> intersect(Set<T> s1, Set<T> s2) {
//		Set<T> retSet = new LinkedHashSet<T>();
//		
//		return retSet;
//	}
	
	public static <K, V> Map<K, V> sortByKey(Map<K, V> map, final boolean increase) {
	     List<Entry<K, V>> list = new LinkedList<Entry<K, V>>(map.entrySet());
	     Collections.sort(list, new Comparator() {
	          public int compare(Object o1, Object o2) {
	        	  if(increase) {
	        		  return ((Comparable) ((Map.Entry) (o1)).getKey())
		              .compareTo(((Map.Entry) (o2)).getKey());
	        	  } else {
	        		  return ((Comparable) ((Map.Entry) (o2)).getKey())
		              .compareTo(((Map.Entry) (o1)).getKey());
	        	  }
	          }
	     });

	    Map<K, V> result = new LinkedHashMap<K, V>();
	    for (Iterator<Entry<K, V>> it = list.iterator(); it.hasNext();) {
	        Map.Entry<K, V> entry = (Map.Entry<K, V>)it.next();
	        result.put(entry.getKey(), entry.getValue());
	    }
	    return result;
	}
	
	public static <K, V> Map<K, V> sortByValue(Map<K, V> map, final boolean increase) {
	     List<Entry<K, V>> list = new LinkedList<Entry<K, V>>(map.entrySet());
	     Collections.sort(list, new Comparator() {
	          public int compare(Object o1, Object o2) {
	        	  if(increase) {
	        		  return ((Comparable) ((Map.Entry) (o1)).getValue())
		              .compareTo(((Map.Entry) (o2)).getValue());
	        	  } else {
	        		  return ((Comparable) ((Map.Entry) (o2)).getValue())
		              .compareTo(((Map.Entry) (o1)).getValue());
	        	  }
	          }
	     });

	    Map<K, V> result = new LinkedHashMap<K, V>();
	    for (Iterator<Entry<K, V>> it = list.iterator(); it.hasNext();) {
	        Map.Entry<K, V> entry = (Map.Entry<K, V>)it.next();
	        result.put(entry.getKey(), entry.getValue());
	    }
	    return result;
	}
	
	public static <K, V> List<K> sortByValueAndReturnKeys(Map<K, V> map, final boolean increase) {
		Map<K, V> sorted = sortByValue(map, increase);
		List<K> list = new LinkedList<K>();
		list.addAll(sorted.keySet());
		return list;
	}
	
//	public static <K, V> Map<Integer, K>
	
	public static String extractClassName(String fullElement) {
		return fullElement.substring(0, fullElement.lastIndexOf("."));
	}
	
	public static String extractElementName(String fullElement) {
		return fullElement.substring(fullElement.lastIndexOf(".") + 1);
	}
	
	public static Class<?> lookupClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new Error(e);
		}
	}
	
	public static Field lookupField(String className, String fieldName) {
		Class<?> clz = Utils.lookupClass(className);
		return lookupField(clz, fieldName);
		
	}
	
	public static Field lookupField(Class<?> clz, String fieldName) {
		try {
			Field[] fields = clz.getDeclaredFields();
			for(Field f : fields) {
				//System.out.println(f);
				if(f.getName().equals(fieldName)) {
					return f;
				}
			}
			throw new Error("Can not find field: " + fieldName + " in " + clz.toString());
		} catch (Throwable e) {
			throw new Error(e);
		}
	}
	
	public static Class<?> loadclass(String classPath, String  className) {
		// Create a File object on the root of the directory containing the class file
		String[] paths = classPath.split(Globals.pathSep);
		File[] files = new File[paths.length];
		for(int i = 0; i < paths.length; i++) {
			files[i] = new File(paths[i]);
		}

		try {
		    // Convert File to a URL
			URL[] urls = new URL[files.length];
			for(int i = 0; i < files.length; i++) {
				urls[i] = files[i].toURL();
			}

		    // Create a new class loader with the directory
		    ClassLoader cl = new URLClassLoader(urls);

		    // Load in the class; MyClass.class should be located in
		    // the directory file:/c:/myclasses/com/mycompany
		    Class<?> cls = cl.loadClass(className);
		    return cls;
		} catch (MalformedURLException e) {
		} catch (ClassNotFoundException e) {
		}
		return null;
	}
	
	@Deprecated
	public static Collection<Class<?>> getAllLoadedClasses(String classPath) {
		// Create a File object on the root of the directory containing the class file
		String[] paths = classPath.split(Globals.pathSep);
		File[] files = new File[paths.length];
		for(int i = 0; i < paths.length; i++) {
			files[i] = new File(paths[i]);
		}

		try {
		    // Convert File to a URL
			URL[] urls = new URL[files.length];
			for(int i = 0; i < files.length; i++) {
				urls[i] = files[i].toURL();
			}

		    // Create a new class loader with the directory
		    ClassLoader cl = new URLClassLoader(urls);

		    //a hacky way
		    Collection<Class<?>> allClasses = new LinkedHashSet<Class<?>>();
		    
		    Field f = ClassLoader.class.getDeclaredField("classes");
		    f.setAccessible(true);
		    Vector<Class<?>> classes =  (Vector<Class<?>>) f.get(cl);
//		    System.out.println("size: " + classes.size());
		    for(Class<?> c : classes) {
		    	allClasses.add(c);
		    }
		    
		    return allClasses;
		} catch (MalformedURLException e) {
			throw new Error(e);
		} catch (IllegalArgumentException e) {
			throw new Error(e);
		} catch (IllegalAccessException e) {
			throw new Error(e);
		} catch (SecurityException e) {
			throw new Error(e);
		} catch (NoSuchFieldException e) {
			throw new Error(e);
		}
	}
}