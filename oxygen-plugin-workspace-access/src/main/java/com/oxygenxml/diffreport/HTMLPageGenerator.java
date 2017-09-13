package com.oxygenxml.diffreport;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import com.oxygenxml.diffreport.generator.HTMLContentGenerator;
import com.oxygenxml.diffreport.parser.XMLMainParser;

import ro.sync.diff.api.DiffContentTypes;
import ro.sync.diff.api.DiffException;
import ro.sync.diff.api.DiffOptions;
import ro.sync.diff.api.Difference;
import ro.sync.diff.api.DifferencePerformer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class HTMLPageGenerator  {
	
	/**
	 * Algorithm that generates the differences between the given files
	 */
	private StandalonePluginWorkspace pluginWorkspaceAccess;
	/**
	 * Progress Bar pop-up dialog
	 */
	private IProgressMonitor progressMonitor;
	private URL firstURL;
	private URL secondURL; 
	private File outputFile;
	/**
	 * Current progress. An integer between 0 and 100.
	 * When parsing each tag, the progress will be incremented.
	 */
	private int progress;
	private double lengthFile1, lengthFile2, totalLength;
	
	public HTMLPageGenerator() {
		this.progress = 0;
		lengthFile1 = lengthFile2 = totalLength = 0;
	}
		

	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	
	public void setPluginWorkspaceAccess(StandalonePluginWorkspace pluginWorkspaceAccess) {
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
	}
	
	
	/**
	 * Saves locally the URLs and File and determines the length of the files
	 * The length is used to check the progress when parsing
	 * @param firstURL Path of the first file as a URL
	 * @param secondURL Path of the second file as a URL
	 * @param outputFile Path of the output file
	 */
	public void generateHTMLReport(URL firstURL, URL secondURL, File outputFile ){
		try {
			File f = new File(firstURL.toURI());
			lengthFile1 = f.length();
			f = new File(secondURL.toURI());
			lengthFile2 = f.length();
			totalLength = lengthFile1 + lengthFile2;
			
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		this.firstURL = firstURL;
		this.secondURL = secondURL;
		this.outputFile = outputFile;
		
		try {
			generateHTMLReport();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Opens the Two XML files and runs the difference algorithm on them.
	 * The algorithm runs in 10% the time of the whole program execution time.
	 * The differences are put in a list that is given further to the parser.
	 * @throws URISyntaxException
	 */
	private void generateHTMLReport() throws URISyntaxException {
		File htmlForFirstFile = outputFile;

		Reader reader1 = null;
		Reader reader2 = null;
		try {
			DifferencePerformer diffPerformer = pluginWorkspaceAccess.getCompareUtilAccess().createDiffPerformer();
			DiffOptions diffOptions = new DiffOptions();
			diffOptions.setEnableHierarchicalDiff(true);
			String contentType = DiffContentTypes.XML_CONTENT_TYPE;
			List<Difference> diffs;
			try {
				
				progressMonitor.setMillisToDecideToPopup(0);
				reader1 = pluginWorkspaceAccess.getUtilAccess().createReader(firstURL, "UTF-8");
				reader2 = pluginWorkspaceAccess.getUtilAccess().createReader(secondURL, "UTF-8");
				
				//Algorithm return a list of differences
				diffs = diffPerformer.performDiff(reader1, reader2, null, null, contentType, diffOptions, null);
				progress += 10;
				progressMonitor.setProgress(progress);
				progressMonitor.setNote("Generating FIRST file: " + progress + " %");
				
			} finally {
				//closing the files
				if (reader1 != null) {
					try {
						reader1.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (reader2 != null) {
					try {
						reader2.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			//reopening the files
			reader1 = pluginWorkspaceAccess.getUtilAccess().createReader(firstURL, "UTF-8");
			reader2 = pluginWorkspaceAccess.getUtilAccess().createReader(secondURL, "UTF-8");
			generateHTMLFile(htmlForFirstFile, reader1, reader2, diffs);

		}catch (MalformedURLException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		} catch (DiffException e) {
			e.printStackTrace();
		} finally {
			//closing the files
			if (reader1 != null) {
				try {
					reader1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader2 != null) {
				try {
					reader2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * Receives the file that needs to be written and the file that
	 * requires to be parsed. Then generates a HTML with the
	 * two XML files highlighting their differences.
	 * The Colors and Interactions between the two texts are written
	 * in CSS and JavaScript.
	 * @param outputFile The file where the HTML file is written 
	 * @param doc1Reader Reader for Left File
	 * @param doc2Reader Reader for Right File
	 * @param diffs list of differences between the files
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private void generateHTMLFile(	File outputFile, 
									Reader doc1Reader, 
									Reader doc2Reader,
									List<Difference> diffs) throws IOException, URISyntaxException{
		
		PrintWriter printWriter = null; 
		try {
			int diffTypeConflict = 0;
			int diffTypeOutgoing = 0;
			int diffTypeIncoming = 0;
			printWriter = new PrintWriter(outputFile);
			StringBuilder htmlBuilder = new StringBuilder();
			/**
			 * Begin the HTML file.			
			 */
			htmlBuilder.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
			htmlBuilder.append("<head><title>Diff Report</title>");
			htmlBuilder.append("<style>/*--------------------------------------------\n");
			
			/**
			 * Add the CSS file.
			 */
			BufferedReader cssReader = new BufferedReader(new FileReader(new File(
					"C:\\Users\\intern3\\git\\Oxygen-Diff-Report-Plug-in\\oxygen-plugin-workspace-access\\src\\Resources\\css")));
			 String line;
			 while ((line = cssReader.readLine()) != null) {
				 htmlBuilder.append(line + "\n");
			 }
			 cssReader.close();
			 
			/**
			 * Start Table.		
			 */
			htmlBuilder.append("</style></head>");
			htmlBuilder.append("<body>\n");
			htmlBuilder.append("<table align=\"center\">\n");
			
			/**
			 * Add Buttons for Differences and Swap between texts. 
			 */
			String leftPath = new File(firstURL.toURI()).toString();
			String rightPath = new File(secondURL.toURI()).toString();
			/**
			 * in the left and right of the buttons, the name of the compared files are displayed
			 */
			htmlBuilder.append("<div class=\"FloatingUp\">\n"
					+ "<div class=\"NameOfFileLeft\">"
					+ "<b>"+givePathGetFileName(leftPath)+"</b>"
					+ "</div>\n"
					+ "<div class=\"NameOfFileRight\">\n"
					+ "<b>"+givePathGetFileName(rightPath)+"</b>"
					+ "</div>\n"
					+ "<div class=\"Button\">\n"
					+ "<button class=\"NextButtonChild Buttons\" onclick=\"nextChildDiff()\" style=\"height:30px;width:30px\"></button>\n  "
					+ "<button class=\"NextButton Buttons\" onclick=\"nextDiff()\" style=\"height:30px;width:30px\"></button>\n  "
					+ "<button class=\"SwapButton Buttons\" onclick=\"swapTexts()\" style=\"height:30px;width:30px\"></button>\n  "
					+ "<button class=\"PreviousButton Buttons\" onclick=\"previousDiff()\" style=\"height:30px;width:30px\"></button>\n"
					+ "<button class=\"PreviousButtonChild Buttons\" onclick=\"previousChildDiff()\" style=\"height:30px;width:30px\"></button>\n"
					+ "</div>\n"	
					);
			htmlBuilder.append("</div>\n");
			htmlBuilder.append("<tr id=\"tr1\">\n");
			htmlBuilder.append("<td id = \"b1\" class=\"spaceUnder block1\">\n");
			htmlBuilder.append("<div class=\"Scroll1\"><div class=\"Container1\" id=\"swap1\">\n");
			htmlBuilder.append("<pre>\n");
			
			/**
			 * Parse first document.
			 */
			XMLMainParser parser = new XMLMainParser();
			HTMLContentGenerator htmlDiffGenerator = new HTMLContentGenerator(diffs, true);
			parser.setContentListener(htmlDiffGenerator);	
			int oldPercentage = 10;
			try {
				oldPercentage = parser.parseInputIntoHTMLFormat(doc1Reader,progressMonitor, progress, lengthFile1, totalLength, oldPercentage, false);
			} catch (IOException e) {
				e.printStackTrace();
				htmlBuilder.append("Cannot read first file content: " + e.getMessage());
			}
			htmlBuilder.append(htmlDiffGenerator.getResultedText());
			
			diffTypeConflict = htmlDiffGenerator.getDiffTypeConflict();
			diffTypeOutgoing = htmlDiffGenerator.getDiffTypeOutgoing();
			diffTypeIncoming = htmlDiffGenerator.getDiffTypeIncoming();
		
			/**
			 * Add canvas.
			 */
			htmlBuilder.append("</pre>\n");
			htmlBuilder.append("</div></div>\n");
			htmlBuilder.append("</td>\n");
			htmlBuilder.append("<td class=\"canvasTD\">\n");		
			htmlBuilder.append("<div class=\"canvasContainer\"><canvas id=\"myCanvas\" width=\"40\"  height=\"300\";\">\n" + 
					"</canvas></div>");
			htmlBuilder.append("</td>\n");
			
			/**
			 * Parse second document.
			 */
			htmlBuilder.append("<td id=\"b2\" class=\"spaceUnder block2\">\n");
			htmlBuilder.append("<div class=\"Scroll2\"><div class=\"Container2\" id=\"swap2\">\n");
			htmlBuilder.append("<pre>\n");
			htmlDiffGenerator = new HTMLContentGenerator(diffs, false);
			parser.setContentListener(htmlDiffGenerator);
			try {
				parser.parseInputIntoHTMLFormat(doc2Reader, progressMonitor, progress, lengthFile2, totalLength, oldPercentage, true);
			} catch (IOException e) {
				e.printStackTrace();
				htmlBuilder.append("Cannot read second file content: " + e.getMessage());
			}
			htmlBuilder.append(htmlDiffGenerator.getResultedText());
			
			progressMonitor.setProgress(90);
			progressMonitor.setNote("Finalizing!");
			
			/**
			 * End Table.
			 */
			htmlBuilder.append("</pre>\n");
			htmlBuilder.append("</div></div>\n");
			htmlBuilder.append("</td>\n");
			htmlBuilder.append("</tr>\n");
			htmlBuilder.append("</table>\n");
			htmlBuilder.append("<div class=\"FloatingDown\">\n"
					+ "<ul>"
					+ "<li class=\"unu\"> <span class=\"unu\">Modified:" + diffTypeConflict + "</span></li>\n"
					+ "<li class=\"doi\"> <span class=\"unu\">Inserted:" + diffTypeOutgoing + "</span></li>\n"
					+ "<li class=\"trei\"> <span class=\"unu\">Removed: " + diffTypeIncoming + "</span></li>\n"
					+ "</ul>");
			htmlBuilder.append("</p></div>");
			htmlBuilder.append("</body>\n");
			htmlBuilder.append("</html>\n");
			
			/**
			 * Script.
			 */
			htmlBuilder.append("<script>");
			BufferedReader jsReader = new BufferedReader(new FileReader(new File(
					"C:\\Users\\intern3\\git\\Oxygen-Diff-Report-Plug-in\\oxygen-plugin-workspace-access\\src\\Resources\\script.js")));
			while ((line = jsReader.readLine()) != null) {
				htmlBuilder.append(line + "\n");
			}
			jsReader.close();
			htmlBuilder.append("</script>");
			
			/**
			 * Write result into output file
			 */
			String html = htmlBuilder.toString();
			printWriter.print(html);
			printWriter.flush();
			progressMonitor.setProgress(100);
			progressMonitor.setNote("Done ");
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			printWriter.close();
		}
	}

	/**
	 * Takes the Path that is given <b>"C:\some\text\FILE_NAME.extension"</b>
	 * and returns the last file with the extension
	 * @param filePath
	 * @return
	 */
	private String givePathGetFileName(String filePath) {
		String result = "";
		int offset = 0;
		//sets the offset to the "." before the extension
		for(int i = 0 ; i < filePath.length(); i++) {
			if(filePath.charAt(i) == '.') {
				offset = i;
				break;
			}
		}
		//if the file has an extension
		if(offset != 0) {
			String aux="";
			//gets the string from the "." to the first "\"
			for(int i = offset ; i > 0; i--) {
				if(filePath.charAt(i) != '\\') {
					aux += filePath.charAt(i);
				} else {
					break;
				}
			}
			//the result has to be reversed because it takes the characters backwards
			result += new StringBuilder(aux).reverse().toString();
			//adds to the result the extension
			for(int i = offset+1 ; i < filePath.length(); i++) {
				result+= filePath.charAt(i);
			}
		}
		
		return result;
	}



}
