package com.oxygenxml.diffreport;

import java.io.File;
import java.net.URL;

/**
 * 
 * @author intern3
 *
 */
public interface ReportGenerator {

	/**
	 * 
	 * @param firstURL
	 * @param secondURL
	 * @param outputFile
	 */
	void generateHTMLReport(URL firstURL, URL secondURL, File outputFile);
	
}
