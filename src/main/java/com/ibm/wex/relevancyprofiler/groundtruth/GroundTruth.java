package com.ibm.wex.relevancyprofiler.groundtruth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class GroundTruth {
    private HashMap<String, List<Expectation>> _expectations = new HashMap<String, List<Expectation>>();

    public Set<String> getQueries() { return _expectations.keySet(); }

    public List<Expectation> getExpectationsFor(String query) {
        if (_expectations.containsKey(query)){
            return _expectations.get(query);
        }

        return new ArrayList<Expectation>();
    }

    public void AddExpectation(String query, String source, String expectedUrl, int expectedRank) {
        Expectation e = new Expectation(expectedUrl, expectedRank, source);

        if (_expectations.containsKey(query)) {
            _expectations.get(query).add(e);
        }
        else {
            ArrayList<Expectation> expectationsForQuery = new ArrayList<Expectation>();
            expectationsForQuery.add(e);
            _expectations.put(query, expectationsForQuery);
        }
    }



}
