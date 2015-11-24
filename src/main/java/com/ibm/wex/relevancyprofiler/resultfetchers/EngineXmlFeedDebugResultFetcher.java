package com.ibm.wex.relevancyprofiler.resultfetchers;

import com.ibm.wex.relevancyprofiler.CLI.ProfilerOptions;
import com.ibm.wex.relevancyprofiler.VelocityQuery;
import com.ibm.wex.relevancyprofiler.groundtruth.Expectation;
import com.ibm.wex.relevancyprofiler.groundtruth.GroundTruth;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class EngineXmlFeedDebugResultFetcher extends XMLFeedFetcher {

    EngineXmlFeedDebugResultFetcher(ProfilerOptions options) {
        super(options);
        super.setDisplayName("xml-feed-display-debug");
    }

    protected ProfilerResult ParseResults(String xml, Expectation interestingResult) {

    }

}
