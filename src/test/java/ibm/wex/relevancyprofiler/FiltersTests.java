package com.ibm.wex.relevancyprofiler.test;


import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityDocument;
import com.ibm.wex.relevancyprofiler.filters.ExpectedNotFoundFilter;
import com.ibm.wex.relevancyprofiler.filters.ExpectedResultsFilter;
import com.ibm.wex.relevancyprofiler.filters.FirstHitsFilter;
import com.ibm.wex.relevancyprofiler.filters.IResultsFilter;
import com.ibm.wex.relevancyprofiler.filters.QueriesWithNoResultsFilter;



public class FiltersTests {

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
		
		_testDoc2 = new VelocityDocument("http://blarg.com/testDocument2.html");
		_testDoc2.setLinkAnalysisScore(13);
		_testDoc2.setRank(1);
		_testDoc2.setScore(245);
		_testDoc2.setSource("test-source");
		_testDoc2.setVseKey("http://blarg.com:80/testDocument2.html");
		_testDoc2.setDesiredAtLeastRank(10);
		
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
	public void shouldGetQueriesWithNoResults() {
		_session.addExpectation("blag", "bundle", "blah blah blah", 5);

		_session.searchVelocity(new MockVelocityQueryConnector());
		
		IResultsFilter filter = new QueriesWithNoResultsFilter();
		List<String> results = filter.filterResults(_session);
		Assert.assertEquals(1, results.size());
	}
	
	
	@Test
	public void shouldGetOnlyExpectedResults() {
		_testDoc2.setExpectedStatus(true);
		_session.addExpectation("blag", "bundle", _testDoc2.getUrl(), _testDoc2.getDesiredAtLeastRank());
		_session.searchVelocity(_mock);
		
		IResultsFilter filter = new ExpectedResultsFilter();
		List<String> results = filter.filterResults(_session);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals("blag,bundle," + _testDoc2.toString(), results.get(0));
	}
	
	
	@Test
	public void shouldGetAllExpectedResults() {
		_testDoc1.setExpectedStatus(true);
		_testDoc2.setExpectedStatus(true);
		_session.addExpectation("blag", "bundle", _testDoc1.getUrl(), _testDoc1.getDesiredAtLeastRank());
		_session.addExpectation("JOJO", "bundle", _testDoc2.getUrl(), _testDoc2.getDesiredAtLeastRank());		
		
		_session.searchVelocity(_mock);
		
		IResultsFilter filter = new ExpectedResultsFilter();
		List<String> results = filter.filterResults(_session);
		Assert.assertEquals(2, results.size());
		Assert.assertEquals("blag,bundle," + _testDoc1.toString(), results.get(0));
		Assert.assertEquals("JOJO,bundle," + _testDoc2.toString(), results.get(1));
	}
	
	
	@Test
	public void shouldNotGetAnyExpectedResultsWhenNoneAreFound() {
		_session.addExpectation("blag", "bundle", "blah blah blah", 5);
		_session.searchVelocity(_mock);
		
		IResultsFilter filter = new ExpectedResultsFilter();
		List<String> results = filter.filterResults(_session);
		Assert.assertEquals(0, results.size());
	}
	
	
	@Test
	public void shouldGetNoFirstHits() {
		_session.addExpectation("blag", "bundle", "blah blah blah", 5);
		
		IResultsFilter filter = new FirstHitsFilter();
		List<String> results = filter.filterResults(_session);
		Assert.assertEquals(0, results.size());
	}
	
	
	@Test
	public void shouldOnlyReturnTheFistHits() {
		_session.addExpectation("no expected found", "bundle", "blah blah blah", 5);  // still has a fist hit
		_testDoc2.setExpectedStatus(true);
		_session.addExpectation("expected is second", "bundle", _testDoc2.getUrl(), _testDoc2.getDesiredAtLeastRank());  // 1
		_session.addExpectation("expected is first", "bundle", _testDoc1.getUrl(), _testDoc1.getDesiredAtLeastRank()); // 1
		
		_session.searchVelocity(_mock);
		
		IResultsFilter filter = new FirstHitsFilter();
		List<String> results = filter.filterResults(_session);
		Assert.assertEquals(3, results.size());
	}
	
	
	
	@Test
	public void shouldNotGetExpectedDocumentsNotFoundWhenTheyAreFound() {
		_session.addExpectation("expected is second", "bundle", _testDoc2.getUrl(), _testDoc2.getDesiredAtLeastRank());

		_session.searchVelocity(_mock);
		
		IResultsFilter filter = new ExpectedNotFoundFilter();
		List<String> results = filter.filterResults(_session);
		Assert.assertEquals(0, results.size());
	}
	
	
	@Test
	public void shouldGetExpectedDocumentsNotFoundWhenTheyAreNotFound() {
		_session.addExpectation("no expected found", "bundle", "blah blah blah", 5);

		_session.searchVelocity(_mock);
		
		IResultsFilter filter = new ExpectedNotFoundFilter();
		List<String> results = filter.filterResults(_session);
		Assert.assertEquals(1, results.size());
	}
	
	
}
