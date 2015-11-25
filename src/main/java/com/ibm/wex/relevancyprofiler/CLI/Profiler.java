package com.ibm.wex.relevancyprofiler.CLI;

import com.ibm.wex.relevancyprofiler.resultfetchers.*;
import com.ibm.wex.relevancyprofiler.groundtruth.GroundTruth;
import com.ibm.wex.relevancyprofiler.groundtruth.GroundTruthReader;


public class Profiler {


	public static void main(String[] args) {
        Profiler p = new Profiler();
        ProfilerOptions settings = p.LoadCommandLine(args);

        // Load the Ground Truth information
        GroundTruthReader gtReader = new GroundTruthReader();
        GroundTruth golden = gtReader.Load(settings.getGroundTruthPath());

        // Choose and initialize the fetcher
        IResultFetcher fetcher = null;
        ProfilerResultSet results = null;
        try {
            fetcher = ResultFetcherFactory.CreateFetcher(settings);
            results = fetcher.collectResults(golden);
        } catch (ResultFetcherFactoryException e) {
            e.printStackTrace();
        }


        // Run the reports




		// write out the results
//		System.out.println("Writing all results to " + _outPath + "...");
//		List<IResultsFilter> filters = new ArrayList<IResultsFilter>();
//
//		filters.add(new AllResultsFilter());
//		filters.add(new QueriesWithNoResultsFilter());
//		filters.add(new ExpectedFoundResultsFilter());
//		filters.add(new FirstHitsFilter());
//		filters.add(new ExpectedNotFoundFilter());
//		filters.add(new SummaryFilter(_project, _urlRoot, _inPath,_maxResults));
//
//		for (IResultsFilter filter : filters) {
//			System.out.println("Saving data to " + filter.getFileName() + "...");
//			filter.saveResults(_outPath, queries);
//		}
//
//		System.out.println("Done.");
	}


    private ProfilerOptions LoadCommandLine(String[] args) {
        CommandLineParser parser = new CommandLineParser();

        try {
            parser.ProcessArguments(args);
        } catch (OptionsParseException e) {
            System.out.println(e.getMessage());
            System.out.println();
            parser.ShowHelp();
            return null;
        }

        // if showing help, only show help then bail out
        if (parser.wantsHelp()) {
            parser.ShowHelp();
            return null;
        }

        ProfilerOptions settings = parser.LoadSettings();

        if (!settings.isValidOptions()) {
            System.out.println(settings.getError());
            parser.ShowHelp();
            return null;
        }

        return settings;
    }


}
