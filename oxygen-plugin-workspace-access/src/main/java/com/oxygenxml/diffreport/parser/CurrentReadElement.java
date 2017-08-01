package com.oxygenxml.diffreport.parser;

/**
 * An Element could be anything that is between two tags
 * no matter their order. It remembers the content of the tag,
 * its begin and end offset in the text it's read from, the type
 * of the element.
 * @author intern3
 *
 */
public class CurrentReadElement{
	
	int beginOffset;
	int endOffset;
	boolean isElementAndHasAttribute;  /*In case between tags there is an element
	 									I want to know if it has an attribute or not*/
	StringBuilder elementContent;
	NodeType type;
	
	public CurrentReadElement() {
		elementContent = new StringBuilder();
		isElementAndHasAttribute = false;
	}

	public int getBeginOffset() {
		return beginOffset;
	}

	public void setBeginOffset(int beginOffset) {
		this.beginOffset = beginOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

	public boolean isElementAndHasAttribute() {
		return isElementAndHasAttribute;
	}

	public void setElementAndHasAttribute(boolean isElementAndHasAttribute) {
		this.isElementAndHasAttribute = isElementAndHasAttribute;
	}

	
	
	
}