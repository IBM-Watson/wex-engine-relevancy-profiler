package com.ibm.wex.relevancyprofiler.metrics;

import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.VelocityDocument;



public class PercentageExpectedMetAtLeastRank implements IRelevancyMetric {

	public Double calculate(ProfilingSession results) {
		List<VelocityDocument> documents = results.getExpectedDocuments();

		if (documents == null || documents.size() == 0) {
			return 0.0;
		}
		
		int numberExpectedThatMeetsAtLeastRank = 0;
		for (VelocityDocument d : documents) {
			if (d.getRank() <= d.getDesiredAtLeastRank()) {
				numberExpectedThatMeetsAtLeastRank++;
			}
		}
		
		return (double)numberExpectedThatMeetsAtLeastRank / documents.size();
	}

	public String getName() {
		return "Percentage Expected Met Specified at Least Rank";
	}

	public String getDescription() {
		return "The percentage of expected documents that have a rank less than or equal to the specified \"at least rank\" for the set of queries.  " +
		   "All found, expected documents are considered.  Higher is better with the best score being 1.0.";
	}

}
