package com.ibm.wex.relevancyprofiler;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class VelocityQueryConnector implements IVelocityConnector {

	
	public String doQuery(String urlRoot, String project, int maxCount, VelocityQuery q) {
		try {
    		String urlString = urlRoot + CreateCgiParams(q.getQuery(), q.getBundle(), project, maxCount);
    		
    		System.out.println("query = " + q.getQuery());
//			System.out.println(urlString);
    		
			URL url = new URL(urlString);
			
			InputStream is = url.openStream();
			return new Scanner(is).useDelimiter("\\A").next();

		} catch (MalformedURLException e) {
			System.out.println("Problem parsing URL to query Velocity...");
			System.out.println(e.getMessage());
			e.printStackTrace();
			
			return null;
		} catch (IOException e) {
			System.out.println("Problem reading data from the XML Feed...");
			System.out.println(e.getMessage());
			e.printStackTrace();
			
			return null;
		}
	}
	
	
	private String CreateCgiParams(String query, String bundle, String project, int maxCount) throws UnsupportedEncodingException {
		// http://localhost/vivisimo/cgi-bin/query-meta.exe?v%3Asources=example-metadata&v%3Aproject=query-meta&render.function=xml-feed-display-debug&query=
		StringBuilder cgi = new StringBuilder();
		if (bundle != null) {
			cgi.append("?v:sources=" + bundle);
			cgi.append("&v:project=" + project);
		}
		else {
			cgi.append("?v:project=" + project);
		}
		
		cgi.append("&render.function=xml-feed-display-debug");
		cgi.append("&query=" + URLEncoder.encode(query,"UTF-8"));
		cgi.append("&render.list-show=" + maxCount);
		cgi.append("&num=" + maxCount);
		
		return cgi.toString();
	}
	

}
