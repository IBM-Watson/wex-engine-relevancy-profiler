package com.ibm.wex.relevancyprofiler.metrics;


import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResultSet;
import com.ibm.wex.relevancyprofiler.resultfetchers.ResultDetails;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MeanAveragePrecisionOfExpectedTest {

    private MeanAveragePrecisionOfExpected _subject = null;
    private ProfilerResultSet _mockResults = null;

    ResultDetails _details1 = null;

    private final String _query = "TEST_QUERY";
    private final String _source = "TEST_SOURCE";
    private final double _delta = 0.01;

    @Before
    public void setUp() {
        _subject = new MeanAveragePrecisionOfExpected();
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
    public void should_get_0_when_no_expected_results_are_found() {
        _mockResults.setTotalCount(_query, _source, 100);
        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(0.0, result, _delta);
    }


    @Test
    public void should_get_1_when_1_expected_result_is_found_for_1_query() {
        _mockResults.setTotalCount(_query, _source, 1);
        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(1.0, result, _delta);
    }


    @Test
    public void should_get_map_when_only_one_query() {
        _mockResults.setTotalCount(_query, _source, 100);
        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);

        Double result = _subject.calculate(_mockResults);
        Double expectedAnswer = new Precision().calculate(_mockResults);
        Assert.assertEquals(expectedAnswer, result, _delta);
    }


    @Test
    public void should_get_proper_map_with_two_queries() {
        _mockResults.setTotalCount(_query, _source, 100);
        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);

        String otherQuery = "Other Query";
        ResultDetails details2 = new ResultDetails();
        details2.setKey("Other Result");
        details2.setRank(2);

        _mockResults.setTotalCount(otherQuery, _source, 2);
        _mockResults.addResult(otherQuery, _source, details2);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(0.255, result, _delta);
    }



    @Test
    public void should_get_map_at_n_when_more_than_n_results_are_returned() {
        _subject = new MeanAveragePrecisionOfExpected(10);

        _mockResults.setTotalCount(_query, _source, 100);

        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(0.1, result, _delta);
    }


    @Test
    public void should_get_precision_at_n_when_less_than_n_results_are_returned() {
        _subject = new MeanAveragePrecisionOfExpected(10);

        _mockResults.setTotalCount(_query, _source, 5);

        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(0.2, result, _delta);
    }



}
