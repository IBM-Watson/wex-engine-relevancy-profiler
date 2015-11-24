package com.ibm.wex.relevancyprofiler.resultfetchers;

import com.ibm.wex.relevancyprofiler.CLI.Profiler;
import com.ibm.wex.relevancyprofiler.CLI.ProfilerOptions;
import com.ibm.wex.relevancyprofiler.groundtruth.Expectation;
import com.ibm.wex.relevancyprofiler.groundtruth.GroundTruth;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;


public class XMLFeedFetcher implements IResultFetcher {

    private String _urlRoot = "";
    private String _project = "";
    private int _maxCount;
    private int _sleepTime = 0;
    private int _numberOfThreads = 20;
    private String _displayName = "xml-feed-display";

    public XMLFeedFetcher(ProfilerOptions options) {
        _urlRoot = options.getEngineEndpoint();
        _project = options.getProjectSettings();
        _maxCount = options.getMaxResults();
        _sleepTime = options.getSleepTime();
        _numberOfThreads = options.getThreadCount();
    }

    protected String getDisplayName() { return _displayName; }
    protected void setDisplayName(String value) { _displayName = value; }


    // collect the first hits
    // collect the expected results
    // into different lists... ?

    public ProfilerResultSet CollectResults(GroundTruth golden) {
        ProfilerResultSet results = new ProfilerResultSet();

        for (String query : golden.getQueries()) {
            for (Expectation expectation : golden.getExpectationsFor(query)) {
                String xml = doQuery(query, expectation.getSource());

                // arg, this issues the same query multiple times potentially....
                // should figure out how to optimize
                ProfilerResult result = ParseResults(xml, expectation); // needs to be abstract..


            }
        }

        return results;
    }



    protected ProfilerResult ParseResults(String XML, Expectation interestingResult) {

        return null;
    }



    private String doQuery(String query, String source) {
        try {
            String urlString = _urlRoot + CreateCgiParams(query, source);

            System.out.println("query = " + query);
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



    private String CreateCgiParams(String query, String bundle) throws UnsupportedEncodingException {
        // http://localhost/vivisimo/cgi-bin/query-meta.exe?v%3Asources=example-metadata&v%3Aproject=query-meta&render.function=xml-feed-display-debug&query=
        StringBuilder cgi = new StringBuilder();
        if (bundle != null) {
            cgi.append("?v:sources=" + bundle);
            cgi.append("&v:project=" + _project);
        }
        else {
            cgi.append("?v:project=" + _project);
        }

        cgi.append("&render.function=" + _displayName);
        cgi.append("&query=" + URLEncoder.encode(query, "UTF-8"));
        cgi.append("&render.list-show=" + _maxCount);
        cgi.append("&num=" + _maxCount);

        return cgi.toString();
    }

}
