package edu.washington.cs.conf.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class JarUtils {

	public static final String ANT_BAT = "D:\\develop-tools\\apache-ant-1.8.2\\bin\\ant.bat";
	
	public static final String ANT_BUILD_FILE =
		"D:\\research\\configurations\\workspace\\configuration-detector\\main\\edu\\washington\\cs\\conf\\util\\build.xml";
	
	public static final String TRACER_FILE =
		"D:\\research\\configurations\\workspace\\configuration-detector\\evoltracer.jar";
	
	public static final String MERGED_JAR = "merged.jar";
	
	public static boolean DELETE_DIR = true;
	
	public static File mergeWithTracer(File originalFile,
			File tracerFile, File mergedFile) {
		System.out.println("Start to merge: " + originalFile.getAbsolutePath()
				+ Globals.lineSep + ", with: " + tracerFile.getAbsolutePath()
				+ Globals.lineSep + ", into: " + mergedFile.getAbsolutePath());
		try {
			//if existed, first remove it
			if (mergedFile.exists()) {
				mergedFile.delete();
			} else {
				//create the parent folder
				if(!mergedFile.getParentFile().exists()) {
					mergedFile.getParentFile().mkdirs();
				}
			}
			mergedFile.createNewFile();
			
			//here is the steps to merge the original jar with the
			//tracer jar:
			//1. create a tmp dir
			//2. copy the ant file, original file, and tracerFile there
			//3. run ant in that dir
			//4. copy the merged jar back to mergedFile
			//5. delete all the temp dir
			File tmpDir = Files.createTempDirectoryNoExp();
			System.out.println("create tmp dir: " + tmpDir.getAbsolutePath());
			
			File antFile = new File(ANT_BUILD_FILE);
			File destAntFile = new File(tmpDir.getAbsolutePath() + Globals.fileSep + antFile.getName());
			Files.copyFileNoExp(antFile, destAntFile);

			File destTracerFile = new File(tmpDir.getAbsolutePath() + Globals.fileSep + tracerFile.getName());
			Files.copyFileNoExp(tracerFile, destTracerFile);
			
			File destJarFile = new File(tmpDir.getAbsolutePath() + Globals.fileSep + originalFile.getName());
			Files.copyFileNoExp(originalFile, destJarFile);
			
			System.out.println("Running ant to merge jars...");
			Command.exec(new String[]{ANT_BAT, "-f", destAntFile.getAbsolutePath()});
			
			System.out.println("Copying merged file...");
			File mergedJarFile = new File(tmpDir.getAbsolutePath() + Globals.fileSep + MERGED_JAR);
			Utils.checkFileExistence(mergedJarFile.getAbsolutePath());
			Files.copyFileNoExp(mergedJarFile, mergedFile);
			
			if(DELETE_DIR) {
			    System.out.println("Delete all tmp dir: " + tmpDir.getAbsolutePath());
		 	    Files.deleteDirectory(tmpDir);
			}
			
		} catch (Throwable e) {
			throw new Error(e);
		}
		System.out.println("Merge completed!");
		return mergedFile;
	}
	
	public static Collection<ZipEntry> getContents(String jarFilePath)
			throws ZipException, IOException {
		return getContents(new File(jarFilePath));
	}

	public static Collection<ZipEntry> getContents(File f) throws ZipException,
			IOException {
		Collection<ZipEntry> entries = new LinkedHashSet<ZipEntry>();
		ZipFile jarFile = new ZipFile(f);
		Enumeration<? extends ZipEntry> e = jarFile.entries();
		while (e.hasMoreElements()) {
			ZipEntry ze = e.nextElement();
			entries.add(ze);
		}
		return entries;
	}

	public static Collection<String> getContentsAsStr(File f)
			throws ZipException, IOException {
		Collection<ZipEntry> entries = getContents(f);
		Collection<String> strs = new LinkedHashSet<String>();
		for (ZipEntry e : entries) {
			strs.add(e.toString());
		}
		return strs;
	}
	
	public static String concaAllJarFiles(String dir) {
		StringBuilder sb = new StringBuilder();
		try {
			List<File> files = Files.getFileListing(new File(dir), ".jar");
			int count = 0;
			for(File f : files) {
				if(count != 0) {
					sb.append(Globals.pathSep);
				}
				count++;
				sb.append(f.getAbsolutePath());
			}
		} catch (FileNotFoundException e) {
			throw new Error(e);
		}
		return sb.toString();
	}

	public static void main(String[] args) throws ZipException, IOException {
//		Collection<String> content = getContentsAsStr(new File(args[0]));
//		for (String c : content) {
//			System.out.println(c);
//		}
		
		String dir = "D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib";
		System.out.println(JarUtils.concaAllJarFiles(dir));
		
//		Command.exec(new String[]{ANT_BAT, "-f", "D:\\research\\confevol\\subject-programs\\ant-test\\build.xml"});
		//Command.runCommand(command, prompt, verbose, nonVerboseMessage, gobbleChars)
	}
}