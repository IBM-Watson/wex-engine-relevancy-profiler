package com.ibm.wex.relevancyprofiler.filters;

import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;



public interface IResultsFilter {
	
	List<String> filterResults(ProfilingSession session);
	
	void saveResults(String directoryName, ProfilingSession session);
	
	String getFileName();
	
	String getHeader();

}
