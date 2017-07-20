package com.oxygenxml.sdksamples.workspace;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import de.schlichtherle.io.archive.zip.raes.ParanoidZip32RaesDriver;

public class XMLParser {

	private Reader reader;
	private BufferedReader buff;
    private String resultedText;
    private String characterResidue;
//    private String str = "<house> </house >"; 
//    private String str2 = "<house color=\"green\">---i would like to say something here---</house>\n"
//    						+"<house color=\"blue\">+++++++</house>";
//	private String str1 = "<block-list:block-list xmlns:block-list=\"http://openoffice.org/2001/block-list\">\n"+
//    "<block-list:block block-list:abbreviated-name=\"--\" block-list:name=\"&#x2013;\"/>\n"+
//	"<block-list:block block-list:abbreviated-name=\"--\" block-list:name=\"bb\"/>\n"
//+"</block-list:block-list>";
    
	private final short UNPROCESSED = 1;
	private final short PROCESSING = 2;
	private final short DEALTWITH = 3;
	private final short CLOSINGTAG = 4;
	private final short DOCTYPE = 5;
	private final short COMMENT = 6;
	private final short CDATA = 7;
	private final short PROCESSING_INSTRUCTION = 8;
	private short beginTag ;
	
	
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
		//System.out.println(resultedString);
		return null;
	}
	
	/**
	 * Depending on what symbols comes next, this method decides
	 * what type of class the tag has
	 * @param reader
	 * @param currentCharacter
	 * @return
	 */
	private char setBeginTag(Reader reader, char currentCharacter){
		try {
			characterResidue = null;
			currentCharacter = (char)reader.read();
			if(currentCharacter == '/'){
				beginTag = CLOSINGTAG;
				return currentCharacter;
			}else if(currentCharacter != '!'){
				
				if(currentCharacter != '?'){
					beginTag = PROCESSING;
					return currentCharacter;
					
				}else{
					beginTag = PROCESSING_INSTRUCTION;
					
				}
			}else{
				currentCharacter = (char)reader.read();
				if(currentCharacter == 'D'){
					
					characterResidue = checkTwoWords(reader, "DOCTYPE");
					
					
					if(characterResidue == null){
						beginTag = DOCTYPE;
					}else {
						characterResidue = "!D" + characterResidue;
						beginTag = PROCESSING;
					}
				}else if(currentCharacter == '['){
					
					characterResidue = checkTwoWords(reader, "[CDATA[");
					if(characterResidue == null){
						beginTag = CDATA;
						characterResidue = "![" + characterResidue;
					}else{
						beginTag = PROCESSING;
					}
					
				}else if(currentCharacter == '-'){
									
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
			String text ="";
			int currentCharacter = reader.read();
			do {
				//				if(currentCharacter == '\n' || currentCharacter == '\t' || currentCharacter == '\r')
				//System.out.print("WARN ME");
				if (currentCharacter == '<'){

					currentCharacter = setBeginTag(reader, currentCharacter);

				} else if(currentCharacter == '>'){
					beginTag = DEALTWITH;
					//					resultedText+= "&gt;";
				}

				if (beginTag == PROCESSING){
					currentCharacter = addElement(reader, currentCharacter);

				} else if (beginTag == CLOSINGTAG){
					currentCharacter = addClosingElement(reader, currentCharacter);

				} else if (beginTag == COMMENT){
					currentCharacter = addComment(reader, currentCharacter);

				} else if (beginTag == PROCESSING_INSTRUCTION){
					currentCharacter = addProcessingInformation(reader, currentCharacter);

				} else if (beginTag == DOCTYPE){
					currentCharacter = addDoctype(reader, currentCharacter);

				} else if (beginTag == CDATA){
					currentCharacter = addCdata(reader, currentCharacter);

				}


				if (beginTag == PROCESSING){
					currentCharacter = addAttribute(reader, currentCharacter);
				} else if(beginTag == DEALTWITH){
					currentCharacter = addText(reader, currentCharacter);
				}

//				System.out.println("Ch: '" + (((int)currentCharacter)) +"'");
//				System.out.println("final: " + resultedText);
			} while(reader.ready() && ((int)currentCharacter != -1));
			
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
	private char addClosingElement(Reader reader, char currentCharacter){
		resultedText += "<span class = \"CloseElement\">&lt;";
		int i=0;

		try {
			boolean firstInit = false;
			do{
				if(firstInit){
					currentCharacter = (char)i;
				}
				firstInit = true;
				if(checkEndTag(currentCharacter)){
					beginTag = UNPROCESSED;
					break;
				}
				
				resultedText += currentCharacter;
			}while((i = reader.read()) != -1);
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
	private char addElement(Reader reader, char currentCharacter){
		
		resultedText += "<span class = \"Element\">&lt;";
//		if(characterResidue != null){
//			System.out.println(characterResidue);
//			resultedText += characterResidue;
//		}
		
		
		
		boolean condition = false;
		int i=0;

		/*
		 * If I get a white space, new line or tab, I register it then
		 * check every time to see if the next character is indeed a character
		 * so I can close the <span> tag */
		try {
			boolean firstInit = false;
			do{
				
				if(firstInit){
					currentCharacter = (char)i;
				}else{
					if(characterResidue != null){
					//	System.out.println(characterResidue + " " + (char)reader.read());
						currentCharacter = (char)reader.read();
						resultedText += characterResidue;
					}
				}
				firstInit = true;
				if(checkEndTag(currentCharacter)){
					
					break;
				}
				if(checkForTabsNewLinesOrWhiteSpaces(currentCharacter)){
					
					condition = true;
				}else if(condition && !checkForTabsNewLinesOrWhiteSpaces(currentCharacter)){
					
					break;
				}
				resultedText += currentCharacter;
			}while((i = reader.read()) != -1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resultedText += "</span>";
		//System.out.println("\n" + currentCharacter + "\n");
		return currentCharacter;
	}
	
	
	
	
	/**
	 * After adding the Element I check if the next character signals an
	 * attribute and I add the specific tags to it
	 * @param index
	 * @return
	 */
	private char addAttribute(Reader reader, char currentCharacter){
		resultedText += "<span class = \"attributeName\">";
		//skipFirstWhiteSpaces(index);
		/*
		 * If I get an EQUAL "=", if the next character is indeed a character
		 * I can close the <span> tag */
		int i = 0;
		//System.out.println(currentCharacter);
		try {
			boolean firstInit = false;
			do{
				if(firstInit){
					currentCharacter = (char)i;
				}
				firstInit = true;
				if(checkEndTag(currentCharacter)){
					
					break;
				}
				
				if(currentCharacter == '='){
					resultedText += currentCharacter;
					break;
				}
				resultedText += currentCharacter;
				//System.out.println(currentCharacter);
			}while((i = reader.read()) != -1);
			resultedText += "</span>";
			
			
			resultedText += "<span class = \"attributeValue\">";
			while((i = reader.read()) != -1){
				currentCharacter = (char)i;
				if(checkEndTagWithNoWriting(currentCharacter)){
					break;
				}
				
				resultedText += currentCharacter;
			}
			resultedText += "</span>&gt;";
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
	private char addText(Reader reader, char currentCharacter){
		try {
			int i = 0;
			
			String copyOfWhiteSpaces = "";
			while((i = reader.read()) != -1){
				currentCharacter = (char)i;
				if(checkBeginTag(currentCharacter)){
					return currentCharacter;
				}
				if(checkForTabsNewLinesOrWhiteSpaces(currentCharacter)){
					copyOfWhiteSpaces += currentCharacter;
				}else{
					break;
				}
			}
		
		
			resultedText += "<span class = \"textField\">";
			resultedText += copyOfWhiteSpaces;
		
			do{
				currentCharacter = (char)i;
				if(checkBeginTag(currentCharacter)){
					//System.out.println("    asf     " + currentCharacter);
					break;
				}
				resultedText += currentCharacter;
			}while((i = reader.read()) != -1);
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
	private char addComment(Reader reader, char currentCharacter){
		
		resultedText += "<span class = \"Comment\">&lt;!--";
		//skipFirstWhiteSpaces(index);
		try{
			int i=0;
			while((i = reader.read()) != -1){
				currentCharacter = (char)i;
				if(checkEndTag(currentCharacter)){
					break;
				}
				resultedText += currentCharacter;
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
	private char addProcessingInformation(Reader reader, char currentCharacter){
		
		resultedText += "<span class = \"Processing Information\">&lt;?";
		
		try{
			boolean commasCounter = true; /*there is the possibility that a tag may appear 
				inside parentheses, in which case it shall not be counted*/
			int i=0;
			while((i = reader.read()) != -1){
				currentCharacter = (char)i;
				if(currentCharacter == '(' || currentCharacter == '[' || currentCharacter == '{'){
					commasCounter = !commasCounter;
				}
				if(currentCharacter == ')' || currentCharacter == ']' || currentCharacter == '}'){
					commasCounter = !commasCounter;
				}
				
				if(currentCharacter == '?'){
					
					if(commasCounter){
					
						char nextCharacter = (char) reader.read();
						
						if(checkEndTagWithNoWriting(nextCharacter)){
							resultedText += "?&gt;";
							break;
						}else{
							resultedText += currentCharacter + "" + nextCharacter;
						}
					}else{
						resultedText += currentCharacter;
					}
				}else{
					resultedText += currentCharacter;
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
	private char addDoctype(Reader reader, char currentCharacter){
		
		resultedText += "<span class = \"Doctype\">&lt;!DOCTYPE";
		
		
		try{
			int closingTagCounter = 1 ; /* I am counting how many times I
			 						stumble across "<>" pair in order
			 						to be able to figure out which one
			 						is the closing tag.*/
			boolean commasCounter = true; /*there is the possibility that a tag may appear 
			 						inside commas, in which case it shall not be counted*/
			int i=0;
			while((i = reader.read()) != -1){
			
				currentCharacter = (char)i;
				
				if(currentCharacter == '\"' || currentCharacter == '\"' ){
					commasCounter = !commasCounter;
				}
				
				if(currentCharacter == '<' && commasCounter){
					closingTagCounter++;
				}
				if(currentCharacter == '>' && commasCounter){
					closingTagCounter--;
				}
				if(currentCharacter == '>' && closingTagCounter == 0 && commasCounter){
					resultedText += "&gt;";
					break;
				}
				
				resultedText += currentCharacter;
				
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
	private char addCdata(Reader reader, char currentCharacter){
		
		resultedText += "<span class = \"CDATA\">&lt;![CDATA[";
		
		try{
			int i=0;
			while((i = reader.read()) != -1){
				
				currentCharacter = (char)i;
				if(currentCharacter == ']'){
					char auxiliarCharacter1 = (char) reader.read();
					if(auxiliarCharacter1 == ']'){
						char auxiliarCharacter2 = (char) reader.read();
						if(checkEndTagWithNoWriting(auxiliarCharacter2)){
							resultedText += "]]&gt;";
							break;
						}else{
							resultedText += currentCharacter + ""+ auxiliarCharacter1 + ""+ auxiliarCharacter2 ;
						}
					}else{
						resultedText += currentCharacter + ""+ auxiliarCharacter1;
					}
				}else{
				
					resultedText += currentCharacter;
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
			resultedText += "&gt;";
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
	
	
	
	
	
	
	
//	private void parseFormat(){
//
//		beginTag = UNPROCESSED;
//		try {
//			char currentCharacter = (char)buff.read();
//			do{
////				if(currentCharacter == '\n' || currentCharacter == '\t' || currentCharacter == '\r')
//					//System.out.print("WARN ME");
//				if(currentCharacter == '<'){
//
//					currentCharacter = setBeginTag(buff, currentCharacter);
//		
//				}else if(currentCharacter == '>'){
//					beginTag = DEALTWITH;
////					resultedText+= "&gt;";
//				}
//				
//			if(beginTag == PROCESSING){
//				currentCharacter = addElement(buff, currentCharacter);
//				
//			}else if(beginTag == CLOSINGTAG){
//				currentCharacter = addClosingElement(buff, currentCharacter);
//				
//			}else if(beginTag == COMMENT){
//				currentCharacter = addComment(buff, currentCharacter);
//				
//			}else if(beginTag == PROCESSING_INSTRUCTION){
//				currentCharacter = addProcessingInformation(buff, currentCharacter);
//				
//			}else if(beginTag == DOCTYPE){
//				currentCharacter = addDoctype(buff, currentCharacter);
//				
//			}else if(beginTag == CDATA){
//				currentCharacter = addCdata(buff, currentCharacter);
//				
//			}
//			
//			
//			if(beginTag == PROCESSING){
//				currentCharacter = addAttribute(buff, currentCharacter);
//			}else if(beginTag == DEALTWITH){
//				currentCharacter = addText(buff, currentCharacter);
//			}
//
//			}while(buff.ready());
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}
	
	

	
}
