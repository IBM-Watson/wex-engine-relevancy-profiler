package com.ibm.wex.relevancyprofiler.metrics;


import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResult;
import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResultSet;
import com.ibm.wex.relevancyprofiler.resultfetchers.ResultDetails;

import java.util.Collection;

public class MeanRankOfFoundExpected implements IMetric {

    public double calculate(ProfilerResultSet results) {
        int countOfRankedResults = 0;
        int sumOfRanks = 0;
        for (ProfilerResult result : results.getResults()) {
            Collection<ResultDetails> details = result.getInterestingResults();
            countOfRankedResults += details.size();

            for (ResultDetails d : details) {
                sumOfRanks += d.getRank();
            }
        }

        double meanRank = 0;
        if (countOfRankedResults > 0) {
            meanRank = (double) sumOfRanks / countOfRankedResults;
        }

        return meanRank;
    }
}
