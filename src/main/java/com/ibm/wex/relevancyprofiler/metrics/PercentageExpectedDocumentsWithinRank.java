package com.ibm.wex.relevancyprofiler.metrics;

import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityDocument;
import com.ibm.wex.relevancyprofiler.filters.ExpectedNotFoundFilter;
import com.ibm.wex.relevancyprofiler.filters.IResultsFilter;



public class PercentageExpectedDocumentsWithinRank implements IRelevancyMetric {

	private int _n = 10;
	
	public PercentageExpectedDocumentsWithinRank(int rank) {
		_n = rank;
	}
	
	@Override
	public Double calculate(ProfilingSession results) {
		int queryCount = results.getQueries().size();
		
		if (queryCount == 0) {
			return null;
		}
		
		List<VelocityDocument> documents = results.getExpectedDocuments();
		
		if (documents == null || documents.size() == 0) {
			return 0.0;
		}

		int countInTopN = 0;
		for (VelocityDocument document : documents) {
			if (document.getRank() < _n) {
				countInTopN++;
			}
		}
		
		IResultsFilter filter = new ExpectedNotFoundFilter();
		int totalResults = documents.size() + filter.filterResults(results).size();
		return (double)countInTopN / totalResults;
	}

	@Override
	public String getName() {
		return "Percentage of Expected Documents in Top " + _n;
	}

	@Override
	public String getDescription() {
		return "The percentage of expected documents that appear in the top " + _n + ", that is documents with a rank less than " + _n + ".  " +
		   "Higher is better with the best score being 1.0.";
	}

}
