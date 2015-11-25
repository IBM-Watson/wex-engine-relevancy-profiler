package com.ibm.wex.relevancyprofiler.resultfetchers;


public class RankedResult {
    private String _key = "";
    private String _secondaryKey = "";
    private String _originSource = "";

    private int _rank = 0;
    private int _naturalRank = 0;

    private double _laScore = 0;
    private double _score = 0;
    private double _baseScore = 0;


    public String getKey() { return _key; }
    public void setKey(String value) { _key = value; }

    public String getSecondaryKey() { return _secondaryKey; }
    public void setSecondaryKey(String value) { _secondaryKey = value; }

    public String getOriginSource() { return _originSource; }
    public void setOriginSource(String value) { _originSource = value; }

    public int getRank() {return _rank; }
    public void setRank(int value) { _rank = value; }

    public int getNaturalRank() { return _naturalRank; }
    public void setNaturalRank(int value) { _naturalRank = value; }

    public double getLinkAnalysisScore() { return _laScore; }
    public void setLinkAnalysisScore(double value) { _laScore = value; }

    public double getScore() { return _score; }
    public void setScore(double  value) { _score = value; }

    public double getBaseScore() { return _baseScore; }
    public void setBaseScore(double value) { _baseScore = value; }

    public boolean keysMatch(String url) {
        return url.equals(_key) || url.equals(_secondaryKey);
    }
}
