package com.oxygenxml.diffreport.parser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import com.oxygenxml.diffreport.generator.HTMLContentGenerator;

public class XMLParserTest {

	/**
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testCheckSerialization() throws IOException {
		String str = "<neighbourhood COLOR=\"BLUE\">\n" + 
				"	<house>\n" + 
				"	<!-- Comment -->\n" + 
				"	<?' PITarget (S (Char* - (Char* '?>\n" + 
				"	</house> \n" + 
				"</neighbourhood>\n";
		XMLMainParser parser = new XMLMainParser();
		HTMLContentGenerator htmlDiffGenerator = new HTMLContentGenerator(null, true);
		
		parser.setContentListener(htmlDiffGenerator);
		parser.parseInputIntoHTMLFormat(new StringReader(str));
		assertEquals("<span class = \"Element\">&lt;neighbourhood </span><span class = \"attributeName\"> COLOR=</span><span class = \"attributeValue\">\"BLUE\"></span>\n" + 
				"	<span class = \"Element\">&lt;house</span><span class = \"Element\">&gt;</span>\n" + 
				"	<span class = \"Comment\">&lt;-- Comment --&gt;</span>\n" + 
				"	<span class = \"PI\">&lt;?' PITarget (S (Char* - (Char* '?&gt;</span>\n" + 
				"	<span class = \"Element\">&lt;/house&gt;</span> \n" + 
				"<span class = \"Element\">&lt;/neighbourhood&gt;</span>\n", htmlDiffGenerator.getResultedText());
	}

	/**
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testCheckSerializationEntities() throws IOException {
		String str = "<hala>"
				+ "</hala>";
		
		XMLMainParser parser = new XMLMainParser();
		HTMLContentGenerator htmlDiffGenerator = new HTMLContentGenerator(null, true);
		
		parser.setContentListener(htmlDiffGenerator);
		parser.parseInputIntoHTMLFormat(new StringReader(str));
		assertEquals("<span class = \"Element\">&lt;hala</span><span class = \"Element\">&gt;</span><span class = \"Element\">&lt;/hala&gt;</span>", htmlDiffGenerator.getResultedText());
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
		
        XMLMainParser parser = new XMLMainParser();
		HTMLContentGenerator htmlDiffGenerator = new HTMLContentGenerator(null, true);
		
		parser.setContentListener(htmlDiffGenerator);
		parser.parseInputIntoHTMLFormat(new StringReader(str));
		assertEquals("<span class = \"Element\">&lt;neighbourhood </span><span class = \"attributeName\"> COLOR=</span><span class = \"attributeValue\">\"BLUE\"></span>\n" + 
				"	<span class = \"Element\">&lt;house</span><span class = \"Element\">&gt;</span> \n" + 
				"	<span class = \"Comment\">&lt;-- Comment --&gt;</span>\n" + 
				"	<span class = \"PI\">&lt;?' PITarget (S (Char* - (Char* '?&gt;</span>\n" + 
				"		<span class = \"Element\">&lt;visitable </span><span class = \"attributeName\"> ATTRIBUTE=</span><span class = \"attributeValue\">\"EXISTING\"></span><span class = \"textField\"> Som&lt;$gt;ething </span><span class = \"Element\">&lt;/visitable&gt;</span> \n" + 
				"	<span class = \"CDATA\">&lt;[CDATA[&lt;greeting&gt;Hello, ]]]]]]world!&lt;/greeting&gt;]]&gt;</span> \n" + 
				"	<span class = \"Doctype\">&lt;DOCTYPE greeting [\n" + 
				"  		&lt;!ELEMENT \"bd\"  greeting (#PCDATA)&gt;</span><span class = \"textField\">\n" + 
				"	]&gt;\n" + 
				"	</span><span class = \"Element\">&lt;/house&gt;</span> \n" + 
				"<span class = \"Element\">&lt;/neighbourhood&gt;</span>", htmlDiffGenerator.getResultedText());
		

	}
	
	@Test
	public void testEnterAndTabs() throws IOException {
		String str = "<h></h>\n" + 
				"\n" + 
				"\n" + 
				"\n" + 
				"\n" + 
				"	\n" + 
				"		\n" + 
				"			";
		
		XMLMainParser parser = new XMLMainParser();
		HTMLContentGenerator htmlDiffGenerator = new HTMLContentGenerator(null, true);
		
		parser.setContentListener(htmlDiffGenerator);
		parser.parseInputIntoHTMLFormat(new StringReader(str));
		assertEquals("<span class = \"Element\">&lt;h</span><span class = \"Element\">&gt;</span><span class = \"Element\">&lt;/h&gt;</span>\n" + 
				"\n" + 
				"\n" + 
				"\n" + 
				"\n" + 
				"	\n" + 
				"		\n" + 
				"			", htmlDiffGenerator.getResultedText());
	}
	
	
	
	
	
}
