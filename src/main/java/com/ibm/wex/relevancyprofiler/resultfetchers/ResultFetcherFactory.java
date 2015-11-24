package com.ibm.wex.relevancyprofiler.resultfetchers;


import com.ibm.wex.relevancyprofiler.CLI.ProfilerOptions;

import static com.ibm.wex.relevancyprofiler.CLI.InputModes.InputMode;

public class ResultFetcherFactory {

    public static IResultFetcher CreateFetcher(ProfilerOptions options) throws ResultFetcherFactoryException {

        switch (options.getInputMode()) {
            case EngineXMLFeedDebug:
                return new EngineXmlFeedDebugResultFetcher(options);

//            case EngineXMLFeed:
//                return new XMLFeedFetcher(options);
//
//            case EngineSearch:
//                return null;
//
//            case EngineAPI:
//                return null;
//
//            case FileEngineXMLFeed:
//                return null;
//
//            case FileCSV:
//                return null;
        }

        throw new ResultFetcherFactoryException("Unknown Result Fetcher specified: " + options.getInputMode());
    }
}
