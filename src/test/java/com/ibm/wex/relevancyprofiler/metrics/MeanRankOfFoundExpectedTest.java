package com.ibm.wex.relevancyprofiler.metrics;


import com.ibm.wex.relevancyprofiler.resultfetchers.ResultDetails;
import org.junit.Assert;
import org.junit.Test;


public class MeanRankOfFoundExpectedTest extends MetricTestBase {

    protected IMetric createMetric() {
        return new MeanRankOfFoundExpected();
    }


    @Test
    public void should_get_0_when_no_expected_results_are_found() {
        _mockResults.setTotalCount(_query, _source, 100);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(0.0, result, _delta);
    }


    @Test
    public void should_get_1_when_1_expected_result_is_found_in_top_spot() {
        _mockResults.setTotalCount(_query, _source, 100);

        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(1.0, result, _delta);
    }


    @Test
    public void should_get_2_when_1_expected_result_is_found_in_second_spot() {
        _mockResults.setTotalCount(_query, _source, 100);

        _details1.setRank(2);
        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, new ResultDetails());

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(2, result, _delta);
    }

    @Test
    public void should_get_correct_average_when_expected_results_are_in_positions_1_and_2() {
        _mockResults.setTotalCount(_query, _source, 100);

        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);

        ResultDetails details2 = new ResultDetails();
        details2.setKey("Another Result");
        details2.setRank(2);
        _mockResults.addResult(_query, _source, details2);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(1.5, result, _delta);
    }

    @Test
    public void should_not_count_expected_results_not_found() {
        _mockResults.setTotalCount(_query, _source, 100);

        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);

        _mockResults.addResultNotFound(_query, _source, "NOT FOUND KEY");

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(1.0, result, _delta);
    }

    @Test
    public void should_still_work_when_no_results_found() {
        _mockResults.setTotalCount(_query, _source, 0);

        _mockResults.addResultNotFound(_query, _source, _details1.getKey());

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(0.0, result, _delta);
    }



}
