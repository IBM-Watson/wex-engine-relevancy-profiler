package com.ibm.wex.relevancyprofiler;


public class VelocityDocument {

	private String _url = "";
	private boolean _expected = false;
	
	private int _atLeastRank = -1;

	// values taken from the results
	private String _source = "";
	private int _rank = -1;
	private int _naturalRank = -1;
	private double _laScore = -1;
	private double _score = -1;
	private String _vseKey = "";
	private double _baseScore = -1;
	

	public VelocityDocument(String url) {
		_url = url;
		_expected = false;
	}
	
	public VelocityDocument(String url, boolean expected, int atLeastRank) {
		_url = url;
		_expected = expected;
		_atLeastRank = atLeastRank;
	}
	
	
	public Boolean equals(VelocityDocument otherDocument) {
		return _source.equals(otherDocument._source) &&
			   _rank == otherDocument._rank &&
			   _naturalRank == otherDocument._naturalRank &&
			   _baseScore == otherDocument._baseScore &&
			   _laScore == otherDocument._laScore &&
			   _score == otherDocument._score &&
			   _vseKey.equals(otherDocument._vseKey) &&
			   _url.equals(otherDocument._url) &&
			   _expected == otherDocument._expected &&
			   _atLeastRank == otherDocument._atLeastRank;
	}
	
	
	

	public String getUrl() { return _url;	}
	public void setUrl(String value) { _url = value; }
	
	public void setExpectedStatus(boolean value) { _expected = value; }
	public boolean isExpected() { return _expected; }
	
	public int getDesiredAtLeastRank() { return _atLeastRank; }
	public void setDesiredAtLeastRank(int value) { _atLeastRank = value; }
	
	public String getSource() {	return _source;	}
	public void setSource(String value) {_source = value; }
	
	public String getVseKey() {	return _vseKey;	}
	public void setVseKey(String value) {_vseKey = value; }
	
	public int getRank() { return _rank; }
	public void setRank(int value) {_rank = value; }
	
	public int getNaturalRank() { return _naturalRank; }
	public void setNaturalRank(int value) {_naturalRank = value; }
	
	public double getScore() { return _score; }
	public void setScore(double value) {_score = value; }
	
	public double getLinkAnalysisScore() { return _laScore; }
	public void setLinkAnalysisScore(double value) {_laScore = value; }
	
	public double getBaseScore() { return _baseScore; }
	public void setBaseScore(double value) { _baseScore = value; }
	
	public boolean DocumentExists() { return _rank != -1; }

	public boolean isFirstHit() { return _rank == 0; }
	
	
	public String toString() {
		StringBuilder line = new StringBuilder();
		line.append(_source + ",");
		line.append(_url + ",");
		line.append(_vseKey + ",");
		line.append(_naturalRank + ",");
		line.append(_rank + ",");
		line.append(_baseScore + ",");
		line.append(_score + ",");
		line.append(_laScore + ",");
		line.append(_atLeastRank + ",");
		line.append(_expected);

		return line.toString();
	}

	/**
	 * Two documents are considered to be the same if either their URL or vse-key are the same.
	 */
	public boolean isSameDocument(VelocityDocument result) {
		return _url.equals(result._url) || _vseKey.equals(result._vseKey);
	}
		
}
