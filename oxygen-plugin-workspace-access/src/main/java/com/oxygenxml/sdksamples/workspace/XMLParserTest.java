package com.oxygenxml.sdksamples.workspace;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Test;

public class XMLParserTest {

	/**
	 * 
	 */
	@Test
	public void testCheckSerialization() {
		String str = "<neighbourhood COLOR=\"BLUE\">\n" + 
				"	<house>\n" + 
				"	<!-- Comment -->\n" + 
				"	<?' PITarget (S (Char* - (Char* '?>' Char*)))? '?>\n" + 
				"	</house> \n" + 
				"</neighbourhood>\n";
		
		XMLParser parser  = new XMLParser(new StringReader(str));
		parser.parseInputIntoHTMLFormat();
		assertEquals("<span class = \"Element\">&lt;neighbourhood  </span><span class = \"attributeName\">COLOR=</span><span class = \"attributeValue\">=\"BLUE\"</span><span class = \"CurrentElementIsClosing\">&gt;</span>\n" + 
				"	<span class = \"Element\">&lt;house<span class = \"CurrentElementIsClosing\">&gt;</span></span>\n" + 
				"	<span class = \"Comment\">&lt;!-- Comment --<span class = \"CurrentElementIsClosing\">&gt;</span></span>\n" + 
				"	<span class = \"Processing Information\">&lt;?' PITarget (S (Char* - (Char* '?>' Char*)))? '?<span class = \"CurrentElementIsClosing\">&gt;</span></span>\n" + 
				"	<span class = \"CloseElement\">&lt;/house<span class = \"CurrentElementIsClosing\">&gt;</span></span> \n" + 
				"<span class = \"CloseElement\">&lt;/neighbourhood<span class = \"CurrentElementIsClosing\">&gt;</span></span>\n", parser.getResultedText());
	}

	/**
	 * 
	 */
	@Test
	public void testCheckSerializationEntities() {
		String str = "<h>"
				+ "</h>";
		
		XMLParser parser  = new XMLParser(new StringReader(str));
		parser.parseInputIntoHTMLFormat();
		//System.out.println(parser.getResultedText());
//		parser.doMyDeed();
		assertEquals("<span class = \"Element\">&lt;h<span class = \"CurrentElementIsClosing\">&gt;</span></span><span class = \"CloseElement\">&lt;/h<span class = \"CurrentElementIsClosing\">&gt;</span></span>", parser.getResultedText());
	}
	
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCornerCase() throws Exception {
        String str = "<neighbourhood COLOR=\"BLUE\">\n" + 
        		"	<house> \n" + 
        		"	<!-- Comment -->\n" + 
        		"	<?' PITarget (S (Char* - (Char* '?>' Char*)))? '?>\n" + 
        		"		<visitable ATTRIBUTE=\"EXISTING\"> Som&lt;$gt;ething </visitable> \n" + 
        		"	<![CDATA[<greeting>Hello, ]]]]]]world!</greeting>]]> \n" + 
        		"	<!DOCTYPE greeting [\n" + 
        		"  		<!ELEMENT \"<<<<<><><><>\"  greeting (#PCDATA)>\n" + 
        		"	]>\n" + 
        		"	</house> \n" + 
        		"</neighbourhood>";
		
		XMLParser parser  = new XMLParser(new StringReader(str));
		parser.parseInputIntoHTMLFormat();
		assertEquals("<span class = \"Element\">&lt;neighbourhood  </span><span class = \"attributeName\">COLOR=</span><span class = \"attributeValue\">=\"BLUE\"</span><span class = \"CurrentElementIsClosing\">&gt;</span>\n" + 
				"	<span class = \"Element\">&lt;house<span class = \"CurrentElementIsClosing\">&gt;</span></span> \n" + 
				"	<span class = \"Comment\">&lt;!-- Comment --<span class = \"CurrentElementIsClosing\">&gt;</span></span>\n" + 
				"	<span class = \"Processing Information\">&lt;?' PITarget (S (Char* - (Char* '?>' Char*)))? '?<span class = \"CurrentElementIsClosing\">&gt;</span></span>\n" + 
				"		<span class = \"Element\">&lt;visitable  </span><span class = \"attributeName\">ATTRIBUTE=</span><span class = \"attributeValue\">=\"EXISTING\"</span><span class = \"CurrentElementIsClosing\">&gt;</span> <span class = \"textField\"> Som&lt;$gt;ething </span><span class = \"CloseElement\">&lt;/visitable<span class = \"CurrentElementIsClosing\">&gt;</span></span> \n" + 
				"	<span class = \"CDATA\">&lt;![CDATA[<greeting>Hello, ]]]]]]world!</greeting>]]<span class = \"CurrentElementIsClosing\">&gt;</span> \n" + 
				"	<span class = \"Doctype\">&lt;!DOCTYPE greeting [\n" + 
				"  		<!ELEMENT \"<<<<<><><><>\"  greeting (#PCDATA)>\n" + 
				"	]<span class = \"CurrentElementIsClosing\">&gt;</span></span>\n" + 
				"	<span class = \"CloseElement\">&lt;/house<span class = \"CurrentElementIsClosing\">&gt;</span></span> \n" + 
				"<span class = \"CloseElement\">&lt;/neighbourhood<span class = \"CurrentElementIsClosing\">&gt;</span></span>", parser.getResultedText());

	}
	
