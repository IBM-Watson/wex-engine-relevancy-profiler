package com.ibm.wex.relevancyprofiler;

import java.util.List;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityDocument;



public class ProfilingSessionTest {

	private ProfilingSession _session = null;
	private VelocityDocument _testDoc1 = null;
	private VelocityDocument _testDoc2 = null;
	private MockVelocityQueryConnector _mock = null;
	
	
	@Before
	public void setUp() throws Exception {
		_session = new ProfilingSession("test-url", "test-project", 100, 2);
		
		_testDoc1 = new VelocityDocument("http://blarg.com/testDocument1.html");
		_testDoc1.setLinkAnalysisScore(55);
		_testDoc1.setRank(0);
		_testDoc1.setScore(55.1);
		_testDoc1.setSource("test");
		_testDoc1.setVseKey("http://blarg.com:80/testDocument1.html");
		_testDoc1.setDesiredAtLeastRank(3);
		_testDoc1.setNaturalRank(0);
		_testDoc1.setBaseScore(55.3);
		
		_testDoc2 = new VelocityDocument("http://blarg.com/testDocument2.html");
		_testDoc2.setLinkAnalysisScore(13);
		_testDoc2.setRank(1);
		_testDoc2.setScore(245);
		_testDoc2.setSource("test-source");
		_testDoc2.setVseKey("http://blarg.com:80/testDocument2.html");
		_testDoc2.setDesiredAtLeastRank(10);
		_testDoc1.setNaturalRank(10);
		_testDoc1.setBaseScore(19);
		
		_mock = new MockVelocityQueryConnector();
		_mock.addDocumentToResults(_testDoc1);
		_mock.addDocumentToResults(_testDoc2);
	}

	@After
	public void tearDown() throws Exception {
		_session = null;
		_testDoc1 = null;
		_testDoc2 = null;
		_mock = null;
	}
	
	

	@Test
	public void shouldHaveNoQueriesAtFirst() {
		Assert.assertFalse(_session.hasQueries());
	}
	
	@Test
	public void shouldHaveQueriesWhenOneIsAdded() {
		_session.addExpectation("blarg", "blarg-bundle", "blag-url", 10);
		Assert.assertTrue(_session.hasQueries());
	}
	
	
	@Test
	public void shouldBeAbleToAddAnExpectation() {
		_session.addExpectation("blarg", "blarg-bundle", "blag-url", 10);
		List<String> expected = _session.getQueries();
		
		Assert.assertEquals(1, expected.size());
		Assert.assertEquals("blarg", expected.get(0));
	}
	
	@Test
	public void shouldNotBeAbleToAddTheSameExpectationTwice() {
		_session.addExpectation("blarg", "blarg-bundle", "blag-url", 10);
		_session.addExpectation("blarg", "blarg-bundle", "blag-url", 10);
		List<String> expected = _session.getQueries();
		
		Assert.assertEquals(1, expected.size());
		Assert.assertEquals("blarg", expected.get(0));	
	}
	
	
	@Test
	public void shouldGetNoResultsOfInterest() {
		List<VelocityDocument> results = _session.getAllResults();
		Assert.assertEquals(0, results.size());
	}
	
	
	@Test
	public void shouldGetAllResultsOfInterest() {
		_session.addExpectation("no expected found", "bundle", "blah blah blah", 5);  // 2
		_testDoc2.setExpectedStatus(true);
		_session.addExpectation("expected is second", "bundle", _testDoc2.getUrl(), _testDoc2.getDesiredAtLeastRank());  // 2
		_session.addExpectation("expected is first", "bundle", _testDoc1.getUrl(), _testDoc1.getDesiredAtLeastRank()); // 1
		
		_session.searchVelocity(_mock);
		
		List<VelocityDocument> results = _session.getAllResults();
		Assert.assertEquals(5, results.size()); // 2+2+1
	}

		
	@Test
	public void shouldOnlyGetTopRankedDocuments() {
		_testDoc1.setExpectedStatus(true);
		_testDoc2.setExpectedStatus(true);
		_session.addExpectation("expected is first and second", "bundle", _testDoc1.getUrl(), _testDoc1.getDesiredAtLeastRank());
		_session.addExpectation("expected is first and second", "bundle", _testDoc2.getUrl(), _testDoc2.getDesiredAtLeastRank());
		_session.addExpectation("expected is first", "bundle", _testDoc1.getUrl(), _testDoc1.getDesiredAtLeastRank());

		_session.searchVelocity(_mock);
		
		List<VelocityDocument> results = _session.getTopRankedExpectedDocuments();
		Assert.assertEquals(2, results.size());
		Assert.assertTrue(_testDoc1.equals(results.get(0)));
		Assert.assertTrue(_testDoc1.equals(results.get(1)));
	}
	
	

	@Test
	public void testGetExpectedDocuments() {
		_testDoc1.setExpectedStatus(true);
		_testDoc2.setExpectedStatus(true);
		_session.addExpectation("expected is first and second", "bundle", _testDoc1.getUrl(), _testDoc1.getDesiredAtLeastRank());
		_session.addExpectation("expected is first and second", "bundle", _testDoc2.getUrl(), _testDoc2.getDesiredAtLeastRank());
		_session.addExpectation("expected is first", "bundle", _testDoc1.getUrl(), _testDoc1.getDesiredAtLeastRank());
		
		_session.searchVelocity(_mock);
		
		List<VelocityDocument> results = _session.getExpectedDocuments();
		Assert.assertEquals(3, results.size());
	}

}
