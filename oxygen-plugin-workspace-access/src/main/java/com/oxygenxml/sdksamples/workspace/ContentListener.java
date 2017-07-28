package com.oxygenxml.sdksamples.workspace;

/**
 * 
 * @author intern3
 *
 */
public interface ContentListener {

	/**
	 * Depending on the Type of the Node it begins a Node
	 * by setting a span with the class name of the type
	 * @param type
	 * @param content
	 * @param offset
	 */
	void startNode(NodeType type);	
	
	
	
	/**
	 * Pastes the content it's given to the result
	 * @param content
	 */
	public void copyContent(String content);
	

	
	/**
	 * Pastes the content and adds the ending span
	 * @param type
	 * @param content
	 * @param offset
	 */
	void endNode(String content);
	
	
	
	/**
	 * Given an index it compares it to any of the 
	 * Beginning or Ending offsets. Starts and ends
	 * spans accordingly;
	 * @param i current Index
	 * @param buffer the Content so far
	 * @return
	 */
	boolean checkDiff(int i, String buffer);
}
