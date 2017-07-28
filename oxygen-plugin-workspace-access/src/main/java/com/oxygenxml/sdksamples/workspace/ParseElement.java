package com.oxygenxml.sdksamples.workspace;

public class ParseElement {

	
	private ContentListener contentListener;
	private CurrentReadElement currentElement;
	
	ParseElement() {
		
	}
	
	public void setContentListener(ContentListener contentListener) {
		this.contentListener = contentListener;
	}
	
	public void setCurrentElement (CurrentReadElement currentElement) {
		this.currentElement = currentElement;
	}
	
	
	public void parse(){
		
		if (currentElement.type == NodeType.ELEMENT){
			parseElement();
		} else if (currentElement.type == NodeType.EMPTYDATA){
			//System.out.println();
			copyEmptyData();
		} else {
			parseNotElement();
		}
		
	}
	
	
	private void copyEmptyData() {
		String elementContent = currentElement.elementContent.toString();
		int length = elementContent.length();
		int beginOffset = currentElement.beginOffset;
		StringBuilder buffer = new StringBuilder();
		
		for(int i = 0 ; i < length ; i++){
//			if(elementContent.charAt(i) == '\r')
//				System.out.println("Elem: r ( " + (i + beginOffset) + ") " + elementContent.charAt(i));
//			if(elementContent.charAt(i) == '\n')
//				System.out.println("Elem: n ( " + (i + beginOffset) + ") " + elementContent.charAt(i));
			
	
			buffer.append(returnChangedCharacter(elementContent.charAt(i)));
			
			
			if(contentListener.checkDiff(i + beginOffset, buffer.toString())){
				buffer = new StringBuilder();
			}
			
		}
		
		contentListener.copyContent(buffer.toString());
	}

	private void parseNotElement(){
		
		contentListener.startNode(currentElement.type);
		String elementContent = currentElement.elementContent.toString();
		int length = elementContent.length();
		int beginOffset = currentElement.beginOffset;
		StringBuilder buffer = new StringBuilder();
		
		
		for(int i = 0 ; i < length ; i++){
			
			
			if(contentListener.checkDiff(i + beginOffset, buffer.toString())){
				buffer = new StringBuilder();
			}
			
			buffer.append(returnChangedCharacter(elementContent.charAt(i)));
			
			
		}
		contentListener.endNode(buffer.toString());
	}
	
	public String returnChangedCharacter(char currentCharacter){
		if(currentCharacter == '<'){
			return "&lt;";
		} else if (currentCharacter == '>' && currentElement.type != NodeType.ELEMENT){
			return "&gt;";
		} else {
			return currentCharacter + "";
		}
	}
	
	
	private void parseElement(){
		
		if(!currentElement.isElementAndHasAttribute){
			parseSimpleElement();
		}else{
			contentListener.startNode(currentElement.type);
			String elementContent = currentElement.elementContent.toString();
			int length = elementContent.length();
			int beginOffset = currentElement.beginOffset;
			StringBuilder buffer = new StringBuilder();
		
			
			int currentIndex = 0;
			
			for(int i = 0 ; i < length ; i++){
				
				currentIndex = i;
				
				if(elementContent.charAt(i) == ' ' || elementContent.charAt(i) == '\n' 
						|| elementContent.charAt(i) == '\t'){
					buffer.append(elementContent.charAt(i));
					break;
				}
				
				buffer.append(returnChangedCharacter(elementContent.charAt(i)));
				
				
				if(contentListener.checkDiff(i + beginOffset, buffer.toString())){
					buffer = new StringBuilder();
				}
				
			}
			contentListener.endNode(buffer.toString());
			
			buffer = new StringBuilder();
			
			//skip white spaces
			for(int i = currentIndex ; i < length ; i++){
				
				currentIndex = i;
				
				if(!(elementContent.charAt(i) == ' ' || elementContent.charAt(i) == '\n' 
						|| elementContent.charAt(i) == '\t')){
					break;
				}
				
				buffer.append(returnChangedCharacter(elementContent.charAt(i)));
				
				if(contentListener.checkDiff(i + beginOffset, buffer.toString())){
					buffer = new StringBuilder();
				}
				
			}
			
			
			contentListener.startNode(NodeType.ATTRIBUTENAME);
			
			//AtributeName
			for(int i = currentIndex ; i < length ; i++){
				
				currentIndex = i;
				
				if(elementContent.charAt(i) == '='){
					buffer.append(elementContent.charAt(i));
					break;
				}
				
				buffer.append(returnChangedCharacter(elementContent.charAt(i)));
				
				if(contentListener.checkDiff(i + beginOffset, buffer.toString())){
					buffer = new StringBuilder();
				}
				
			}
			
			contentListener.endNode(buffer.toString());
			buffer = new StringBuilder();
			contentListener.startNode(NodeType.ATTRIBUTEVALUE);
			
			//AtributeValue
			for(int i = currentIndex+1 ; i < length ; i++){
				
				
				buffer.append(returnChangedCharacter(elementContent.charAt(i)));
				
				if(contentListener.checkDiff(i + beginOffset, buffer.toString())){
					buffer = new StringBuilder();
				}
				
			}
			
			contentListener.endNode(buffer.toString());
		}
		
	}
	
	private void parseSimpleElement() {
		contentListener.startNode(currentElement.type);
		String elementContent = currentElement.elementContent.toString();
		int length = elementContent.length();
		int beginOffset = currentElement.beginOffset;
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < length; i++) {

			if(elementContent.charAt(i) == '<'){
				buffer.append("&lt;");
			} else if(elementContent.charAt(i) == '>' ){
				
			} else {
				buffer.append(elementContent.charAt(i));
			} 

			if (contentListener.checkDiff(i + beginOffset, buffer.toString())) {
				buffer = new StringBuilder();
			}

		}

		contentListener.endNode(buffer.toString());
		contentListener.copyContent("<span class = \"Element\">&gt;</span>");
	}
	
}















