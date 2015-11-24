package com.ibm.wex.relevancyprofiler.filters;

import java.util.ArrayList;
import java.util.List;

import com.ibm.wex.relevancyprofiler.groundtruth.Expectation;
import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityQuery;

public class ExpectedNotFoundFilter extends Filter implements IResultsFilter {

	/**
	 * Gets the list of expected results that could not be found in the top N results 
	 * for a given query suitable for display to the user.
	 */
	public List<FilterRecord> filterResults(ProfilingSession session) {
		List<FilterRecord> records = new ArrayList<FilterRecord>();

		for (VelocityQuery q : session.getQueriesAndResults()) {
			for (Expectation e : q.getMissingExpectations()) {
				FilterRecord currentRecord = new FilterRecord();
                currentRecord.addField(q.getQuery()).addField(q.getBundle()).addField(e.getUrl());
				records.add(currentRecord);
			}
		}
		
		return records;
	}
	
	

	public String getFileName() {
		return "not-found-but-expected.csv";
	}
	
	
	public FilterRecord getHeader() { return new FilterRecord().addField("query").addField("bundle").addField("url"); }

}
