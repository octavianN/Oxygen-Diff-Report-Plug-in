package com.oxygenxml.diffreport;

import java.io.File;
import java.net.URL;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Builds the HTML page.<br>
 * Recives the file URL's and with the Parser and list of differences, it creates the HTML page.
 * @author Dina_Andrei
 *
 */
public interface PageGenerator {
	
	/**
	 * Main function that will build the page. It receives the paths to the two <b>unparsed</b> files
	 * and output files. It then generates the differences between the two files and parses them. Then the
	 * result is written in the output with the html and javaScript is given.
	 * @param firstURL - left XML file
	 * @param secondURL - right XML file 
	 * @param outputFile - resulted HTML file
	 */
	void generateHTMLReport(URL firstURL, URL secondURL, File outputFile );
	 
	/**
	 * The progress bar that will pop to show the progress when generating the differences and parsing the files
	 * @param progressMonitor - interface that wraps a ProgressMonitor with just the needed methods. 
	 */
	void setProgressMonitor(IProgressMonitor progressMonitor);
	 
	/**
	 * It sets the algorithm that will generate the differences between the files
	 * @param pluginWorkspaceAccess
	 */
	void setPluginWorkspaceAccess(StandalonePluginWorkspace pluginWorkspaceAccess);
	
}
