package edu.washington.cs.conf.mutation;

import java.io.File;
import java.io.IOException;

import edu.washington.cs.conf.util.Files;

//move configuration files for setting configuration options
public class FileMover {
	
	private final File originalFile;
	private final File mutatedFile;
	
	private final File tmpFile;
	
	public FileMover(String originalFilePath, String mutatedFilePath) {
		Files.checkFileExistence(originalFilePath);
		Files.checkFileExistence(mutatedFilePath);
		this.originalFile = new File(originalFilePath);
		this.mutatedFile = new File(mutatedFilePath);
		try {
			this.tmpFile = File.createTempFile("backup-config-file", ".properties");
			System.out.println("Tmp file: " + this.tmpFile.getAbsolutePath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	//move the original configuration file to a backup file
	//move the mutated file to replace the original configuration file
	public void setUpMutatedConfFile() {
		try {
			Files.copyFile(this.originalFile, this.tmpFile);
			Files.copyFile(this.mutatedFile, this.originalFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	//move the backup original configuration file to the original place
	//then delete the tmpFile
	public void restoreOriginalConfFile() {
		try {
			Files.copyFile(this.tmpFile, this.originalFile);
			//delte the tmp file
			if(!this.tmpFile.delete()) {
				throw new RuntimeException("Cannot delete: " + this.tmpFile.getAbsolutePath());
			}
			System.out.println("Deleting: " + this.tmpFile.getAbsolutePath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		String originalFilePath = "./sample-config-files-after-mutated/mutated-11-jmeter.properties";
		String mutatedFilePath = "./sample-config-files-after-mutated/mutated-10-jmeter.properties";
		FileMover mover = new FileMover(originalFilePath, mutatedFilePath);
		mover.setUpMutatedConfFile();
		mover.restoreOriginalConfFile();
	}
}