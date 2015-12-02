package com.ibm.wex.relevancyprofiler.metrics;


import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResult;
import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResultSet;



public class Recall implements IMetric {

    public double calculate(ProfilerResultSet results) {
        int totalRelevantRetrieved = 0;
        int totalRelevant = 0;
        for (ProfilerResult result : results.getResults()) {
            int countOfRelevantRetrieved = result.getInterestingResults().size();
            int countOfRelevantNotRetrieved = result.getExpectedNotFoundResults().size();

            totalRelevant = totalRelevant + countOfRelevantNotRetrieved + countOfRelevantRetrieved;
            totalRelevantRetrieved = totalRelevantRetrieved + countOfRelevantRetrieved;
        }

        double recall = 0;
        if (totalRelevant > 0) {
            recall = (double) totalRelevantRetrieved / totalRelevant;
        }

        return recall;
    }
}
