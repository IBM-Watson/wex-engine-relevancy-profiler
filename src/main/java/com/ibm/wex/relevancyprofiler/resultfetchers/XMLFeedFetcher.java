package com.ibm.wex.relevancyprofiler.resultfetchers;

import com.ibm.wex.relevancyprofiler.CLI.ProfilerOptions;
import com.ibm.wex.relevancyprofiler.groundtruth.Expectation;
import com.ibm.wex.relevancyprofiler.groundtruth.GroundTruth;
import com.ibm.wex.relevancyprofiler.groundtruth.Query;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class XMLFeedFetcher implements IResultFetcher {

    private String _urlRoot = "";
    private String _project = "";
    private int _maxCount;
    private int _sleepTime = 0;
    private int _numberOfThreads = 20;
    private String _displayName = "xml-feed-display";

    private int awaitingCompletion = 0;
    private final Object lock = new Object();


    public XMLFeedFetcher(ProfilerOptions options) {
        _urlRoot = options.getEngineEndpoint();
        _project = options.getProjectSettings();
        _maxCount = options.getMaxResults();
        _sleepTime = options.getSleepTime();
        _numberOfThreads = options.getThreadCount();
    }

    protected String getDisplayName() { return _displayName; }
    protected void setDisplayName(String value) { _displayName = value; }



    public ProfilerResultSet collectResults(final GroundTruth golden)
    {
        ExecutorService threadExecutor = Executors.newFixedThreadPool(_numberOfThreads);

        final ProfilerResultSet results = new ProfilerResultSet();

        for (final Query query : golden.getQueries())
        {
            Runnable queryWorker = new Runnable() {
                public void run() {
                    String xml = doQuery(query.getQueryString(), query.getSource());
                    Document xmlDoc = buildXmlDocument(xml);
                    int totalResults = getResultsCount(xmlDoc);
                    NodeList records = getDocumentResults(xmlDoc);

                    ResultDetails firstResult = getFirstResult(records);

                    results.setTotalCount(query.getQueryString(), query.getSource(), totalResults);

                    if (firstResult == null)
                    {
                        for (Expectation expectation : golden.getExpectationsFor(query))
                        {
                            results.addResultNotFound(expectation.getQuery(), expectation.getSource(), expectation.getUrl());
                        }
                    }
                    else
                    {
                        results.setFirstHit(query.getQueryString(), query.getSource(), firstResult);

                        for (Expectation expectation : golden.getExpectationsFor(query))
                        {
                            if (firstResult.keysMatch(expectation.getUrl()))
                            {
                                results.addResult(expectation.getQuery(), expectation.getSource(), firstResult);
                            }
                            else
                            {
                                // this can be optimized a bit futher, maybe take a list of expectations and return
                                // the findings instead of just doing one at a time
                                ResultDetails interesting = getInterestingResult(records, expectation);
                                if (interesting == null)
                                {
                                    results.addResultNotFound(expectation.getQuery(), expectation.getSource(), expectation.getUrl());
                                }
                                else
                                {
                                    results.addResult(expectation.getQuery(), expectation.getSource(), interesting);
                                }
                            }
                        }
                    }

                    waitALittle();
                    workerDone();
                }
            };

            synchronized(lock) {
                awaitingCompletion++;
                lock.notifyAll();
            }

            threadExecutor.execute(queryWorker);
        }

        try {
            while (awaitingCompletion > 0) {
                Thread.sleep(1000);
            }
            threadExecutor.shutdown();
            // threadExecutor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return results;
    }



    private void workerDone() {
        synchronized (lock) {
            awaitingCompletion--;
            lock.notifyAll();
        }
    }

    private void waitALittle() {
        if (_sleepTime > 0)
        {
            System.out.println("Waiting " + _sleepTime + "ms before doing next query...");
            try {
                Thread.sleep(_sleepTime);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }




    private Document buildXmlDocument(String xml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));

            return db.parse(is);

        } catch (Exception e) {
            System.out.println("Problem parsing the results XML.");
            // System.out.println(xml);
            System.out.flush();
            e.printStackTrace();

            return null;
        }
    }


    private int getResultsCount(Document xml) {
        Node documentList = xml.getElementsByTagName("list").item(0);

        if (documentList == null) {
            return 0;
        }

        int num = Integer.parseInt(documentList.getAttributes().getNamedItem("num").getNodeValue()); // total number of results returned
        int per = Integer.parseInt(documentList.getAttributes().getNamedItem("per").getNodeValue()); // total number of results requested

        if (num > per) {
            System.out.println("May have not requested all results - make sure num and per values are high enough.");
        }

        return num;
    }


    private NodeList getDocumentResults(Document xml) {
        return xml.getElementsByTagName("document");
    }


    private ResultDetails getFirstResult(NodeList records) {
        if (records == null || records.getLength() == 0) {
            return null;
        }

        int i = 0;
        int spotlightCount = 0;
        Element topHit = null;
        while (topHit == null && i < records.getLength()) {
            Element currentElement = (Element) records.item(i);

            if (!currentElement.getAttribute("url").isEmpty()) {
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
        ResultDetails topResult = createRankedResultFromXML(topHit);
        topResult.setRank(i - spotlightCount + 1); // boosted documents cause the rank to increment

        return topResult;
    }

    private ResultDetails getInterestingResult(NodeList records, Expectation expectation) {
        if (records == null || records.getLength() == 0) {
            return null;
        }

        int i = 0;
        int spotlightCount = 0;
        Element topHit = null;
        while (topHit == null && i < records.getLength()) {
            Element currentElement = (Element) records.item(i);

            if (!currentElement.getAttribute("url").isEmpty()) {
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
            ResultDetails result = createRankedResultFromXML(record);

            if (result.keysMatch(expectation.getUrl())) {
                result.setRank(i - spotlightCount + 1);
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



    private ResultDetails createRankedResultFromXML(Element xml) {
        // have to use both because either one could have
        // been given to us to verify
        String url = xml.getAttribute("url");  // debug version returns vse-key
        String truncatedUrl = xml.getAttribute("truncated-url");
        String source = xml.getAttribute("source");
        // double laScore = Double.parseDouble(xml.getAttribute("la-score"));
        double score = Double.parseDouble(xml.getAttribute("score"));
        // double baseScore = xml.getAttribute("vse-base-score");
        int rank = Integer.parseInt(xml.getAttribute("rank"));

        ResultDetails result = new ResultDetails();
        // result.setLinkAnalysisScore(laScore);
        result.setKey(url);
        result.setSecondaryKey(truncatedUrl);
        result.setOriginSource(source);
        result.setScore(score);
        result.setNaturalRank(rank);
        // result.setBaseScore(baseScore);

        return result;
    }



}
