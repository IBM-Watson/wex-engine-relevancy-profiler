package com.ibm.wex.relevancyprofiler.metrics;


import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResult;
import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResultSet;
import com.ibm.wex.relevancyprofiler.resultfetchers.ResultDetails;

import java.util.Collection;

public class MeanAveragePrecisionOfExpected implements IMetric {

    private int _n = -1;

    public MeanAveragePrecisionOfExpected() { }
    public MeanAveragePrecisionOfExpected(int n) { _n = n; }


    public double calculate(ProfilerResultSet results) {
        double totalAveragePrecision = 0;

        for (ProfilerResult result : results.getResults()) {
            totalAveragePrecision += calculateAveragePrecisionForQuery(result);
        }

        int numberOfQueries = results.getQueryCount();
        double meanAveragePrecision = 0;
        if (numberOfQueries > 0) {
            meanAveragePrecision = totalAveragePrecision / numberOfQueries;
        }

        return meanAveragePrecision;
    }

    private double calculateAveragePrecisionForQuery(ProfilerResult result)  {
        int countOfRelevantRetrieved = result.getInterestingResults().size();
        int countOfRetrieved = result.getTotalResults();
        int totalRelevantDocuments = countOfRelevantRetrieved + result.getExpectedNotFoundResults().size();

        Collection<ResultDetails> details = null;
        if (_n > 0) {
            countOfRetrieved = Math.min(_n, countOfRetrieved);
            countOfRelevantRetrieved = result.getInterestingResultsAt(_n).size();
        }

        //compute precision for the query
        double precision = 0;
        if (countOfRetrieved > 0) {
            precision = (double) countOfRelevantRetrieved / countOfRetrieved;
        }

        // now compute the average of the precision
        double averagePrecision = 0;
        if (totalRelevantDocuments > 0) {
            averagePrecision = precision / totalRelevantDocuments;
        }

        return averagePrecision;
    }
}
