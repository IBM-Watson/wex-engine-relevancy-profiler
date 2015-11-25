package com.ibm.wex.relevancyprofiler.resultfetchers;


import java.util.HashMap;
import java.util.Map;

public class ProfilerResultSet {
    private Map<String, ProfilerResult> _results = new HashMap<String, ProfilerResult>();


    public ProfilerResultSet() { }


    public void addResult(String query, String source, RankedResult result) {
        String key = initializeForQuery(query, source);
        _results.get(key).addResult(result);
    }


    public void setFirstHit(String query, String source, RankedResult firstResult) {
        String key = initializeForQuery(query, source);
        _results.get(key).setFirstHit(firstResult);
    }


    private String initializeForQuery(String query, String source) {
        String key = query + " :: " + source;

        if(!_results.containsKey(key)) {
            _results.put(key, new ProfilerResult());
        }

        return key;
    }


    public void addResultNotFound(String query, String source, String expectedKey) {
        String key = initializeForQuery(query, source);
        _results.get(key).addResultsNotFound(expectedKey);
    }
}
