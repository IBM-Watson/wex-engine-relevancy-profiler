package com.ibm.wex.relevancyprofiler.resultfetchers;

import com.ibm.wex.relevancyprofiler.CLI.ProfilerOptions;
import org.w3c.dom.Element;


public class EngineXmlFeedDebugResultFetcher extends XMLFeedFetcher {

    EngineXmlFeedDebugResultFetcher(ProfilerOptions options) {
        super(options);
        super.setDisplayName("xml-feed-display-debug");
    }


    @Override
    protected ResultDetails createRankedResultFromXML(Element xml) {
        String vseKey = xml.getAttribute("vse-key");
        String url = xml.getAttribute("url");
        String truncatedUrl = xml.getAttribute("truncated-url");
        String source = xml.getAttribute("source");
        double laScore = Double.parseDouble(xml.getAttribute("la-score"));
        double score = Double.parseDouble(xml.getAttribute("score"));
        double baseScore = Double.parseDouble(xml.getAttribute("vse-base-score"));
        int rank = Integer.parseInt(xml.getAttribute("rank"));

        ResultDetails result = new ResultDetails();
        result.setLinkAnalysisScore(laScore);
        result.setKey(vseKey);
        result.setSecondaryKey(url);
        result.setSecondaryKey(truncatedUrl);
        result.setOriginSource(source);
        result.setScore(score);
        result.setNaturalRank(rank);
        result.setBaseScore(baseScore);

        return result;
    }


}
