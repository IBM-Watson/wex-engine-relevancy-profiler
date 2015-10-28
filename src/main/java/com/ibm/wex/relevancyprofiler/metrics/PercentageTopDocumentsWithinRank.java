package com.ibm.wex.relevancyprofiler.metrics;

import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityDocument;


/**
 * Calculates the percentage of top documents from the expected queries that are in the Top N
 * This metric only looks at the top documents.  This metric will consider all queries, even those
 * that did not return results.
 * 
 * % Top in Top N = (Count of Top Documents in Top N) / (Number of Queries) 
 */
public class PercentageTopDocumentsWithinRank implements IRelevancyMetric {

	private int _n = 10;
	
	public PercentageTopDocumentsWithinRank(int rank) {
		_n = rank;
	}
	
	
	public Double calculate(ProfilingSession results) {
		int queryCount = results.getQueries().size();
		
		if (queryCount == 0) {
			return null;
		}
		
		List<VelocityDocument> topDocuments = results.getTopRankedExpectedDocuments();
		
		if (topDocuments == null || topDocuments.size() == 0) {
			return 0.0;
		}
		
		int countInTopN = 0;
		for (VelocityDocument document : topDocuments) {
			if (document.getRank() < _n) {
				countInTopN++;
			}
		}
		
		
		return (double)countInTopN / queryCount;
	}
	
	public String getName() {
		return "Percentage of Top Expected Documents in Top " + _n;
	}

	public String getDescription() {
		return "The percentage of top expected documents that appear in the top " + _n + ", that is documents with a rank less than " + _n + " that have the lowest rank for a query.  " +
		   "Only the lowest ranked documents are considered.  Higher is better with the best score being 1.0.";
	}

}
