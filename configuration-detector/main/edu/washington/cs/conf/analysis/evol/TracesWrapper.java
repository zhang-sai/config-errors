package edu.washington.cs.conf.analysis.evol;

import edu.washington.cs.conf.util.Utils;

//a wrapping class of all traces needed for error diagnosis
public class TracesWrapper {

	public final String oldSigFile;
	public final String newSigFile;
	public final String oldPredicateFile;
	public final String newPredicateFile;
	public final String oldHistoryFile;
	public final String newHistoryFile;
	
	public final String oldSliceCache;
	public final String newSliceCache;
	
	public final String oldCountingFile;
	public final String newCountingFile;
	
	public boolean useCountingFile() {
		return this.oldCountingFile != null && this.newCountingFile != null;
	}
	
	public TracesWrapper(String oldSigFile, String newSigFile,
			String oldPredicateFile, String newPredicateFile,
			String oldCountingFile, String newCountingFile,
			String oldSliceCache, String newSliceCache, boolean dummy) { //ugly guly hack by adding useless dummy flag
		Utils.checkFileExistence(oldSigFile);
		Utils.checkFileExistence(newSigFile);
		Utils.checkFileExistence(oldPredicateFile);
		Utils.checkFileExistence(newPredicateFile);
		Utils.checkFileExistence(oldCountingFile);
		Utils.checkFileExistence(newCountingFile);
		Utils.checkFileExistence(oldSliceCache);
		Utils.checkFileExistence(newSliceCache);
		this.oldSigFile = oldSigFile;
		this.newSigFile = newSigFile;
		this.oldPredicateFile = oldPredicateFile;
		this.newPredicateFile = newPredicateFile;
		this.oldHistoryFile = null;
		this.newHistoryFile = null;
		this.oldSliceCache = oldSliceCache;
		this.newSliceCache = newSliceCache;
		this.oldCountingFile = oldCountingFile;
		this.newCountingFile = newCountingFile;
	}
	
	public TracesWrapper(String oldSigFile, String newSigFile,
			String oldPredicateFile, String newPredicateFile,
			String oldHistoryFile, String newHistoryFile,
			String oldSliceCache, String newSliceCache) {
		Utils.checkFileExistence(oldSigFile);
		Utils.checkFileExistence(newSigFile);
		Utils.checkFileExistence(oldPredicateFile);
		Utils.checkFileExistence(newPredicateFile);
		Utils.checkFileExistence(oldHistoryFile);
		Utils.checkFileExistence(newHistoryFile);
		Utils.checkFileExistence(oldSliceCache);
		Utils.checkFileExistence(newSliceCache);
		this.oldSigFile = oldSigFile;
		this.newSigFile = newSigFile;
		this.oldPredicateFile = oldPredicateFile;
		this.newPredicateFile = newPredicateFile;
		this.oldHistoryFile = oldHistoryFile;
		this.newHistoryFile = newHistoryFile;
		this.oldSliceCache = oldSliceCache;
		this.newSliceCache = newSliceCache;
		this.oldCountingFile = null;
		this.newCountingFile = null;
	}
	
}
