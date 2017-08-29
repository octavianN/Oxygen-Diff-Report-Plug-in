package com.oxygenxml.diffreport;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import java.util.Random;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import com.oxygenxml.diffreport.generator.HTMLContentGenerator;
import com.oxygenxml.diffreport.parser.XMLMainParser;

import ro.sync.diff.api.DiffContentTypes;
import ro.sync.diff.api.DiffException;
import ro.sync.diff.api.DiffOptions;
import ro.sync.diff.api.Difference;
import ro.sync.diff.api.DifferencePerformer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class HTMLPageGenerator 
							extends SwingWorker<Void, Void> {
	
	
	private StandalonePluginWorkspace pluginWorkspaceAccess;
	private ProgressMonitor progressMonitor;
	private URL firstURL;
	private URL secondURL; 
	private File outputFile;
	private int progress;
	private double lengthFile1, lengthFile2;
	


	public void setProgressMonitor(ProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}


	public HTMLPageGenerator() {
		this.progress = 0;
		lengthFile1 = lengthFile2 = 0;
	}
		
	
	public void setPluginWorkspaceAccess(StandalonePluginWorkspace pluginWorkspaceAccess) {
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
	}
	


	
	
	public void generateHTMLReport(URL firstURL, URL secondURL, File outputFile ){
		try {
			File f = new File(firstURL.toURI());
			lengthFile1 = f.length();
			f = new File(secondURL.toURI());
			lengthFile2 = f.length();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.firstURL = firstURL;
		this.secondURL = secondURL;
		this.outputFile = outputFile;
		
		execute();
//		generateHTMLReport();
		
		
	}
	
	private void generateHTMLReport() {
		File htmlForFirstFile = outputFile;
		progressMonitor.setProgress(progress);
		progressMonitor.setNote("Checking Differences: " + progress + " %");
		
		Reader reader1 = null;
		Reader reader2 = null;
		try {
			DifferencePerformer diffPerformer = pluginWorkspaceAccess.getCompareUtilAccess().createDiffPerformer();
			DiffOptions diffOptions = new DiffOptions();
			diffOptions.setEnableHierarchicalDiff(true);
			String contentType = DiffContentTypes.XML_CONTENT_TYPE;

			List<Difference> diffs;
			try {
				reader1 = pluginWorkspaceAccess.getUtilAccess().createReader(firstURL, "UTF-8");
				reader2 = pluginWorkspaceAccess.getUtilAccess().createReader(secondURL, "UTF-8");
				
				diffs = diffPerformer.performDiff(reader1, reader2, null, null, contentType, diffOptions, null);
				progress += 10;
				progressMonitor.setProgress(progress);
				progressMonitor.setNote("Generating FIRST file: " + progress + " %");
				
			} finally {
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

			reader1 = pluginWorkspaceAccess.getUtilAccess().createReader(firstURL, "UTF-8");
			reader2 = pluginWorkspaceAccess.getUtilAccess().createReader(secondURL, "UTF-8");
			generateHTMLFile(htmlForFirstFile, reader1, reader2, diffs, progressMonitor);

		}catch (MalformedURLException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		} catch (DiffException e) {
			e.printStackTrace();
		} finally {
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
	 */
	private void generateHTMLFile(	File outputFile, 
									Reader doc1Reader, 
									Reader doc2Reader,
									List<Difference> diffs, 
									ProgressMonitor progressMonitor) throws IOException{
		
		PrintWriter printWriter = null; 
		try {
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
			 
//			 progressMonitor.setProgress(20);
//			 progressMonitor.setNote("Loaded " + 20 + " %");
			/**
			 * Start Table.		
			 */
			htmlBuilder.append("</style></head>");
			htmlBuilder.append("<body>\n");
			htmlBuilder.append("<table align=\"center\">\n");
			
			/**
			 * Add Buttons for Differences and Swap between texts. 
			 */
			htmlBuilder.append("<div class=\"Floating\"><div class=\"Absolute\">\n"
					+ "<button class=\"NextButtonChild Buttons\" onclick=\"nextChildDiff()\" style=\"height:30px;width:50px\"><b> &#11015; </b></button>\n  "
					+ "<button class=\"NextButton Buttons\" onclick=\"nextDiff()\" style=\"height:30px;width:50px\"><b> &#11247; </b></button>\n  "
					+ "<button class=\"SwapButton Buttons\" onclick=\"swapTexts()\" ><b> swap </b></button>\n  "
					+ "<button class=\"PreviousButton Buttons\" onclick=\"previousDiff()\" style=\"height:30px;width:50px\"><b> &#11245; </b></button>\n"
					+ "<button class=\"PreviousButtonChild Buttons\" onclick=\"previousChildDiff()\" style=\"height:30px;width:50px\"><b> &#11014; </b></button>\n"
					);
			htmlBuilder.append("</div></div>\n");
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
			try {
				parser.parseInputIntoHTMLFormat(doc1Reader,progressMonitor, progress, lengthFile1, false);
			} catch (IOException e) {
				e.printStackTrace();
				htmlBuilder.append("Cannot read first file content: " + e.getMessage());
			}
			htmlBuilder.append(htmlDiffGenerator.getResultedText());
		
			
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
				parser.parseInputIntoHTMLFormat(doc2Reader, progressMonitor, progress, lengthFile2, true);
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


	
	 @Override
     public Void doInBackground() {
//         Random random = new Random();
//         int progress = 0;
//         setProgress(0);
//         try {
//             Thread.sleep(1000);
//             while (progress < 100 && !isCancelled()) {
//                 //Sleep for up to one second.
//                 Thread.sleep(random.nextInt(1000));
//                 //Make random progress.
//                 progress += random.nextInt(20);
//                 setProgress(Math.min(progress, 100));
//             }
//         } catch (InterruptedException ignore) {}
		 generateHTMLReport();
         return null;
     }

    @Override
    public void done() {
        Toolkit.getDefaultToolkit().beep();
//        progressMonitor.setProgress(0);
        if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(outputFile.toURI());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        DiffReportFileChooserDialogue.getInstance().setVisible(false);
    }



}
