package com.ibm.wex.relevancyprofiler.filters;

import java.util.ArrayList;
import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityQuery;

public class QueriesWithNoResultsFilter extends Filter
										implements IResultsFilter {

	/**
	 * The list of those queries and sources that did not return any results in a format suitable for display.
	 */
	public List<FilterRecord> filterResults(ProfilingSession session) {
		List<FilterRecord> records = new ArrayList<FilterRecord>();

		for (VelocityQuery q : session.getQueriesAndResults()) {
			if (!q.hasResults()) {
				FilterRecord currentRecord = new FilterRecord();
				currentRecord.addField(q.getQuery()).addField(q.getBundle());
				records.add(currentRecord);
			}
		}
		
		return records;
	}
	

	public String getFileName() {
		return "empty-results.csv";
	}
	
	
	public FilterRecord getHeader() { return new FilterRecord().addField("query").addField("bundle"); }

}
