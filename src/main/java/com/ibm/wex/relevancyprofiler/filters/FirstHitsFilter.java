package com.ibm.wex.relevancyprofiler.filters;

import java.util.ArrayList;
import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityDocument;
import com.ibm.wex.relevancyprofiler.VelocityQuery;

public class FirstHitsFilter extends Filter implements IResultsFilter {

	/**
	 * Gets the list of all expected an unexpected first hits in a format suitable for display.
	 */
	public List<FilterRecord> filterResults(ProfilingSession session) {
		List<FilterRecord> records = new ArrayList<FilterRecord>();
	
		for (VelocityQuery q : session.getQueriesAndResults()) {
    		String query = q.getQuery();
    		String bundle = q.getBundle();
    		
    		for (VelocityDocument doc : q.getResults()) {
    			if (doc.isFirstHit()) {
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
		return "first-hits-results.csv";
	}
	
	
	public FilterRecord getHeader() {
		FilterRecord header = new FilterRecord();
		header.addField("query");
		header.addField("requested bundle");
		header.addField("document source");
		header.addField("url");
		header.addField("vse-key");
		header.addField("natural rank");
		header.addField("rank");
		header.addField("base score");
		header.addField("score");
		header.addField("la-score");
		header.addField("at least rank");
		header.addField("is expected?");

		return header;
	}

}
