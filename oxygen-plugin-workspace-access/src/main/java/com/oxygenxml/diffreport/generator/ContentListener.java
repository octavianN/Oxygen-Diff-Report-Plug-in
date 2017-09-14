package com.oxygenxml.diffreport.generator;

import com.oxygenxml.diffreport.parser.NodeType;

/**
 * 
 * Content listener to the parse events. It decides when a node starts and when it ends.
 * Also it is responsible for dealing with the starting and ending offsets of the parent
 * and child differences. 
 * 
 * @author Dina_Andrei
 */
public interface ContentListener {

	/**
	 * Depending on the Type of the Node, it begins a Node
	 * by setting a span with the class name of the type
	 * @param type The start node type
	 */
	void startNode(NodeType type);	
	
	
	
	/**
	 * Pastes the content it's given to the result.
	 * @param content The content to be copied. 
	 */
	public void copyContent(String content);
	

	
	/**
	 * Pastes the content and adds the ending span.
	 * @param content The content to be copied before the end node.
	 */
	void endNode(String content);
	
	
	
	/**
	 * Given an offset, it is compared with all of the offset and checks if it is one of 
	 * Beginning or Ending offsets. Starts and ends spans accordingly.
	 * @param i current Index
	 * @param buffer the Content so far
	 * @return <code>true</code> if a diff entry was found.
	 */
	boolean checkDiff(int i, String buffer);
}
