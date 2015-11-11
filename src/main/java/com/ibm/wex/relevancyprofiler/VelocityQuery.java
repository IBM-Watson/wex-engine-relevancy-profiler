package com.ibm.wex.relevancyprofiler;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Represents a query that is to be profiled.
 * 
 *  The Query is the phrase that will be run against Velocity. The Source is the source 
 *  (or source bundle) that the query will be run against. Expected Documents is a set of 
 *  Expectations, that is URLs and their expected highest ranks. Results are the list of 
 *  "interesting" documents saved from the Velocity search results.  At a minimum, "interesting"
 *  documents will include the first result from the search and the expected documents that were
 *  present in the top N results of the search.  
 * 
 */
public class VelocityQuery {

	private String _query = "";
	private String _bundle = "";
	
	private List<Expectation> _expectedDocuments = null;
	private List<VelocityDocument> _results = null;

    private int _totalRetrievedForQuery = 0;
	
	/**
	 * Creates a new QuerySet to be used for profiling purposes.
	 * @param query The query string or phrase.  This can use any operators the search engine being profiled can accept.
	 * @param source The source or source bundle this query will be run against.
	 */
	public VelocityQuery(String query, String source) {
		_expectedDocuments = new ArrayList<Expectation>();
		_results = new ArrayList<VelocityDocument>();
		
		_query = query;
		
		// This is what the search is done against and can be the same as 
		// source derived from the results later.  Typically this will be 
        // a bundle but it could be just a source.
		_bundle = source; 
	}
	
	
	public String getQuery() { return _query; }
	public void setQuery(String value) { _query = value; }

	public String getBundle() { return _bundle;	}
	public void setBundle(String value) { _bundle = value;	}
	
	public List<VelocityDocument> getResults() { return _results; }
	public List<Expectation> getExpectations() { return _expectedDocuments; }
	
	
	/**
	 * Gets the first result returned for this query.
	 * @return The top document (the document with rank 0) that was returned by the query.
	 */
	public VelocityDocument getTopExpectedResult() {
		VelocityDocument topDoc = null;
		for(VelocityDocument d : _results) {
			if (d.DocumentExists() && d.isExpected()) {
				if (topDoc == null || d.getRank() < topDoc.getRank()) {
					topDoc = d;
				}
			}
		}
		
		return topDoc;		
	}
	
	/**
	 * Gets those expected documents that were returned in the results of a search using the given query.
	 * @return A list of VelocityDocuments with information for expected documents.
	 */
	public List<VelocityDocument> getExpectedResults() {
		List<VelocityDocument> documents = new ArrayList<VelocityDocument>();
		
		for(VelocityDocument d : _results) {
			if (d.DocumentExists() && d.isExpected()) {
				documents.add(d);
			}
		}
		
		return documents;
	}
	
	/**
	 * Gets true if this QuerySet has results, false otherwise.
	 * @return True if this QuerySet has results, false otherwise.
	 */
	public boolean hasResults() { return _results != null && _results.size() > 0; }
	
	/**
	 * Adds a URL to the list of those expected for this query.
	 * @param newUrl The URL or VSE-KEY of the expected document.
	 * @param highestDesiredRank The highest rank this URL should have to be considered relevant. 
	 */
	public void addExpectedUrl(String newUrl, int highestDesiredRank) {	
		if (containsExpectedUrl(newUrl)) {
			return;
		}
		
		_expectedDocuments.add(new Expectation(newUrl, highestDesiredRank));
	}
	
	/**
	 * Tells whether the expected URL is in the expected set already.
	 * @param url The URL to check.  This is the url of the document as it is held in Velocity.
	 * @return True if the URL is present, false otherwise.
	 */
	public boolean containsExpectedUrl(String url) {
		return containsExpectedUrl(url, url);
	}
	

