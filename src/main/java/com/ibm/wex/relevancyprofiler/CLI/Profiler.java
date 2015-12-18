package com.ibm.wex.relevancyprofiler.CLI;

import com.ibm.wex.relevancyprofiler.reports.IReport;
import com.ibm.wex.relevancyprofiler.reports.MetricsSummaryReport;
import com.ibm.wex.relevancyprofiler.reports.NoResultsQueryReport;
import com.ibm.wex.relevancyprofiler.resultfetchers.*;
import com.ibm.wex.relevancyprofiler.groundtruth.GroundTruth;
import com.ibm.wex.relevancyprofiler.groundtruth.GroundTruthReader;

import java.util.ArrayList;
import java.util.List;


public class Profiler {


	public static void main(String[] args) {
        Profiler p = new Profiler();
        ProfilerOptions settings = p.LoadCommandLine(args);

        if (settings == null) {
            return;
        }

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
        List<IReport> reports = new ArrayList<IReport>();

        reports.add(new MetricsSummaryReport());
        reports.add(new NoResultsQueryReport(settings));

        for (IReport r : reports) {
            r.writeReport(results);
        }
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
