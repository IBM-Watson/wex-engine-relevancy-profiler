package com.ibm.wex.relevancyprofiler.filters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.metrics.*;


public class SummaryFilter extends Filter implements IResultsFilter {

	private String _project = "";
	private String _queriesFile = "";
	private String _endpoint = "";
	private int _maxResults = 0;
	
	private int _n = 10;
	private int _nTop = 3;
	
	public SummaryFilter(String project, String endpoint, String queriesFilePath, int maxResults) {
		_project = project;
		_endpoint = endpoint;
		_queriesFile = queriesFilePath;
		_maxResults = maxResults;
	}
	
	public List<FilterRecord> filterResults(ProfilingSession session) {
		String now = new SimpleDateFormat("MMMM dd, yyyy h:m:s a").format(new Date().getTime());
		String n = System.lineSeparator();
		
		StringBuilder lines = new StringBuilder();
		lines.append(n);
		lines.append("Run Date: " + now + n);
		lines.append("Velocity Endpoint: " + _endpoint + n);
		lines.append("Project: " + _project + n);
		lines.append("Queries File: " + _queriesFile + n);
		lines.append("Max Results Returned: " + _maxResults + n);
		lines.append(n);
		
		int expectedFound = new ExpectedFoundResultsFilter().filterResults(session).size();
		int expectedNotFound = new ExpectedNotFoundFilter().filterResults(session).size();
		lines.append("Number Of Queries: " + session.getQueries().size() + n);
		lines.append("Total Expected Documents: " + (expectedFound + expectedNotFound) + n);
		lines.append("Number Of Expected Documents Found: " + expectedFound + n);
		lines.append("Number Of Expected Documents Not Found: " + expectedNotFound + n);

		lines.append(n);
		lines.append("== Metrics Results ==" + n);
		lines.append(n);
		List<IRelevancyMetric> metrics = prepareMetrics();
		for (IRelevancyMetric metric : metrics) {
			lines.append(metric.getName() + n);
			lines.append("\t" + metric.calculate(session) + n);
			lines.append(n);
		}
		
		lines.append(n);
		lines.append("== Metrics Summary ==" + n);
		lines.append(n);
		for (IRelevancyMetric metric : metrics) {
			lines.append(metric.getName() + n);
			lines.append("\t" + metric.getDescription() + n);
			lines.append(n);
		}
		
		List<FilterRecord> summary = new ArrayList<FilterRecord>();
		summary.add(new FilterRecord().addField(lines.toString()));
		return summary;
	}

	public String getFileName() {
		return "run-summary.txt";
	}
	
	public FilterRecord getHeader() {
		return new FilterRecord().addField("== Run Summary ==");
	}
	
	
	private List<IRelevancyMetric> prepareMetrics() {
		List<IRelevancyMetric> metrics = new ArrayList<IRelevancyMetric>();
		metrics.add(new RecallMetric());
        metrics.add(new PrecisionMetric());
		metrics.add(new AverageRankOfExpectedResults());
		metrics.add(new MedianRankOfExpectedResults());
		metrics.add(new AverageRankOfTopExpectedResults());
		metrics.add(new PercentageExpectedMetAtLeastRank());
		metrics.add(new PercentageExpectedDocumentsWithinRank(_n));
		metrics.add(new PercentageTopDocumentsWithinRank(_nTop));
		metrics.add(new PercentageDocumentsNotFound());
		metrics.add(new MeanReciprocalRankOfExpectedResults());
		metrics.add(new MeanAveragePrecisionOfExpectedResults());
		metrics.add(new NormalizedDiscountedCumulativeGainOfExpectedResults());
		return metrics;
	}

}
