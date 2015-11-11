package com.ibm.wex.relevancyprofiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



/**
 * The ResultSet is used by a client (e.g. a user interface) to store information relevant to this profiling session.
 * This class has the responsibility of searching Velocity and holding the query set for the profiling run.
 *
 */
public class ProfilingSession {
	
	private String _urlRoot = "";
	private String _project = "";
	private int _maxCount;
	private int _sleepTime = 0;
	private int _numberOfThreads = 10;

	private List<VelocityQuery> _querySet = null;

	
	/**
	 * Creates a new ResultSet initializing the set with the parameters given. 
	 * @param urlRoot The Root Velocity URL for query-meta to be searched.
	 * @param project The project to be searched.
	 * @param maxCount The maximum number of results to be returned.
	 */
	public ProfilingSession(String urlRoot, String project, int maxCount, int numberOfThreads) {
		_querySet = new ArrayList<VelocityQuery>();
		
		_urlRoot = urlRoot;
		_maxCount = maxCount;
		_project = project;
		_numberOfThreads = numberOfThreads;
	}
	
	
	/**
	 * Gets the set of queries that will be searched.
	 * @return The list of queries.  Queries will have expectations and, once a search has been done, results for that query.
	 */
	public List<String> getQueries() {
		List<String> queries = new ArrayList<String>();
		for (VelocityQuery q : _querySet) {
			queries.add(q.getQuery());
		}
		
		return queries;
	}
	
	public List<VelocityQuery> getQueriesAndResults() {
		return _querySet;
	}
	
	
	/**
	 * Tells whether this ResultSet has queries or not.
	 * @return True if this RestulSet has queries, false otherwise.
	 */
	public boolean hasQueries() {
		return !(_querySet == null || _querySet.size() == 0);
	}
	
	/**
	 * Gets the amount of time between queries in milliseconds.
	 * @return The number of milliseconds between queries.
	 */
	public int getSleepTime() { return _sleepTime; }


	/**
	 * Sets the amount of time between queries in milliseconds
	 * @param value The number of milliseconds.
	 */
	public void setSleepTime(int value) { _sleepTime = value; }
	
	
	/**
	 * Adds a query to the list of expected queries for this profiling session.  A URL can only be added once for a given bundle.
	 * @param query The text phrase that will be searched in Velocity.  This should be as a user would enter it.
	 * @param bundle The source or bundle the search will go against.
	 * @param url The URL expected in the results
	 * @param atLeastRank The highest rank that URL is expected to have. 
	 * @return The position at which the expected query was added, or the current positition if the query already existed.
	 */
	public int addExpectation(String query, String bundle, String url, int atLeastRank) {
		int i = 0;
		for (VelocityQuery q : _querySet) {
			if (q.getQuery().equals(query) && q.getBundle().equals(bundle)) {
				q.addExpectedUrl(url, atLeastRank);
				return i;
			}
			
			i++;
		}
		
		VelocityQuery q = new VelocityQuery(query, bundle);
		q.addExpectedUrl(url, atLeastRank);
		_querySet.add(q);
		
		return _querySet.size() - 1;
	}
	
	
	private int awaitingCompletion = 0;
	private final Object lock = new Object();

	
	/**
	 * Performs a search on Velocity for the given expected queries for this profiling session.
	 * @param connector Queries Velocity.
	 */
	public void searchVelocity(final IVelocityConnector connector) {	
		ExecutorService threadExecutor = Executors.newFixedThreadPool(_numberOfThreads);
		
		for (final VelocityQuery q : _querySet) {
			
			Runnable query = new Runnable() {
				public void run() {
					String xml = connector.doQuery(_urlRoot, _project, _maxCount, q);
					q.parseResultXml(xml);
					
					waitALittle();
					workerDone();					
				}			
			};
			
			 synchronized(lock) {
				 awaitingCompletion++;
				 lock.notifyAll();
		     }
			threadExecutor.execute(query);			
		}
		
		try {
			while (awaitingCompletion > 0) {
				Thread.sleep(1000);
			}
			threadExecutor.shutdown();
			// threadExecutor.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	private void workerDone() {
		synchronized(lock) {
			awaitingCompletion--;
			lock.notifyAll();
        }
	}
	
	/**
	 * Returns all results for this session in a format suitable for display.  This will include 
	 * expected URLs that were not found and top hits that were not expected.  The first item in the
	 * list is a header.
	 * @return A list of all results.
	 */
	public List<VelocityDocument> getAllResults() {
		List<VelocityDocument> documents = new ArrayList<VelocityDocument>();
		
		for (VelocityQuery q : _querySet) {
    		documents.addAll(q.getResults());

    		for (Expectation doc : q.getMissingExpectations()) {
    			documents.add(new VelocityDocument(doc.getUrl(), true, doc.getDesiredHighestRank()));
    		}
		}
		
		Collections.sort(documents, new Comparator<VelocityDocument>() {
			public int compare(VelocityDocument x, VelocityDocument y) {
				return x.toString().compareTo(y.toString());
			}
		});
		
		return documents;		
	}
	

	
	/**
	 * Gets the top ranked expected documents.
	 * @return The list of top ranked expected documents.
	 */
	public List<VelocityDocument> getTopRankedExpectedDocuments() {
		List<VelocityDocument> docs = new ArrayList<VelocityDocument>();
		
		for (VelocityQuery q : _querySet) {
			VelocityDocument topDoc = q.getTopExpectedResult();
			if (topDoc != null) {
				docs.add(topDoc);
			}
		}
		
		return docs;
	}
	
	
	/**
	 * Gets the list of all documents that were expected.  That is, rather than
	 * returning a list of strings, this is the actual document with the full payload
	 * of information associated with the document.
	 */
	public List<VelocityDocument> getExpectedDocuments() {
		List<VelocityDocument> docs = new ArrayList<VelocityDocument>();
		
		for (VelocityQuery q : _querySet) {
			docs.addAll(q.getExpectedResults());
		}
		
		return docs;
	}


	
	
	private void waitALittle() {
		if (_sleepTime > 0)
		{
			System.out.println("Waiting " + _sleepTime + "ms before doing next query...");
			try {
				Thread.sleep(_sleepTime);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
	}


}