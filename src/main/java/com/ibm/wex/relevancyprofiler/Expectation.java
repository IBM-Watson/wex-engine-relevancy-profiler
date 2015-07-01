package com.ibm.wex.relevancyprofiler;

/**
 * An Expectation holds an URL that is expected to be present in 
 * a given result set and the and desired highest rank that URL should have.
 * 
 */
public class Expectation {
	
	private String _url;
	private int _atLeastRank;
	
	/**
	 * Creates a new Expectation.
	 * 
	 * @param newUrl The URL that is expected.  This URL must be the string as it is contained in Velocity.  Either the URL or vse-key will work.
	 * @param atLeastRank The highest rank this URL is expected to have.
	 */
	public Expectation(String newUrl, int highestDesiredRank) {
		_url = newUrl;
		_atLeastRank = highestDesiredRank;
	}
	
	public void setUrl(String value) { _url = value; }
	public String getUrl() { return _url; }
	
	public void setDesiredHighestRank(int value) { _atLeastRank = value; }
	public int getDesiredHighestRank() { return _atLeastRank; }

}
