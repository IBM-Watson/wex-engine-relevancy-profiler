package com.ibm.wex.relevancyprofiler.metrics;

import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityDocument;



public class AverageRankOfExpectedResults implements IRelevancyMetric {

	@Override
	public Double calculate(ProfilingSession results) {
		List<VelocityDocument> documents = results.getExpectedDocuments();
		
		if (documents == null || documents.size() == 0) {
			return null;
		}
		
		int summation = 0;
		for (VelocityDocument d : documents) {
			summation += d.getRank();
		}
		
		return (double)summation / documents.size();
	}

	@Override
	public String getName() {
		return "Average Rank of Expected Results";
	}

	@Override
	public String getDescription() {
		return "The average rank of all expected documents that were found.  " +
			   "Missing documents are not considered in this metric.  Lower is better with the best score being 0.";
	}

	
	
}
