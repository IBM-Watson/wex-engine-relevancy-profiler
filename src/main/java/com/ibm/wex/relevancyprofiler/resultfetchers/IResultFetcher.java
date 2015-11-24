package com.ibm.wex.relevancyprofiler.resultfetchers;

import com.ibm.wex.relevancyprofiler.VelocityQuery;
import com.ibm.wex.relevancyprofiler.groundtruth.GroundTruth;

public interface IResultFetcher {
	
	ProfilerResultSet CollectResults(GroundTruth golden);
}
