package com.ibm.wex.relevancyprofiler.metrics;


import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResultSet;
import com.ibm.wex.relevancyprofiler.resultfetchers.ResultDetails;
import org.junit.After;
import org.junit.Before;

public abstract class MetricTestBase {
    protected IMetric _subject = null;
    protected ProfilerResultSet _mockResults = null;

    protected ResultDetails _details1 = null;

    protected final String _query = "TEST_QUERY";
    protected final String _source = "TEST_SOURCE";
    protected final double _delta = 0.01;


    protected abstract IMetric createMetric();

    @Before
    public void setUp() {
        _subject = createMetric();
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

}
