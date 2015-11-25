package com.ibm.wex.relevancyprofiler.resultfetchers;

import com.ibm.wex.relevancyprofiler.CLI.ProfilerOptions;

public class EngineXmlFeedDebugResultFetcher extends XMLFeedFetcher {

    EngineXmlFeedDebugResultFetcher(ProfilerOptions options) {
        super(options);
        super.setDisplayName("xml-feed-display-debug");
    }

//    protected ProfilerResult ParseResults(String xml, Expectation interestingResult) {
//
//    }

}
