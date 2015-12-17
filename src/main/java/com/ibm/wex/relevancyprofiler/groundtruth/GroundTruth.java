package com.ibm.wex.relevancyprofiler.groundtruth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class GroundTruth {
    private HashMap<Query, List<Expectation>> _expectations = new HashMap<Query, List<Expectation>>();

    public Set<Query> getQueries() { return _expectations.keySet(); }

    public List<Expectation> getExpectationsFor(Query query) {
        if (_expectations.containsKey(query)){
            return _expectations.get(query);
        }

        return new ArrayList<Expectation>();
    }

    public void AddExpectation(Query query, String expectedUrl, int expectedRank) {
        Expectation e = new Expectation(expectedUrl, expectedRank, query.getSource(), query.getQueryString());

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
