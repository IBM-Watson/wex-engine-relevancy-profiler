package com.ibm.wex.relevancyprofiler.metrics;

import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityDocument;
import com.ibm.wex.relevancyprofiler.VelocityQuery;

public class NormalizedDiscountedCumulativeGainOfExpectedResults implements
		IRelevancyMetric {

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
			summation += normalizedDiscountedCumulativeGain(queryAndResults);
		}

		return summation / numberOfQueries;
	}

	private double normalizedDiscountedCumulativeGain(VelocityQuery queryAndResults) {
		double dcg = discountedCumulativeGain(queryAndResults.getExpectedResults());
		double idealdcg = idealDiscountedCumulativeGain(queryAndResults.getExpectations().size());
		return dcg / idealdcg;
	}
	
	private double discountedCumulativeGain(
			List<VelocityDocument> queryResults) {
		double score = 0;

		for (VelocityDocument expectedResult : queryResults) {
			double relevance = 1.0d;
			int ranking = (expectedResult.getRank() + 1);
			if (ranking > 1) {
				// for all positions after the first one,
				// reduce the "gain" as ranking increases
				relevance /= logBase2(ranking + 1);
			}
			score += relevance;
		}

		return score;
	}
	
	private double idealDiscountedCumulativeGain(int n) {
		double score = 0;

		for (int ranking = 1; ranking <= n; ranking++) {
			double relevance = 1.0d;
			if (ranking > 1) {
				relevance /= logBase2(ranking + 1);
			}
			score += relevance;
		}

		return score;
	}

	private static double logBase2(double value) {
		return Math.log(value) / Math.log(2);
	}

	public String getName() {
		return "Normalized Discounted Cumulative Gain";
	}

	public String getDescription() {
		return "The normalized discounted cumulative gain of all expected documents.  "
				+ "Missing documents are considered in this metric.  "
				+ "It is the average of the normalized discounted cumulative gains of all the queries. "
				+ "The relevance judgments considered are binary."
				+ "Higher is better: 1 means the all the expected documents are ranked in the top positions for all queries, "
				+ "0 means that no document is found at all.";
	}

}
