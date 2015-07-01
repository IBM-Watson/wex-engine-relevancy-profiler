package com.ibm.wex.relevancyprofiler.filters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.ibm.wex.relevancyprofiler.ProfilingSession;


public abstract class Filter implements IResultsFilter {


	public void saveResults(String directoryName, ProfilingSession session) {
		BufferedWriter writer = null;
        try {
        	// make sure the directory exists and if it doesn't, create it.
        	File out = getFilePath(directoryName, getFileName());
        	
        	writer = new BufferedWriter(new FileWriter(out));
        	writer.write(getHeader() + "\n");

        	List<String> lines = filterResults(session);
        	for (String line : lines) {
        		writer.write(line + "\n");
        	}
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        finally {
          try {
                if (writer != null) {
                  writer.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
	
	/**
	 * Gets a file path that is suitable for saving the output of a profiling session.
	 * @param directortyName The name of the directory in which to save the file.
	 * @param fileName The root name of the file to be saved.
	 * @return A file ready to accept a writer.
	 */
	protected File getFilePath(String directortyName, String fileName) {
	    File file1 = new File(directortyName);
	    file1.mkdirs(); // make sure the directory holding the data exists
	    
	    // String now = new SimpleDateFormat("yyyyMMdd").format(new Date().getTime());	    
	    File file2 = new File(file1, fileName);
	    
	    return file2;
	}
	
}
