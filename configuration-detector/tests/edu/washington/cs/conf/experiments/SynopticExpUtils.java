package edu.washington.cs.conf.experiments;

import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;

public class SynopticExpUtils {
	
	public static ConfEntityRepository getConfEntityRepository() {
		List<ConfEntity> entities = getSynopticList();
		return new ConfEntityRepository(entities);
	}
	
	public static List<ConfEntity> getSynopticList() {
		ConfEntity entity1 = new ConfEntity("synoptic.main.SynopticOptions",
				"allHelp", false);
		ConfEntity entity2 = new ConfEntity("synoptic.main.SynopticOptions",
				"argsFilename", false);
		ConfEntity entity3 = new ConfEntity("synoptic.main.SynopticOptions",
				"debugParse", false);
		ConfEntity entity4 = new ConfEntity("synoptic.main.SynopticOptions",
				"doBenchmarking", false);
		ConfEntity entity5 = new ConfEntity("synoptic.main.SynopticOptions",
				"dotExecutablePath", false);
		ConfEntity entity6 = new ConfEntity("synoptic.main.SynopticOptions",
				"dumpInitialGraphDotFile", false);
		ConfEntity entity7 = new ConfEntity("synoptic.main.SynopticOptions",
				"dumpInitialGraphPngFile", false);
		ConfEntity entity8 = new ConfEntity("synoptic.main.SynopticOptions",
				"dumpInitialPartitionGraph", false);
		ConfEntity entity9 = new ConfEntity("synoptic.main.SynopticOptions",
				"dumpIntermediateStages", false);
		ConfEntity entity10 = new ConfEntity("synoptic.main.SynopticOptions",
				"dumpInvariants", false);
		ConfEntity entity11 = new ConfEntity("synoptic.main.SynopticOptions",
				"exportAsGML", false);
		ConfEntity entity12 = new ConfEntity("synoptic.main.SynopticOptions",
				"help", false);
		ConfEntity entity13 = new ConfEntity("synoptic.main.SynopticOptions",
				"ignoreNonMatchingLines", false);
		ConfEntity entity14 = new ConfEntity("synoptic.main.SynopticOptions",
				"internCommonStrings", false);
		ConfEntity entity15 = new ConfEntity("synoptic.main.SynopticOptions",
				"logLvlExtraVerbose", false);
		ConfEntity entity16 = new ConfEntity("synoptic.main.SynopticOptions",
				"logLvlQuiet", false);
		ConfEntity entity17 = new ConfEntity("synoptic.main.SynopticOptions",
				"mineNeverConcurrentWithInv", false);
		ConfEntity entity18 = new ConfEntity("synoptic.main.SynopticOptions",
				"noCoarsening", false);
		ConfEntity entity19 = new ConfEntity("synoptic.main.SynopticOptions",
				"noRefinement", false);
		ConfEntity entity20 = new ConfEntity("synoptic.main.SynopticOptions",
				"onlyMineInvariants", false);
		ConfEntity entity21 = new ConfEntity("synoptic.main.SynopticOptions",
				"outputEdgeLabels", false);
		ConfEntity entity22 = new ConfEntity("synoptic.main.SynopticOptions",
				"outputInvariantsToFile", false);
		ConfEntity entity23 = new ConfEntity("synoptic.main.SynopticOptions",
				"outputPathPrefix", false);
		ConfEntity entity24 = new ConfEntity("synoptic.main.SynopticOptions",
				"partitionRegExp", false);
		ConfEntity entity25 = new ConfEntity("synoptic.main.SynopticOptions",
				"performExtraChecks", false);
		ConfEntity entity26 = new ConfEntity("synoptic.main.SynopticOptions",
				"randomSeed", false);
		ConfEntity entity27 = new ConfEntity("synoptic.main.SynopticOptions",
				"recoverFromParseErrors", false);
		ConfEntity entity28 = new ConfEntity("synoptic.main.SynopticOptions",
				"regExps", false);
		ConfEntity entity29 = new ConfEntity("synoptic.main.SynopticOptions",
				"runAllTests", false);
		ConfEntity entity30 = new ConfEntity("synoptic.main.SynopticOptions",
				"runTests", false);
		ConfEntity entity31 = new ConfEntity("synoptic.main.SynopticOptions",
				"separateVTimeIndexSets", false);
		ConfEntity entity32 = new ConfEntity("synoptic.main.SynopticOptions",
				"separatorRegExp", false);
		ConfEntity entity33 = new ConfEntity("synoptic.main.SynopticOptions",
				"showInitialNode", false);
		ConfEntity entity34 = new ConfEntity("synoptic.main.SynopticOptions",
				"showTerminalNode", false);
		ConfEntity entity35 = new ConfEntity("synoptic.main.SynopticOptions",
				"useFSMChecker", false);
		ConfEntity entity36 = new ConfEntity("synoptic.main.SynopticOptions",
				"useTransitiveClosureMining", false);
		ConfEntity entity37 = new ConfEntity("synoptic.main.SynopticOptions",
				"version", false);
		
		List<ConfEntity> list = new LinkedList<ConfEntity>();
		list.add(entity1);
		list.add(entity2);
		list.add(entity3);
		list.add(entity4);
		list.add(entity5);
		list.add(entity6);
		list.add(entity7);
		list.add(entity8);
		list.add(entity9);
		list.add(entity10);
		list.add(entity11);
		list.add(entity12);
		list.add(entity13);
		list.add(entity14);
		list.add(entity15);
		list.add(entity16);
		list.add(entity17);
		list.add(entity18);
		list.add(entity19);
		list.add(entity20);
		list.add(entity21);
		list.add(entity22);
		list.add(entity23);
		list.add(entity24);
		list.add(entity25);
		list.add(entity26);
		list.add(entity27);
		list.add(entity28);
		list.add(entity29);
		list.add(entity30);
		list.add(entity31);
		list.add(entity32);
		list.add(entity33);
		list.add(entity34);
		list.add(entity35);
		list.add(entity36);
		list.add(entity37);
		
		return list;
	}
}
