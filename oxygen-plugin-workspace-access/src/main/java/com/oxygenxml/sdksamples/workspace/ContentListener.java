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
	void startNode(NodeType type);
	
	/**
	 * 
	 * @param type
	 * @param content
	 * @param offset
	 */
	
	void endNode(String content);
	
	public void copyContent(String content);

	boolean checkDiff(int i, String buffer);
}
