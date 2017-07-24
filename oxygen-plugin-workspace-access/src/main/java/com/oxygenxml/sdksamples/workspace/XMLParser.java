package com.oxygenxml.sdksamples.workspace;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import ro.sync.diff.api.Difference;




public class XMLParser {

	private Reader reader;
    private String characterResidue;
    private List<Difference> performDiff;
    private HTMLDiffGenerator htmlDiffGenerator;
    
    private BeginTag currentTag ;

	
	
//    public XMLParser(BufferedReader buff) {
////    	this((Reader)buff);
//    	this.buff = buff;
//    	this.resultedText = "";
//	}
	XMLParser(Reader reader, List<Difference> performDiff){
		this.reader = reader;
		this.htmlDiffGenerator = new HTMLDiffGenerator();
		this.performDiff = performDiff;
	}
	
	public String getResultedText(){
		return htmlDiffGenerator.getResultedText();
	}
	
	/**
	 * Compares two strings character by character to the point those two
	 * are not alike anymore and remembers the common part in a String
	 * @param reader reading character by character
	 * @param comparedToThis the string we compare it to
	 * @return the common part of the string
	 */
	private String checkTwoWords(Reader reader, String comparedToThis) throws IOException{
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
	private int setBeginTag(Reader reader, int currentCharacter){
		try {
			
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
	public void parseInputIntoHTMLFormat(){

		currentTag =BeginTag.UNPROCESSED;
		try {

			int currentCharacter = reader.read();
			do {
				
			//	System.out.print("before: " + beginTag);
				if ((char)currentCharacter == '<'){

					currentCharacter = setBeginTag(reader, currentCharacter);

				} else if((char)currentCharacter == '>'){
					//if(beginTag != CLOSINGTAG)
						currentTag = BeginTag.DEALTWITH;
				}

			//	System.out.print(" middle: " + beginTag);
				
				if (currentTag == BeginTag.PROCESSING){
					currentCharacter = addElement(reader, currentCharacter);

				} else if (currentTag == BeginTag.CLOSINGTAG){
					currentCharacter = addClosingElement(reader, currentCharacter);
					//System.out.println(beginTag);

				} else if (currentTag == BeginTag.COMMENT){
					currentCharacter = addComment(reader);

				} else if (currentTag == BeginTag.PROCESSING_INSTRUCTION){
					currentCharacter = addProcessingInformation(reader);

				} else if (currentTag == BeginTag.DOCTYPE){
					currentCharacter = addDoctype(reader);

				} else if (currentTag == BeginTag.CDATA){
					currentCharacter = addCdata(reader);

				}

		//		System.out.println(" after: " + beginTag);
				
				if (currentTag == BeginTag.PROCESSING){
					currentCharacter = addAttribute(reader, currentCharacter);
				}
				if(currentTag == BeginTag.DEALTWITH){
//					System.out.println(beginTag);
					currentCharacter = addText(reader);
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
	private boolean checkForTabsNewLinesOrWhiteSpaces(char character){
		if(character == ' ' || character == '\n' || character == '\t' || character == '\r'){
			htmlDiffGenerator.addSpaces(character);
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
	private int addClosingElement(Reader reader, int currentCharacter){
		htmlDiffGenerator.startSpan(HTMLTypes.Element, "&lt;");
		
		StringBuilder buffer = new StringBuilder();

		try {

			do{
				
				if(checkEndTag((char)currentCharacter, buffer)){
//					beginTag = UNPROCESSED;
					break;
				}
				
				buffer.append((char)currentCharacter);
				
			}while((currentCharacter = reader.read()) != -1);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		buffer.append("</span>");
		
		htmlDiffGenerator.endSpan(HTMLTypes.Element, buffer.toString());
		
		//System.out.println("\n" + currentCharacter + "\n");
		return currentCharacter;

	}
	
	
	
	
	/**
	 * After checking that the current tag is "<" i am adding
	 * the <span></span> tokens and everything between 
	 * @param index current position in the text
	 * @return the updated position
	 */
	private int addElement(Reader reader, int currentCharacter){
		
		htmlDiffGenerator.startSpan(HTMLTypes.Element, "&lt;");
		
		StringBuilder buffer = new StringBuilder();
		
		boolean condition = false;

		/*
		 * If I get a white space, new line or tab, I register it then
		 * check every time to see if the next character is indeed a character
		 * so I can close the <span> tag */
		try {
			boolean firstInit = false;
			
			do{

				if(!firstInit){
					if(characterResidue != null){
					//	System.out.println(characterResidue + " " + (char)reader.read());
						currentCharacter = (char)reader.read();
						buffer.append(characterResidue);
					}
				}
				firstInit = true;
				if(checkEndTag((char)currentCharacter,buffer)){
					
					break;
				}
				if(checkForTabsNewLinesOrWhiteSpaces((char)currentCharacter)){
					
					condition = true;
				}else if(condition && !checkForTabsNewLinesOrWhiteSpaces((char)currentCharacter)){
					
					break;
				}
				buffer.append((char)currentCharacter);
			}while((currentCharacter = reader.read()) != -1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buffer.append("</span>");
		
		htmlDiffGenerator.endSpan(HTMLTypes.Element, buffer.toString());
		return currentCharacter;
	}
	
	
	
	
	
	
	private int addAtributeName(Reader reader, int currentCharacter){
		htmlDiffGenerator.startSpan(HTMLTypes.attributeName, "");
		
		StringBuilder buffer = new StringBuilder();
		/*
		 * If I get an EQUAL "=", if the next character is indeed a character
		 * I can close the <span> tag */

		try {
			
			
			do{
//				System.out.println((char)currentCharacter);
				if(checkEndTag((char)currentCharacter,buffer)){
					
					break;
				}
				
				if((char)currentCharacter == '='){
					buffer.append((char)currentCharacter);
					break;
				}
				buffer.append((char)currentCharacter);
				//System.out.println(currentCharacter);
			}while((currentCharacter = reader.read()) != -1);
			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		buffer.append("</span>");
		
		htmlDiffGenerator.endSpan(HTMLTypes.attributeName, buffer.toString());
		return currentCharacter;
	}
	
	
	
	
	private int addAtributeValue(Reader reader, int currentCharacter){
		htmlDiffGenerator.startSpan(HTMLTypes.attributeValue, "");
		
		StringBuilder buffer = new StringBuilder();
		try {
			
			
			do{

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
		
		htmlDiffGenerator.endSpan(HTMLTypes.attributeValue, buffer.toString());
		
		return currentCharacter;
	}
	
	
	
	
	
	
	/**
	 * After adding the Element I check if the next character signals an
	 * attribute and I add the specific tags to it
	 * @param index
	 * @return
	 */
	private int addAttribute(Reader reader, int currentCharacter){
		
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
	private int addText(Reader reader){
		
		
		StringBuilder buffer = new StringBuilder();
		int currentCharacter  = 0;
		try {
			
			currentTag = BeginTag.UNPROCESSED;

			String copyOfWhiteSpaces = "";
			while((currentCharacter = reader.read()) != -1){

				if(checkBeginTag((char)currentCharacter)){
					return currentCharacter;
				}
				if(checkForTabsNewLinesOrWhiteSpaces((char)currentCharacter)){
					currentTag = BeginTag.PROCESSING;
					copyOfWhiteSpaces += (char)currentCharacter;
				}else{
					currentTag = BeginTag.PROCESSING;
					break;
				}
			}
			
			if(currentTag == BeginTag.UNPROCESSED || currentCharacter == -1){ /*This is used to check if white spaces are used 
			 														or if we have tabs new lines at the end of a file*/
//				System.out.println(beginTag);
				return -1;
			}
			
			
			htmlDiffGenerator.startSpan(HTMLTypes.textField, copyOfWhiteSpaces);

			
			
			
			do{
				if(checkBeginTag((char)currentCharacter)){
					
					break;
				}
				buffer.append((char)currentCharacter);
			}while((currentCharacter = reader.read()) != -1);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		buffer.append("</span>");
		
		htmlDiffGenerator.endSpan(HTMLTypes.Element, buffer.toString());
		
		return currentCharacter;
	}
	
	
	
	

	/**
	 * Takes the Comment section and parses it
	 * @param reader
	 * @param currentCharacter
	 * @return
	 */
	private int addComment(Reader reader){
		
		int currentCharacter  = 0;
		htmlDiffGenerator.startSpan(HTMLTypes.Comment, "&lt;!--");
		
		
		StringBuilder buffer = new StringBuilder();
		
		//skipFirstWhiteSpaces(index);
		try{
			
			while((currentCharacter = reader.read()) != -1){
				if(checkEndTag((char)currentCharacter,buffer)){
					break;
				}
				buffer.append((char)currentCharacter);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		buffer.append("</span>");
		
		htmlDiffGenerator.endSpan(HTMLTypes.Comment, buffer.toString());
		
		return currentCharacter;
		
	}
	
	
	
	
	
	/**
	 * Takes the Processing Information section and parses it
	 * @param reader
	 * @param currentCharacter
	 * @return
	 */
	private int addProcessingInformation(Reader reader){
		
		htmlDiffGenerator.startSpan(HTMLTypes.PI, "&lt;?");
		
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
		
		htmlDiffGenerator.endSpan(HTMLTypes.PI, buffer.toString());
		
		return currentCharacter;
		
	}
	
	
	
	
	
	/**
	 * Parses through the text and counts every tag excepting the
	 * ones that are between commas, those are not dealt with
	 * @param reader
	 * @param currentCharacter
	 * @return
	 */
	private int addDoctype(Reader reader){
		
		htmlDiffGenerator.startSpan(HTMLTypes.Doctype, "&lt;!DOCTYPE");
		
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
		
		htmlDiffGenerator.endSpan(HTMLTypes.PI, buffer.toString());
		return currentCharacter;
	}
	
	
	
	
	
	
	/**
	 * Parses through the text and closes it ONLY if it stumbles upon
	 * "]]>" tag
	 * @param reader
	 * @param currentCharacter
	 * @return
	 */
	private int addCdata(Reader reader){
		
		htmlDiffGenerator.startSpan(HTMLTypes.CDATA, "&lt;![CDATA[");
		
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
		
		
		htmlDiffGenerator.endSpan(HTMLTypes.PI, buffer.toString());
		
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
			BufferedReader buff = new BufferedReader(in);
			XMLParser parser = new XMLParser((Reader) buff, null);
			parser.parseInputIntoHTMLFormat();
			System.out.println(parser.getResultedText());
	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	

	
}
