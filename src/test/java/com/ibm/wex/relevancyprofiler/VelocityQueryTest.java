package com.ibm.wex.relevancyprofiler;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.wex.relevancyprofiler.Expectation;
import com.ibm.wex.relevancyprofiler.VelocityDocument;
import com.ibm.wex.relevancyprofiler.VelocityQuery;



public class VelocityQueryTest {
	
	private VelocityQuery _query = null;
	
	private MockVelocityQueryConnector _mock = null;
	private VelocityDocument _testDoc1 = null;
	private VelocityDocument _testDoc2 = null;

	@Before
	public void setUp() throws Exception {
		_query = new VelocityQuery("test", "test-source");
		
		_testDoc1 = new VelocityDocument("http://blarg.com/blarg.html");
		_testDoc1.setLinkAnalysisScore(55);
		_testDoc1.setRank(0);
		_testDoc1.setScore(55.1);
		_testDoc1.setSource("test");
		_testDoc1.setVseKey("http://blarg.com:80/blarg.html");
		
		_testDoc2 = new VelocityDocument("http://blarg.com/testDocument2.html");
		_testDoc2.setLinkAnalysisScore(13);
		_testDoc2.setRank(1);
		_testDoc2.setScore(245);
		_testDoc2.setSource("test-source");
		_testDoc2.setVseKey("http://blarg.com:80/testDocument2.html");
		
		_mock = new MockVelocityQueryConnector();
	}

	@After
	public void tearDown() throws Exception {
		_query = null;
		_testDoc1 = null;
		_testDoc2 = null;
		_mock = null;
	}
	
	@Test
	public void shouldHaveNoResultsWhenTheQueryHasNotBeenInitialized() {
		Assert.assertNotNull(_query.getResults());
		Assert.assertEquals(0, _query.getResults().size());
		Assert.assertFalse(_query.hasResults());
	}
	
	@Test
	public void shouldHaveNoExpectedResultsWhenTheQueryHasNotBeenInitialized() {
		Assert.assertNotNull(_query.getExpectedResults());
		Assert.assertEquals(0, _query.getExpectedResults().size());
	}
	
	@Test
	public void shouldHaveNoTopDocumentWhenTheQueryHasNotBeenInitialized() {
		Assert.assertNull(_query.getTopExpectedResult());
	}
	
	@Test
	public void shouldHaveNoMissingResultsWhenTheQueryHasNotBeenInitialized() {
		Assert.assertNotNull(_query.getMissingExpectations());
		Assert.assertEquals(0, _query.getMissingExpectations().size());
	}
	
	
	@Test
	public void shouldHaveNoExpectationsWhenNoneWereAdded() {
		Assert.assertNotNull(_query.getExpectations());
		Assert.assertEquals(0, _query.getExpectations().size());
	}

	@Test
	public void shouldHaveAnExpectationWhenOneIsAdded() {
		String url = "blarg";
		int highestRank = 10;
		
		_query.addExpectedUrl(url, highestRank);
		Assert.assertEquals(1, _query.getExpectations().size());
		
		Expectation addedExpectation = _query.getExpectations().get(0); 
		Assert.assertEquals(url, addedExpectation.getUrl());
		Assert.assertEquals(highestRank, addedExpectation.getDesiredHighestRank());
	}
	
	
	@Test
	public void shouldNotBeAbleToAddTheSameUrlTwiceToExpectations() {
		String url = "blarg";
		
		_query.addExpectedUrl(url, 10);
		_query.addExpectedUrl(url, 9);
		Assert.assertEquals(1, _query.getExpectations().size());
	}
	
	
	@Test
	public void shouldBeAbleToParseXMLWithNoResults() {
		String xml = _mock.createTestXml();
		_query.parseResultXml(xml);
		
		Assert.assertFalse(_query.hasResults());
		Assert.assertEquals(0, _query.getResults().size());
	}
	
	
	@Test
	public void shouldBeAbleToParseXMLWithOneResult() {
		_mock.addDocumentToResults(_testDoc1);
		String xml = _mock.createTestXml();
		
		_query.parseResultXml(xml);
		
		Assert.assertTrue(_query.hasResults());
		Assert.assertEquals(1, _query.getResults().size());
		Assert.assertTrue(_query.getResults().get(0).equals(_testDoc1));
	}
	
	
	@Test
	public void shouldKeepTopAndExpectedWhenNoExpectedResults() {
		_mock.addDocumentToResults(_testDoc1);
		_mock.addDocumentToResults(_testDoc2);
		String xml = _mock.createTestXml();
		_query.parseResultXml(xml);
		
		Assert.assertTrue(_query.hasResults());
		Assert.assertEquals(1, _query.getResults().size());
		Assert.assertTrue(_query.getResults().get(0).equals(_testDoc1));
	}
	
	
	@Test
	public void shouldKeepTopAndExpectedWhenExpectedIsNotFirst() {
		_query.addExpectedUrl(_testDoc2.getUrl(), 5);
		_testDoc2.setExpectedStatus(true);
		_testDoc2.setDesiredAtLeastRank(5);

		_mock.addDocumentToResults(_testDoc1);
		_mock.addDocumentToResults(_testDoc2);
		String xml = _mock.createTestXml();
		
		_query.parseResultXml(xml);
		
		Assert.assertTrue(_query.hasResults());
		Assert.assertEquals(2, _query.getResults().size());
		Assert.assertTrue(_query.getResults().get(0).equals(_testDoc1));
		Assert.assertTrue(_query.getResults().get(1).equals(_testDoc2));
		
		Assert.assertTrue(_query.getTopExpectedResult().equals(_testDoc2));
	}
	
	
	@Test
	public void shouldKeepTopAndExpectedWhenExpectedIsFirstAndNoOthersAreExpected() {
		_query.addExpectedUrl(_testDoc1.getUrl(), 5);
		_testDoc1.setExpectedStatus(true);
		_testDoc1.setDesiredAtLeastRank(5);

		_mock.addDocumentToResults(_testDoc1);
		_mock.addDocumentToResults(_testDoc2);
		String xml = _mock.createTestXml();
		
		_query.parseResultXml(xml);
	
		Assert.assertTrue(_query.hasResults());
		Assert.assertEquals(1, _query.getResults().size());
		Assert.assertTrue(_query.getResults().get(0).equals(_testDoc1));
		
		Assert.assertTrue(_query.getTopExpectedResult().equals(_testDoc1));		
	}
	
	
	
