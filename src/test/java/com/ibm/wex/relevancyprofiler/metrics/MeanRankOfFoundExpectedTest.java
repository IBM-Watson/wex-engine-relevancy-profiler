package com.ibm.wex.relevancyprofiler.metrics;


import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResultSet;
import com.ibm.wex.relevancyprofiler.resultfetchers.ResultDetails;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MeanRankOfFoundExpectedTest {

    private MeanRankOfFoundExpected _subject = null;
    private ProfilerResultSet _mockResults = null;

    ResultDetails _details1 = null;

    private final String _query = "TEST_QUERY";
    private final String _source = "TEST_SOURCE";
    private final double _delta = 0.01;

    @Before
    public void setUp() {
        _subject = new MeanRankOfFoundExpected();
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
