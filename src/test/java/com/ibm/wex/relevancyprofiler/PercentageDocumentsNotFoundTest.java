package com.ibm.wex.relevancyprofiler;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ibm.wex.relevancyprofiler.metrics.PercentageDocumentsNotFound;



public class PercentageDocumentsNotFoundTest extends RelevancyMetricsTest {

	@Before
	public void setUp() {
		initializeMetric(new PercentageDocumentsNotFound());
		super.setUp();
	}
	
	
	@Test
	public void shouldReturnNullWhenNoQueries() {
		searchVelocity();
		Assert.assertNull(getMetric().calculate(getSession()));
	}

	
	@Test
	public void shouldReturn1WhenNoExpectedDocsFound() {
		addExpectation("expected is not found", "bundle", "not found", 10);
		searchVelocity();
		
		Assert.assertEquals(1.0, getMetric().calculate(getSession()), getDelta());
	}
	
	
	@Test
	public void shouldReturn0WhenAllDocsAreFound() {
		addExpectation("first query", "bundle", getTestDoc1().getUrl(), getTestDoc1().getDesiredAtLeastRank());
		addExpectation("second query", "bundle", getTestDoc2().getUrl(), getTestDoc2().getDesiredAtLeastRank());
		
		searchVelocity();
		
		Assert.assertEquals(0.0, getMetric().calculate(getSession()), getDelta());
	}
	
	@Test
	public void shouldReturn50WhenOneDocsIsNotFoundOfTwoExpected() {
		addExpectation("first query", "bundle", getTestDoc1().getUrl(), getTestDoc1().getDesiredAtLeastRank());
		addExpectation("second query", "bundle", "does not exist!", 10);
		
		searchVelocity();
		
		Assert.assertEquals(0.5, getMetric().calculate(getSession()), getDelta());
	}
	

}
