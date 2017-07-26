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
		Position position1;
		Position position2;
		Position position3;
		Position position4;
		DiffEntry diff;
//		createDiffEntry(); TODO 
		
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
		
		assertEquals("<span class = \"Element\">&lt;neighbourhood<span class = \"Element\">&gt;</span></span>\n" + 
				"<span class = \"Element\">&lt;/neigh<span class=\"diffEntry\" id=0 >bourhood</span><span class = \"Element\">&gt;</span></span>",htmlDiffGenerator.getResultedText());
	}
	
	
	@Test
	public void testDiffMarkerForElementSimple1() {
		String str1 = "<haleluha  color = \"LALA\">\n" + 
				"<bla>     <gf>                        </gf>    </bla>\n" + 
				"</haleluha>";
		
		//		7 Diff Entries
		//		| 0 9  ------   0  9 |
		//
		//		| 18 24  ------   18  24 |
		//
		//		| 26 31  ------   26  31 |
		//
		//		| 36 40  ------   36  40 |
		//
		//		| 64 69  ------   64  69 |
		//
		//		| 73 79  ------   73  79 |
		//
		//		| 80 91  ------   80  91 |
		
		List<Difference> diffs = new ArrayList<Difference>();
		diffs.add(createDiffEntry(0, 9, 0, 9));
		diffs.add(createDiffEntry(18, 24, 0, 9));
		diffs.add(createDiffEntry(26, 31, 0, 9));
		diffs.add(createDiffEntry(36, 40, 0, 9));
		diffs.add(createDiffEntry(64, 69, 0, 9));
		diffs.add(createDiffEntry(73, 79, 0, 9));
		diffs.add(createDiffEntry(80, 91, 0, 9));
		
		XMLParser parser = new XMLParser();
		HTMLContentGenerator htmlDiffGenerator = new HTMLContentGenerator(diffs, true);
		parser.setContentListener(htmlDiffGenerator);
		parser.parseInputIntoHTMLFormat(new StringReader(str1));
		
		assertEquals("<span class = \"Element\">&lt;"
				+ "<span class=\"diffEntry\" id=0 >haleluha  </span>  </span>"
				+ "<span class = \"attributeName\">color =</span>"
				+ "<span class = \"attributeValue\"> "
				+ "<span class=\"diffEntry\" id=0 >\"LALA\"</span>"
				+ "</span>"
				+ "<span class = \"Element\">&gt;</span>\n" + 
				"<span class=\"diffEntry\" id=1 ><span class = \"Element\">&lt;bla"
				+ "<span class = \"Element\">&gt;</span></span>     "
				+ "<span class=\"diffEntry\" id=2 ><span class = \"Element\">&lt;gf<span class = \"Element\">&gt;</span></span>                        "
				+ "<span class=\"diffEntry\" id=3 ><span class = \"Element\">&lt;/gf<span class = \"Element\">&gt;</span></span>    "
				+ "<span class=\"diffEntry\" id=4 ><span class = \"Element\">&lt;/bla<span class = \"Element\">&gt;</span></span>\n" + 
				"<span class=\"diffEntry\" id=5 ><span class = \"Element\">&lt;/haleluha<span class = \"Element\">&gt;</span></span>",htmlDiffGenerator.getResultedText());
	}


	private DiffEntry createDiffEntry(int lStart, int lEnd, int rStart, int rEnd) {
		Position position1 = new OffsetPosition(lStart);
		Position position2 = new OffsetPosition(lEnd);
		Position position3 = new OffsetPosition(rStart);
		Position position4 = new OffsetPosition(rEnd);
		DiffEntry diff = new DiffEntry(position1, position2, position3, position4);
		
		return diff;
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