	@Test
	public void shouldKeepTopAndExpectedWhenExpectedIsFirstAndNoOthersAreExpectedAndExpectedUrlMatchesVseKey() {
		_query.addExpectedUrl(_testDoc1.getVseKey(), 5);
		_testDoc1.setExpectedStatus(true);
		_testDoc1.setDesiredAtLeastRank(5);

		_mock.addDocumentToResults(_testDoc1);
		_mock.addDocumentToResults(_testDoc2);
	
		String xml = _mock.createTestXml();
		_query.parseResultXml(xml);
	
		Assert.assertTrue(_query.hasResults());
		Assert.assertEquals(1, _query.getResults().size());
		Assert.assertTrue(_query.getResults().get(0).equals(_testDoc1));
		
		Assert.assertTrue(_query.getTopExpectedResult().equals(_testDoc1));		
	}
	
	
	@Test
	public void shouldContainExpectedUrlWhenInResults() {
		_query.addExpectedUrl(_testDoc2.getUrl(), 5);
		_testDoc2.setExpectedStatus(true);
		_testDoc2.setDesiredAtLeastRank(5);
		
		Assert.assertTrue(_query.containsExpectedUrl(_testDoc2.getUrl()));
		Assert.assertFalse(_query.containsExpectedUrl("blarg"));
	}
	
	@Test
	public void shouldContainExpectedVseKeyWhenInResults() {
		_query.addExpectedUrl(_testDoc2.getVseKey(), 5);
		
		Assert.assertTrue(_query.containsExpectedUrl("blarg", _testDoc2.getVseKey()));
		Assert.assertFalse(_query.containsExpectedUrl("blarg", "blarg"));
	}



	@Test
	public void shouldGetExpectationsWithoutResultsFound() {
		_query.addExpectedUrl(_testDoc2.getVseKey(), 5);
		Assert.assertEquals(1, _query.getMissingExpectations().size());
		
		_mock.addDocumentToResults(_testDoc1);
		_mock.addDocumentToResults(_testDoc2);
		String xml = _mock.createTestXml();
		
		_query.parseResultXml(xml);
		
		Assert.assertEquals(0, _query.getMissingExpectations().size());
	}

	
	@Test
	public void shouldOnlyAddResultThatHasNotAlreadyBeenAdded() {
		_query.addResult(_testDoc1);
		Assert.assertEquals(1, _query.getResults().size());
		
		_query.addResult(_testDoc1);
		Assert.assertEquals(1, _query.getResults().size());	
	}

	@Test
	public void shouldContainResultsWhenTheAreAdded() {
		Assert.assertFalse(_query.containsResult(_testDoc1));
		
		_query.addResult(_testDoc1);
		Assert.assertTrue(_query.containsResult(_testDoc1));
	}

	
	// The problem is that when there are two documents with the same URL
	// but different vse-keys, the currently algorithm will count both of them.
	// I think we really only want to keep the first one.
	@Test
	public void shouldNotDoubleCountDocumentsWithSameUrl() {
		VelocityDocument testDoc3 = new VelocityDocument("http://blarg.com/testDocument2.html");
		testDoc3.setLinkAnalysisScore(13);
		testDoc3.setRank(1);
		testDoc3.setDesiredAtLeastRank(5);
		testDoc3.setScore(245);
		testDoc3.setSource("test-source");
		testDoc3.setVseKey("http://blarg.com:80/testDocument2Again.html");

		// because this is a mock, I need to make some manual adjustments for easier
		// comparison later.
		_testDoc2.setRank(0);
		_testDoc2.setDesiredAtLeastRank(5);
		_testDoc2.setExpectedStatus(true);
		
		_mock.addDocumentToResults(_testDoc2);
		_mock.addDocumentToResults(testDoc3);
		
		_query.addExpectedUrl(testDoc3.getUrl(), 5);
		
		String xml = _mock.createTestXml();
		_query.parseResultXml(xml);
	
		Assert.assertTrue(_query.hasResults());
		Assert.assertEquals(1, _query.getResults().size());
		Assert.assertTrue(_query.getResults().get(0).equals(_testDoc2));			
	}
	

}

