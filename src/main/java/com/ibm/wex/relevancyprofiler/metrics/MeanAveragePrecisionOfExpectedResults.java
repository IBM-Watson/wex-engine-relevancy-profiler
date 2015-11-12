package com.ibm.wex.relevancyprofiler.metrics;

import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityDocument;
import com.ibm.wex.relevancyprofiler.VelocityQuery;

public class MeanAveragePrecisionOfExpectedResults implements IRelevancyMetric {

	public Double calculate(ProfilingSession results) {
		List<VelocityQuery> queriesAndResults = results.getQueriesAndResults();

		if (queriesAndResults == null || queriesAndResults.size() == 0) {
			return null;
		}
		
		// this is needed because we also consider the case in which
		// no top ranked document is returned for the query
		double numberOfQueries = queriesAndResults.size();
		
		double summation = 0.0d;
		for (VelocityQuery queryAndResults : queriesAndResults) {
			List<VelocityDocument> queryResults = queryAndResults.getExpectedResults();
			summation += averagePrecision(queryResults, queryAndResults.getExpectations().size());
		}
		
		return summation / numberOfQueries;
	}

	private double averagePrecision(List<VelocityDocument> queryResults, int numExpectedDocuments) {
		double score = 0.0d;
		double numberOfHits = 0.0d;
		
		for (VelocityDocument resultDocument : queryResults) {
			numberOfHits += 1.0d;
			score += numberOfHits / (resultDocument.getRank() + 1);
		}

		// should be the min(numExpectedDocuments, k)
		// because computing average precision at k means
		// that no more thank k relevant document can be expected
		// but I'm not sure where to get k (the number of top ranked documents considered) from
		return score / (double) numExpectedDocuments;
	}

	public String getName() {
		return "Mean Average Precision of Expected Results";
	}

	public String getDescription() {
		return "The mean average precision of all expected documents.  "
				+ "Missing documents are considered in this metric.  "
				+ "It is the average of the area under the precision/recall curve of all the queries. "
				+ "Higher is better: 1 means the all the expected documents are ranked in the top positions for all queries, "
				+ "0 means that no document in found at all.";
	}

}
