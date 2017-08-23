package com.oxygenxml.diffreport.parser;

/**
 * Node Types in a HTML File
 * @author intern3
 *
 */
public enum NodeType {
	/**
	 * If a text has no content it means the data between the start node and the closing
	 * node is irrelevant, so is empty data that has to be just copy-pasted into the result
	 */
	EMPTYDATA,
	
	//Normal Element 
	ELEMENT,
	
	//Closing Element
	ELEMENT_CLOSE,
	
	// The text that appears between a start and closing node
	TEXTFIELD,
	
	//AttributeName of an element
	ATTRIBUTENAME,
	
	//AttributeValue of the AttributeName 
	ATTRIBUTEVALUE,
	
	//Processing Information Node
	PI,
	
	//Doctype Node
	DOCTYPE,
	
	//CData Ndoe
	CDATA,
	
	//Comment Field
	COMMENT,
	
	//DiffEntry field
	DIFFENTRY
}
