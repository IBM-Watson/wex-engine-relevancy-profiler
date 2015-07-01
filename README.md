# WEX Engine Relevancy Profiler
Watson Explorer Engine is the search and indexing platform within the [IBM Watson Explorer (WEX) product family](http://www.ibm.com/smarterplanet/us/en/ibmwatson/explorer.html).  Engine is commonly used for enterprise search use cases and also serves as the backend enabling enhanced 360-degree views for WEX Application Builder, as well as a wide range of custom search-based applications.

The Relevancy Profiler tool is a command line tool that uses a ground truth file created by subject matter experts to profile search relevancy with the intent of assisting Engine Admins with relevancy tuning.

# Usage

There are two parts to using the relevancy profiler.  The command line tool reads the ground truth file containing query terms and expected results.  Results are downloaded from a custom Display template based on the XML Feed Display.  The top search results and computed relevancy metrics are then saved to files for further analysis.

## Building the Tool
Building the relevancy profiler requires [Maven](http://maven.apache.org/) to build it.  You can build it with the following command.

`
mvn package
`

## Configuring the Custom XML Feed Display Template
Every Engine instance that you will be searching will require the [xml-feed-display-debug.xml](/engine/xml-feed-display-debug.xml) template. Typically, if you have dedicated query servers used for aggregating results you would add the node to one of those servers.

To add the custom XML Feed Display node to your Engine configuration use the following steps.

1. Click the `+` sign next to XML on the left side menu to add a new XML node.
2. Edit the name field to be `xml-feed-display-debug`
3. Edit the element field to be `function`
4. Click `Add`.
5. Copy and paste the contents of the entire [xml-feed-display-debug.xml](/engine/xml-feed-display-debug.xml) file into the editing text box.
6. Click OK.

There should now be a display node named xml-feed-display-debug available in your Engine instance.  Since the relevancy profiler sets the display property at query time, no other actions are necessary.

## Creating a Ground Truth File for Queries
The ground truth file is a simple CSV with the following columns.

| Field        | Description      |
|--------------|------------------|
| Query        | The query term or phrase that you would like to use for the search. |
| Source       | The source that should be searched.  This is useful if you are performing experiments that might require changes to the source. |
| Expected URL | The URL of the document that your subject matter experts consider to the "best" result. | 
| Desired Rank | The rank the expected URL should appear at.  Changing this rank to be greater than 1 provides for tolerances in the metrics.  A common example is to say that if a document appears on the first page (desired rank of 10) might be good enough in many circumstances. |

Query operators should work without issue.  If using a phrase search ("quote" operator) be sure to format the CSV file so that the CSV file is parsed as expected.

The queries and expected values that you use as your ground truth will have significant influence over the metrics that are produced.  You should collaborate closely with multiple subject matter experts (SMEs) to create a reasonably representative ground truth file.  Keep an eye out for individual biases and errors in the ground truth, as biases and errors will be reflected in the metrics and in turn in the relevancy tuning that you configure.  In short...

**You're relevancy metrics will only be as good as the ground truth used to calculate them.**   

## Relevancy Profiler Usage

To show the options when running the tool use the following command.
```
java -jar relevancy-profiler-jar-with-dependencies.jar --help
```

The relevancy profiler tool uses older terminology to refer to some components.  Velocity is currently known as Watson Explorer Engine.

| Command line argument             | Description        |
|-----------------------------------|--------------------|
| `-c,--concurrent-requests <arg>`  | Optional (Default 10).  The number of concurrent requests that can be made to the server. This along with the sleep time allows you to adjust the profiling tool's aggressiveness when querying the server. |
| `-h,--help`                       | Shows this help message. |
| `-i,--in-path <arg>`              | Path to a file containing the list of queries to be used as a baseline for measuring relevancy. |
| `-m,--max-results <arg>`          | Optional (Default 100).  The number of results to limit the results set. |
| `-o,--out-path <arg>`             | The name of a folder to save the relevancy reports. |
| `-p,--project <arg>`              | The project to be used for searching. |
| `-s,--sleep <arg>`                | Optional  (Default 0). The amount of time in ms to wait between queries. |
| `-v,--velocity <arg>`             | Valid URL which points to the full Velocity endpoint. |

Here's an example for running the relevancy profiler against a localhost Engine instance.

```
java -jar relevancy-profiler-jar-with-dependencies.jar -p query-meta -v http://localhost:9080/vivisimo/cgi-bin/query-meta.exe -i queries-to-profile.csv -o sample-output
```

And you'll see the following on the screen.

```
query = horse
query = blarg
query = automobile
query = 1983 WITHIN CONTENT year
Writing all results to sample-output...
Saving data to all-results.csv...
Saving data to empty-results.csv...
Saving data to expected-results.csv...
Saving data to first-hits-results.csv...
Saving data to not-found-but-expected.csv...
Saving data to run-summary.txt...
Done.
```

When the analysis is complete, 6 files will be saved to the folder sample-ouptput (as described in the run summary from the example above).

## Interpreting Relevancy Metrics and Output
Calculated metrics are written to `run-summary.txt`.  This is a convenient way to keep track of the settings used for a particular run of the relevancy profiler.  In addition to computed metrics, a number of data files are saved.  These files are provided so that it's possible to perform root cause analysis of the output for the run.  The metrics are great at helping you understand if your relevancy tuning changes are improving or hurting the overall relevancy while the data outputted will help you figure out what happened so you can decide what tuning actions to take next.

| Relevancy Metric                                | Description        |
|-------------------------------------------------|--------------------|
| Average Rank of Expected Results                | The average rank of all expected documents that were found.  Missing documents are not considered in this metric.  Lower is better with the best score being 0. |
| Median Rank of Expected Results                 | The median rank (that is the one in the middle of a sorted list of ranks) of all expected documents that were found.  Missing documents are not considered in this metric.  Lower is better with the best score being 0. |
| Average Rank of Top Expected Results            | The average rank of only the lowest ranked expected document for a query that were found.  Missing documents are not considered in this metric, nor are documents beyond the top for a query.  Lower is better with the best score being 0. |
| Percentage Expected Met Specified at Least Rank | The percentage of expected documents that have a rank less than or equal to the specified "at least rank" for the set of queries.  All found, expected documents are considered.  Higher is better with the best score being 1.0. |
| Percentage of Expected Documents in Top 10      | The percentage of expected documents that appear in the top 10, that is documents with a rank less than 10.  Higher is better with the best score being 1.0. |
| Percentage of Top Expected Documents in Top 3   | The percentage of top expected documents that appear in the top 3, that is documents with a rank less than 3 that have the lowest rank for a query.  Only the lowest ranked documents are considered.  Higher is better with the best score being 1.0. |
| Percentage of Expected Documents Not Found      | The percentage of documents that were expected but not found in the results returned.  This number can change if the number of results returned by Velocity when profiling is altered.  Lower is better with the best score being 0. |

Relevancy tuning is part art, part science.  See [Relevancy Tuning Considerations](relevancy-tuning.md) for more information on the questions you should be asking and the kinds of experiments you should be doing.


# Licensing
All sample code contained within this project repository or any subdirectories is licensed according to the terms of the MIT license, which can be viewed in the file [licenses/LICENSE](/licenses/LICENSE).

## Implicit agreement of the CLA
By submitting any contributions to this project implicitly you agree to the terms of the contributors license agreement located in the file [licenses/CLA.md](/licenses/CLA.md).

# Open Source @ IBM
[Find more open source projects on the IBM Github Page](http://ibm.github.io/)
