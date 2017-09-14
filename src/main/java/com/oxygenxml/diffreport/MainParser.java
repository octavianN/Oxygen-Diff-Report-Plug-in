package com.oxygenxml.diffreport;

import java.io.IOException;
import java.io.Reader;

import com.oxygenxml.diffreport.generator.ContentListener;

/**
 * Parser.<br>
 * It iterates through the file character by character and gives it
 * to the ContentListener.<br>
 * Takes an XML file and notifies when certain events appear
 * @author Dina_Andrei
 *
 */
public interface MainParser {
	
	/**
	 * Passes the File and wraps it into a ReaderWithIndex
	 * It then initialize the parser and sets it with the
	 * current Listener.
	 * It is reading the current tag:
	 * NOTE: A tag could be between "> <"( a TextField )
	 * Then It passes the result to the parser.
	 * @param read - current File of the two XML files
	 * @param progressMonitor - Progress Bar Generator
	 * @param progress - percentage of the total execution time 
	 * @param lengthFile - number of bytes in the given file
	 * @param totalLength - length of both files
	 * @param oldPercentage - the percentage that has already accumulated 
	 * @param isSecondFile a boolean that tells if we are dealing with the second file or the first
	 * if <code>isSecondFile == true </code> the second file is parsed
	 * else if <code>isSecondFile == false </code> the first file is parsed
	 * @throws IOException
	 */
	 int parseInputIntoHTMLFormat(Reader read, 
				IProgressMonitor progressMonitor, 
				int progress, 
				double lengthFile,
				double totalLength,
				int oldPercentage,
				boolean isSecondFile) throws IOException;
	 
	 /**
	  * Setter for the ContentListener
	  * @param contentListener
	  */
	 void setContentListener(ContentListener contentListener);
}
