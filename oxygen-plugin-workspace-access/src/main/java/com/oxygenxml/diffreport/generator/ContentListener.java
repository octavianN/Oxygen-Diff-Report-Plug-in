package com.oxygenxml.diffreport.generator;

import com.oxygenxml.diffreport.parser.NodeType;

/**
 * 
 * Content listener to the parse events.
 * 
 * @author intern3
 */
public interface ContentListener {

	/**
	 * Depending on the Type of the Node it begins a Node
	 * by setting a span with the class name of the type
	 * @param type The start node type
	 */
	void startNode(NodeType type);	
	
	
	
	/**
	 * Pastes the content it's given to the result
	 * @param content The content to be copied. 
	 */
	public void copyContent(String content);
	

	
	/**
	 * Pastes the content and adds the ending span
	 * @param content The content to be copied before the end node.
	 */
	void endNode(String content);
	
	
	
	/**
	 * Given an index it compares it to any of the 
	 * Beginning or Ending offsets. Starts and ends
	 * spans accordingly;
	 * @param i current Index
	 * @param buffer the Content so far
	 * @return <code>true</code> if a diff entry was found.
	 */
	boolean checkDiff(int i, String buffer);
}
