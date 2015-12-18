package com.ibm.wex.relevancyprofiler.reports;


import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResultSet;

public interface IReport {

    void writeReport(ProfilerResultSet results);
}
