package com.ibm.wex.relevancyprofiler.metrics;


import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityDocument;
import com.ibm.wex.relevancyprofiler.VelocityQuery;

public class PrecisionAt10Metric implements IRelevancyMetric {
    private final int N = 10;

    public Double calculate(ProfilingSession results) {
        // relevantDocumentCount = sum the number of expectations per query
        // intersectionOfRelevantRetrieved = sum the number of expectations found per query

        int retrievedCount = 0;
        int intersectionOfRelevantRetrieved = 0;
        for (VelocityQuery q : results.getQueriesAndResults()) {
            retrievedCount += Math.min(q.getTotalRetrieved(), N);

            for (VelocityDocument expected : q.getExpectedResults()) {
                if (expected.getRank() < N) {
                    intersectionOfRelevantRetrieved += 1;
                }
            }
        }

        // recall = intersectionOfRelevantRetrieved / relevantDocumentCount
        if (retrievedCount > 0) {
            return (double)intersectionOfRelevantRetrieved / retrievedCount;
        }

        return 0.0;
    }

    public String getName() {
        return "Precision at " + N;
    }

    public String getDescription() {
        return "Precision metric taking into account only the first " + N + " results.";
    }
}
