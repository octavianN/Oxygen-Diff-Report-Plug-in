package com.oxygenxml.diffreport.generator;


import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.Position;

import org.junit.Test;

import com.oxygenxml.diffreport.parser.XMLMainParser;

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
		
		@Override
		public String toString() {
			return offs + "";
		}
		
	}
	
	
	@Test
	public void testDiffMarkerForElementSimple1() throws IOException {
		String str = "<neighbour>\n" + 
				"    <paragraph>\n" + 
				"        \n" + 
				"        <p> Some Text </p>\n" + 
				"\n" + 
				"    </paragraph>\n" + 
				"</neighbour>";
		
	
//		3 Diff Entries
//		| 0 7  ------   0  11 |
//
//		| 50 56  ------   54  54 |
//
//		| 84 92  ------   82  94 |
		
		
		List<Difference> diffs = new ArrayList<Difference>();
		diffs.add(createDiffEntry(0, 7, 0, 11));
		diffs.add(createDiffEntry(50, 56, 54, 54));
		diffs.add(createDiffEntry(84, 92, 82, 94));
	   
		
		XMLMainParser parser = new XMLMainParser();
		HTMLContentGenerator htmlDiffGenerator = new HTMLContentGenerator(diffs, true);
		
		parser.setContentListener(htmlDiffGenerator);
		parser.parseInputIntoHTMLFormat(new StringReader(str));
		
		assertEquals("<span class = \"Element\">&lt;<span class=\"diffEntry diffTypeConflict\" id=\"0\">neighb</span>our</span><span class = \"Element\">&gt;</span>\n" + 
				"    <span class = \"Element\">&lt;paragraph</span><span class = \"Element\">&gt;</span>\n" + 
				"        \n" + 
				"        <span class = \"Element\">&lt;p</span><span class = \"Element\">&gt;</span><span class = \"textField\"> S<span class=\"diffEntry diffTypeOutgoing\" id=\"1\">ome T</span>ext </span><span class = \"Element\">&lt;/p&gt;</span>\n" + 
				"\n" + 
				"    <span class = \"Element\">&lt;/paragraph&gt;</span>\n" + 
				"<span class = \"Element\">&lt;/<span class=\"diffEntry diffTypeConflict\" id=\"2\">neighbo</span>ur&gt;</span>",htmlDiffGenerator.getResultedText());
	}
	
	
	@Test
	public void testDiffMarkerForElementSimple2() throws IOException {
		String str = "<space>                        \n" + 
				"                <p>EmptySPACE</p>\n" + 
				"\n" + 
				"                <element with = \"Attribute\">\n" + 
				"                        Enter, Tabs some Kebabs\n" + 
				"                </element>\n" + 
				"</space>";
		
		//		1 Diff Entries
//		| 148 148  ------   148  152 |
		
		List<Difference> diffs = new ArrayList<Difference>();
		diffs.add(createDiffEntry(148, 148, 148, 152));

		
		XMLMainParser parser = new XMLMainParser();
		HTMLContentGenerator htmlDiffGenerator = new HTMLContentGenerator(diffs, true);
		
		parser.setContentListener(htmlDiffGenerator);
		parser.parseInputIntoHTMLFormat(new StringReader(str));
		
		assertEquals("<span class = \"Element\">&lt;space</span><span class = \"Element\">&gt;</span>                        \n" + 
				"                <span class = \"Element\">&lt;p</span><span class = \"Element\">&gt;</span>EmptySPACE<span class = \"Element\">&lt;/p&gt;</span>\n" + 
				"\n" + 
				"                <span class = \"Element\">&lt;element </span><span class = \"attributeName\"> with =</span><span class = \"attributeValue\"> \"Attribute\"></span><span class = \"textField\">\n" + 
				"                        Enter, Tabs</span> <span class=\"diffEntry diffTypeIncoming\" id=\"1\"></span>some Kebabs\n" + 
				"                </span><span class = \"Element\">&lt;/element&gt;</span>\n" + 
				"<span class = \"Element\">&lt;/space&gt;</span>",htmlDiffGenerator.getResultedText());
	}


	private DiffEntry createDiffEntry(int lStart, int lEnd, int rStart, int rEnd) {
		Position position1 = new OffsetPosition(lStart);
		Position position2 = new OffsetPosition(lEnd);
		Position position3 = new OffsetPosition(rStart);
		Position position4 = new OffsetPosition(rEnd);
		DiffEntry diff = new DiffEntry(position1, position2, position3, position4);
		
		return diff;
	}
	
	
	
	
	@Test
	public void testDiffMarkerEntersAndTabs() throws IOException {
		String str = "<space>\n" + 
				"\n" + 
				"\n" + 
				"\n" + 
				"                    \n" + 
				"\n" + 
				"\n" + 
				"</space>\n" + 
				"\n" + 
				"\n" + 
				"";
		
	
//		1 Diff Entries
//		| 7 34  ------   7  14 ||
		
		
		List<Difference> diffs = new ArrayList<Difference>();
		diffs.add(createDiffEntry(7, 14, 7, 34));
;
	   
		
		XMLMainParser parser = new XMLMainParser();
		HTMLContentGenerator htmlDiffGenerator = new HTMLContentGenerator(diffs, true);
		
		parser.setContentListener(htmlDiffGenerator);
		parser.parseInputIntoHTMLFormat(new StringReader(str));
		
		assertEquals("<span class = \"Element\">&lt;space</span><span class = \"Element\">&gt;</span>\n" + 
				"<span class=\"diffEntry diffTypeConflict\" id=\"0\">\n" + 
				"\n" + 
				"\n" + 
				"   </span>                 \n" + 
				"\n" + 
				"\n" + 
				"<span class = \"Element\">&lt;/space&gt;</span>\n" + 
				"\n" + 
				"\n" + 
				"",htmlDiffGenerator.getResultedText());
	}
	
	
	
	/**
	 * Testing if the ELEMENT differences hilights correctly
	 * @throws IOException 
	 */
	@Test
	public void testDiffMarkerForElementHarder1() throws IOException {
		String str = "<!DOCTYPE nein PUBLIC \"PERSONNEL\" \"personal.dtd\">\n" + 
				"<?xml-stylesheet type=\"text/css\" href=\"personal.css\"?>\n" + 
				"<nein>\n" + 
				"    <person tf=\"harris.anderson\" photo=\"personal-images/harris.anderson.jpg\">                \n" + 
				"        <name>\n" + 
				"            <given>Harris</given>\n" + 
				"            <family>Anderson</family>\n" + 
				"        </name>\n" + 
				"        <![CDATA[hjsdvgksebgfkaseg]]> \n" + 
				"        <!-- Commentariu -->\n" + 
				"        <email>harris.anderson@example.com</email>\n" + 
				"        <link subordinates=\"robert.taylor helen.jackson michelle.taylor jason.chen harris.anderson brian.carter\"/>\n" + 
				"        <url href=\"http://www.example.com/na/harris-anderson.html\"/>\n" + 
				"    </person>\n" + 
				"</nein> ";

		
//		7 Diff Entries		
//		| 0 49  ------   0  54 |
//
//		| 105 111  ------   110  121 |
//
//		| 123 126  ------   133  136 |
//
//		| 317 346  ------   327  379 |
//
//		| 360 373  ------   393  402 |
//
//		| 542 611  ------   571  571 |
//
//		| 626 633  ------   586  598 |
//
//		| 633 634  ------   598  598 |
	   
		List<Difference> diffs = new ArrayList<Difference>();
		diffs.add(createDiffEntry(0, 49, 0, 54));
		diffs.add(createDiffEntry(105, 111, 110, 121));
		diffs.add(createDiffEntry(123, 126, 133, 136));
		diffs.add(createDiffEntry(317, 346, 327, 379));
		diffs.add(createDiffEntry(360, 373, 393, 402));
		diffs.add(createDiffEntry(542, 611, 571, 571));
		diffs.add(createDiffEntry(626, 633, 586, 598));
		diffs.add(createDiffEntry(633, 634, 598, 598));
		
		XMLMainParser parser = new XMLMainParser();
		HTMLContentGenerator htmlDiffGenerator = new HTMLContentGenerator(diffs, true);
		
		parser.setContentListener(htmlDiffGenerator);
		parser.parseInputIntoHTMLFormat(new StringReader(str));
		
		assertEquals("<span class = \"Doctype\"><span class=\"diffEntry diffTypeConflict\" id=\"0\">&lt;DOCTYPE nein PUBLIC \"PERSONNEL\" \"personal.dtd\"&gt;</span>\n" + 
				"<span class = \"PI\">&lt;?xml-stylesheet type=\"text/css\" href=\"personal.css\"?&gt;</span>\n" + 
				"<span class = \"Element\">&lt;<span class=\"diffEntry diffTypeConflict\" id=\"0\">nein</span></span><span class = \"Element\">&gt;</span>\n" + 
				"    <span class = \"Element\">&lt;person </span> <span class=\"diffEntry diffTypeConflict\" id=\"2\"><span class = \"attributeName\">tf</span>=</span><span class = \"attributeValue\">\"harris.anderson\" photo=\"personal-images/harris.anderson.jpg\"></span>                \n" + 
				"        <span class = \"Element\">&lt;name</span><span class = \"Element\">&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;given</span><span class = \"Element\">&gt;</span>Harris<span class = \"Element\">&lt;/given&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;family</span><span class = \"Element\">&gt;</span>Anderson<span class = \"Element\">&lt;/family&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;/name&gt;</span>\n" + 
				"        <span class = \"CDATA\"><span class=\"diffEntry diffTypeConflict\" id=\"3\">&lt;[CDATA[hjsdvgksebgfkaseg]]&gt;</span> \n" + 
				"        <span class = \"Comment\">&lt;-- <span class=\"diffEntry diffTypeConflict\" id=\"3\">Commentariu </span>--&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;email</span><span class = \"Element\">&gt;</span>harris.anderson@example.com<span class = \"Element\">&lt;/email&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;link </span><span class = \"attributeName\"> subordinates=</span><span class = \"attributeValue\">\"robert.taylor helen.jackson michelle.taylor jason.chen harris.anderson brian.carter\"/></span>\n" + 
				"<span class=\"diffEntry diffTypeOutgoing\" id=\"5\">        <span class = \"Element\">&lt;url </span><span class = \"attributeName\"> href=</span><span class = \"attributeValue\">\"http://www.example.com/na/harris-anderson.html\"/></span></span>\n" + 
				"    <span class = \"Element\">&lt;/person&gt;</span>\n" + 
				"<span class = \"Element\"><span class=\"diffEntry diffTypeConflict\" id=\"6\">&lt;/nein</span>&gt;</span> <span class=\"diffEntry diffTypeOutgoing\" id=\"7\">",htmlDiffGenerator.getResultedText());
	}
	
	/**
	 * Test thet
	 * @throws IOException
	 */
	@Test
	public void testDiffMarkerForElementHarder2() throws IOException {
		String str = "<smartphonelist>\n" + 
				"    <smartphone id=\"1\">\n" + 
				"        <brandName>Samsung</brandName>\n" + 
				"        <model>A5</model>\n" + 
				"        <screenSize unet=\"inch\">5.5</screenSize>\n" + 
				"        <prafs>\n" + 
				"            <numberOfCores>4</numberOfCores>\n" + 
				"            <frequence unet=\"GHz\">1.6</frequence>\n" + 
				"        </prafs>\n" + 
				"        <memory>\n" + 
				"            <ram unet=\"Gb\">2</ram>\n" + 
				"            <storage unet=\"Gb\">16</storage>\n" + 
				"        </memory>\n" + 
				"        \n" + 
				"        <camera unet=\"mp\">8</camera>\n" + 
				"    </smartphone>\n" + 
				"    <smartphone id=\"2\">\n" + 
				"        <brandName>Acer</brandName>\n" + 
				"        <model>z6</model>\n" + 
				"        <screenSize unet=\"inch\">5.5</screenSize>\n" + 
				"        <processor>\n" + 
				"            <numberOfCores>8</numberOfCores>\n" + 
				"            <frequence unet=\"GHz\">2.3</frequence>\n" + 
				"        </processor>\n" + 
				"        <memory>\n" + 
				"            <ram unet=\"Gb\">3</ram>\n" + 
				"            <storage unet=\"Gb\">64</storage>\n" + 
				"        </memory>\n" + 
				"        <camera unet=\"mp\">13</camera>\n" + 
				"    </smartphone>\n" + 
				"    <smartphone id=\"3\">\n" + 
				"        <brandName>Lg</brandName>\n" + 
				"        <model>G2</model>\n" + 
				"        <screenSize unit=\"inch\">5.2</screenSize>\n" + 
				"        <processor>\n" + 
				"            <numberOfCores>2</numberOfCores>\n" + 
				"            <frequence unit=\"GHz\">2.6</frequence>\n" + 
				"        </processor>\n" + 
				"        <memory>\n" + 
				"            <ram unit=\"Gb\">2</ram>\n" + 
				"            <storage unit=\"Gb\">64</storage>\n" + 
				"        </memory>\n" + 
				"        <camera unit=\"mp\">13</camera>\n" + 
				"    </smartphone>\n" + 
				"    <smartphone id=\"4\">\n" + 
				"        <brandName>Iphone</brandName>\n" + 
				"        <model>5</model>\n" + 
				"        <screenSize unit=\"inch\">4.7</screenSize>\n" + 
				"        <processor>\n" + 
				"            <numberOfCores>2</numberOfCores>\n" + 
				"            <frequence unit=\"GHz\">1.8</frequence>\n" + 
				"        </processor>\n" + 
				"        <memory>\n" + 
				"            <ram unit=\"Gb\">2</ram>\n" + 
				"            <storage unit=\"Gb\">32</storage>\n" + 
				"        </memory>\n" + 
				"        <camera unit=\"mp\">12</camera>\n" + 
				"    </smartphone>\n" + 
				"    <smartphone id=\"5\">\n" + 
				"        <brandName>OnePlus</brandName>\n" + 
				"        <model>3</model>\n" + 
				"        <screenSize unit=\"inch\">5.5</screenSize>\n" + 
				"        <processor>\n" + 
				"            <numberOfCores>8</numberOfCores>\n" + 
				"            <frequence unit=\"GHz\">2.25</frequence>\n" + 
				"        </processor>\n" + 
				"        <memory>\n" + 
				"            <ram unit=\"Gb\">6</ram>\n" + 
				"            <storage unit=\"Gb\">64</storage>\n" + 
				"        </memory>\n" + 
				"        <camera unit=\"mp\">16</camera>\n" + 
				"    </smartphone>\n" + 
				"</smartphonelist>";

		
//		12 Diff Entries		
//		| 125 130  ------   125  130 |
//
//		| 163 170  ------   163  174 |
//
//		| 238 243  ------   242  247 |
//
//		| 274 282  ------   278  290 |
//
//		| 316 321  ------   324  329 |
//
//		| 355 360  ------   363  368 |
//
//		| 421 426  ------   429  434 |
//
//		| 566 571  ------   574  579 |
//
//		| 683 688  ------   691  696 |
//
//		| 765 770  ------   773  778 |
//
//		| 804 809  ------   812  817 |
//
//		| 861 866  ------   869  874 |
	   
		List<Difference> diffs = new ArrayList<Difference>();
		diffs.add(createDiffEntry(125, 130, 125, 130));
		diffs.add(createDiffEntry(163, 170, 163, 174));
		diffs.add(createDiffEntry(238, 243, 242, 247));
		diffs.add(createDiffEntry(274, 282, 278, 280));
		diffs.add(createDiffEntry(316, 321, 324, 329));
		diffs.add(createDiffEntry(355, 360, 363, 368));
		diffs.add(createDiffEntry(421, 426, 429, 434));
		diffs.add(createDiffEntry(566, 571, 574, 579));
		diffs.add(createDiffEntry(683, 688, 691, 696));
		diffs.add(createDiffEntry(765, 770, 773, 778));
		diffs.add(createDiffEntry(804, 809, 812, 817));
		diffs.add(createDiffEntry(861, 866, 869, 874));

		
		XMLMainParser parser = new XMLMainParser();
		HTMLContentGenerator htmlDiffGenerator = new HTMLContentGenerator(diffs, true);
		
		parser.setContentListener(htmlDiffGenerator);
		parser.parseInputIntoHTMLFormat(new StringReader(str));
		
		assertEquals("<span class = \"Element\">&lt;smartphonelist</span><span class = \"Element\">&gt;</span>\n" + 
				"    <span class = \"Element\">&lt;smartphone </span><span class = \"attributeName\"> id=</span><span class = \"attributeValue\">\"1\"></span>\n" + 
				"        <span class = \"Element\">&lt;brandName</span><span class = \"Element\">&gt;</span>Samsung<span class = \"Element\">&lt;/brandName&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;model</span><span class = \"Element\">&gt;</span>A5<span class = \"Element\">&lt;/model&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;screenSize </span> <span class=\"diffEntry diffTypeConflict\" id=\"0\"><span class = \"attributeName\">unet</span>=</span><span class = \"attributeValue\">\"inch\"></span>5.5<span class = \"Element\">&lt;/screenSize&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;<span class=\"diffEntry diffTypeConflict\" id=\"1\">prafs</span></span><span class = \"Element\">&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;numberOfCores</span><span class = \"Element\">&gt;</span>4<span class = \"Element\">&lt;/numberOfCores&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;frequence </span> <span class=\"diffEntry diffTypeConflict\" id=\"2\"><span class = \"attributeName\">unet</span>=</span><span class = \"attributeValue\">\"GHz\"></span>1.6<span class = \"Element\">&lt;/frequence&gt;</span>\n" + 
				"        <span class = \"Element\"><span class=\"diffEntry diffTypeConflict\" id=\"3\">&lt;/prafs</span>&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;memory</span><span class = \"Element\">&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;ram </span> <span class=\"diffEntry diffTypeConflict\" id=\"4\"><span class = \"attributeName\">unet</span>=</span><span class = \"attributeValue\">\"Gb\"></span>2<span class = \"Element\">&lt;/ram&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;storage </span> <span class=\"diffEntry diffTypeConflict\" id=\"5\"><span class = \"attributeName\">unet</span>=</span><span class = \"attributeValue\">\"Gb\"></span>16<span class = \"Element\">&lt;/storage&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;/memory&gt;</span>\n" + 
				"        \n" + 
				"        <span class = \"Element\">&lt;camera </span> <span class=\"diffEntry diffTypeConflict\" id=\"6\"><span class = \"attributeName\">unet</span>=</span><span class = \"attributeValue\">\"mp\"></span>8<span class = \"Element\">&lt;/camera&gt;</span>\n" + 
				"    <span class = \"Element\">&lt;/smartphone&gt;</span>\n" + 
				"    <span class = \"Element\">&lt;smartphone </span><span class = \"attributeName\"> id=</span><span class = \"attributeValue\">\"2\"></span>\n" + 
				"        <span class = \"Element\">&lt;brandName</span><span class = \"Element\">&gt;</span>Acer<span class = \"Element\">&lt;/brandName&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;model</span><span class = \"Element\">&gt;</span>z6<span class = \"Element\">&lt;/model&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;screenSize </span> <span class=\"diffEntry diffTypeConflict\" id=\"7\"><span class = \"attributeName\">unet</span>=</span><span class = \"attributeValue\">\"inch\"></span>5.5<span class = \"Element\">&lt;/screenSize&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;processor</span><span class = \"Element\">&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;numberOfCores</span><span class = \"Element\">&gt;</span>8<span class = \"Element\">&lt;/numberOfCores&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;frequence </span> <span class=\"diffEntry diffTypeConflict\" id=\"8\"><span class = \"attributeName\">unet</span>=</span><span class = \"attributeValue\">\"GHz\"></span>2.3<span class = \"Element\">&lt;/frequence&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;/processor&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;memory</span><span class = \"Element\">&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;ram </span> <span class=\"diffEntry diffTypeConflict\" id=\"9\"><span class = \"attributeName\">unet</span>=</span><span class = \"attributeValue\">\"Gb\"></span>3<span class = \"Element\">&lt;/ram&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;storage </span> <span class=\"diffEntry diffTypeConflict\" id=\"10\"><span class = \"attributeName\">unet</span>=</span><span class = \"attributeValue\">\"Gb\"></span>64<span class = \"Element\">&lt;/storage&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;/memory&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;camera </span> <span class=\"diffEntry diffTypeConflict\" id=\"11\"><span class = \"attributeName\">unet</span>=</span><span class = \"attributeValue\">\"mp\"></span>13<span class = \"Element\">&lt;/camera&gt;</span>\n" + 
				"    <span class = \"Element\">&lt;/smartphone&gt;</span>\n" + 
				"    <span class = \"Element\">&lt;smartphone </span><span class = \"attributeName\"> id=</span><span class = \"attributeValue\">\"3\"></span>\n" + 
				"        <span class = \"Element\">&lt;brandName</span><span class = \"Element\">&gt;</span>Lg<span class = \"Element\">&lt;/brandName&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;model</span><span class = \"Element\">&gt;</span>G2<span class = \"Element\">&lt;/model&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;screenSize </span><span class = \"attributeName\"> unit=</span><span class = \"attributeValue\">\"inch\"></span>5.2<span class = \"Element\">&lt;/screenSize&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;processor</span><span class = \"Element\">&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;numberOfCores</span><span class = \"Element\">&gt;</span>2<span class = \"Element\">&lt;/numberOfCores&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;frequence </span><span class = \"attributeName\"> unit=</span><span class = \"attributeValue\">\"GHz\"></span>2.6<span class = \"Element\">&lt;/frequence&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;/processor&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;memory</span><span class = \"Element\">&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;ram </span><span class = \"attributeName\"> unit=</span><span class = \"attributeValue\">\"Gb\"></span>2<span class = \"Element\">&lt;/ram&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;storage </span><span class = \"attributeName\"> unit=</span><span class = \"attributeValue\">\"Gb\"></span>64<span class = \"Element\">&lt;/storage&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;/memory&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;camera </span><span class = \"attributeName\"> unit=</span><span class = \"attributeValue\">\"mp\"></span>13<span class = \"Element\">&lt;/camera&gt;</span>\n" + 
				"    <span class = \"Element\">&lt;/smartphone&gt;</span>\n" + 
				"    <span class = \"Element\">&lt;smartphone </span><span class = \"attributeName\"> id=</span><span class = \"attributeValue\">\"4\"></span>\n" + 
				"        <span class = \"Element\">&lt;brandName</span><span class = \"Element\">&gt;</span>Iphone<span class = \"Element\">&lt;/brandName&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;model</span><span class = \"Element\">&gt;</span>5<span class = \"Element\">&lt;/model&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;screenSize </span><span class = \"attributeName\"> unit=</span><span class = \"attributeValue\">\"inch\"></span>4.7<span class = \"Element\">&lt;/screenSize&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;processor</span><span class = \"Element\">&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;numberOfCores</span><span class = \"Element\">&gt;</span>2<span class = \"Element\">&lt;/numberOfCores&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;frequence </span><span class = \"attributeName\"> unit=</span><span class = \"attributeValue\">\"GHz\"></span>1.8<span class = \"Element\">&lt;/frequence&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;/processor&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;memory</span><span class = \"Element\">&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;ram </span><span class = \"attributeName\"> unit=</span><span class = \"attributeValue\">\"Gb\"></span>2<span class = \"Element\">&lt;/ram&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;storage </span><span class = \"attributeName\"> unit=</span><span class = \"attributeValue\">\"Gb\"></span>32<span class = \"Element\">&lt;/storage&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;/memory&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;camera </span><span class = \"attributeName\"> unit=</span><span class = \"attributeValue\">\"mp\"></span>12<span class = \"Element\">&lt;/camera&gt;</span>\n" + 
				"    <span class = \"Element\">&lt;/smartphone&gt;</span>\n" + 
				"    <span class = \"Element\">&lt;smartphone </span><span class = \"attributeName\"> id=</span><span class = \"attributeValue\">\"5\"></span>\n" + 
				"        <span class = \"Element\">&lt;brandName</span><span class = \"Element\">&gt;</span>OnePlus<span class = \"Element\">&lt;/brandName&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;model</span><span class = \"Element\">&gt;</span>3<span class = \"Element\">&lt;/model&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;screenSize </span><span class = \"attributeName\"> unit=</span><span class = \"attributeValue\">\"inch\"></span>5.5<span class = \"Element\">&lt;/screenSize&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;processor</span><span class = \"Element\">&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;numberOfCores</span><span class = \"Element\">&gt;</span>8<span class = \"Element\">&lt;/numberOfCores&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;frequence </span><span class = \"attributeName\"> unit=</span><span class = \"attributeValue\">\"GHz\"></span>2.25<span class = \"Element\">&lt;/frequence&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;/processor&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;memory</span><span class = \"Element\">&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;ram </span><span class = \"attributeName\"> unit=</span><span class = \"attributeValue\">\"Gb\"></span>6<span class = \"Element\">&lt;/ram&gt;</span>\n" + 
				"            <span class = \"Element\">&lt;storage </span><span class = \"attributeName\"> unit=</span><span class = \"attributeValue\">\"Gb\"></span>64<span class = \"Element\">&lt;/storage&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;/memory&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;camera </span><span class = \"attributeName\"> unit=</span><span class = \"attributeValue\">\"mp\"></span>16<span class = \"Element\">&lt;/camera&gt;</span>\n" + 
				"    <span class = \"Element\">&lt;/smartphone&gt;</span>\n" + 
				"<span class = \"Element\">&lt;/smartphonelist&gt;</span>",htmlDiffGenerator.getResultedText());
	}
	

	@Test
	public void test() throws IOException {
		String str = "<!DOCTYPE personnel PUBLIC \"PERSONNEL\" \"personal.dtd\">\n" + 
				"<?xml-stylesheet type=\"text/css\" href=\"personal.css\"?>\n" + 
				"<personnel>\n" + 
				"    <person id=\"harris.anderson\" photo=\"personal-images/harris.anderson.jpg\">                \n" + 
				"        <name>\n" + 
				"            <given>Harris</given>\n" + 
				"            <family>Anderson</family>\n" + 
				"        </name>\n" + 
				"        <![CDATA[<greeting>Hello, ]]]]]]world!</greeting>]]> \n" + 
				"        <!-- Comment -->\n" + 
				"        <email>harris.anderson@example.com</email>\n" + 
				"        <link subordinates=\"robert.taylor helen.jackson michelle.taylor jason.chen harris.anderson brian.carter\"/>\n" + 
				"    </person>\n" + 
				"</personnel>\n";
		System.out.println(str);
		
//		| 0 54  ------   0  49 |
//
//		| 110 121  ------   105  111 |
//
//		| 133 136  ------   123  126 |
//
//		| 327 379  ------   317  346 |
//
//		| 393 402  ------   360  373 |
//
//		| 571 571  ------   542  611 |
//
//		| 586 598  ------   626  633 |
//
//		| 598 599  ------   633  635 |
		
		List<Difference> diffs = new ArrayList<Difference>();
		diffs.add(createDiffEntry(0, 54, 0, 49));
		diffs.add(createDiffEntry( 110, 121, 105, 111));
		diffs.add(createDiffEntry(133, 136, 123, 126));
		diffs.add(createDiffEntry(327, 379, 317, 346));
		diffs.add(createDiffEntry(393, 402, 360, 373));
		diffs.add(createDiffEntry(571, 571, 542, 611));
		diffs.add(createDiffEntry(586, 598, 626, 633));
		diffs.add(createDiffEntry(598, 599, 633, 634));
		
		XMLMainParser parser = new XMLMainParser();
		HTMLContentGenerator htmlDiffGenerator = new HTMLContentGenerator(diffs, true);
		
		parser.setContentListener(htmlDiffGenerator);
		parser.parseInputIntoHTMLFormat(new StringReader(str));
		assertEquals("<span class = \"Doctype\"><span class=\"diffEntry diffTypeConflict\" id=\"0\">&lt;DOCTYPE personnel PUBLIC \"PERSONNEL\" \"personal.dtd\"&gt;</span></span      >\n" + 
				"        <span class = \"PI\">&lt;?xml-stylesheet type=\"text/css\" href=\"personal.css\"?&gt;</span      >\n" + 
				"        <span class = \"Element\">&lt;<span class=\"diffEntry diffTypeConflict\" id=\"1\">personnel</span></span      ><span class = \"Element\">&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;person </span      > <span class=\"diffEntry diffTypeConflict\" id=\"2\"><span class = \"attributeName\">id</span>=</span      ><span class = \"attributeValue\">\"harris.anderson\" photo=\"personal-images/harris.anderson.jpg\"</span      ><span class = \"Element\">&gt;</span>                \n" + 
				"        <span class = \"Element\">&lt;name</span      ><span class = \"Element\">&gt;</span>\n" + 
				"        <span class = \"Element\">&lt;given</span      ><span class = \"Element\">&gt;</span>Harris<span class = \"Element\">&lt;/given&gt;</span      >\n" + 
				"        <span class = \"Element\">&lt;family</span      ><span class = \"Element\">&gt;</span>Anderson<span class = \"Element\">&lt;/family&gt;</span      >\n" + 
				"        <span class = \"Element\">&lt;/name&gt;</span      >\n" + 
				"        <span class = \"CDATA\"><span class=\"diffEntry diffTypeConflict\" id=\"3\">&lt;[CDATA[&lt;greeting&gt;Hello, ]]]]]]world!&lt;/greeting&gt;]]&gt;</span></span      > \n" + 
				"        <span class = \"Comment\">&lt;-- <span class=\"diffEntry diffTypeConflict\" id=\"4\">Comment </span>--&gt;</span      >\n" + 
				"        <span class = \"Element\">&lt;email</span      ><span class = \"Element\">&gt;</span>harris.anderson@example.com<span class = \"Element\">&lt;/email&gt;</span      >\n" + 
				"        <span class = \"Element\">&lt;link </span      ><span class = \"attributeName\"> subordinates=</span      ><span class = \"attributeValue\">\"robert.taylor helen.jackson michelle.taylor jason.chen harris.anderson brian.carter\"/<span class=\"diffEntry diffTypeIncoming\" id=\"5\"></span></span      ><span class = \"Element\">&gt;</span>\n" + 
				"        <span class=\"diffEntry diffTypeIncoming\" id=\"6\">    <span class = \"Element\">&lt;/person&gt;</span      ></span>\n" + 
				"            <span class = \"Element\"><span class=\"diffEntry diffTypeConflict\" id=\"6\">&lt;/personnel</span>&gt;<span class=\"diffEntry diffTypeConflict\" id=\"7\"></span      ></span>",htmlDiffGenerator.getResultedText());
	

	}
	
}

