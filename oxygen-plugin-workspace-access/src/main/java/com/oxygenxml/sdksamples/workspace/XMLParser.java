package com.oxygenxml.sdksamples.workspace;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;



public class XMLParser {

	private Reader reader;
    private String resultedText;
    private String characterResidue;
    
    private short beginTag ;
	private final short UNPROCESSED = 1;
	private final short PROCESSING = 2;
	private final short DEALTWITH = 3;
	private final short CLOSINGTAG = 4;
	private final short DOCTYPE = 5;
	private final short COMMENT = 6;
	private final short CDATA = 7;
	private final short PROCESSING_INSTRUCTION = 8;
	
	
//    public XMLParser(BufferedReader buff) {
////    	this((Reader)buff);
//    	this.buff = buff;
//    	this.resultedText = "";
//	}
	XMLParser(Reader reader){
		this.reader = reader;
		this.resultedText = "";
	}
	
	public String getResultedText(){
		return resultedText;
	}
	
	/**
	 * Compares two strings character by character to the point those two
	 * are not alike anymore and remembers the common part in a String
	 * @param reader reading character by character
	 * @param comparedToThis the string we compare it to
	 * @return the common part of the string
	 */
	private String checkTwoWords(Reader reader, String comparedToThis){
		String resultedString = "";
		try{
			int index = 1;
			while(reader.ready() && index < comparedToThis.length()){
				char currentCharacter = (char) reader.read();
				if(currentCharacter != comparedToThis.charAt(index++)){
					return (resultedString + currentCharacter);
				}
				resultedString += currentCharacter;
			}
		}catch(IOException e){
			e.printStackTrace();
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
				beginTag = CLOSINGTAG;
				return (char)currentCharacter;
			}else if((char)currentCharacter != '!'){
				
				if((char)currentCharacter != '?'){
					beginTag = PROCESSING;
					return (char)currentCharacter;
					
				}else{
					beginTag = PROCESSING_INSTRUCTION;
					
				}
			}else{
				currentCharacter = reader.read();
				if((char)currentCharacter == 'D'){
					
					characterResidue = checkTwoWords(reader, "DOCTYPE");
					
					
					if(characterResidue == null){
						beginTag = DOCTYPE;
					}else {
						characterResidue = "!D" + characterResidue;
						beginTag = PROCESSING;
					}
				}else if((char)currentCharacter == '['){
					
					characterResidue = checkTwoWords(reader, "[CDATA[");
					if(characterResidue == null){
						beginTag = CDATA;
						characterResidue = "![" + characterResidue;
					}else{
						beginTag = PROCESSING;
					}
					
				}else if((char)currentCharacter == '-'){
									
					characterResidue = checkTwoWords(reader, "--");
					
					if(characterResidue == null){
						beginTag = COMMENT;
						characterResidue = "!-" + characterResidue;
					}else{
						beginTag = PROCESSING;
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

		beginTag = UNPROCESSED;
		try {

			int currentCharacter = reader.read();
			do {
				
			//	System.out.print("before: " + beginTag);
				if ((char)currentCharacter == '<'){

					currentCharacter = setBeginTag(reader, currentCharacter);

				} else if((char)currentCharacter == '>'){
					//if(beginTag != CLOSINGTAG)
						beginTag = DEALTWITH;
				}

			//	System.out.print(" middle: " + beginTag);
				
				if (beginTag == PROCESSING){
					currentCharacter = addElement(reader, currentCharacter);

				} else if (beginTag == CLOSINGTAG){
					currentCharacter = addClosingElement(reader, currentCharacter);
					//System.out.println(beginTag);

				} else if (beginTag == COMMENT){
					currentCharacter = addComment(reader);

				} else if (beginTag == PROCESSING_INSTRUCTION){
					currentCharacter = addProcessingInformation(reader);

				} else if (beginTag == DOCTYPE){
					currentCharacter = addDoctype(reader);

				} else if (beginTag == CDATA){
					currentCharacter = addCdata(reader);

				}

		//		System.out.println(" after: " + beginTag);
				
				if (beginTag == PROCESSING){
					currentCharacter = addAttribute(reader, currentCharacter);
				}
				if(beginTag == DEALTWITH){
//					System.out.println(beginTag);
					currentCharacter = addText(reader);
				}
				
				
//				System.out.println("Ch: '" + (((int)currentCharacter)) +"'");
//				System.out.println("final: " + resultedText);
			} while(reader.ready() && (currentCharacter != -1));
			
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
			resultedText+=character;
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
		resultedText += "<span class = \"Element\">&lt;";


		try {

			do{
				
				if(checkEndTag((char)currentCharacter)){
//					beginTag = UNPROCESSED;
					break;
				}
				
				resultedText += (char)currentCharacter;
				
			}while((currentCharacter = reader.read()) != -1);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resultedText += "</span>";
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
		
		resultedText += "<span class = \"Element\">&lt;";
//		if(characterResidue != null){
//			System.out.println(characterResidue);
//			resultedText += characterResidue;
//		}
		
		
		
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
						resultedText += characterResidue;
					}
				}
				firstInit = true;
				if(checkEndTag((char)currentCharacter)){
					
					break;
				}
				if(checkForTabsNewLinesOrWhiteSpaces((char)currentCharacter)){
					
					condition = true;
				}else if(condition && !checkForTabsNewLinesOrWhiteSpaces((char)currentCharacter)){
					
					break;
				}
				resultedText += (char)currentCharacter;
			}while((currentCharacter = reader.read()) != -1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resultedText += "</span>";
//		System.out.println( (char)currentCharacter);
		return currentCharacter;
	}
	
	
	
	
	/**
	 * After adding the Element I check if the next character signals an
	 * attribute and I add the specific tags to it
	 * @param index
	 * @return
	 */
	private int addAttribute(Reader reader, int currentCharacter){
		resultedText += "<span class = \"attributeName\">";
		

		/*
		 * If I get an EQUAL "=", if the next character is indeed a character
		 * I can close the <span> tag */

		try {
			do{
//				System.out.println((char)currentCharacter);
				if(checkEndTag((char)currentCharacter)){
					
					break;
				}
				
				if((char)currentCharacter == '='){
					resultedText += (char)currentCharacter;
					break;
				}
				resultedText += (char)currentCharacter;
				//System.out.println(currentCharacter);
			}while((currentCharacter = reader.read()) != -1);
			
			resultedText += "</span>";
			
			
			resultedText += "<span class = \"attributeValue\">";
			
			do{

				if(checkEndTagWithNoWriting((char)currentCharacter)){
					break;
				}
				
				resultedText += (char)currentCharacter;
			}while((currentCharacter = reader.read()) != -1);
			
			
			resultedText += "</span><span class = \"Element\">&gt;</span>";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
		int currentCharacter  = 0;
		try {
			
			beginTag = UNPROCESSED;

			String copyOfWhiteSpaces = "";
			while((currentCharacter = reader.read()) != -1){

				if(checkBeginTag((char)currentCharacter)){
					return currentCharacter;
				}
				if(checkForTabsNewLinesOrWhiteSpaces((char)currentCharacter)){
					beginTag = PROCESSING;
					copyOfWhiteSpaces += (char)currentCharacter;
				}else{
					beginTag = PROCESSING;
					break;
				}
			}
			
			if(beginTag == UNPROCESSED || currentCharacter == -1){ /*This is used to check if white spaces are used 
			 														or if we have tabs new lines at the end of a file*/
//				System.out.println(beginTag);
				return -1;
			}
			
			
			resultedText += "<span class = \"textField\">";
			resultedText += copyOfWhiteSpaces;
		
			do{
			//	System.out.println((char)currentCharacter);
				if(checkBeginTag((char)currentCharacter)){
					
					break;
				}
				resultedText += (char)currentCharacter;
			}while((currentCharacter = reader.read()) != -1);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		resultedText += "</span>";
		
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
		resultedText += "<span class = \"Comment\">&lt;!--";
		//skipFirstWhiteSpaces(index);
		try{
			
			while((currentCharacter = reader.read()) != -1){
				if(checkEndTag((char)currentCharacter)){
					break;
				}
				resultedText += (char)currentCharacter;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		resultedText += "</span>";
		
		return currentCharacter;
		
	}
	
	
	
	
	
	/**
	 * Takes the Processing Information section and parses it
	 * @param reader
	 * @param currentCharacter
	 * @return
	 */
	private int addProcessingInformation(Reader reader){
		
		resultedText += "<span class = \"PI\">&lt;?";
		int currentCharacter  = 0;
		
		try{
			boolean commasCounter = true; /*there is the possibility that a tag may appear 
				inside parentheses, in which case it shall not be counted*/

			while((currentCharacter = reader.read()) != -1){
				if((char)currentCharacter == '(' || (char)currentCharacter == '[' || (char)currentCharacter == '{'){
					commasCounter = !commasCounter;
				}
				if((char)currentCharacter == ')' || (char)currentCharacter == ']' || (char)currentCharacter == '}'){
					commasCounter = !commasCounter;
				}
				
				if((char)currentCharacter == '?'){
					
					if(commasCounter){
					
						char nextCharacter = (char) reader.read();
						
						if(checkEndTagWithNoWriting(nextCharacter)){
							resultedText += "?<span class = \"Element\">&gt;</span>";
							break;
						}else{
							resultedText += (char)currentCharacter + "" + nextCharacter;
						}
					}else{
						resultedText += (char)currentCharacter;
					}
				}else{
					resultedText += (char)currentCharacter;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		resultedText += "</span>";
		
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
		
		resultedText += "<span class = \"Doctype\">&lt;!DOCTYPE";
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
					resultedText += "<span class = \"Element\">&gt;</span>";
					break;
				}
				
				resultedText += (char)currentCharacter;
				
			}
			
//			System.out.println(currentCharacter);
			
			resultedText += "</span>";
			
		}catch (IOException e) {
			e.printStackTrace();
		}
		
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
		
		resultedText += "<span class = \"CDATA\">&lt;![CDATA[";
		int currentCharacter  = 0;
		
		try{
			
			
			while((currentCharacter = reader.read()) != -1){
				
				if((char)currentCharacter == ']'){
					char auxiliarCharacter1 = (char) reader.read();
					if(auxiliarCharacter1 == ']'){
						char auxiliarCharacter2 = (char) reader.read();
						if(checkEndTagWithNoWriting(auxiliarCharacter2)){
							resultedText += "]]<span class = \"Element\">&gt;</span>";
							break;
						}else{
							resultedText += (char)currentCharacter + ""+ auxiliarCharacter1 + ""+ auxiliarCharacter2 ;
						}
					}else{
						resultedText += (char)currentCharacter + ""+ auxiliarCharacter1;
					}
				}else{
				
					resultedText += (char)currentCharacter;
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return currentCharacter;
	}
	
	
	
	
	/**
	 * Checks if at the current index in the text we have a begin "<" tag or not
	 * @param character
	 * @return
	 */
	private boolean checkBeginTag(char character){
		if(character == '<'){
			beginTag = DEALTWITH;
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
	private boolean checkEndTag(char character){
		if(character == '>'){
			beginTag = DEALTWITH;
			resultedText += "<span class = \"Element\">&gt;</span>";
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
			beginTag = DEALTWITH;
			return true;
		}
		return false;
	}
	
	
	
	
	
	public static void main(String... args) {
		try {
			FileReader in = new FileReader("html.in");
			BufferedReader buff = new BufferedReader(in);
			XMLParser parser = new XMLParser((Reader) buff);
			parser.parseInputIntoHTMLFormat();
			System.out.println(parser.getResultedText());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
//	public void doMyDeed(){
//		try {
//			int i;
//			while((i = reader.read()) != -1){
//				System.out.print((char)i);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	

	
}
