package com.ibm.wex.relevancyprofiler.resultfetchers;

import com.ibm.wex.relevancyprofiler.CLI.ProfilerOptions;
import com.ibm.wex.relevancyprofiler.groundtruth.Expectation;
import com.ibm.wex.relevancyprofiler.groundtruth.GroundTruth;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
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



    public ProfilerResultSet collectResults(GroundTruth golden) {
        ProfilerResultSet results = new ProfilerResultSet();

        for (String query : golden.getQueries()) {
            for (Expectation expectation : golden.getExpectationsFor(query)) {
                String xml = doQuery(query, expectation.getSource());

                // this issues the same query multiple times potentially....
                // should figure out how to optimize
                NodeList records = parseResults(xml);
                RankedResult firstResult = getFirstResult(records);

                if (firstResult != null) {
                    results.setFirstHit(query, expectation.getSource(), firstResult);

                    if (firstResult.keysMatch(expectation.getUrl())) {
                        results.addResult(query, expectation.getSource(), firstResult);
                    }
                    else {
                        RankedResult interesting = getInterestingResult(records, expectation);
                        if (interesting == null) {
                            results.addResultNotFound(query, expectation.getSource(), expectation.getUrl());
                        }
                        else {
                            results.addResult(query, expectation.getSource(), interesting);
                        }
                    }
                }
                else {
                    results.addResultNotFound(query, expectation.getSource(), expectation.getUrl());
                }
            }

        }

        return results;
    }


    private NodeList parseResults(String xml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));

            Document doc = db.parse(is);

            NodeList list = doc.getElementsByTagName("list");
            if (list == null || list.getLength() < 1) {
                return null;
            }
            else {
                Node listNode = list.item(0);
                int num = Integer.parseInt(listNode.getAttributes().getNamedItem("num").getNodeValue()); // total number of results returned
                int per = Integer.parseInt(listNode.getAttributes().getNamedItem("per").getNodeValue()); // total number of results requested
                if (num != per) {
                    System.out.println("May have not requested enough documents.");
                }
            }

            return doc.getElementsByTagName("document");
        } catch(Exception e) {
            System.out.println(xml);
            System.out.flush();
            e.printStackTrace();
        }

        return null;
    }


    private RankedResult getFirstResult(NodeList records) {
        if (records == null || records.getLength() == 0) {
            return null;
        }

        int i = 0;
        int spotlightCount = 0;
        Element topHit = null;
        while (topHit == null && i < records.getLength()) {
            Element currentElement = (Element) records.item(i);

            if (!currentElement.getAttribute("vse-key").isEmpty()) {
                topHit = currentElement;
            }
            else {
                spotlightCount++;
            }

            i++;
        }

        if (topHit == null) {
            return null;
        }

        // save the top result
        Element firstRecord = (Element) records.item(i);
        RankedResult topResult = createRankedResultFromXML(firstRecord);
        topResult.setRank(topResult.getRank() - spotlightCount); // boosted documents cause the rank to increment

        return topResult;
    }

    private RankedResult getInterestingResult(NodeList records, Expectation expectation) {
        if (records == null || records.getLength() == 0) {
            return null;
        }

        int i = 0;
        int spotlightCount = 0;
        Element topHit = null;
        while (topHit == null && i < records.getLength()) {
            Element currentElement = (Element) records.item(i);

            if (!currentElement.getAttribute("vse-key").isEmpty()) {
                topHit = currentElement;
            }
            else {
                spotlightCount++;
            }

            i++;
        }

        if (topHit == null) {
            return null;
        }

        for (; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            RankedResult result = createRankedResultFromXML(record);

            if (result.keysMatch(expectation.getUrl())) {
                result.setRank(result.getRank() - spotlightCount);
                return result;
            }
        }

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
            cgi.append("?v:sources=").append(bundle);
            cgi.append("&v:project=").append(_project);
        }
        else {
            cgi.append("?v:project=").append(_project);
        }

        cgi.append("&render.function=").append(_displayName);
        cgi.append("&query=").append(URLEncoder.encode(query, "UTF-8"));
        cgi.append("&render.list-show=").append(_maxCount);
        cgi.append("&num=").append(_maxCount);

        return cgi.toString();
    }



    private RankedResult createRankedResultFromXML(Element xml) {
        // have to use both because either one could have
        // been given to us to verify
        String vseKey = xml.getAttribute("vse-key");
        String url = xml.getAttribute("url");
        String source = xml.getAttribute("source");
        double laScore = Double.parseDouble(xml.getAttribute("la-score"));
        double score = Double.parseDouble(xml.getAttribute("score"));
        // double baseScore = xml.getAttribute("vse-base-score");
        int rank = Integer.parseInt(xml.getAttribute("rank"));

        RankedResult result = new RankedResult();
        result.setLinkAnalysisScore(laScore);
        result.setKey(vseKey);
        result.setSecondaryKey(url);
        result.setOriginSource(source);
        result.setScore(score);
        result.setNaturalRank(rank);
//        result.setBaseScore(baseScore);

        return result;
    }



}
