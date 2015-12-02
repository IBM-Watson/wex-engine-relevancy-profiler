package com.ibm.wex.relevancyprofiler.metrics;


import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResult;
import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResultSet;

public class Precision {

    private int _n = -1;

    public Precision() { }
    public Precision(int n) { _n = n; }

    public double calculate(ProfilerResultSet results) {
        int totalRelevantRetrieved = 0;
        int totalRetrieved = 0;
        for (ProfilerResult result : results.getResults()) {
            int countOfRelevantRetrieved = result.getInterestingResults().size();
            int countOfRetrieved = result.getTotalResults();

            if (_n > 0) {
                countOfRetrieved = Math.min(_n, countOfRetrieved);
                countOfRelevantRetrieved = result.getInterestingResultsAt(_n).size();
            }

            totalRetrieved += countOfRetrieved;
            totalRelevantRetrieved += countOfRelevantRetrieved;
        }

        double precision = 0;
        if (totalRetrieved > 0) {
            precision = (double) totalRelevantRetrieved / totalRetrieved;
        }

        return precision;
    }
}
