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
				"	<?' PITarget (S (Char* - (Char* '?>\n" + 
				"	</house> \n" + 
				"</neighbourhood>\n";
		XMLParser parser = new XMLParser();
		
		HTMLContentGenerator htmlDiffGenerator = new HTMLContentGenerator(null, true);
		
		parser.setContentListener(htmlDiffGenerator);
		parser.parseInputIntoHTMLFormat(new StringReader(str));
		assertEquals("<span class = \"Element\">&lt;neighbourhood  </span><span class = \"attributeName\">COLOR=</span><span class = \"attributeValue\">=\"BLUE\""
				+ "</span><span class = \"Element\">&gt;</span>\n" + 
				"	<span class = \"Element\">&lt;house<span class = \"Element\">&gt;</span></span>\n" + 
				"	<span class = \"Comment\">&lt;!-- Comment --<span class = \"Element\">&gt;</span></span>\n" + 
				"	<span class = \"PI\">&lt;?' PITarget (S (Char* - (Char* '?&gt;</span>\n" + 
				"	<span class = \"Element\">&lt;/house<span class = \"Element\">&gt;</span></span> \n" + 
				"<span class = \"Element\">&lt;/neighbourhood<span class = \"Element\">&gt;</span></span>\n", htmlDiffGenerator.getResultedText());
	}

	/**
	 * 
	 */
	@Test
	public void testCheckSerializationEntities() {
		String str = "<hala>"
				+ "</hala>";
		
		XMLParser parser = new XMLParser();
		HTMLContentGenerator htmlDiffGenerator = new HTMLContentGenerator(null, true);
		parser.setContentListener(htmlDiffGenerator);
		parser.parseInputIntoHTMLFormat(new StringReader(str));
		assertEquals("<span class = \"Element\">&lt;hala<span class = \"Element\">&gt;</span></span><span class "
				+ "= \"Element\">&lt;/hala<span class = \"Element\">&gt;</span></span>", htmlDiffGenerator.getResultedText());
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
        		"	<?' PITarget (S (Char* - (Char* '?>\n" + 
        		"		<visitable ATTRIBUTE=\"EXISTING\"> Som&lt;$gt;ething </visitable> \n" + 
        		"	<![CDATA[<greeting>Hello, ]]]]]]world!</greeting>]]> \n" + 
        		"	<!DOCTYPE greeting [\n" + 
        		"  		<!ELEMENT \"bd\"  greeting (#PCDATA)>\n" + 
        		"	]>\n" + 
        		"	</house> \n" + 
        		"</neighbourhood>";
		
        XMLParser parser = new XMLParser();
		HTMLContentGenerator htmlDiffGenerator = new HTMLContentGenerator(null, true);
		parser.setContentListener(htmlDiffGenerator);
		parser.parseInputIntoHTMLFormat(new StringReader(str));
		assertEquals("<span class = \"Element\">&lt;neighbourhood  </span><span class = \"attributeName\">COLOR=</span><span class = \"attributeValue\">=\"BLUE\"</span>"
				+ "<span class = \"Element\">&gt;</span>\n" + 
				"	<span class = \"Element\">&lt;house<span class = \"Element\">&gt;</span></span> \n" + 
				"	<span class = \"Comment\">&lt;!-- Comment --<span class = \"Element\">&gt;</span></span>\n" + 
				"	<span class = \"PI\">&lt;?' PITarget (S (Char* - (Char* '?&gt;</span>\n" + 
				"		<span class = \"Element\">&lt;visitable  </span><span class = \"attributeName\">ATTRIBUTE=</span>"
				+ "<span class = \"attributeValue\">=\"EXISTING\"</span><span class = \"Element\">&gt;</span> <span class = \"textField\"> Som&lt;$gt;ething </span>"
				+ "<span class = \"Element\">&lt;/visitable<span class = \"Element\">&gt;</span></span> \n" + 
				"	<span class = \"CDATA\">&lt;![CDATA[<greeting>Hello, ]]]]]]world!</greeting>]]<span class = \"Element\">&gt;</span> \n" + 
				"	<span class = \"Doctype\">&lt;!DOCTYPE greeting [\n" + 
				"  		<!ELEMENT \"bd\"  greeting (#PCDATA)>\n" + 
				"	]<span class = \"Element\">&gt;</span></span>\n" + 
				"	<span class = \"Element\">&lt;/house<span class = \"Element\">&gt;</span></span> \n" + 
				"<span class = \"Element\">&lt;/neighbourhood<span class = \"Element\">&gt;</span></span>", htmlDiffGenerator.getResultedText());

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
		
		XMLParser parser = new XMLParser();
		HTMLContentGenerator htmlDiffGenerator = new HTMLContentGenerator(null, true);
		parser.setContentListener(htmlDiffGenerator);
		parser.parseInputIntoHTMLFormat(new StringReader(str));
		assertEquals("<span class = \"Element\">&lt;h<span class = \"Element\">&gt;</span>"
				+ "</span><span class = \"Element\">&lt;/h<span class = \"Element\">&gt;</span></span>\n" + 
				"\n" + 
				"\n" + 
				"\n" + 
				"\n" + 
				"	\n" + 
				"		\n" + 
				"			", htmlDiffGenerator.getResultedText());
	}
	
	
	
	
	
}
