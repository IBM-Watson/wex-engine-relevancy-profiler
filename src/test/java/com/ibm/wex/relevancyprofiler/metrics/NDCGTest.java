package com.ibm.wex.relevancyprofiler.metrics;


import com.ibm.wex.relevancyprofiler.resultfetchers.ResultDetails;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class NDCGTest extends MetricTestBase {

    protected IMetric createMetric() {
        return new NormalizedDiscountedCumulativeGainOfExpected();
    }

    @Test
    public void should_get_0_when_no_expected_results_are_found() {
        _mockResults.setTotalCount(_query, _source, 100);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(0.0, result, _delta);
    }


    @Test
    public void should_get_1_when_only_expected_result_is_top() {
        _mockResults.setTotalCount(_query, _source, 1);

        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(1.0, result, _delta);
    }


    @Test
    public void should_get_something_good_when_expected_result_is_second() {
        _mockResults.setTotalCount(_query, _source, 2);

        _details1.setRank(1);
        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(0.63, result, _delta);
    }


    @Test
    public void should_still_work_with_two_results() {
        _mockResults.setTotalCount(_query, _source, 100);

        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);

        ResultDetails details2 = new ResultDetails();
        details2.setKey("Another Result");
        details2.setRank(1);
        _mockResults.addResult(_query, _source, details2);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(1.22, result, _delta);
    }


    @Test
    public void should_still_work_with_two_queries() {
        _mockResults.setTotalCount(_query, _source, 100);

        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);

        ResultDetails details2 = new ResultDetails();
        details2.setKey("Another Result");
        details2.setRank(0);
        _mockResults.addResult(_query, _source, details2);
        _mockResults.setFirstHit(_query, _source, details2);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(1, result, _delta);
    }
}
