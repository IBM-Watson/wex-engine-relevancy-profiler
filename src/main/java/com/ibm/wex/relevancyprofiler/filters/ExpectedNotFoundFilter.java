package com.ibm.wex.relevancyprofiler.filters;

import java.util.ArrayList;
import java.util.List;

import com.ibm.wex.relevancyprofiler.Expectation;
import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityQuery;

public class ExpectedNotFoundFilter extends Filter implements IResultsFilter {

	/**
	 * Gets the list of expected results that could not be found in the top N results 
	 * for a given query suitable for display to the user.  
	 * The first item in the list is a header.
	 * @return The list of expected results not found.
	 */
	@Override
	public List<String> filterResults(ProfilingSession session) {
		List<String> lines = new ArrayList<String>();

		for (VelocityQuery q : session.getQueriesAndResults()) {
			for (Expectation e : q.getMissingExpectations()) {
				lines.add(q.getQuery() + "," + q.getBundle() + "," + e.getUrl());
			}
		}
		
		return lines;
	}
	
	

	@Override
	public String getFileName() {
		return "not-found-but-expected.csv";
	}
	
	
	public String getHeader() {
		return "query,bundle,url";
	}

}
