package com.oxygenxml.diffreport.parser;

/**
 * An Element could be anything that is between two tags
 * no matter their order. It remembers the content of the tag,
 * its begin and end offset in the text it's read from, the type
 * of the element.
 * @author Dina_Andrei
 *
 */
public class CurrentReadElement{
	/**
	 * The beginning offset of the tag extracted from the file
	 */
	int beginOffset;
	/**
	 * the ending offset of the tag extracted from the file
	 */
	int endOffset;
	/**
	 * In case between tags there is an element
		I want to know if it has an attribute or not
	 */
	boolean isElementAndHasAttribute;  
	/**
	 * the content of the tag
	 */
	StringBuilder elementContent;
	/**
	 * Tag Type: Element, CDATA, Doctype etc.. 
	 */
	NodeType type;
	
	public CurrentReadElement() {
		elementContent = new StringBuilder();
		isElementAndHasAttribute = false;
	}



	
	
	
}