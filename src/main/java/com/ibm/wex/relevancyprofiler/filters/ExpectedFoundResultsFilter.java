package com.ibm.wex.relevancyprofiler.filters;

import java.util.ArrayList;
import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityDocument;
import com.ibm.wex.relevancyprofiler.VelocityQuery;



public class ExpectedFoundResultsFilter extends Filter implements IResultsFilter {

	/**
	 * Gets the list of only those results that were expected in a format suitable for display.
	 */
	public List<FilterRecord> filterResults(ProfilingSession session) {
		List<FilterRecord> records = new ArrayList<FilterRecord>();
		
		for (VelocityQuery q : session.getQueriesAndResults()) {
    		String query = q.getQuery();
    		String bundle = q.getBundle();
    		
    		for (VelocityDocument doc : q.getResults()) {
    			if (doc.isExpected()) {
					FilterRecord currentRecord = new FilterRecord();
                    currentRecord.addField(query).addField(bundle);
                    currentRecord.addField(doc.getSource());
                    currentRecord.addField(doc.getUrl());
                    currentRecord.addField(doc.getVseKey());
                    currentRecord.addField(String.valueOf(doc.getNaturalRank()));
                    currentRecord.addField(String.valueOf(doc.getRank()));
                    currentRecord.addField(String.valueOf(doc.getBaseScore()));
                    currentRecord.addField(String.valueOf(doc.getScore()));
                    currentRecord.addField(String.valueOf(doc.getLinkAnalysisScore()));
                    currentRecord.addField(String.valueOf(doc.getDesiredAtLeastRank()));
                    currentRecord.addField(String.valueOf(doc.isExpected()));

                    records.add(currentRecord);
    			}
    		}
    	}
		
		return records;
	}


	public String getFileName() {
		return "expected-found-results.csv";
	}
	
	
	public FilterRecord getHeader() {
        FilterRecord record = new FilterRecord();
        record.addField("query");
        record.addField("requested bundle");
        record.addField("document source");
        record.addField("url");
        record.addField("vse-key");
        record.addField("natural rank");
        record.addField("rank");
        record.addField("base score");
        record.addField("score");
        record.addField("la-score");
        record.addField("at least rank");
        record.addField("is expected?");
		
		return record;
	}

}
