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


    }

}
