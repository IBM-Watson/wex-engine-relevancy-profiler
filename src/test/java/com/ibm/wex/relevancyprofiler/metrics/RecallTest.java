package com.ibm.wex.relevancyprofiler.metrics;


import org.junit.Assert;
import org.junit.Test;



public class RecallTest extends MetricTestBase {

    protected IMetric createMetric() {
        return new Recall();
    }

    @Test
    public void should_get_0_percent_recall_when_no_expected_results_are_found() {
        _mockResults.setTotalCount(_query, _source, 100);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(0.0, result, _delta);
    }


    @Test
    public void should_get_100_percent_recall_when_1_of_1_expected_results_are_found() {
        _mockResults.setTotalCount(_query, _source, 100);

        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(1.0, result, _delta);
    }

    @Test
    public void should_get_50_percent_recall_when_1_of_2_expected_results_are_found(){
        _mockResults.setTotalCount(_query, _source, 100);

        _mockResults.addResult(_query, _source, _details1);
        _mockResults.setFirstHit(_query, _source, _details1);
        _mockResults.addResultNotFound(_query, _source, "Not Found");

        Double result = _subject.calculate(_mockResults);
        Assert.assertEquals(0.5, result, _delta);
    }


}
