package com.ibm.wex.relevancyprofiler.test;


import org.junit.After;
import org.junit.Before;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityDocument;
import com.ibm.wex.relevancyprofiler.metrics.IRelevancyMetric;



public abstract class RelevancyMetricsTest {

	private IRelevancyMetric _metric = null;
	private ProfilingSession _session = null;
	private VelocityDocument _testDoc1 = null;
	private VelocityDocument _testDoc2 = null;
	private MockVelocityQueryConnector _mock = null;
	
	public IRelevancyMetric getMetric() { return _metric; }
	public ProfilingSession getSession() { return _session; }
	public VelocityDocument getTestDoc1() { return _testDoc1; }
	public VelocityDocument getTestDoc2() { return _testDoc2; }
	public MockVelocityQueryConnector getMock() { return _mock; }
	
	
	@Before
	public void setUp() {
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
	public void tearDown() {
		_metric = null;
		_session = null;
		_mock = null;
		_testDoc1 = null;
		_testDoc2 = null;
	}
	
	
	protected void initializeMetric(IRelevancyMetric metric) {
		_metric = metric;
	}
	
	protected void searchVelocity() {
		_session.searchVelocity(_mock);
	}


	protected void addExpectation(String term, String source, String url, int rank) {
		_session.addExpectation(term, source, url, rank);
	}
	
	

}