	/**
	 * Tells whether the expected URL or vsek-key is in the expected set already. 
	 * @param url The URL to check.  This is the url of the document as it is held in Velocity.
	 * @param vseKey The vse-key to check.  This is the vse-key of the document as it is held in Velocity.
	 * @return True if the url or vse-key is contained in the expected result set, false otherwise.
	 */
	public boolean containsExpectedUrl(String url, String vseKey) {
		for (Expectation expectedUrl : _expectedDocuments) {
			if (expectedUrl.getUrl().equals(url) || expectedUrl.getUrl().equals(vseKey)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Gets the expected rank for the URL from the set. 
	 * @param url The URL to check.  This is the url of the document as it is held in Velocity.
	 * @param vseKey The vse-key to check.  This is the vse-key of the document as it is held in Velocity.
	 * @return The expected rank of the URL or -1 if the URL could not be found in either the vse-key or URL fields.
	 */
	public int getExpectedRankForUrl(String url, String vseKey) {
		for (Expectation expectedUrl : _expectedDocuments) {
			if (expectedUrl.getUrl().equals(url) || expectedUrl.getUrl().equals(vseKey)) {
				return expectedUrl.getDesiredHighestRank();
			}
		}
		
		return -1;
	}
	
	
	/**
	 * Gets the list of expectations that don't have a matching vse-key or URL in the result set. 
	 */
	public List<Expectation> getMissingExpectations() {
		List<Expectation> orphans = new ArrayList<Expectation>();
		
		for (Expectation expected : _expectedDocuments) {
			boolean found = false;
			
			for (VelocityDocument result : _results) { 
				if (expected.getUrl().equals(result.getUrl()) || expected.getUrl().equals(result.getVseKey())) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				orphans.add(expected);
			}
		}
		
		return orphans;
	}
	
	
	/**
	 * Adds a result to the list for this query.
	 * @param result The result to add.
	 */
	public void addResult(VelocityDocument result) {
		if (containsResult(result)) {
			return;
		}
		
		_results.add(result);
	}
	
	/**
	 * Tells whether the result is contained in the result set for this query.
	 * @param result The result to check with correct URL or vse-key
	 * @return true if the given result is present, false otherwise.
	 */
	public boolean containsResult(VelocityDocument result) {
		for (VelocityDocument d : _results) {
			if (d.isSameDocument(result)) {
				return true;
			}
		}
		
		return false;
	}


	public int getTotalRetrieved() {
		return _totalRetrievedForQuery;
	}


	
	/**
	 * Parses the results XML and populates the result set for this query.
	 * @param xml The XML as a string from Velocity.  This XML is expected to be from the xml-feed-display-debug display create specifically for profiling purposes.
	 */
	public void parseResultXml(String xml) {
		NodeList nodes = findDocumentsInXml(xml);
		
//		System.out.println("XXX");
//		for (int i = 0; i < nodes.getLength(); i++) {
//			Element hit = (Element) nodes.item(i);
//			System.out.println("---------");
//			System.out.println(hit.getAttribute("url"));
//			System.out.println(hit.getAttribute("vse-key"));
//			System.out.println("---------");
//		}
//		System.out.println("XXX");
	
		if (nodes == null || nodes.getLength() == 0) {
			return;
		}
		
		int i = 0;
		int spotlightCount = 0;
		Element topHit = null;
		while (topHit == null && i < nodes.getLength()) {
			Element currentElement = (Element) nodes.item(i);
			
			if (!currentElement.getAttribute("vse-key").isEmpty()) {
				topHit = currentElement;
			}
			else {
				spotlightCount++;
			}
			
			i++;
		}
		
		if (topHit == null) {
			return;
		}
		
		// handle the 0th case, it's special because it might be what we're looking for...
		int expectedRank = getExpectedRankForUrl(topHit.getAttribute("url"), topHit.getAttribute("vse-key"));
		VelocityDocument doc = createDocument(topHit, expectedRank);
		doc.setRank(0);
		_results.add(doc);
		
		// go through the rest... if we must...
		for (; i < nodes.getLength(); i++) {
			Element docElement = (Element) nodes.item(i);
			
			// have to use both because either one could have 
			// been given to us to verify
			String vseKey = docElement.getAttribute("vse-key");
			String url = docElement.getAttribute("url");
			
			// if a tolerableMaxRank greater than 0 is returned it means the 
			// document is one that we are actually interested in...
			if (containsExpectedUrl(url, vseKey)) {
				VelocityDocument result = createDocument(docElement, getExpectedRankForUrl(url, vseKey));
				result.setRank(i - spotlightCount);
				
				// There is a case where the same URL can be listed under two vse-keys.  This essentially
				// means that the same document is listed twice, but we might double count this.
				// to not throw off metrics, such cases need to be skipped.
				if (!containsResult(result)) {
					_results.add(result);
				}
			}
		}
	}
	
	
	private VelocityDocument createDocument(Element elem, int tolerableMaxRank) {
		String vseKey = elem.getAttribute("vse-key");
		String url = elem.getAttribute("url");
		String source = elem.getAttribute("source");
		double laScore = convertToDouble(elem.getAttribute("la-score"));
		double score = convertToDouble(elem.getAttribute("score"));
		double baseScore = convertToDouble(elem.getAttribute("vse-base-score"));
		int rank = convertToInt(elem.getAttribute("rank"));
		
		VelocityDocument result = new VelocityDocument(url, tolerableMaxRank >= 0, tolerableMaxRank);
		result.setLinkAnalysisScore(laScore);
		result.setVseKey(vseKey);
		result.setUrl(url);
		result.setSource(source);
		result.setScore(score);
		result.setNaturalRank(rank);
		result.setBaseScore(baseScore);
		
		return result;
	}


	// read the XML and return a list of all the document nodes
	private NodeList findDocumentsInXml(String xml) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(xml));

	        Document doc = db.parse(is);

	        NodeList list = doc.getElementsByTagName("list");
	        if (list == null || list.getLength() < 1) {
	        	return null;
	        }
            else {
                Node listNode = list.item(0);
                int num = convertToInt(listNode.getAttributes().getNamedItem("num").getNodeValue()); // total number of results returned
                int per = convertToInt(listNode.getAttributes().getNamedItem("per").getNodeValue()); // total number of results requested

                _totalRetrievedForQuery = Math.min(num, per); // actual number of results round received for what was requested
            }
	        
	        
			return doc.getElementsByTagName("document");
		} catch(Exception e) {
//			System.out.println("XXXXXX");
			System.out.println(xml);  // need to consider some logging here so it's easier to diagnose stupid problems
//			System.out.println("XXXXXX");
			System.out.flush();
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	private double convertToDouble(String d) {
		try {
			return Double.parseDouble(d);
		}
		catch (NumberFormatException ex) {
			return -1;						
		}
	}
	
	private int convertToInt(String i) {
		try {
			return Integer.parseInt(i);
		}
		catch (NumberFormatException ex) {
			return -1;						
		}
	}



}
