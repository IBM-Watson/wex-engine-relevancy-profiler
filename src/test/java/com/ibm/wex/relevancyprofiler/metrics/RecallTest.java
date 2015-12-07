package com.ibm.wex.relevancyprofiler.metrics;


import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResultSet;
import com.ibm.wex.relevancyprofiler.resultfetchers.ResultDetails;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RecallTest {

    private Recall _subject = null;
    private ProfilerResultSet _mockResults = null;

    ResultDetails _details1 = null;

    private final String _query = "TEST_QUERY";
    private final String _source = "TEST_SOURCE";
    private final double _delta = 0.01;

    @Before
    public void setUp() {
        _subject = new Recall();
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
