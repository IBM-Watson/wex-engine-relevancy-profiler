package com.ibm.wex.relevancyprofiler;

public interface IVelocityConnector {
	
	String doQuery(String urlRoot, String project, int maxCount, VelocityQuery q); 

}
