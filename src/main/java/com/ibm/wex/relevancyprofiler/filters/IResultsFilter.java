package com.ibm.wex.relevancyprofiler.filters;

import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;



public interface IResultsFilter {
	
	List<FilterRecord> filterResults(ProfilingSession session);
	
	void saveResults(String directoryName, ProfilingSession session);
	
	String getFileName();
	
	FilterRecord getHeader();

}
