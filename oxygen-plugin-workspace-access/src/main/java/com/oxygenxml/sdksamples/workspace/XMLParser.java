package com.oxygenxml.sdksamples.workspace;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;


public class XMLParser {

	class ReaderWithIndex extends Reader{
		private int index;
		private Reader innerReader;

		public int getIndex(){
			return index;
		}
		
		public ReaderWithIndex(Reader innerReader) {
			this.innerReader = innerReader;
			this.index = 0;
		}
		
		
		@Override
		public int read(char[] cbuf, int off, int len) throws IOException {
			index += len;
			return innerReader.read(cbuf, off, len);
		}

		@Override
		public void close() throws IOException {
			innerReader.close();
			
		}
		
	}
	
    private String characterResidue;
    private ContentListener contentListener;
    private BeginTag currentTag ;

	
	
//    public XMLParser(BufferedReaderWithIndex buff) {
////    	this((Reader)buff);
//    	this.buff = buff;
//    	this.resultedText = "";
//	}
	XMLParser(){
	}
	
	public void setContentListener(ContentListener contentListener) {
		this.contentListener = contentListener;
	}
	
	/**
	 * Compares two strings character by character to the point those two
	 * are not alike anymore and remembers the common part in a String
	 * @param reader reading character by character
	 * @param comparedToThis the string we compare it to
	 * @return the common part of the string
	 */
	private String checkTwoWords(ReaderWithIndex reader, String comparedToThis) throws IOException{
		String resultedString = "";
		
			int index = 1;
			while(reader.ready() && index < comparedToThis.length()){
				char currentCharacter = (char) reader.read();
				if(currentCharacter != comparedToThis.charAt(index++)){
					return (resultedString + currentCharacter);
				}
				resultedString += currentCharacter;
			}
		

		return null;
	}
	
