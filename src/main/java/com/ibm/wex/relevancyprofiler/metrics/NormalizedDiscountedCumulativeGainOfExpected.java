package com.ibm.wex.relevancyprofiler.metrics;


import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResult;
import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResultSet;
import com.ibm.wex.relevancyprofiler.resultfetchers.ResultDetails;

import java.util.Collection;

public class NormalizedDiscountedCumulativeGainOfExpected implements IMetric {

    public double calculate(ProfilerResultSet results) {
        double numberOfQueries = results.getQueryCount();

        double summation = 0.0d;
        for (ProfilerResult result : results.getResults()) {
            summation += normalizedDiscountedCumulativeGain(result);
        }

        return summation / numberOfQueries;
    }


    private double normalizedDiscountedCumulativeGain(ProfilerResult result) {
        double dcg = discountedCumulativeGain(result.getInterestingResults());
        double ideal_dcg = idealDiscountedCumulativeGain(result.getExpectations().size());

        if (ideal_dcg > 0) {
            return dcg / ideal_dcg;
        }

        return 0;
    }


    private double discountedCumulativeGain(Collection<ResultDetails> results) {
        double score = 0;

        for (ResultDetails details : results) {
            double relevance = 1.0;
            int ranking = (details.getRank());
            if (ranking > 1) {
                // for all positions after the first one,
                // reduce the "gain" as ranking increases
                relevance /= logBase2(ranking + 2);
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
                relevance /= logBase2(ranking + 2);
            }
            score += relevance;
        }

        return score;
    }

    private static double logBase2(double value) {
        return Math.log(value) / Math.log(2);
    }

}
