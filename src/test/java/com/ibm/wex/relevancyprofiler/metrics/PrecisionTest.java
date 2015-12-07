package com.ibm.wex.relevancyprofiler.metrics;


import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResultSet;
import com.ibm.wex.relevancyprofiler.resultfetchers.ResultDetails;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PrecisionTest {

    private Precision _subject = null;
    private ProfilerResultSet _mockResults = null;

    ResultDetails _details1 = null;

    private final String _query = "TEST_QUERY";
    private final String _source = "TEST_SOURCE";
    private final double _delta = 0.01;

    @Before
    public void setUp() {
        _subject = new Precision();
        _mockResults = new ProfilerResultSet();

        _details1 = new ResultDetails();
        _details1.setRank(1);
        _details1.setKey("DETAILS_1");
    }


    @After
    public void tearDown() {
        _subject = null;
        _mockResults = null;

        _details1 = null;
    }


    @Test
    public void should_get_0_percent_precision_when_no_expected_results_are_found() {
        _mockResults.setTotalCount(_query, _source, 100);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(0.0, result, _delta);
    }


    @Test
    public void should_get_100_percent_precision_when_1_of_1_expected_results_are_found_and_only_1_total_result() {
        _mockResults.setTotalCount(_query, _source, 1);

        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(1.0, result, _delta);
    }


    @Test
    public void should_get_50_percent_precision_when_1_of_1_expected_results_are_found_and_2_total_results() {
        _mockResults.setTotalCount(_query, _source, 2);

        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(0.5, result, _delta);
    }

    @Test
    public void should_get_50_percent_precision_when_1_of_1_expected_results_are_found_and_2_total_results_anywhere() {
        _mockResults.setTotalCount(_query, _source, 2);

        _details1.setRank(2);
        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, new ResultDetails());

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(0.5, result, _delta);
    }

    @Test
    public void should_get_100_percent_precision_when_2_of_2_expected_results_are_found_and_2_total_results() {
        _mockResults.setTotalCount(_query, _source, 2);

        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);
        _mockResults.addResult(_query, _source, new ResultDetails());

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(1.0, result, _delta);
    }

    @Test
    public void should_get_precision_lots_of_results_are_returned() {
        _mockResults.setTotalCount(_query, _source, 100);

        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(0.01, result, _delta);
    }


    @Test
    public void should_get_precision_at_n_when_more_than_n_results_are_returned() {
        _subject = new Precision(10);

        _mockResults.setTotalCount(_query, _source, 100);

        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(0.1, result, _delta);
    }


    @Test
    public void should_get_precision_at_n_when_less_than_n_results_are_returned() {
        _subject = new Precision(10);

        _mockResults.setTotalCount(_query, _source, 5);

        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(0.2, result, _delta);
    }






}