	/**
	 * Depending on what symbols comes next, this method decides
	 * what type of class the tag has
	 * @param reader
	 * @param currentCharacter
	 * @return
	 */
	private int setBeginTag(ReaderWithIndex reader, int currentCharacter){
		try {

			contentListener.checkDiff(reader.getIndex(),"");

			characterResidue = null;
			currentCharacter = reader.read();
			
			
			if((char)currentCharacter == '/'){
				currentTag = BeginTag.CLOSINGTAG;
				return (char)currentCharacter;
			}else if((char)currentCharacter != '!'){
				
				if((char)currentCharacter != '?'){
					currentTag = BeginTag.PROCESSING;
					return (char)currentCharacter;
					
				}else{
					currentTag = BeginTag.PROCESSING_INSTRUCTION;
					
				}
			}else{
				currentCharacter = reader.read();
				if((char)currentCharacter == 'D'){
					
					characterResidue = checkTwoWords(reader, "DOCTYPE");
					
					
					if(characterResidue == null){
						currentTag = BeginTag.DOCTYPE;
					}else {
						characterResidue = "!D" + characterResidue;
						currentTag = BeginTag.PROCESSING;
					}
				}else if((char)currentCharacter == '['){
					
					characterResidue = checkTwoWords(reader, "[CDATA[");
					if(characterResidue == null){
						currentTag = BeginTag.CDATA;
						characterResidue = "![" + characterResidue;
					}else{
						currentTag = BeginTag.PROCESSING;
					}
					
				}else if((char)currentCharacter == '-'){
									
					characterResidue = checkTwoWords(reader, "--");
					
					if(characterResidue == null){
						currentTag = BeginTag.COMMENT;
						characterResidue = "!-" + characterResidue;
					}else{
						currentTag = BeginTag.PROCESSING;
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return currentCharacter;
	}
	
	
	
	/**
	 * The function summoned in the main class that parses through
	 * the text and saves the result
	 */
	public void parseInputIntoHTMLFormat(Reader reader){

		currentTag = BeginTag.UNPROCESSED;
		try {
			ReaderWithIndex ru = new ReaderWithIndex(reader);
			int currentCharacter = reader.read();
			do {
				
				if ((char)currentCharacter == '<'){
//					System.out.println(ru.getIndex()+"  Elem");
					currentCharacter = setBeginTag(ru, currentCharacter);

				} else if((char)currentCharacter == '>'){

					currentTag = BeginTag.DEALTWITH;
				}

				
				if (currentTag == BeginTag.PROCESSING){
					currentCharacter = addElement(ru, currentCharacter);
//					System.out.println(ru.getIndex()+"  Elem");

				} else if (currentTag == BeginTag.CLOSINGTAG){
					currentCharacter = addClosingElement(ru, currentCharacter);
//					System.out.println(ru.getIndex()+"  CloseElem");
					//System.out.println(beginTag);

				} else if (currentTag == BeginTag.COMMENT){
					currentCharacter = addComment(ru);

				} else if (currentTag == BeginTag.PROCESSING_INSTRUCTION){
					currentCharacter = addProcessingInformation(ru);

				} else if (currentTag == BeginTag.DOCTYPE){
					currentCharacter = addDoctype(ru);

				} else if (currentTag == BeginTag.CDATA){
					currentCharacter = addCdata(ru);

				}


				
				if (currentTag == BeginTag.PROCESSING){
					currentCharacter = addAttribute(ru, currentCharacter);
//					System.out.println(ru.getIndex()+"  Attribute");
				}
				if(currentTag == BeginTag.DEALTWITH){
//					System.out.println(beginTag);
					currentCharacter = addText(ru);
//					System.out.println(ru.getIndex()+"  Text");
				}
				
				
//				System.out.println("Ch: '" + (((int)currentCharacter)) +"'");
//				System.out.println("final: " + resultedText);
			} while((currentCharacter != -1));
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	

	
	
	/**
	 * Checks if the given character is a white space, a new line or a tab and
	 * returns the answer;
	 * @param character
	 * @return
	 */
	private boolean checkForTabsNewLinesOrWhiteSpaces(char character, int index){
		

		
		if(character == ' ' || character == '\n' || character == '\t' || character == '\r'){
			if(currentTag != BeginTag.ELEMENT_WITH_PROBLEMS){
				contentListener.endNode(NodeType.EmptyData, character + "");
			}
			return true;
		}
		return false;
	}

	
	
	
	/**
	 * The Closing Element is the one with the form </ELEMENT>
	 * I am just parsing through the text until I find the
	 * closing tag
	 * @param reader
	 * @param currentCharacter
	 * @return
	 */
	private int addClosingElement(ReaderWithIndex reader, int currentCharacter){
		contentListener.startNode(NodeType.Element, "&lt;");
		
		StringBuilder buffer = new StringBuilder();

		try {

			do{
				if(contentListener.checkDiff(reader.getIndex(),buffer.toString())){

					buffer = new StringBuilder();
				}
				if(checkEndTag((char)currentCharacter, buffer)){

					break;
				}
				
				buffer.append((char)currentCharacter);
				
			}while((currentCharacter = reader.read()) != -1);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		buffer.append("</span>");
		
		contentListener.endNode(NodeType.Element, buffer.toString());
		
		currentTag = BeginTag.POSSIBLY_ENDING_FILE;
		
		//System.out.println("\n" + currentCharacter + "\n");
		return currentCharacter;

	}
	
	
	
	
	/**
	 * After checking that the current tag is "<" i am adding
	 * the <span></span> tokens and everything between 
	 * @param index current position in the text
	 * @return the updated position
	 */
	private int addElement(ReaderWithIndex reader, int currentCharacter){
		
		BeginTag tag = currentTag;
		currentTag = BeginTag.ELEMENT_WITH_PROBLEMS;
		contentListener.startNode(NodeType.Element, "&lt;");
		
		StringBuilder buffer = new StringBuilder();
		
		boolean condition = false;

		/*
		 * If I get a white space, new line or tab, I register it then
		 * check every time to see if the next character is indeed a character
		 * so I can close the <span> tag */
		try {
			boolean firstInit = false;
			
			do{
				if(contentListener.checkDiff(reader.getIndex(),buffer.toString())){

					buffer = new StringBuilder();
				}
				

				if(!firstInit){
					if(characterResidue != null){
						currentCharacter = (char)reader.read();
						buffer.append(characterResidue);
					}
				}
				firstInit = true;
				if(checkEndTag((char)currentCharacter,buffer)){
					
					break;
				}
				if(checkForTabsNewLinesOrWhiteSpaces((char)currentCharacter, reader.getIndex())){
					
					buffer.append((char)currentCharacter);
					condition = true;
				}else if(condition && !checkForTabsNewLinesOrWhiteSpaces((char)currentCharacter, reader.getIndex())){
					
					break;
				}
				buffer.append((char)currentCharacter);
				
			}while((currentCharacter = reader.read()) != -1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		buffer.append("</span>");
		
		if(currentTag == BeginTag.ELEMENT_WITH_PROBLEMS)
			currentTag = tag;
		
		
		contentListener.endNode(NodeType.Element, buffer.toString());
		return currentCharacter;
	}
	
	
	
	
	
	
	private int addAtributeName(ReaderWithIndex reader, int currentCharacter){
		contentListener.startNode(NodeType.attributeName, "");
		
		StringBuilder buffer = new StringBuilder();
		/*
		 * If I get an EQUAL "=", if the next character is indeed a character
		 * I can close the <span> tag */

		try {
			
			
			
			
			do{
				
				if(contentListener.checkDiff(reader.getIndex(),buffer.toString())){

					buffer = new StringBuilder();
				}
				
				if(checkEndTag((char)currentCharacter,buffer)){
					
					break;
				}
				
				if((char)currentCharacter == '='){
					buffer.append((char)currentCharacter);
					break;
				}
				buffer.append((char)currentCharacter);

			}while((currentCharacter = reader.read()) != -1);
			
			
			
			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		buffer.append("</span>");
		
		contentListener.endNode(NodeType.attributeName, buffer.toString());
		return currentCharacter;
	}
	
	
	
	
	private int addAtributeValue(ReaderWithIndex reader, int currentCharacter){
		contentListener.startNode(NodeType.attributeValue, "");
		
		StringBuilder buffer = new StringBuilder();
		try {
			
			
			do{

				if(contentListener.checkDiff(reader.getIndex(),buffer.toString())){
					
					buffer = new StringBuilder();
				}
				
				if(checkEndTagWithNoWriting((char)currentCharacter)){
					break;
				}
				
				buffer.append((char)currentCharacter);
			}while((currentCharacter = reader.read()) != -1);
			
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buffer.append("</span><span class = \"Element\">&gt;</span>");
		
		contentListener.endNode(NodeType.attributeValue, buffer.toString());
		
		return currentCharacter;
	}
	
	
	
	
	
	
	/**
	 * After adding the Element I check if the next character signals an
	 * attribute and I add the specific tags to it
	 * @param index
	 * @return
	 */
	private int addAttribute(ReaderWithIndex reader, int currentCharacter){
		
		currentCharacter = addAtributeName(reader, currentCharacter);
		currentCharacter = addAtributeValue(reader, currentCharacter);

		
		//System.out.println(currentCharacter);
		return currentCharacter;
	}
	
	
	
	
	
	/**
	 * This method takes the text between two entities
	 * ex: <something> THIS IS WHAT IT TAKES </something>
	 * @param reader
	 * @param currentCharacter
	 * @return
	 */
	private int addText(ReaderWithIndex reader){
		
		
		StringBuilder buffer = new StringBuilder();
		int currentCharacter  = 0;
		try {
			
			currentTag = BeginTag.UNPROCESSED;

			String copyOfWhiteSpaces = "";
			
			
			
			while((currentCharacter = reader.read()) != -1){

				if(checkBeginTag((char)currentCharacter)){
					return currentCharacter;
				}
				if(checkForTabsNewLinesOrWhiteSpaces((char)currentCharacter, reader.getIndex())){
//					System.out.println(currentTag);
					currentTag = BeginTag.POSSIBLY_ENDING_FILE;
					copyOfWhiteSpaces += (char)currentCharacter;
				}else{
					currentTag = BeginTag.POSSIBLY_ENDING_FILE;
					break;
				}
			}
			
			if(currentTag == BeginTag.UNPROCESSED || currentCharacter == -1){ /*This is used to check if white spaces are used 
			 														or if we have tabs new lines at the end of a file*/
				return -1;
			}
			
			currentTag = BeginTag.PROCESSING;
			
			contentListener.startNode(NodeType.textField, copyOfWhiteSpaces);
			
			
			
			
			do{
				
				
				if(contentListener.checkDiff(reader.getIndex(),buffer.toString())){
					System.out.println((char)currentCharacter +""+ reader.getIndex()+ " ");
					
					buffer = new StringBuilder();
				}
				
				
				if(checkBeginTag((char)currentCharacter)){
					
					break;
				}
				buffer.append((char)currentCharacter);
			}while((currentCharacter = reader.read()) != -1);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		buffer.append("</span>");
		
		contentListener.endNode(NodeType.Element, buffer.toString());
		
		return currentCharacter;
	}
	
	
	
	

	/**
	 * Takes the Comment section and parses it
	 * @param reader
	 * @param currentCharacter
	 * @return
	 */
	private int addComment(ReaderWithIndex reader){
		
		int currentCharacter  = 0;
		contentListener.startNode(NodeType.Comment, "&lt;!-");
		
		
		StringBuilder buffer = new StringBuilder();
		
		//skipFirstWhiteSpaces(index);
		try{
			
			while((currentCharacter = reader.read()) != -1){
				
				if(contentListener.checkDiff(reader.getIndex(),buffer.toString())){

					buffer = new StringBuilder();
				}
				
				if(checkEndTag((char)currentCharacter,buffer)){
					break;
				}
				
				buffer.append((char)currentCharacter);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		buffer.append("</span>");
		
		contentListener.endNode(NodeType.Comment, buffer.toString());
		
		return currentCharacter;
		
	}
	
	
	
	
	
	/**
	 * Takes the Processing Information section and parses it
	 * @param reader
	 * @param currentCharacter
	 * @return
	 */
	private int addProcessingInformation(ReaderWithIndex reader){
		
		contentListener.startNode(NodeType.PI, "&lt;?");
		
		StringBuilder buffer = new StringBuilder();
		
		int currentCharacter  = 0;
		
		try{
			boolean commasCounter = true; /*there is the possibility that a tag may appear 
				inside parentheses, in which case it shall not be counted*/

			while((currentCharacter = reader.read()) != -1){
				
				
				if((char)currentCharacter == '?'){
					
					if(commasCounter){
					
						char nextCharacter = (char) reader.read();
						
						if(checkEndTagWithNoWriting(nextCharacter)){
							buffer.append("?&gt;");
							break;
						}else{
							buffer.append( (char)currentCharacter + "" + nextCharacter);
						}
					}else{
						buffer.append((char)currentCharacter);
					}
				}else{
					buffer.append((char)currentCharacter);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		buffer.append("</span>");
		
		contentListener.endNode(NodeType.PI, buffer.toString());
		
		return currentCharacter;
		
	}
	
	
	
	
	
	/**
	 * Parses through the text and counts every tag excepting the
	 * ones that are between commas, those are not dealt with
	 * @param reader
	 * @param currentCharacter
	 * @return
	 */
	private int addDoctype(ReaderWithIndex reader){
		
		contentListener.startNode(NodeType.Doctype, "&lt;!D");
		
		StringBuilder buffer = new StringBuilder();
		
		int currentCharacter  = 0;
		
		
		
		try{
			int closingTagCounter = 1 ; /* I am counting how many times I
			 						stumble across "<>" pair in order
			 						to be able to figure out which one
			 						is the closing tag.*/
			boolean commasCounter = true; /*there is the possibility that a tag may appear 
			 						inside commas, in which case it shall not be counted*/

			while((currentCharacter = reader.read()) != -1){
			
				
				if((char)currentCharacter == '\"' || (char)currentCharacter == '\"' ){
					commasCounter = !commasCounter;
				}
				
				if((char)currentCharacter == '<' && commasCounter){
					closingTagCounter++;
				}
				if((char)currentCharacter == '>' && commasCounter){
					closingTagCounter--;
				}
				if((char)currentCharacter == '>' && closingTagCounter == 0 && commasCounter){
					buffer.append("<span class = \"Element\">&gt;</span>");
					break;
				}
				
				buffer.append((char)currentCharacter);
				
			}
			
//			System.out.println(currentCharacter);
			
			
		}catch (IOException e) {
			e.printStackTrace();
		}
		buffer.append("</span>");
		
		contentListener.endNode(NodeType.PI, buffer.toString());
		return currentCharacter;
	}
	
	
	
	
	
	
	/**
	 * Parses through the text and closes it ONLY if it stumbles upon
	 * "]]>" tag
	 * @param reader
	 * @param currentCharacter
	 * @return
	 */
	private int addCdata(ReaderWithIndex reader){
		
		contentListener.startNode(NodeType.CDATA, "&lt;![");
		
		StringBuilder buffer = new StringBuilder();
		
		int currentCharacter  = 0;
		
		try{
			
			
			while((currentCharacter = reader.read()) != -1){
				
				if((char)currentCharacter == ']'){
					char auxiliarCharacter1 = (char) reader.read();
					if(auxiliarCharacter1 == ']'){
						char auxiliarCharacter2 = (char) reader.read();
						if(checkEndTagWithNoWriting(auxiliarCharacter2)){
							buffer.append("]]<span class = \"Element\">&gt;</span>");
							break;
						}else{
							buffer.append((char)currentCharacter + ""+ auxiliarCharacter1 + ""+ auxiliarCharacter2) ;
						}
					}else{
						buffer.append((char)currentCharacter + ""+ auxiliarCharacter1);
					}
				}else{
				
					buffer.append((char)currentCharacter);
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		contentListener.endNode(NodeType.PI, buffer.toString());
		
		return currentCharacter;
	}
	
	
	
	
	/**
	 * Checks if at the current index in the text we have a begin "<" tag or not
	 * @param character
	 * @return
	 */
	private boolean checkBeginTag(char character){
		if(character == '<'){
			currentTag = BeginTag.DEALTWITH;
			//resultedText += "&lt;";
			return true;
		}
		return false;
	}
	
	
	

	/**
	 * Checks if at the current index in the text we have an end ">" tag or not
	 * @param character
	 * @return
	 */
	private boolean checkEndTag(char character, StringBuilder buffer){
		if(character == '>'){
			currentTag = BeginTag.DEALTWITH;
			buffer.append("<span class = \"Element\">&gt;</span>");
			return true;
		}
		return false;
	}
	
	
	
	
	/**
	 * It does precisely what "checkEndTag" does but does 
	 * not write anything on the buffer
	 * @param character
	 * @return
	 */
	private boolean checkEndTagWithNoWriting(char character){
		if(character == '>'){
			currentTag = BeginTag.DEALTWITH;
			return true;
		}
		return false;
	}
	
	
	
	
	
	public static void main(String... args) {
		try {
			FileReader in = new FileReader("html.in");
			Reader buf = (Reader)(new BufferedReader(in));
			XMLParser parser = new XMLParser();
			HTMLContentGenerator contentGenerator = new HTMLContentGenerator(null, false);
			parser.setContentListener(contentGenerator);
			parser.parseInputIntoHTMLFormat(buf);
			
			
			System.out.println(contentGenerator.getResultedText());
	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	

	
}
