package com.ibm.wex.relevancyprofiler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ibm.wex.relevancyprofiler.metrics.PercentageTopDocumentsWithinRank;



public class PercentageTopDocumentsWithinRankTest extends RelevancyMetricsTest {

	@Before
	public void setUp() {
		// this is just a really simple test -- only looking at top 2 results by default.
		initializeMetric(new PercentageTopDocumentsWithinRank(2));
		super.setUp();
	}
	
	
	@Test
	public void shouldReturnNullWhenNoResultsToCount() {
		searchVelocity();
		Assert.assertNull(getMetric().calculate(getSession()));
	}
	
	
	@Test
	public void shouldReturn0WhenNoExpectedDocsFound() {
		addExpectation("expected is not found", "bundle", "not found", 10);
		searchVelocity();
		
		Assert.assertEquals(0.0, getMetric().calculate(getSession()), getDelta());
	}
	
	
	@Test
	public void shouldReturn0WhenNoTopDocsInTop2() {
		VelocityDocument testDoc3 = new VelocityDocument("http://blarg.com/testDocument3.html");
		testDoc3.setLinkAnalysisScore(33);
		testDoc3.setRank(5);
		testDoc3.setScore(322);
		testDoc3.setSource("test-source");
		testDoc3.setVseKey("http://blarg.com:80/testDocument3.html");
		testDoc3.setDesiredAtLeastRank(10);
		
		getMock().addDocumentToResults(testDoc3);
		
		addExpectation("expected is third", "bundle", testDoc3.getUrl(), testDoc3.getDesiredAtLeastRank());
		
		searchVelocity();
		
		Assert.assertEquals(0.0, getMetric().calculate(getSession()), getDelta());
	}
	
	@Test
	public void shouldReturn0WhenNoDocumentsFound() {
		addExpectation("nothing here", "bundle", "does not exist!", 10);
		
		searchVelocity();
		
		Assert.assertEquals(0.0, getMetric().calculate(getSession()), getDelta());
	}
	
	
	@Test
	public void shouldReturn50WhenOneDocOfTwoQueriesInTop2() {
		addExpectation("first query", "bundle", getTestDoc1().getUrl(), getTestDoc1().getDesiredAtLeastRank());
		addExpectation("second query", "bundle", "does not exist!", 10);
		
		searchVelocity();
		
		Assert.assertEquals(0.5, getMetric().calculate(getSession()), getDelta());
	}
	
	
	@Test
	public void shouldReturn1WhenTwoDocsOfTwoQuereisInTop2() {
		addExpectation("first query", "bundle", getTestDoc1().getUrl(), getTestDoc1().getDesiredAtLeastRank());
		addExpectation("second query", "bundle", getTestDoc2().getUrl(), getTestDoc2().getDesiredAtLeastRank());
		
		searchVelocity();
		
		Assert.assertEquals(1.0, getMetric().calculate(getSession()), getDelta());
	}
	
	
	@Test
	public void shouldReturn1WhenTwoDocsOfOneQueryInTop2() {
		addExpectation("query", "bundle", getTestDoc1().getUrl(), getTestDoc1().getDesiredAtLeastRank());
		addExpectation("query", "bundle", getTestDoc2().getUrl(), getTestDoc2().getDesiredAtLeastRank());
		
		searchVelocity();
		
		Assert.assertEquals(1.0, getMetric().calculate(getSession()), getDelta());
	}
	
	@Test
	public void shouldReturn1WhenTwoDocsOfOneQueryInTop2ButOneNotFound() {
		addExpectation("query", "bundle", getTestDoc1().getUrl(), getTestDoc1().getDesiredAtLeastRank());
		addExpectation("query", "bundle", "not found", 10);
		
		searchVelocity();
		
		Assert.assertEquals(1.0, getMetric().calculate(getSession()), getDelta());
	}
	
	
	@Test
	public void shouldReturn66WhenTwoDocsOfThreeQuereisInTop2() {
		VelocityDocument testDoc3 = new VelocityDocument("http://blarg.com/testDocument3.html");
		testDoc3.setLinkAnalysisScore(33);
		testDoc3.setRank(5);
		testDoc3.setScore(322);
		testDoc3.setSource("test-source");
		testDoc3.setVseKey("http://blarg.com:80/testDocument3.html");
		testDoc3.setDesiredAtLeastRank(10);
		
		getMock().addDocumentToResults(testDoc3);
		
		addExpectation("first query", "bundle", getTestDoc1().getUrl(), getTestDoc1().getDesiredAtLeastRank());
		addExpectation("second query", "bundle", getTestDoc2().getUrl(), getTestDoc2().getDesiredAtLeastRank());
		addExpectation("expected is third", "bundle", testDoc3.getUrl(), testDoc3.getDesiredAtLeastRank());
		
		searchVelocity();
		
		Assert.assertEquals(2.0/3, getMetric().calculate(getSession()), getDelta());
	}
	
	
	
}
