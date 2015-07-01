package com.ibm.wex.relevancyprofiler.metrics;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityDocument;

public class MedianRankOfExpectedResults implements IRelevancyMetric {

	
	@Override
	public Double calculate(ProfilingSession results) {
		List<VelocityDocument> expectedDocuments = results.getExpectedDocuments();
		
		if (expectedDocuments == null || expectedDocuments.size() == 0) {
			return null;
		}
		
		Collections.sort(expectedDocuments, new Comparator<VelocityDocument>() {
			@Override
			public int compare(VelocityDocument x, VelocityDocument y) {
				return x.getRank() - y.getRank();
			}
		});
		
		if (expectedDocuments.size() == 1) {
			return (double) expectedDocuments.get(0).getRank();
		}
		else if (expectedDocuments.size() == 2) {
			int rank1 = expectedDocuments.get(0).getRank();
			int rank2 = expectedDocuments.get(1).getRank();
			
			return ((double) (rank1 + rank2)) / 2;
		}
		else if (expectedDocuments.size() % 2 == 1) {
			return (double) expectedDocuments.get(expectedDocuments.size() / 2).getRank();
		}
		else {
			int rank1 = expectedDocuments.get(expectedDocuments.size() / 2).getRank();
			int rank2 = expectedDocuments.get((expectedDocuments.size() / 2) + 1).getRank();
			
			return ((double) (rank1 + rank2)) / 2;
		}
	}

	@Override
	public String getName() {
		return "Median Rank of Expected Results";
	}

	@Override
	public String getDescription() {
		return "The median rank (that is the one in the middle of a sorted list of ranks) of all expected documents that were found.  " +
			   "Missing documents are not considered in this metric.  Lower is better with the best score being 0.";
	}

}
