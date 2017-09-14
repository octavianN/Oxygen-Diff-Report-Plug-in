package com.oxygenxml.diffreport;

/**
 * Takes the input from the dialogue and makes the connection between
 * the Generate Diff Button and the Oxygen Plug-In
 * @author Dina_Andrei
 *
 */
public interface ReportGenerator {

	/**
	 * Takes the input from the dialogue and makes the connection between
	 * the Generate Diff Button and the Oxygen Plug-In
	 * @param firstURL Left File URL Path
	 * @param secondURL Right File URL Path
	 * @param outputFile The File where the result is written;
	 * @param progressMonitor 
	 */
	void setPageGenerator(PageGenerator pg);
	
	
}



