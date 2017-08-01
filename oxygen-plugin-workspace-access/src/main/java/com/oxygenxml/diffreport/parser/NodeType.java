package com.oxygenxml.diffreport.parser;

public enum NodeType {
	EMPTYDATA,
	ELEMENT,
	ELEMENT_CLOSE,
	TEXTFIELD,
	ATTRIBUTENAME,
	ATTRIBUTEVALUE,
	PI,
	DOCTYPE,
	CDATA,
	COMMENT,
	DIFFENTRY
}
