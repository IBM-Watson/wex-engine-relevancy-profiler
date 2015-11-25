package com.ibm.wex.relevancyprofiler.groundtruth;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

public class GroundTruthReader {


    public GroundTruth Load(String groundTruthPath) {
        File csvFile = new File(groundTruthPath);
        GroundTruth expectations = null;
        int lineNumber = 0;

        try {
            expectations = new GroundTruth();

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

                    // the whole idea of expected rank is probably not important
                    // with the new ways of computing the desired IR metrics
                    // consider removing in the future XXXXX
                    int expectedRank = 10;
                    if (queryRecord.getRecordNumber() > 3) {
                        expectedRank = Integer.parseInt(queryRecord.get(3));
                    }

                    expectations.AddExpectation(query, source, expectedUrl, expectedRank);
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

        return expectations;
    }
}
