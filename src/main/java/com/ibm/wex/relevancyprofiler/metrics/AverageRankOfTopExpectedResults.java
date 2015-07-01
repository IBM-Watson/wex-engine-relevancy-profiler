package com.ibm.wex.relevancyprofiler.metrics;

import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityDocument;



public class AverageRankOfTopExpectedResults implements IRelevancyMetric {

	@Override
	public Double calculate(ProfilingSession results) {
		List<VelocityDocument> topDocuments = results.getTopRankedExpectedDocuments();
		
		if (topDocuments == null || topDocuments.size() == 0) {
			return null;
		}
		
		int summation = 0;
		for (VelocityDocument d : topDocuments) {
			summation += d.getRank();
		}
		
		return ((double) summation) / results.getQueries().size();
	}

	@Override
	public String getName() {
		return "Average Rank of Top Expected Results";
	}

	@Override
	public String getDescription() {
		return "The average rank of only the lowest ranked expected document for a query that were found.  " +
	  	       "Missing documents are not considered in this metric, nor are documents beyond the top for a query.  " +
		       "Lower is better with the best score being 0.";
	}

}
