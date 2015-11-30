package com.ibm.wex.relevancyprofiler.resultfetchers;


import java.util.ArrayList;
import java.util.List;

public class ProfilerResult {
    private String _query = "";
    private String _querySource = "";
    private int _totalResults = 0;

    private RankedResult _topResult = null;
    private List<RankedResult> _interestingResults = new ArrayList<RankedResult>();
    private List<String> _expectedNotFound = new ArrayList<String>();


    public String getQuery() { return _query; }
    public String getQuerySource() { return _querySource; }

    public int getTotalResults() { return _totalResults; }
    public void setTotalResults(int value) { _totalResults = value; }


    public ProfilerResult(String query, String querySource) {
        _query = query;
        _querySource = querySource;
    }

    public void addInterestingResult(RankedResult resultToAdd) {
        _interestingResults.add(resultToAdd);
    }


    public void setFirstHit(RankedResult firstResult) {
        // maybe should check if they aren't the same or something?
        // throw or log an error... ?
        _topResult = firstResult;
    }

    public void addResultsNotFound(String expectedKey) {
        if (!_expectedNotFound.contains(expectedKey)) {
            _expectedNotFound.add(expectedKey);
        }
    }
}
