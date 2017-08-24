package com.oxygenxml.diffreport;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.ProgressMonitor;
import javax.swing.text.BadLocationException;

import com.oxygenxml.diffreport.generator.HTMLContentGenerator;
import com.oxygenxml.diffreport.parser.XMLMainParser;

import ro.sync.diff.api.DiffContentTypes;
import ro.sync.diff.api.DiffException;
import ro.sync.diff.api.DiffOptions;
import ro.sync.diff.api.DiffProgressEvent;
import ro.sync.diff.api.DiffProgressListener;
import ro.sync.diff.api.Difference;
import ro.sync.diff.api.DifferencePerformer;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ToolbarComponentsCustomizer;
import ro.sync.exml.workspace.api.standalone.ToolbarInfo;
import ro.sync.exml.workspace.api.standalone.ui.ToolbarButton;


/**
 * Plugin extension - workspace access extension.
 */
public class DiffReportPlugin implements WorkspaceAccessPluginExtension, ReportGenerator {
  /**
   * The custom messages area. A sample component added to your custom view.
   */
private StandalonePluginWorkspace pluginWorkspaceAccess;

  /**
   * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationStarted(ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace)
   */
  @Override
  public void applicationStarted(final StandalonePluginWorkspace pluginWorkspaceAccess) {
	  
	  //You can set or read global options.
	  //The "ro.sync.exml.options.APIAccessibleOptionTags" contains all accessible keys.
	  //		  pluginWorkspaceAccess.setGlobalObjectProperty("can.edit.read.only.files", Boolean.FALSE);
	  // Check In action
	  this.pluginWorkspaceAccess = pluginWorkspaceAccess;
	//You can access the content inside each opened WSEditor depending on the current editing page (Text/Grid or Author).  
	  // A sample action which will be mounted on the main menu, toolbar and contextual menu.
	final Action selectionSourceAction = createShowSelectionAction(pluginWorkspaceAccess);

	  pluginWorkspaceAccess.addEditorChangeListener(
			  new WSEditorChangeListener() {
				  @Override
				  public boolean editorAboutToBeOpenedVeto(URL editorLocation) {
					  //You can reject here the opening of an URL if you want
					  return true;
				  }
				  @Override
				  public void editorOpened(URL editorLocation) {
					  checkActionsStatus(editorLocation);
				  }

				  // Check actions status
				  private void checkActionsStatus(URL editorLocation) {
					  WSEditor editorAccess = pluginWorkspaceAccess.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
					  if (editorAccess != null) {
						  selectionSourceAction.setEnabled(
								  EditorPageConstants.PAGE_AUTHOR.equals(editorAccess.getCurrentPageID())
								  || EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID()));
					  }
				  }

				  @Override
				  public void editorClosed(URL editorLocation) {
					  //An edited XML document has been closed.
				  }

				  /**
				   * @see ro.sync.exml.workspace.api.listeners.WSEditorChangeListener#editorAboutToBeClosed(java.net.URL)
				   */
				  @Override
				  public boolean editorAboutToBeClosed(URL editorLocation) {
					  //You can veto the closing of an XML document.
					  //Allow close
					  return true;
				  }

				  /**
				   * The editor was relocated (Save as was called).
				   * 
				   * @see ro.sync.exml.workspace.api.listeners.WSEditorChangeListener#editorRelocated(java.net.URL, java.net.URL)
				   */
				  @Override
				  public void editorRelocated(URL previousEditorLocation, URL newEditorLocation) {
					  //
				  }

				  @Override
				  public void editorPageChanged(URL editorLocation) {
					  checkActionsStatus(editorLocation);
				  }

				  @Override
				  public void editorSelected(URL editorLocation) {
					  checkActionsStatus(editorLocation);
				  }

				  @Override
				  public void editorActivated(URL editorLocation) {
					  checkActionsStatus(editorLocation);
				  }
			  }, 
			  StandalonePluginWorkspace.MAIN_EDITING_AREA);


