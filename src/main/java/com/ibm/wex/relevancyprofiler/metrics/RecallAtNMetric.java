package com.ibm.wex.relevancyprofiler.metrics;


import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityQuery;

public class RecallAtNMetric implements IRelevancyMetric {


    public Double calculate(ProfilingSession results) {
        // relevantDocumentCount = sum the number of expectations per query
        // intersectionOfRelevantRetrieved = sum the number of expectations found per query

        int relevantDocumentCount = 0;
        int intersectionOfRelevantRetrieved = 0;
        for (VelocityQuery q : results.getQueriesAndResults()) {
            relevantDocumentCount += q.getExpectations().size();
            intersectionOfRelevantRetrieved += q.getExpectedResults().size();
        }

        // recall = intersectionOfRelevantRetrieved / relevantDocumentCount
        if (relevantDocumentCount > 0) {
            return (double)intersectionOfRelevantRetrieved / relevantDocumentCount;
        }

        return 0.0;
    }

    public String getName() {
        return "Recall at N";
    }

    public String getDescription() {
        return "From Wikipedia: Recall in information retrieval is the fraction of the documents that are relevant to the query that are successfully retrieved. For example for text search on a set of documents recall is the number of correct results divided by the number of results that should have been returned  See also: https://en.wikipedia.org/wiki/Precision_and_recall";
    }
}
