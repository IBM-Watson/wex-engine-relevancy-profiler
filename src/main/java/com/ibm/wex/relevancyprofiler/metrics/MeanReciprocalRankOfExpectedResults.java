package com.ibm.wex.relevancyprofiler.metrics;

import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityDocument;



public class MeanReciprocalRankOfExpectedResults implements IRelevancyMetric {

	public Double calculate(ProfilingSession results) {
		List<VelocityDocument> documents = results.getTopRankedExpectedDocuments();
		
		// this is needed because we also consider the case in which
		// no top ranked document is returned for the query
		double numberOfQueries = results.getQueries().size();
		
		if (documents == null || documents.size() == 0) {
			return null;
		}
		
		double summation = 0.0d;
		for (VelocityDocument d : documents) {
			summation += 1 / (double) (d.getRank() + 1);
		}
		
		return summation / numberOfQueries;
	}

	public String getName() {
		return "Mean Reciprocal Rank of Expected Results";
	}

	public String getDescription() {
		return "The mean reciprocal rank of all expected documents.  " +
			   "Missing documents are considered in this metric, lowering the score.  "
			   + "Higher is better: 1 means the rank of the top ranked expected document is always 1, "
			   + "0.5 means the rank of the top ranked expected document is 2 (on average), "
			   + "0.33 means the rank is 3 (on average) and so on.";
	}

	
	
}
