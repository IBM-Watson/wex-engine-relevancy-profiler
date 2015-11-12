package com.ibm.wex.relevancyprofiler;

import com.ibm.wex.relevancyprofiler.metrics.RecallAtNMetric;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RecallTest extends RelevancyMetricsTest {

    @Before
    public void setUp() {
        initializeMetric(new RecallAtNMetric());
        super.setUp();
    }


    @Test
    public void shouldComputRecallForOneExpectedFound() {
        addExpectation("expected is second", "bundle", getTestDoc2().getUrl(), getTestDoc2().getDesiredAtLeastRank());
        searchVelocity();

        Assert.assertEquals(1.0, getMetric().calculate(getSession()), getDelta());
    }

    @Test
    public void shouldComputeRecallForOneExpectedFoundOneExpectedNotFound() {
        addExpectation("expected is second", "bundle", getTestDoc2().getUrl(), getTestDoc2().getDesiredAtLeastRank());
        addExpectation("some document that doesn't exist", "bundle", "some url", 10);
        searchVelocity();

        Assert.assertEquals(0.5, getMetric().calculate(getSession()), getDelta());
    }

    @Test
    public void shouldComputeRecallForNoExpectedFound() {
        addExpectation("some other document that doesn't exist", "bundle", "some other url", 10);
        addExpectation("some document that doesn't exist", "bundle", "some url", 10);
        searchVelocity();

        Assert.assertEquals(0.0, getMetric().calculate(getSession()), getDelta());
    }

    @Test
    public void shouldComputeRecallForNoExpectations() {
        searchVelocity();

        Assert.assertEquals(0.0, getMetric().calculate(getSession()), getDelta());
    }

}