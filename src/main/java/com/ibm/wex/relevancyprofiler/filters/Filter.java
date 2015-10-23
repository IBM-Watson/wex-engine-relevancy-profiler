package com.ibm.wex.relevancyprofiler.filters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;


public abstract class Filter implements IResultsFilter {


	public void saveResults(String directoryName, ProfilingSession session) {
		CSVPrinter printer = null;

		try {
        	printer = createCsvPrinter(directoryName, getFileName());
        	printer.printRecord((Object[]) getHeader().getRecord());

            for (FilterRecord record : filterResults(session)) {
                printer.printRecord((Object[]) record.getRecord());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        finally {
          try {
                if (printer != null) {
					printer.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
	

	private CSVPrinter createCsvPrinter(String directoryName, String fileName) throws IOException {
		File directoryPath = new File(directoryName);
		directoryPath.mkdirs(); // make sure the directory holding the data exists

		// String now = new SimpleDateFormat("yyyyMMdd").format(new Date().getTime());
		return new CSVPrinter(new FileWriter(new File(directoryPath, fileName)), CSVFormat.DEFAULT);
	}
	
}
