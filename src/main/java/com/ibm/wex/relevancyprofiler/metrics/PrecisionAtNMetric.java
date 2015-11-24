package com.ibm.wex.relevancyprofiler.metrics;


import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityQuery;

public class PrecisionAtNMetric implements IRelevancyMetric {

    private int _N;

    public PrecisionAtNMetric() { }

    public PrecisionAtNMetric(int n) {
        _N = n;
    }

    public Double calculate(ProfilingSession results) {
        // relevantDocumentCount = sum the number of expectations per query
        // intersectionOfRelevantRetrieved = sum the number of expectations found per query

        int retrievedCount = 0;
        int intersectionOfRelevantRetrieved = 0;
        for (VelocityQuery q : results.getQueriesAndResults()) {
            retrievedCount += q.getTotalRetrieved();
            intersectionOfRelevantRetrieved += q.getExpectedResults().size();
        }

        // recall = intersectionOfRelevantRetrieved / relevantDocumentCount
        if (retrievedCount > 0) {
            return (double)intersectionOfRelevantRetrieved / retrievedCount;
        }

        return 0.0;
    }

    public String getName() {
        return "Precision at N";
    }

    public String getDescription() {
        return "From Wikipedia: In the field of information retrieval, precision is the fraction of retrieved documents that are relevant to the query. Precision takes all retrieved documents into account, but it can also be evaluated at a given cut-off rank, considering only the topmost results returned by the system. This measure is called precision at n or P@n.  See also: https://en.wikipedia.org/wiki/Precision_and_recall  P@n in this case is determined by the 'Max Results' parameter.";
    }
}
