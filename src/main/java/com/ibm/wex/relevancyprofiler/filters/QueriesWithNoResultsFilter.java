package com.ibm.wex.relevancyprofiler.filters;

import java.util.ArrayList;
import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityQuery;

public class QueriesWithNoResultsFilter extends Filter
										implements IResultsFilter {

	/**
	 * The list of those queries and sources that did not return any results in a format suitable for display.
	 * The first item in the list is a header.
	 * @return A list of queries with no results.
	 */
	@Override
	public List<String> filterResults(ProfilingSession session) {
		List<String> lines = new ArrayList<String>();

		for (VelocityQuery q : session.getQueriesAndResults()) {
			if (!q.hasResults()) {
				lines.add(q.getQuery() + "," + q.getBundle());
			}
		}
		
		return lines;
	}
	

	@Override
	public String getFileName() {
		return "empty-results.csv";
	}
	
	
	public String getHeader() {
		return "query,bundle";
	}

}
