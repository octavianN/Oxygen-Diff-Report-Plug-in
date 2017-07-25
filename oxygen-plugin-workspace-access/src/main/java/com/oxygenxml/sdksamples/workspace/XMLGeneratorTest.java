package com.oxygenxml.sdksamples.workspace;


import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.Position;

import org.junit.Test;

import ro.sync.diff.api.Difference;
import ro.sync.diff.text.DiffEntry;

public class XMLGeneratorTest {
	private class OffsetPosition implements Position {
		private int offs;
		public OffsetPosition(int offs) {
			this.offs = offs;
			// TODO Auto-generated constructor stub
		}
		@Override
		public int getOffset() {
			return offs;
		}
		
	}
	
	
	@Test
	public void testDiffMarkerForElementSimple() {
		String str2 = "<neighbourhood>\n" + 
				"</neighbourhood>";
		
		String str1 = "<neigh>\n" + 
				"</neigh>";
		
		
		List<Difference> diffs = new ArrayList<Difference>();
		Position position1 = new OffsetPosition(6);
		Position position2 = new OffsetPosition(14);
		Position position3 = new OffsetPosition(6);
		Position position4 = new OffsetPosition(6);
		DiffEntry diff = new DiffEntry(position1, position2, position3, position4);
		
		position1 = new OffsetPosition(23);
		position2 = new OffsetPosition(31);
		position3 = new OffsetPosition(15);
		position4 = new OffsetPosition(15);
		diff = new DiffEntry(position1, position2, position3, position4);
		diffs.add(diff);
				
	   
		
		XMLParser parser = new XMLParser();
		
		HTMLContentGenerator htmlDiffGenerator = new HTMLContentGenerator(diffs, true);
		
		parser.setContentListener(htmlDiffGenerator);
		parser.parseInputIntoHTMLFormat(new StringReader(str2));
		
		assertEquals("",htmlDiffGenerator.getResultedText());
	}
	
	/**
	 * Testing if the ELEMENT differences hilights correctly
	 */
	@Test
	public void testDiffMarkerForElementHarder() {
		String str2 = "<neighbourhood COLOR=\"BLUE\">\n" + 
				"	<house>\n" + 
				"		<!-- Comment -->\n" + 
				"		<?' PITarget (S (Char* - (Char* '?>\n" + 
				"	</house>\n" + 
				"</neighbourhood>";
		
		String str1 = "<neighbourhood COLOR=\"BLUE\">\n" + 
				"	<hse>\n" + 
				"		<!-- Com -->\n" + 
				"		<?' PITarget eflkaefljlaf '?>\n" + 
				"	</hse>\n" + 
				"</neighbourhood>";
		
		
		List<Difference> diffs = new ArrayList<Difference>();
		Position position1 = new OffsetPosition(33);
		Position position2 = new OffsetPosition(36);
		Position position3 = new OffsetPosition(33);
		Position position4 = new OffsetPosition(38);
		DiffEntry diff = new DiffEntry(position1, position2, position3, position4);
		diffs.add(diff);
				
	   
		
		XMLParser parser = new XMLParser();
		
		HTMLContentGenerator htmlDiffGenerator = new HTMLContentGenerator(diffs, true);
		
		parser.setContentListener(htmlDiffGenerator);
		parser.parseInputIntoHTMLFormat(new StringReader(str1));
		
		assertEquals("<span class = \"Element\">&lt;neighbourhood  </span><span class = \"attributeName\">COLOR=</span><span class = \"attributeValue\">=\"BLUE\"</span><span class = \"Element\">&gt;</span>\n" + 
				"	<span class = \"Element\">&lt;<span class=\"diffEntry\" id=0 >hse<span class = \"Element\">&gt;</span></span>\n" + 
				"		<span class = \"Comment\">&lt;!-- Com --<span class = \"Element\">&gt;</span></span>\n" + 
				"		<span class = \"PI\">&lt;?' PITarget eflkaefljlaf '?&gt;</span>\n" + 
				"	<span class = \"Element\">&lt;/hse<span class = \"Element\">&gt;</span></span>\n" + 
				"<span class = \"Element\">&lt;/neighbourhood<span class = \"Element\">&gt;</span></span>",htmlDiffGenerator.getResultedText());
	}
	
	
	

}
