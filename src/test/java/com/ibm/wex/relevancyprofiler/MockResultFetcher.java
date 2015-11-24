package com.ibm.wex.relevancyprofiler;

import java.util.ArrayList;
import java.util.List;

import com.ibm.wex.relevancyprofiler.resultfetchers.IResultFetcher;


public class MockResultFetcher implements IResultFetcher {

	private List<VelocityDocument> _documents = new ArrayList<VelocityDocument>();

	public String doQuery(String urlRoot, String project, int maxCount, VelocityQuery q) {
		return createTestXml();
	}
	
	

	public void addDocumentToResults(VelocityDocument document) {
		_documents.add(document);
	}
	
	
	// Creates a test XML document which contains the given documents in order
	public String createTestXml() {
		StringBuilder xml = new StringBuilder();
		
		xml.append("<?xml version=\"1.0\"?>\n");
		xml.append("<vce>");
		
		xml.append("<boost type=\"scm\" name=\"Guidelines\" precedence=\"1\">");
		xml.append("    <document/>");
		xml.append("  </boost>");
		xml.append("  <boost type=\"scm\" name=\"Competence and Training\" precedence=\"2\">");
		xml.append("    <document/>");
		xml.append("  </boost>");
		
		xml.append("<list path=\"\" num=\"200\" level=\"0\" start=\"0\" per=\"" + _documents.size() + "\">");
		
		for (int i = 0; i < _documents.size(); i++) {
			VelocityDocument currentDoc = _documents.get(i);
			
			xml.append("<document url=\"" + currentDoc.getUrl() + "\" vse-key=\"" + currentDoc.getVseKey() + "\" filetypes=\"html\" ");
			xml.append("score=\"" + currentDoc.getScore() + " \" la-score=\"" + currentDoc.getLinkAnalysisScore() + "\" ");
			xml.append("vse-base-score=\"" + currentDoc.getBaseScore() + "\" ");
			xml.append("rank=\"" + currentDoc.getNaturalRank() + "\" source=\"" + currentDoc.getSource() + "\" truncated-url=\"" + currentDoc.getUrl() + "\" ");
			xml.append("context=\"http://blah.com/vivisimo/cgi-bin/query-meta.exe?stuff\">");
			xml.append("<content name=\"date\" type=\"html\">09/20/2005</content>");
			xml.append("<content type=\"text\" name=\"title\">The Title of Document " + i + "</content>");
			xml.append("<content name=\"snippet\" type=\"html\"> this is the snippet </content>");
			xml.append("</document>");
		}
		
		xml.append("</list>");
		xml.append("</vce>");
			
		return xml.toString();
	}
	

	
	
}
