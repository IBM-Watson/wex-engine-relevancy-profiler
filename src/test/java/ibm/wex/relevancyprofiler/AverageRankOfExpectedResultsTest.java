package com.ibm.wex.relevancyprofiler.test;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.ibm.wex.relevancyprofiler.metrics.AverageRankOfExpectedResults;



public class AverageRankOfExpectedResultsTest extends RelevancyMetricsTest {

	@Before
	public void setUp() {
		initializeMetric(new AverageRankOfExpectedResults());
		super.setUp();
	}
	
	
	@Test
	public void shouldReturnNullWhenNoResultsToAverage() {
		searchVelocity();
		Assert.assertNull(getMetric().calculate(getSession()));
	}
	
	
	@Test
	public void shouldReturnAverageWhenThereIsOneExpectedResult() {
		addExpectation("expected is second", "bundle", getTestDoc2().getUrl(), getTestDoc2().getDesiredAtLeastRank());
		searchVelocity();
		
		Assert.assertEquals(1.0, getMetric().calculate(getSession()));
	}
	
	
	@Test
	public void shouldReturnAverageWhenThereAreTwoExpectedResults() {
		addExpectation("expected is second", "bundle", getTestDoc2().getUrl(), getTestDoc2().getDesiredAtLeastRank());
		addExpectation("expected is first", "bundle", getTestDoc1().getUrl(), getTestDoc1().getDesiredAtLeastRank());
		searchVelocity();
		
		Assert.assertEquals(0.5, getMetric().calculate(getSession()));
	}
	
	
}
