package com.ibm.wex.relevancyprofiler.resultfetchers;

import com.ibm.wex.relevancyprofiler.groundtruth.GroundTruth;

public interface IResultFetcher {
	
	ProfilerResultSet collectResults(GroundTruth golden);
}
