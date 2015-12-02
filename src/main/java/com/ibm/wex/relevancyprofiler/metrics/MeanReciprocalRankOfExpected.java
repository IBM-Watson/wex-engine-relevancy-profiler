package com.ibm.wex.relevancyprofiler.metrics;


import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResult;
import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResultSet;
import com.ibm.wex.relevancyprofiler.resultfetchers.ResultDetails;

public class MeanReciprocalRankOfExpected implements IMetric {

    public double calculate(ProfilerResultSet results) {
        double numberOfQueries = results.getQueryCount();

        double summation = 0;
        for (ProfilerResult r : results.getResults()) {
            for (ResultDetails details : r.getInterestingResults()) {  //should this only be the top result or all results?
                summation += 1 / (double) (details.getRank() + 1); //ranks are 0-based so... this metric borks
            }
        }

        return summation / numberOfQueries;
    }
}
