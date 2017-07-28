package com.oxygenxml.sdksamples.workspace;



/**
 * This class parses the element that is given to it 
 * by the XMLMainParser and at each element it gives the data
 * to the Listener. This last one decides what to do with it
 * @author intern3
 *
 */
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
	
	/**
	 * If I have an element I might have an attribute too, so
	 * depending on the type of the element I parse it accordingly
	 */
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
	
	/**
	 * In case I have empty data I don't have to begin a node and end it
	 * The whole content, has to be pasted in the result. But I am also
	 * checking the offsets. There might be discrepancies in the two texts
	 * right in the empty data section 
	 */
	private void copyEmptyData() {
		String elementContent = currentElement.elementContent.toString();
		int length = elementContent.length();
		int beginOffset = currentElement.beginOffset;
		StringBuilder buffer = new StringBuilder();
		
		for(int i = 0 ; i < length ; i++){
	
			buffer.append(returnChangedCharacter(elementContent.charAt(i)));
			
			
			if(contentListener.checkDiff(i + beginOffset, buffer.toString())){
				buffer = new StringBuilder();
			}
			
		}
		
		contentListener.copyContent(buffer.toString());
	}
	
	
	
	
	/**
	 * Begins with starting a node accordingly to the element type
	 * then it parsers through the content and gives it to the Listener
	 */
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
	
	
	
	/**
	 * replaces the beginning and ending tags or leaves the character as it is
	 * @param currentCharacter
	 * @return
	 */
	public String returnChangedCharacter(char currentCharacter){
		if(currentCharacter == '<'){
			return "&lt;";
		} else if (currentCharacter == '>' && currentElement.type != NodeType.ELEMENT){
			return "&gt;";
		} else {
			return currentCharacter + "";
		}
	}
	
	
	
	
	/**
	 * Parses the Element differently :
	 * If it has or not an attribute
	 */
	private void parseElement(){
		
		if(!currentElement.isElementAndHasAttribute){
			parseSimpleElement();
		}else{
			parseElementWithAttribute();
		}
		
	}
	
	
	
	/**
	 * Starts the node and closes it with a tag that
	 * is wrapped between two spans
	 */
	
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
	
	
	/**
	 * Parses an element that has an attribute
	 */
	private void parseElementWithAttribute(){
		contentListener.startNode(currentElement.type);
		String elementContent = currentElement.elementContent.toString();
		int length = elementContent.length();
		int beginOffset = currentElement.beginOffset;
		StringBuilder buffer = new StringBuilder();
	
		
		int currentIndex = 0;
		
		//The element***********************************************************
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
		//ending element**********************************************************
		
		
		buffer = new StringBuilder();//reset buffer
		
		
		//skip white spaces-----------------------------------------------
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
		//ending white spaces-----------------------------------------------
		
		
		
		//AtributeName*******************************************************
		contentListener.startNode(NodeType.ATTRIBUTENAME);
		
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
		//End AttributeName****************************************************
		
		
		buffer = new StringBuilder(); //reset buffer
		
		
		//AtributeValue--------------------------------------------------------
		contentListener.startNode(NodeType.ATTRIBUTEVALUE);
		
		for(int i = currentIndex+1 ; i < length ; i++){
			
			
			buffer.append(returnChangedCharacter(elementContent.charAt(i)));
			
			if(contentListener.checkDiff(i + beginOffset, buffer.toString())){
				buffer = new StringBuilder();
			}
			
		}
		
		contentListener.endNode(buffer.toString());
		
		//End AttributeValue----------------------------------------------------
	}
	
}




