package com.ibm.wex.relevancyprofiler.metrics;

import com.ibm.wex.relevancyprofiler.ProfilingSession;


public interface IRelevancyMetric {

	Double calculate(ProfilingSession results);
	
	String getName();
	
	String getDescription();
	
}
