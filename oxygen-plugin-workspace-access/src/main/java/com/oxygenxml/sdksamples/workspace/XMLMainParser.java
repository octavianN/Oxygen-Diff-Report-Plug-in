package com.oxygenxml.sdksamples.workspace;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;



public class XMLMainParser {
	
	class ReaderWithIndex extends Reader{
		
		private int index;
		private Reader innerReader;

		public int getIndex(){
			return index;
		}
		
		public ReaderWithIndex(Reader innerReader) {
			this.innerReader = innerReader;
			this.index = -1;
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

	
	
	private ContentListener contentListener;
	StringBuilder resultToCheckIfItReadsCorrectly;
	
	
	XMLMainParser(){
		resultToCheckIfItReadsCorrectly = new StringBuilder();
	}
	
	public void setContentListener(ContentListener contentListener) {
		this.contentListener = contentListener;
	}
	
	
	public void parseInputIntoHTMLFormat(Reader read) {
		int lastCharacter = Integer.MIN_VALUE;
		try {
			ReaderWithIndex reader = new ReaderWithIndex(read);
			ParseElement parser = new ParseElement();
			parser.setContentListener(contentListener);

			do {
				CurrentReadElement currentElement = new CurrentReadElement();
				
				lastCharacter = readTag(currentElement, reader, lastCharacter);

				parser.setCurrentElement(currentElement);
				parser.parse();
				

				 resultToCheckIfItReadsCorrectly.append(currentElement.beginOffset+ " ");
				 resultToCheckIfItReadsCorrectly.append(currentElement.endOffset);
				resultToCheckIfItReadsCorrectly.append(currentElement.elementContent.toString());
//				resultToCheckIfItReadsCorrectly.append(currentElement.type + " " );
//				resultToCheckIfItReadsCorrectly.append(currentElement.isElementAndHasAttribute + " " );

			} while (lastCharacter != -1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private int readTag(CurrentReadElement currentElement, ReaderWithIndex reader, int currentCharacter) throws IOException {
		
		if (currentCharacter == Integer.MIN_VALUE) {
			currentCharacter = reader.read();
		}
		
		
		if (currentCharacter == '<') {
			currentElement.elementContent.append((char) currentCharacter);
			currentElement.beginOffset = reader.getIndex();
			currentCharacter = readElementWithBeginTag(currentElement, reader);
		} else {
			currentElement.beginOffset = reader.getIndex()+1;
			currentCharacter = readElementWithEndTag(currentElement, reader);  //TODO Sa mai adaug un 1 la offset de inceput?
		}

		return currentCharacter;
		
	}
	
	/**
	 * primul caracter citit va fi fie "<" fie ">"
	 * @param currentElement
	 * @param reader
	 * @return
	 */
	
	private String checkTwoWords(ReaderWithIndex reader, String comparedToThis) throws IOException{
		
		StringBuilder buffer = new StringBuilder();
		int currentCharacter = 0;
		
		int index = 1;
		while(index < comparedToThis.length() && (currentCharacter = reader.read()) != -1){
			
			if((char)currentCharacter != comparedToThis.charAt(index++)){
				return (buffer.toString() + (char)currentCharacter);
			}
			buffer.append(currentCharacter);
		}
	

		return null;
	}

	
	
	
	
	private int findNodeType (CurrentReadElement currentElement, ReaderWithIndex reader) throws IOException{
		int currentCharacter = 0;
		NodeType currentTag = null;

		currentCharacter = reader.read();

		if ((char) currentCharacter == '/') {
			currentTag = NodeType.ELEMENT_CLOSE;
			currentElement.elementContent.append("/");

		} else if ((char) currentCharacter != '!') {

			if ((char) currentCharacter != '?') {
				currentTag = NodeType.ELEMENT;
				currentElement.elementContent.append((char) currentCharacter);

			} else {
				currentTag = NodeType.PI;
				currentElement.elementContent.append("?");

			}
		} else {
			currentCharacter = reader.read();

			if ((char) currentCharacter == 'D') {

				String charactersInTheWordSoFar = checkTwoWords(reader, "DOCTYPE");

				if (charactersInTheWordSoFar == null) {
					currentElement.elementContent.append("DOCTYPE");
					currentTag = NodeType.DOCTYPE;
				} else {
					currentElement.elementContent.append("D" + charactersInTheWordSoFar);
					currentTag = NodeType.ELEMENT;
				}

			} else if ((char) currentCharacter == '[') {

				String charactersInTheWordSoFar = checkTwoWords(reader, "[CDATA[");

				if (charactersInTheWordSoFar == null) {
					currentTag = NodeType.CDATA;
					currentElement.elementContent.append("[CDATA[");
				} else {
					currentElement.elementContent.append("[" + charactersInTheWordSoFar);
					currentTag = NodeType.ELEMENT;
				}

			} else if ((char) currentCharacter == '-') {

				String charactersInTheWordSoFar = checkTwoWords(reader, "--");

				if (charactersInTheWordSoFar == null) {
					currentTag = NodeType.COMMENT;
					currentElement.elementContent.append("--");
				} else {
					currentElement.elementContent.append("-" + charactersInTheWordSoFar);
					currentTag = NodeType.ELEMENT;
				}
			}
		}

		currentElement.type = currentTag;
			
		
		return currentCharacter;
	}

	
	
	
	
	
	
	private int readElementWithEndTag(CurrentReadElement currentElement, ReaderWithIndex reader) throws IOException{
		int currentCharacter = 0;
		boolean mayHaveAttribute = false;
		
		currentElement.type = NodeType.EMPTYDATA;
		
		while((currentCharacter = reader.read()) != -1){
			
			if((char)currentCharacter == '\r'){
				reader.index--;
			}
			
			if(currentCharacter == '<'){
				break;
			}
			
//			if((char)currentCharacter == '\r'){System.out.println("WARRNING!!!  "+ reader.getIndex());}
//			if((char)currentCharacter == '\n'){System.out.println("WARRNING!!!  "+ reader.getIndex());}
			
			if(checkForTabsNewLinesOrWhiteSpaces((char)currentCharacter)){
				mayHaveAttribute = true;
			}else if(mayHaveAttribute){
				currentElement.type = NodeType.TEXTFIELD;
			}
			if((char)currentCharacter != '\r'){
				currentElement.elementContent.append((char)currentCharacter);
			}
			
		}
		
		currentElement.endOffset = reader.getIndex()-1;
		return currentCharacter;
	}

	
	
	
	
	private int readElementWithBeginTag(CurrentReadElement currentElement, ReaderWithIndex reader) throws IOException{
			
		int currentCharacter = findNodeType(currentElement, reader);
			
		switch (currentElement.type) {
		case ELEMENT:	
			currentCharacter = addElement(currentElement, reader);
			break;
		case ELEMENT_CLOSE:
			currentCharacter = addClosingElement(currentElement, reader);
			break;
		case PI:
			currentCharacter = addProcessingInformation(currentElement, reader);
			break;
		case DOCTYPE:
			currentCharacter = addDoctype(currentElement, reader);
			break;
		case CDATA:
			currentCharacter = addCdata(currentElement, reader);
			break;
		case COMMENT:
			currentCharacter = addComment(currentElement, reader);
			break;
		default:
			break;
		}
		
		currentElement.endOffset = reader.getIndex();
		
		return currentCharacter;
	}
	
	
	
	/**
	 * Checks if the given character is a white space, a new line or a tab and
	 * returns the answer;
	 * @param character
	 * @return
	 */
	private boolean checkForTabsNewLinesOrWhiteSpaces(char character){

		if(character == ' ' || character == '\n' || character == '\t' || character == '\r'){
			return true;
		}
		return false;
	}
	
	
	
	private int addElement(CurrentReadElement currentElement, ReaderWithIndex reader) throws IOException{
		
		int currentCharacter = 0;
		boolean mayHaveAttribute = false;
		
		while((currentCharacter = reader.read()) != -1){
			
			if(currentCharacter == '>'){
				currentElement.elementContent.append((char)currentCharacter);
				break;
			}
			
			if(checkForTabsNewLinesOrWhiteSpaces((char)currentCharacter)){
				mayHaveAttribute = true;
			}else if(mayHaveAttribute){
				currentElement.isElementAndHasAttribute = true;
			}
			
			currentElement.elementContent.append((char)currentCharacter);
		}
		
		
		return currentCharacter;
	}
	
	
	
	
	
	
	private int addClosingElement(CurrentReadElement currentElement, ReaderWithIndex reader) throws IOException{
		
		int currentCharacter = 0;
		
		
		while((currentCharacter = reader.read()) != -1){
			
			if(currentCharacter == '>'){
				currentElement.elementContent.append((char)currentCharacter);
				break;
			}
			
			currentElement.elementContent.append((char)currentCharacter);
			
		}
		
		
		return currentCharacter;
	}
	
	
	
	
	
	private int addComment(CurrentReadElement currentElement, ReaderWithIndex reader) throws IOException{
		
		int currentCharacter = 0;

		
		while((currentCharacter = reader.read()) != -1){
			
			if(currentCharacter == '>'){
				currentElement.elementContent.append((char)currentCharacter);
				break;
			}
			
			currentElement.elementContent.append((char)currentCharacter);
			
		}
		
		
		return currentCharacter;
		
	}
	
	
	
	
	
	
	private int addProcessingInformation(CurrentReadElement currentElement, ReaderWithIndex reader) throws IOException{
				

		int currentCharacter = 0;

		while ((currentCharacter = reader.read()) != -1) {

			if ((char) currentCharacter == '?') {

				char nextCharacter = (char) reader.read();

				if ((char) nextCharacter == '>') {
					currentElement.elementContent.append("?>");
					break;
				} else {
					currentElement.elementContent.append((char) currentCharacter + "" + nextCharacter);
				}

			} else {
				currentElement.elementContent.append((char) currentCharacter);
			}
		}

		return currentCharacter;
		
	}
	
	
	
	
	
	
	private int addCdata(CurrentReadElement currentElement, ReaderWithIndex reader) throws IOException{
			

		int currentCharacter = 0;

		while ((currentCharacter = reader.read()) != -1) {
			
			if ((char) currentCharacter == ']') {
				
				char auxiliarCharacter1 = (char) reader.read();
				if (auxiliarCharacter1 == ']') {
					
					char auxiliarCharacter2 = (char) reader.read();
					if (auxiliarCharacter2 == '>') {
						
						currentElement.elementContent.append("]]>");
						break;
					} else {
						currentElement.elementContent
								.append((char) currentCharacter + "" + auxiliarCharacter1 + "" + auxiliarCharacter2);
					}
				} else {
					currentElement.elementContent.append((char) currentCharacter + "" + auxiliarCharacter1);
				}
			} else {

				currentElement.elementContent.append((char) currentCharacter);
			}
		}

		return currentCharacter;
	}
	
	
	
	
	
	
	
	private int addDoctype(CurrentReadElement currentElement, ReaderWithIndex reader) throws IOException{
				
		int currentCharacter = 0;

		boolean closedCounter = true; /*
										 * there is the possibility that a tag
										 * may appear inside commas, in which
										 * case it shall not be counted
										 */

		while ((currentCharacter = reader.read()) != -1) {

			if ((char) currentCharacter == '\"' || (char) currentCharacter == '\"') {
				closedCounter = !closedCounter;
			}

			if ((char) currentCharacter == '>' && closedCounter) {
				currentElement.elementContent.append(">");
				break;
			}

			currentElement.elementContent.append((char) currentCharacter);

		}

		// System.out.println(currentCharacter);

		return currentCharacter;
	}
	
	
	
	
	public static void main(String[] args) {
		try {
			FileReader in = new FileReader("html.in");
			Reader buf = (Reader) (new BufferedReader(in));

			XMLMainParser parser = new XMLMainParser();
			HTMLContentGenerator generator = new HTMLContentGenerator(null, true);
			parser.setContentListener(generator);
			parser.parseInputIntoHTMLFormat(buf);
			System.out.println(parser.resultToCheckIfItReadsCorrectly);
			System.out.println(generator.getResultedText());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}














