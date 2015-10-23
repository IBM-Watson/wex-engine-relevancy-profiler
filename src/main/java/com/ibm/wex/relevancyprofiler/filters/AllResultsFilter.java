package com.ibm.wex.relevancyprofiler.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ibm.wex.relevancyprofiler.Expectation;
import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityDocument;
import com.ibm.wex.relevancyprofiler.VelocityQuery;




public class AllResultsFilter extends Filter
							  implements IResultsFilter {

	/**
	 * Returns all results for this session in a format suitable for display.  This will include 
	 * expected URLs that were not found and top hits that were not expected.
	 */
	public List<FilterRecord> filterResults(ProfilingSession session) {
		List<VelocityDocument> documents = session.getAllResults();
		List<FilterRecord> records = new ArrayList<FilterRecord>();
		
		if (documents == null) {
			return records;
		}
		
		 for (VelocityQuery q : session.getQueriesAndResults()) {
	        String query = q.getQuery();
	        String bundle = q.getBundle();

	        for (VelocityDocument doc : q.getResults()) {
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
	        
	        for (Expectation doc : q.getMissingExpectations()) {
                FilterRecord missingRecord = new FilterRecord();
                missingRecord.addField(query).addField(bundle)
                             .addField("").addField(doc.getUrl())
                             .addField("").addField("-1").addField("-1").addField("-1").addField("-1").addField("-1")
                             .addField(String.valueOf(doc.getDesiredHighestRank())).addField("true");

                records.add(missingRecord);
	        }
	    }
		    
		Collections.sort(records, new Comparator<FilterRecord>() {
			public int compare(FilterRecord x, FilterRecord y) {
				return x.compareTo(y);
			}
		});
			
		return records;
	}
	
	
	public String getFileName() {
		return "all-results.csv";
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
		header.addField("is expected");
		
		return header;
	}

}
