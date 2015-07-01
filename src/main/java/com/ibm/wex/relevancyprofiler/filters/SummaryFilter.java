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
	
	@Override
	public List<String> filterResults(ProfilingSession session) {
		String now = new SimpleDateFormat("MMMM dd, yyyy h:m:s a").format(new Date().getTime());
		
		List<String> lines = new ArrayList<String>();
		lines.add("");
		lines.add("Run Date: " + now);
		lines.add("Velocity Endpoint: " + _endpoint);
		lines.add("Project: " + _project);
		lines.add("Queries File: " + _queriesFile);
		lines.add("Max Results Returned: " + _maxResults);
		lines.add("");
		
		int expectedFound = new ExpectedResultsFilter().filterResults(session).size();
		int expectedNotFound = new ExpectedNotFoundFilter().filterResults(session).size();
		lines.add("Number Of Queries: " + session.getQueries().size());
		lines.add("Total Expected Documents: " + (expectedFound + expectedNotFound));
		lines.add("Number Of Expected Documents Found: " + expectedFound);
		lines.add("Number Of Expected Documents Not Found: " + expectedNotFound);
		
		lines.add("");
		lines.add("== Metrics Results ==");
		lines.add("");
		List<IRelevancyMetric> metrics = prepareMetrics();
		for (IRelevancyMetric metric : metrics) {
			lines.add(metric.getName());
			lines.add("\t" + metric.calculate(session));
			lines.add("");
		}
		
		lines.add("");
		lines.add("== Metrics Summary ==");
		lines.add("");
		for (IRelevancyMetric metric : metrics) {
			lines.add(metric.getName());
			lines.add("\t" + metric.getDescription());
			lines.add("");
		}
		
		
		return lines;
	}

	@Override
	public String getFileName() {
		return "run-summary.txt";
	}
	
	public String getHeader() {
		return "== Run Summary ==";
	}
	
	
	private List<IRelevancyMetric> prepareMetrics() {
		List<IRelevancyMetric> metrics = new ArrayList<IRelevancyMetric>();
		metrics.add(new AverageRankOfExpectedResults());
		metrics.add(new MedianRankOfExpectedResults());
		metrics.add(new AverageRankOfTopExpectedResults());
		metrics.add(new PercentageExpectedMetAtLeastRank());
		metrics.add(new PercentageExpectedDocumentsWithinRank(_n));
		metrics.add(new PercentageTopDocumentsWithinRank(_nTop));
		metrics.add(new PercentageDocumentsNotFound());
		
		return metrics;
	}

}
