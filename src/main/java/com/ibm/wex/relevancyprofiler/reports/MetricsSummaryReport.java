package com.ibm.wex.relevancyprofiler.reports;


import com.ibm.wex.relevancyprofiler.metrics.*;
import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResultSet;

public class MetricsSummaryReport {

    // should probably take a stream or something...
    public void writeReport(ProfilerResultSet results) {
        Recall r = new Recall();
        System.out.println("Recall = " + r.calculate(results) );

        Precision p = new Precision();
        System.out.println("Precision = " + p.calculate(results) );

        Precision p1 = new Precision(1);
        System.out.println("P@1 = " + p1.calculate(results));

        Precision p3 = new Precision(3);
        System.out.println("P@3 = " + p3.calculate(results) );

        Precision p10 = new Precision(10);
        System.out.println("P@10 = " + p10.calculate(results) );

        Precision p50 = new Precision(50);
        System.out.println("P@50 = " + p50.calculate(results) );

        MeanRankOfFoundExpected meanRank = new MeanRankOfFoundExpected();
        System.out.println("Mean Rank = " + meanRank.calculate(results));

        MeanAveragePrecisionOfExpected map = new MeanAveragePrecisionOfExpected();
        System.out.println("MAP = " + map.calculate(results));

        MeanAveragePrecisionOfExpected map1 = new MeanAveragePrecisionOfExpected(1);
        System.out.println("MAP@1 = " + map1.calculate(results));

        MeanAveragePrecisionOfExpected map3 = new MeanAveragePrecisionOfExpected(3);
        System.out.println("MAP@3 = " + map3.calculate(results));

        MeanAveragePrecisionOfExpected map10 = new MeanAveragePrecisionOfExpected(10);
        System.out.println("MAP@10 = " + map10.calculate(results));

        MeanAveragePrecisionOfExpected map50 = new MeanAveragePrecisionOfExpected(50);
        System.out.println("MAP@50 = " + map50.calculate(results));

        MeanReciprocalRankOfExpected mrr = new MeanReciprocalRankOfExpected();
        System.out.println("MRR = " + mrr.calculate(results));

        NormalizedDiscountedCumulativeGainOfExpected ndcg = new NormalizedDiscountedCumulativeGainOfExpected();
        System.out.println("NDCG = " + ndcg.calculate(results));

        System.out.println("Queries with 0 Results = " + results.getQueriesWithNoResults().size());
        System.out.println("Number Expected Results Not Found = " + results.getCountOfExpectedNotFound());

    }

}
