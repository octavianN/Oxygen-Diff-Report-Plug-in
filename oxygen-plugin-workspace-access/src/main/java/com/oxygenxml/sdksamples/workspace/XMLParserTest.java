package com.oxygenxml.sdksamples.workspace;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Test;

public class XMLParserTest {

	@Test
	public void testCheckSerialization() {
		String str = "<block-list:block-list xmlns:block-list=\"http://openoffice.org/2001/block-list\">\n"+
			    "<block-list:block block-list:abbreviated-name=\"--\" block-list:name=\"&#x2013;\"/>\n"+
				"<block-list:block block-list:abbreviated-name=\"--\" block-list:name=\"bb\"/>\n"
			+"</block-list:block-list>";
		
		XMLParser parser  = new XMLParser(new StringReader(str));
		parser.parseInputIntoHTMLFormat();
		assertEquals("<span class = \"Element\">&lt;block-list:block-list </span><span class = \"attributeName\">xmlns:block-list</span><span class = \"attributeValue\">\"http://openoffice.org/2001/block-list\"</span><span class = \"textField\">&gt;\n" + 
				"</span><span class = \"Element\">&lt;block-list:block </span><span class = \"attributeName\">block-list:abbreviated-name</span><span class = \"attributeValue\">\"--\" block-list:name</span><span class = \"textField\">=\"&#x2013;\"/&gt;\n" + 
				"</span><span class = \"Element\">&lt;block-list:block </span><span class = \"attributeName\">block-list:abbreviated-name</span><span class = \"attributeValue\">\"--\" block-list:name</span><span class = \"textField\">=\"bb\"/&gt;\n" + 
				"</span><span class = \"CloseElement\">&lt;/block-list:block-list&gt</span>", parser.getResultedText());
	}

	
	@Test
	public void testCheckSerializationEntities() {
		String str = "<root> <p> test &lt; other text </p></root>";
		
		XMLParser parser  = new XMLParser(new StringReader(str));
		parser.parseInputIntoHTMLFormat();
		assertEquals("<span class = \"Element\">&lt;root</span>&gt; <span class = \"Element\">&lt;p</span>&gt; test <span class = \"Element\">&lt; </span><span class = \"attributeName\">other text &lt;/p</span><span class = \"attributeValue\">gt;&lt;/root&gt</span><span class = \"textField\"></span>", parser.getResultedText());
	}
}
