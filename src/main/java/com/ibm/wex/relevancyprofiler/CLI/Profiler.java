package com.ibm.wex.relevancyprofiler.CLI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import com.ibm.wex.relevancyprofiler.resultfetchers.EngineXmlFeedDebugResultFetcher;
import com.ibm.wex.relevancyprofiler.groundtruth.GroundTruth;
import com.ibm.wex.relevancyprofiler.groundtruth.GroundTruthReader;
import com.ibm.wex.relevancyprofiler.resultfetchers.IResultFetcher;
import com.ibm.wex.relevancyprofiler.resultfetchers.ResultFetcherFactory;
import com.ibm.wex.relevancyprofiler.resultfetchers.ResultFetcherFactoryException;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.ibm.wex.relevancyprofiler.filters.*;



public class Profiler {

	

	public static void main(String[] args) {
        Profiler p = new Profiler();
        ProfilerOptions settings = p.LoadCommandLine(args);

        // Load the Ground Truth information
        GroundTruthReader gtReader = new GroundTruthReader();
        GroundTruth golden = gtReader.Load(settings.getGroundTruthPath());

        // Choose and initialize the fetcher
        IResultFetcher fetcher = null;
        try {
            fetcher = ResultFetcherFactory.CreateFetcher(settings);
            fetcher.CollectResults(golden);
        } catch (ResultFetcherFactoryException e) {
            e.printStackTrace();
        }



        // Run the reports










		// read the inPath
		ProfilingSession queries = ReadInputFile();
		if (queries == null || !queries.hasQueries()) {
			System.out.println("No queries were found in the input file.");
			return;
		}

		GetResults(queries);
		
		// write out the results
		System.out.println("Writing all results to " + _outPath + "...");
		List<IResultsFilter> filters = new ArrayList<IResultsFilter>();
		
		filters.add(new AllResultsFilter());
		filters.add(new QueriesWithNoResultsFilter());
		filters.add(new ExpectedFoundResultsFilter());
		filters.add(new FirstHitsFilter());
		filters.add(new ExpectedNotFoundFilter());
		filters.add(new SummaryFilter(_project, _urlRoot, _inPath,_maxResults));
		
		for (IResultsFilter filter : filters) {
			System.out.println("Saving data to " + filter.getFileName() + "...");
			filter.saveResults(_outPath, queries);
		}		
	
		System.out.println("Done.");
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























	

	private ProfilingSession ReadInputFile() {
        File csvFile = new File(_inPath);
        ProfilingSession queries = null;
		int lineNumber = 0;

        try {
    		queries = new ProfilingSession(_urlRoot, _project, _maxResults, _numberOfThreads);

			CSVParser parser = CSVParser.parse(csvFile, Charset.defaultCharset(), CSVFormat.DEFAULT);
			for (CSVRecord queryRecord : parser) {
				lineNumber++;

				// CSV format: query,source,expectedUrl,expectedRank
				if (queryRecord.size() < 3){
					System.out.println("Potential problem on line: " + lineNumber);
				}
				else {
					String query = queryRecord.get(0);
					String source = queryRecord.get(1);
					String expectedUrl = queryRecord.get(2);

					int expectedRank = 10;
					if (queryRecord.getRecordNumber() > 3) {
						expectedRank = Integer.parseInt(queryRecord.get(3));
					}

					queries.addExpectation(query, source, expectedUrl, expectedRank);
				}
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("failed at line: " + lineNumber);
		}

		System.out.println("Read " + lineNumber + " lines.");
        return queries;
	}
	
	
	private void GetResults(ProfilingSession queries) {
		if (_throttleQueries) {
			queries.setSleepTime(_sleepTime);
		}
		else {
			queries.setSleepTime(0);
		}
		
		queries.searchVelocity(new EngineXmlFeedDebugResultFetcher());
	}
	
	

	


}
