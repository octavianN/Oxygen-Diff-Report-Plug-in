package com.oxygenxml.sdksamples.workspace;

/**
 * 
 * @author intern3
 *
 */
public interface ContentListener {

	/**
	 * 
	 * @param type
	 * @param content
	 * @param offset
	 */
	void startNode(NodeType type, String content);
	
	/**
	 * 
	 * @param type
	 * @param content
	 * @param offset
	 */
	
	void endNode(NodeType type, String content);

	boolean checkDiff(int i, String buffer);
}
