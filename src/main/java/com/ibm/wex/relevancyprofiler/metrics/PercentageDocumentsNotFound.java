package com.ibm.wex.relevancyprofiler.metrics;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.filters.ExpectedNotFoundFilter;
import com.ibm.wex.relevancyprofiler.filters.IResultsFilter;



public class PercentageDocumentsNotFound implements IRelevancyMetric {

	@Override
	public Double calculate(ProfilingSession results) {
		int queryCount = results.getQueries().size();
		
		if (queryCount == 0) {
			return null;
		}
		
		IResultsFilter filter = new ExpectedNotFoundFilter();
		int notFoundCount = filter.filterResults(results).size();
		int totalDocuments = results.getExpectedDocuments().size() + notFoundCount;
		
		return (double)notFoundCount / totalDocuments;
	}

	@Override
	public String getName() {
		return "Percentage of Expected Documents Not Found";
	}

	@Override
	public String getDescription() {
		return "The percentage of documents that were expected but not found in the results returned.  " +
		       "This number can change if the number of results returned by Velocity when profiling is altered.  " + 
		       "Lower is better with the best score being 0.";

	}

}
