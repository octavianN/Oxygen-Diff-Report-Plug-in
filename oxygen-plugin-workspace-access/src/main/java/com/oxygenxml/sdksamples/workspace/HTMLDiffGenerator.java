package com.oxygenxml.sdksamples.workspace;

public class HTMLDiffGenerator {

	
	private StringBuilder resultedText;
	
	
	
	public String getResultedText() {
		return resultedText.toString();
	}


	public void setResultedText(StringBuilder resultedText) {
		this.resultedText = resultedText;
	}


	public HTMLDiffGenerator() {
		resultedText = new StringBuilder();
	}
	
	
	
	
	public void startSpan(HTMLTypes type, String content){
		
		switch(type){
		case Element:
			resultedText.append( "<span class = \"Element\">" );
			break;
		case textField:
			resultedText.append("<span class = \"textField\">");;
			break;
		case attributeName:
			resultedText.append( "<span class = \"attributeName\">");
			break;
		case attributeValue:
			resultedText.append("<span class = \"attributeValue\">");
			break;
		case PI:
			resultedText.append("<span class = \"PI\">");
			break;
		case Doctype:
			resultedText.append("<span class = \"Doctype\">");
			break;
		case CDATA:
			resultedText.append("<span class = \"CDATA\">");
			break;
		case Comment:
			resultedText.append("<span class = \"Comment\">");
			
		}
		
		resultedText.append(content);
		
		
	}
	
	public void endSpan(HTMLTypes type, String content){
		
		resultedText.append(content);
		
		
	}
	
	public void addSpaces(char character){
		resultedText.append(character);
	}
	
	
}
