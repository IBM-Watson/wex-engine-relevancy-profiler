package com.ibm.wex.relevancyprofiler.groundtruth;

/**
 * An Expectation holds an URL that is expected to be present in 
 * a given result set and the and desired highest rank that URL should have.
 * 
 */
public class Expectation {
	
	private String _url;
	private int _atLeastRank;
	private String _source;
	
	public Expectation(String newUrl, int highestDesiredRank, String source) {
		_url = newUrl;
		_atLeastRank = highestDesiredRank;
		_source = source;
	}
	
	public String getUrl() { return _url; }
	public int getDesiredHighestRank() { return _atLeastRank; }
    public String getSource() { return _source; }

}