	  //You can use this callback to populate your custom toolbar (defined in the plugin.xml) or to modify an existing Oxygen toolbar 
	  // (add components to it or remove them) 
	  pluginWorkspaceAccess.addToolbarComponentsCustomizer(new ToolbarComponentsCustomizer() {
		  /**
		   * @see ro.sync.exml.workspace.api.standalone.ToolbarComponentsCustomizer#customizeToolbar(ro.sync.exml.workspace.api.standalone.ToolbarInfo)
		   */
		  public void customizeToolbar(ToolbarInfo toolbarInfo) {
			  //The toolbar ID is defined in the "plugin.xml"
			  if("DiffReportPluginToolbarID".equals(toolbarInfo.getToolbarID())) {
				  List<JComponent> comps = new ArrayList<JComponent>(); 
				  JComponent[] initialComponents = toolbarInfo.getComponents();
				  boolean hasInitialComponents = initialComponents != null && initialComponents.length > 0; 
				  if (hasInitialComponents) {
					  // Add initial toolbar components
					  for (JComponent toolbarItem : initialComponents) {
						  comps.add(toolbarItem);
					  }
				  }
				  
				  
				  //Add your own toolbar button using our "ro.sync.exml.workspace.api.standalone.ui.ToolbarButton" API component
				  ToolbarButton toolbarActivationButton = createToolbarButton(pluginWorkspaceAccess);
				  comps.add(toolbarActivationButton);
				  toolbarInfo.setComponents(comps.toArray(new JComponent[0]));
			  } 
		  }
	  });


  }
  
  
  /**
   * Method that shows the "PopUpDialogue" on the screen
   * by pressing the button in the toolbar
   * @param pluginWorkspaceAccess 
   * @return the button 
   */
  	private ToolbarButton createToolbarButton(final StandalonePluginWorkspace pluginWorkspaceAccess){
  		ImageIcon imageIcon = new ImageIcon("C:\\Users\\intern3\\git\\Oxygen-Diff-Report-Plug-in\\oxygen-plugin-workspace-access\\src\\Resources\\DiffFiles24.png");
		@SuppressWarnings("serial")
		AbstractAction showDiffAction = new AbstractAction("HTML: Show Diff", imageIcon) {
  			@Override
  			public void actionPerformed(ActionEvent e) {
  				if(e.getActionCommand() != null){
  					DiffReportFileChooserDialogue myDialog = DiffReportFileChooserDialogue.getInstance();
  					if (myDialog.getReportGenerator() == null) {
  						myDialog.setReportGenerator(DiffReportPlugin.this);
  					}
  					myDialog.setVisible(true);
  					myDialog.pack();
  				}
  				
  			}
  		};
  		ToolbarButton button = new ToolbarButton(showDiffAction, false);
  		return button;
  	}
  	

	
  	/**
  	 * This function helps the printTheDiferencesInTheConsole method
  	 * parse the file and print the differences between the two XMLs
  	 * @param reader the current file we are reading from
  	 * @param difference the difference we are interested in
  	 */
	@SuppressWarnings("unused")
	private void printTheDiferencesInTheConsoleCharacterParser(Reader reader, Difference difference){
		int i, contor = 0;
		
		try {
			while((i = reader.read()) != -1){
				if (contor >= difference.getLeftIntervalStart() && contor < difference.getLeftIntervalEnd()) {
					
					if (((char)i != '\r')) {
						System.out.print((char)i);
						
					}
				}
				contor++;
				if (contor > difference.getLeftIntervalEnd()) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public void generateHTMLReport(URL firstURL, URL secondURL, File outputFile, ProgressMonitor progressMonitor){
		
		File htmlForFirstFile= outputFile;
		
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

				
				DiffProgressListener listener = new DiffProgressListener() {
					
					@Override
					public void update(DiffProgressEvent arg0) {
						System.out.println("Progress: " + arg0.getCount());
						progressMonitor.setProgress(arg0.getCount());
					}
					
					@Override
					public void start() {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void finished() {
						// TODO Auto-generated method stub
						
					}
				};
				
				diffs = diffPerformer.performDiff(reader1, reader2, null, null, contentType, diffOptions, listener);
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
			generateHTMLFile(htmlForFirstFile, reader1, reader2, diffs);

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
	private void generateHTMLFile(File outputFile, Reader doc1Reader, Reader doc2Reader,List<Difference> diffs) throws IOException{
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
				parser.parseInputIntoHTMLFormat(doc1Reader);
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
				parser.parseInputIntoHTMLFormat(doc2Reader);
			} catch (IOException e) {
				e.printStackTrace();
				htmlBuilder.append("Cannot read second file content: " + e.getMessage());
			}
			htmlBuilder.append(htmlDiffGenerator.getResultedText());
			
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

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			printWriter.close();
		}
	}
	
	
	/**
	 * Create the Swing action which shows the current selection.
	 * 
	 * @param pluginWorkspaceAccess The plugin workspace access.
	 * @return The "Show Selection" action
	 */
	@SuppressWarnings("serial")
	private AbstractAction createShowSelectionAction(
			final StandalonePluginWorkspace pluginWorkspaceAccess) {
		return new AbstractAction("Show Selection") {
			  @Override
			  public void actionPerformed(ActionEvent actionevent) {
				  //Get the current opened XML document
				  WSEditor editorAccess = pluginWorkspaceAccess.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
				  // The action is available only in Author mode.
				  if(editorAccess != null){
					  if (EditorPageConstants.PAGE_AUTHOR.equals(editorAccess.getCurrentPageID())) {
						  WSAuthorEditorPage authorPageAccess = (WSAuthorEditorPage) editorAccess.getCurrentPage();
						  AuthorDocumentController controller = authorPageAccess.getDocumentController();
						  if (authorPageAccess.hasSelection()) {
							  AuthorDocumentFragment selectionFragment;
							  try {
								  // Create fragment from selection
								  selectionFragment = controller.createDocumentFragment(
										  authorPageAccess.getSelectionStart(),
										  authorPageAccess.getSelectionEnd() - 1
										  );
								  // Serialize
								  String serializeFragmentToXML = controller.serializeFragmentToXML(selectionFragment);
								  // Show fragment
								  pluginWorkspaceAccess.showInformationMessage(serializeFragmentToXML);
							  } catch (BadLocationException e) {
								  pluginWorkspaceAccess.showErrorMessage("Show Selection Source operation failed: " + e.getMessage());
							  }
						  } else {
							  // No selection
							  pluginWorkspaceAccess.showInformationMessage("No selection available.");
						  }
					  } else if (EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID())) {
						  WSTextEditorPage textPage = (WSTextEditorPage) editorAccess.getCurrentPage();
						  if (textPage.hasSelection()) {
							  pluginWorkspaceAccess.showInformationMessage(textPage.getSelectedText());
						  } else {
							  // No selection
							  pluginWorkspaceAccess.showInformationMessage("No selection available.");
						  }
					  }
				  }
			  }
		  };
	}
  
  /**
   * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationClosing()
   */
  @Override
  public boolean applicationClosing() {
	  //You can reject the application closing here
    return true;
  }
}