package com.oxygenxml.sdksamples.workspace;

public class CurrentReadElement{
	
	int beginOffset;
	int endOffset;
	boolean isElementAndHasAttribute;
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