	@Test
	public void testEnterAndTabs() {
		String str = "<h></h>\n" + 
				"\n" + 
				"\n" + 
				"\n" + 
				"\n" + 
				"	\n" + 
				"		\n" + 
				"			";
		
		XMLParser parser  = new XMLParser(new StringReader(str));
		parser.parseInputIntoHTMLFormat();
		//System.out.println(parser.getResultedText());
//		parser.doMyDeed();
		assertEquals("<span class = \"Element\">&lt;h<span class = \"CurrentElementIsClosing\">&gt;</span></span><span class = \"CloseElement\">&lt;/h<span class = \"CurrentElementIsClosing\">&gt;</span></span>\n" + 
				"\n" + 
				"\n" + 
				"\n" + 
				"\n" + 
				"	\n" + 
				"		\n" + 
				"			", parser.getResultedText());
	}
	
	
	@Test
	public void testPerson() {
		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<!DOCTYPE personnel PUBLIC \"PERSONNEL\" \"personal.dtd\">\n" + 
				"<?xml-stylesheet type=\"text/css\" href=\"personal.css\"?>\n" + 
				"<personnel>\n" + 
				"    <person id=\"harris.anderson\" photo=\"personal-images/harris.anderson.jpg\">                \n" + 
				"        <name>\n" + 
				"            <given>Harris</given>\n" + 
				"            <family>Anderson</family>\n" + 
				"        </name>\n" + 
				"        <email>harris.anderson@example.com</email>\n" + 
				"        <link subordinates=\"robert.taylor helen.jackson michelle.taylor jason.chen harris.anderson brian.carter\"/>\n" + 
				"        <url href=\"http://www.example.com/na/harris-anderson.html\"/>\n" + 
				"    </person>\n" + 
				"</personnel>";
		
		XMLParser parser  = new XMLParser(new StringReader(str));
		parser.parseInputIntoHTMLFormat();
		//System.out.println(parser.getResultedText());
//		parser.doMyDeed();
		assertEquals("<span class = \"PI\">&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?<span class = \"Element\">&gt;</span></span>\n" + 
				"<span class = \"Doctype\">&lt;!DOCTYPE personnel PUBLIC \"PERSONNEL\" \"personal.dtd\"<span class = \"Element\">&gt;</span></span>\n" + 
				"<span class = \"PI\">&lt;?xml-stylesheet type=\"text/css\" href=\"personal.css\"?<span class = \"Element\">&gt;</span></span>\n" + 
				"<span class = \"Element\">&lt;personnel<span class = \"Element\">&gt;</span></span>\n" + 
				"    <span class = \"Element\">&lt;person  </span><span class = \"attributeName\">id=</span><span class = \"attributeValue\">=\"harris.anderson\" photo=\"personal-images/harris.anderson.jpg\"</span><span class = \"Element\">&gt;</span>                \n" + 
				"        <span class = \"Element\">&lt;name<span class = \"Element\">&gt;</span></span>\n" + 
				"            <span class = \"Element\">&lt;given<span class = \"Element\">&gt;</span></span>", parser.getResultedText());
	}
	
	
}
