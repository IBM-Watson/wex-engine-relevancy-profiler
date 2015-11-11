package com.ibm.wex.relevancyprofiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.ibm.wex.relevancyprofiler.metrics.RecallMetric;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.ibm.wex.relevancyprofiler.filters.*;



public class Profiler {

	private static String _urlRoot = null;  // http://localhost/vivisimo/cgi-bin/query-meta.exe
	private static String _project = "";
	private static String _inPath = "";
	private static String _outPath = "";
	private static int _sleepTime = 0;
	private static Boolean _throttleQueries = false;
	private static int _maxResults = 100;
	private static int _numberOfThreads = 10;
	

	public static void main(String[] args) {
		
		if (!ProcessArguments(args)) {
			return;
		}
					
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

	

	private static ProfilingSession ReadInputFile() {
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
	
	
	private static void GetResults(ProfilingSession queries) {
		if (_throttleQueries) {
			queries.setSleepTime(_sleepTime);
		}
		else {
			queries.setSleepTime(0);
		}
		
		queries.searchVelocity(new VelocityQueryConnector());
	}
	
	

	

	private static boolean ProcessArguments(String[] args) {
		String helpFlag = "h";
		String velocityFlag = "v";
		String outPathFlag = "o";
		String inPathFlag = "i";
		String sleepTimeFlag = "s";
		String maxResultsFlag = "m";
		String projectFlag = "p";
		String threadCountFlag = "c";
		
		Options options = new Options();

		// add options
		options.addOption(velocityFlag, "velocity", true, "Valid URL which points to the full Velocity endpoint.");
		options.addOption(projectFlag, "project", true, "The project to be used for searching.");
		options.addOption(outPathFlag, "out-path", true, "The name of a folder to save the relevancy reports.");
		options.addOption(inPathFlag, "in-path", true, "Path to a file containing the list of queries to be used as a baseline for measuring relevancy.");
		options.addOption(sleepTimeFlag, "sleep", true, "Optional  (Default " + _sleepTime + "). The amount of time in ms to wait between queries.");
		options.addOption(maxResultsFlag, "max-results", true, "Optional (Default " + _maxResults + ").  The number of results to limit the results set.");
		options.addOption(threadCountFlag, "concurrent-requests", true, "Optional (Default " + _numberOfThreads + ").  The number of concurrent requests that can be made to the server. This along with the sleep time allow you to adjust the profiling tool's aggressiveness when querying the servere.");
		options.addOption(helpFlag, "help", false, "Shows this help message.");
		
		// may want options to limit the reports that are created as the amount of analysis increases
		
		
		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null; 
		
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			System.out.println();
			ShowHelp(options);
			return false;
		}
		
		// if showing help, only show help then bail out
		if (cmd.hasOption(helpFlag)) {
			ShowHelp(options);
			return false;
		}
		
		Boolean missingRequiredField = false;
		
		if (cmd.hasOption(projectFlag)) {
			_project = cmd.getOptionValue(projectFlag);
		}
		else {
			System.out.println("Missing required parameter: project");
			missingRequiredField = true;
		}
		
		
		if (cmd.hasOption(velocityFlag)) {
			_urlRoot = cmd.getOptionValue(velocityFlag);
		}
		else {
			System.out.println("Missing required parameter: velocity");
			missingRequiredField = true;
		}
		
		// this is expected to be a folder..
		if (cmd.hasOption(outPathFlag)) {
			_outPath = cmd.getOptionValue(outPathFlag);
		}
		
		if (cmd.hasOption(sleepTimeFlag)) {
			try {
				_sleepTime = Integer.parseInt(cmd.getOptionValue(sleepTimeFlag));
			}
			catch (NumberFormatException ex) {
				System.out.println("Problem parsing sleep time, using default of " + _sleepTime + " ms.");						
			}
			
			// if the option is set, throttle even if the user doesn't know how to enter a proper number
			_throttleQueries = true;
		}
		
		if (cmd.hasOption(threadCountFlag)) {
			try {
				int proposedNumberOfThreads = Integer.parseInt(cmd.getOptionValue(threadCountFlag));
				if (proposedNumberOfThreads > 0) {
					_numberOfThreads = proposedNumberOfThreads;
				}
			}
			catch (NumberFormatException ex) {
				System.out.println("Problem parsing concurrent requests count, using default of " + _numberOfThreads + ".");						
			}
		}
		
		if (cmd.hasOption(maxResultsFlag)) {
			try {
				_maxResults = Integer.parseInt(cmd.getOptionValue(maxResultsFlag));
			}
			catch (NumberFormatException ex) {
				System.out.println("Problem parsing max results, using default of " + _maxResults + ".");						
			}			
		}
		
		if (cmd.hasOption(inPathFlag)) {
			_inPath = cmd.getOptionValue(inPathFlag);
		}
		else {
			System.out.println("Missing required parameter: in-path");
			missingRequiredField = true;
		}
		
		if (missingRequiredField) {
			ShowHelp(options);
			return false;
		}
		
		return true;
	}
	
	
	private static void ShowHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("relevancy-profiler", options);
	}


}
