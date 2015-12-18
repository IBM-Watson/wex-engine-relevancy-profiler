package com.ibm.wex.relevancyprofiler.reports;


import com.ibm.wex.relevancyprofiler.CLI.ProfilerOptions;
import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResult;
import com.ibm.wex.relevancyprofiler.resultfetchers.ProfilerResultSet;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class NoResultsQueryReport implements IReport {

    private String _outputDirectory;
    private final String _filename = "queries-with-no-results.csv";

    public NoResultsQueryReport(ProfilerOptions options) {
        _outputDirectory = options.getOutputPath();
    }


    public void writeReport(ProfilerResultSet results) {
        CSVPrinter printer = null;

        try {
            printer = createCsvPrinter();
            printer.printRecord((Object[]) getHeader());

            for (ProfilerResult result : results.getResultsWithExpectationsNotFoud()) {
                for (String missingExpectation : result.getExpectedNotFoundResults()) {
                    String[] line = (new String[] {
                            result.getQuery(),
                            result.getQuerySource(),
                            missingExpectation
                    });
                    printer.printRecord(line);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (printer != null) {
                    printer.flush();
                    printer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private String[] getHeader() {
        return (new String[]{"query", "source", "expected key"});
    }


    private CSVPrinter createCsvPrinter() throws IOException {
        File directoryPath = new File(_outputDirectory);
        directoryPath.mkdirs(); // make sure the directory holding the data exists

        return new CSVPrinter(new FileWriter(new File(directoryPath, _filename)), CSVFormat.DEFAULT);
    }

}
