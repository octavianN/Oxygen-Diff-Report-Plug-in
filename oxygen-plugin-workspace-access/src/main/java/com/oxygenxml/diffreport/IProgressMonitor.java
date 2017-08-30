package com.oxygenxml.diffreport;
/**
 * 
 * Wrapper for the Progress Monitor
 * @author Dina_Andrei
 *
 */
public interface IProgressMonitor {
	
	/**
	 * Sets the <number>progress</number> in the progress bar
	 */
	void setProgress(int progress);

	/**
	 * As the bar progresses the Note displays in which state the program is:
	 * generating the differences
	 * parsing one of the documents
	 * finalizing: adding the <script>javaScript</script> and printing into the file
	 */
	void setNote(String string);

}
