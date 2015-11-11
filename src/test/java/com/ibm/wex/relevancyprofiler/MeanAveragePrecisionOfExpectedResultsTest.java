package com.ibm.wex.relevancyprofiler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ibm.wex.relevancyprofiler.metrics.MeanAveragePrecisionOfExpectedResults;



public class MeanAveragePrecisionOfExpectedResultsTest extends RelevancyMetricsTest {

	@Before
	public void setUp() {
		initializeMetric(new MeanAveragePrecisionOfExpectedResults());
		super.setUp();
	}
	
	
	@Test
	public void shouldReturnNullWhenNoResultsToAverage() {
		searchVelocity();
		Assert.assertNull(getMetric().calculate(getSession()));
	}
	
	
	@Test
	public void shouldReturn1WhenOnlyExpectedResultIsTop() {
		addExpectation("expected is first", "bundle", getTestDoc1().getUrl(), getTestDoc1().getDesiredAtLeastRank());
		searchVelocity();
		
		Assert.assertEquals(1.0, getMetric().calculate(getSession()), getDelta());
	}
	
	
	@Test
	public void shouldReturn06WhenOnlyExpectedResultIsSecond() {
		addExpectation("expected is second", "bundle", getTestDoc2().getUrl(), getTestDoc2().getDesiredAtLeastRank());
		searchVelocity();
		
		Assert.assertEquals(0.5, getMetric().calculate(getSession()), getDelta());
	}
	
}
