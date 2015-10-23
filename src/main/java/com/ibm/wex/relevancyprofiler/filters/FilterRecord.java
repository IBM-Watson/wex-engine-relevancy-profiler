package com.ibm.wex.relevancyprofiler.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FilterRecord implements Comparable<FilterRecord> {

    private List<String> _fields = new ArrayList<String>();

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }

        FilterRecord other = (FilterRecord)obj;

        if (other._fields.size() != this._fields.size()) {
            return false;
        }

        for (int i = 0; i < this._fields.size(); i++) {
            if (!this._fields.get(i).equals(other._fields.get(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return _fields.hashCode();
    }



    public FilterRecord addField(String value) {
        _fields.add(value);
        return this;
    }

    public FilterRecord addFields(String[] values) {
        _fields.addAll(Arrays.asList(values));
        return this;
    }


    public String getFieldAt(int i) {
        return _fields.get(i);
    }


    public String[] getRecord() {
        return _fields.toArray(new String[_fields.size()]);
    }

    public int compareTo(FilterRecord other) {
        return String.join(",", _fields).compareTo(String.join(",", other.getRecord()));
    }

}
