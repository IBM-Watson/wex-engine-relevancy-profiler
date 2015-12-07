package com.ibm.wex.relevancyprofiler.metrics;


import com.ibm.wex.relevancyprofiler.resultfetchers.ResultDetails;
import org.junit.Assert;
import org.junit.Test;


public class MeanAveragePrecisionOfExpectedTest extends MetricTestBase {

    protected IMetric createMetric() {
        return new MeanAveragePrecisionOfExpected();
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
