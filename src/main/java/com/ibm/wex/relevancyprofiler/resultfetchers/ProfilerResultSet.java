package com.ibm.wex.relevancyprofiler.resultfetchers;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;

public class ProfilerResultSet {
    private Map<String, ProfilerResult> _results = new HashMap<String, ProfilerResult>();


    public ProfilerResultSet() { }


    public void addResult(String query, String source, ResultDetails result) {
        String key = initializeForQuery(query, source);
        _results.get(key).addInterestingResult(result);
    }


    public void setFirstHit(String query, String source, ResultDetails firstResult) {
        String key = initializeForQuery(query, source);
        _results.get(key).setFirstHit(firstResult);
    }


    public void addResultNotFound(String query, String source, String expectedKey) {
        String key = initializeForQuery(query, source);
        _results.get(key).addResultsNotFound(expectedKey);
    }


    public void setTotalCount(String query, String source, int totalResults) {
        String key = initializeForQuery(query, source);
        _results.get(key).setTotalResults(totalResults);
    }


    public Collection<ProfilerResult> getResults() {
        return _results.values();
    }

    public int getQueryCount() { return _results.keySet().size(); }



    private String initializeForQuery(String query, String source) {
        String key = query + " :: " + source;

        if(!_results.containsKey(key)) {
            _results.put(key, new ProfilerResult(query, source));
        }

        return key;
    }

    public Collection<String> getQueriesWithNoResults() {
        Collection<String> zeroResultQueries = new ArrayList<String>();
        for (ProfilerResult r : _results.values()) {
            if (r.getTotalResults() == 0) {
                zeroResultQueries.add(r.getQuery());
            }
        }

        return zeroResultQueries;
    }


    public int getCountOfExpectedNotFound() {
        int expectedNotFound = 0;
        for (ProfilerResult r : _results.values()) {
            expectedNotFound += r.getExpectedNotFoundResults().size();
        }

        return expectedNotFound;
    }
}
