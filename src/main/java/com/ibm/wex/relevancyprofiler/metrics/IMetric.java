package com.ibm.wex.relevancyprofiler.metrics;


import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResultSet;

public interface IMetric {

    double calculate(ProfilerResultSet results);
}
