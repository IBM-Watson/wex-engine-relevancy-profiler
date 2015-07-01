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
	 * expected URLs that were not found and top hits that were not expected.  The first item in the
	 * list is a header.
	 * @return A list of all results.
	 */
	@Override
	public List<String> filterResults(ProfilingSession session) {
		List<VelocityDocument> documents = session.getAllResults();
		List<String> lines = new ArrayList<String>();
		
		if (documents == null) {
			return lines;
		}
		
		 for (VelocityQuery q : session.getQueriesAndResults()) {
	        String query = q.getQuery();
	        String bundle = q.getBundle();
	        
	        for (VelocityDocument doc : q.getResults()) {
	          lines.add(query + "," + bundle + "," + doc.toString());
	        }
	        
	        for (Expectation doc : q.getMissingExpectations()) {
	          lines.add(query + "," + bundle + ",," + doc.getUrl() + ",,-1,-1,-1,-1,-1," + doc.getDesiredHighestRank() + ",true");
	        }
	    }
		    
		Collections.sort(lines, new Comparator<String>() {
			@Override
			public int compare(String x, String y) {
				return x.compareTo(y);
			}
		});
			
		return lines;
	}
	
	
	@Override
	public String getFileName() {
		return "all-results.csv";
	}
	
	
	public String getHeader() {
		StringBuilder line = new StringBuilder();
		line.append("query,");
		line.append("requested bundle,");
		line.append("document source,");
		line.append("url,");
		line.append("vse-key,");
		line.append("natural rank,");
		line.append("rank,");
		line.append("base score,");
		line.append("score,");
		line.append("la-score,");
		line.append("at least rank,");
		line.append("is expected?");
		
		return line.toString();		
	}

}
