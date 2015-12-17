package com.ibm.wex.relevancyprofiler.groundtruth;


import java.util.Objects;

public class Query {
    private String _query;
    private String _source;

//    public Query() {}
    public Query(String q, String source) {
        _query = q;
        _source = source;
    }

    public String getQueryString() { return _query; }
    public void setQueryString(String value) { _query = value; }

    public String getSource() { return _source; }
    public void setSource(String value) { _source = value; }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if((o == null) || (o.getClass() != this.getClass())) {
            return false;
        }

        Query test = (Query)o;
        return test._query.equals(this._query) && test._source.equals(this._source);
    }


    public int hashCode() {
        return Objects.hashCode(_query + _source);
    }
}
