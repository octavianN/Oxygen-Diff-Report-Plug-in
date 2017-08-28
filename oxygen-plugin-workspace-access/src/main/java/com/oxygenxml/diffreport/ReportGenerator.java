package com.oxygenxml.diffreport;

import java.io.File;
import java.net.URL;

import javax.swing.ProgressMonitor;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * 
 * @author intern3
 *
 */
public interface ReportGenerator {

	/**
	 * Takes the imput from the dialogue and makes the connection between
	 * the Generate Diff Button and the Oxygen Plug-In
	 * @param firstURL Left File URL Path
	 * @param secondURL Right File URL Path
	 * @param outputFile The File where the result is written;
	 * @param progressMonitor 
	 */
	HTMLPageGenerator getPageGenerator();
	void setPageGenerator(HTMLPageGenerator pg);
	void setPluginWorkspaceAccess(StandalonePluginWorkspace pluginWorkspaceAccess);
	
	
}


