package com.oxygenxml.diffreport;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.apache.batik.ext.swing.GridBagConstants;

import automaticSave.ContentPersister;
import automaticSave.ContentPersisterIMPL;
import automaticSave.Interactor;
import constants.ImageConstants;
import constants.Tags;
import ro.sync.diff.api.DiffException;
import ro.sync.diff.api.DiffOptions;
import ro.sync.exml.workspace.api.standalone.ui.ToolbarButton;
import ro.sync.ui.Icons;
import translator.TranslatorImplementation;

/**
 * Dialog with three fields. Compare two files and choose where to save the result.
 * @author Dina_Andrei
 *
 */
public class DiffReportFileChooserDialogue extends JDialog
											implements PropertyChangeListener,
											Interactor{
	
	/**
   * TODO Comment for <code>serialVersionUID</code>
   */
	private static final long serialVersionUID = 8779248380549044360L;
  	private JButton generateDiffButton;
	private JComboBox<String> firstComboBoxField;
	private JComboBox<String> secondComboBoxField;
	private JComboBox<String> thirdComboBoxField;
	private String mostRecentlySavedTextField;
	private ReportGenerator reportGenerator;
	private int algorithmName;
	ContentPersister contentPersister;
	ProgressMonitor progressMonitor;
	/**
	 * Launches the execute() method on the parser and diffGenerator to be executed in background.
	 */
	private SwingWorker<Boolean, Void> swingWorker;
	
	/**
	 * The Singleton Instance of the object.
	 */
	private static volatile DiffReportFileChooserDialogue instance;

	/**
	 * Constructor.
	 * Sets the preferences of the dialog and builds it.
	 */
	private DiffReportFileChooserDialogue() {
		
		
		this.setModal(true);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				DiffReportFileChooserDialogue.this.setVisible(false);
			}
		});
		
		
		
		this.add(crateMainPanel(), BorderLayout.CENTER);
		this.setPreferredSize(new Dimension(450, 215));
		this.pack();
		this.setLocationRelativeTo(null);
		this.setTitle(Tags.DIFF_REPORT_GENERATOR);
		
		
	}

	/**
	 * If the object already exists, returns the created object,
	 * otherwise it creates it and returns it
	 * @return the only created instance of the Object
	 */
    public static DiffReportFileChooserDialogue getInstance() {
        if (instance == null) {
            synchronized (DiffReportFileChooserDialogue.class) {
                if (instance == null) {
                    instance = new DiffReportFileChooserDialogue();
                }
            }
        }
        return instance;
    }
	

	
	
	//Getters and Setters

	public Vector<String> getThirdLabelField() {
		return getListFromComboBox(thirdComboBoxField);
	}



	public void setThirdLabelField(Vector<String> thirdLabelField) {
		this.thirdComboBoxField = new JComboBox<String>(thirdLabelField);
	}
	
	
	public JButton getCompareButton() {
		return generateDiffButton;
	}


	public Vector<String> getFirstLabelField() {
		return getListFromComboBox(firstComboBoxField);
	}

	public void setFirstLabelField(Vector<String> firstLabelField) {
		this.firstComboBoxField = new JComboBox<String>(firstLabelField);
	}

	
	public Vector<String> getSecondLabelField() {
		return getListFromComboBox(secondComboBoxField);
		
	}


	public void setSecondLabelField(Vector<String> secondLabelField) {
		this.secondComboBoxField = new JComboBox<String>(secondLabelField);
	}


	public void setReportGenerator(ReportGenerator reportGenerator) {
		this.reportGenerator = reportGenerator;
	}

	public Object getReportGenerator() {
		return this.reportGenerator;
	}

	/**
	 * Gets All the elements of a JComboBox and puts them in a Vector.
	 * @param comboBox
	 * @return 
	 */
	public Vector<String> getListFromComboBox(JComboBox<String> comboBox){
		Vector<String> vector = new Vector<String>();
		int size = comboBox.getItemCount();
		for (int i = 0; i < size; i++) {
		  String item = comboBox.getItemAt(i);
		  if(item!=null) {
			  vector.addElement(item);
		  }
		}
		
		return vector;
	}
	
	
	/**
	 * Builds the Panel that contains the file choosers and
	 * the one that has the buttons:"Generate Diff" and "Cancel"  
	 * @return the resulted Panel
	 */
	private JPanel crateMainPanel(){
		BorderLayout layout = new BorderLayout();
		JPanel mainPanel = new JPanel(layout);	
		JPanel fileChooserPanel = createFileChooserPanel();
		JPanel buttonPanel = createButtons();
		
		mainPanel.setBorder(new EmptyBorder(0, 0, 5, 0));
		fileChooserPanel.setBorder(new EmptyBorder(15, 15, 0, 5));
		
		mainPanel.add(fileChooserPanel, BorderLayout.NORTH);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		
		return mainPanel;
	}
	
	
	
	/**
	 * Each component is given to a function that creates a panel.
	 * Every panel has three elements:
	 * JLabel, JTextField, ToolbarButton.
	 * @return the panel with all the given components
	 */
	private JPanel createFileChooserPanel(){
		GridBagLayout layout = new GridBagLayout();
		JPanel panel = new JPanel(layout);
		JPanel auxiliaryPanel;
		GridBagConstraints constraints = new GridBagConstraints();
		
		TranslatorImplementation tr = new TranslatorImplementation();
		JLabel fileOne_Label = new JLabel(tr.getTraslation(Tags.LEFT_FILE));
		JLabel fileTwo_Label = new JLabel(tr.getTraslation(Tags.RIGHT_FILE));
		JLabel fileThree_Label = new JLabel(tr.getTraslation(Tags.OUTPUT_FILE));
		
		firstComboBoxField = new JComboBox<String>();
		secondComboBoxField = new JComboBox<String>();
		thirdComboBoxField = new JComboBox<String>();
		
		//loads the paths.
		loadPathsIfExists();
		
		ToolbarButton browseButton1 = createBrowseButton(firstComboBoxField, true);
		ToolbarButton browseButton2 = createBrowseButton(secondComboBoxField, false);
		ToolbarButton browseButton3 = createBrowseButton(thirdComboBoxField, false);
		
		firstComboBoxField.setEditable(true);
		secondComboBoxField.setEditable(true);
		thirdComboBoxField.setEditable(true);
				
		constraints.fill = GridBagConstants.BOTH;
		constraints.anchor = GridBagConstants.WEST;
		constraints.gridx = 0;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.insets = new Insets(0, 6, 5, 0);

		constraints.gridy = 0;
		auxiliaryPanel = createFilePannel(fileOne_Label, firstComboBoxField, browseButton1, 13);
		panel.add(auxiliaryPanel, constraints);

		constraints.gridy ++ ;
		auxiliaryPanel = createFilePannel(fileTwo_Label, secondComboBoxField, browseButton2, 10);
		panel.add(auxiliaryPanel, constraints);
		
		constraints.gridy ++;
		auxiliaryPanel = createComboBoxPanel();
		constraints.fill = GridBagConstants.NONE;
		constraints.insets = new Insets(0, 6, 30, 0);
		panel.add(auxiliaryPanel, constraints);
		
		constraints.gridy++;
		constraints.fill = GridBagConstants.BOTH;
		auxiliaryPanel = createFilePannel(fileThree_Label, thirdComboBoxField, browseButton3, 17);
		constraints.insets = new Insets(0, 6, 0, 0);
		panel.add(auxiliaryPanel, constraints);

		
		return panel;
	}
	
	/**
	 * Loads the paths if they exist to those before closing.
	 */
	private void loadPathsIfExists() {
		contentPersister = new ContentPersisterIMPL();
		contentPersister.loadPath(this);
		firstComboBoxField.setSelectedItem(null);
		secondComboBoxField.setSelectedItem(null);
		thirdComboBoxField.setSelectedItem(null);
		
	}
	
	/**
	 * Puts all the parameters together into a panel with certain constraints
	 * @param text - the text area 
	 * @param field - The field with the URL path to the file
	 * @param button - Browse button to choose the path
	 * @param ipadxMargin - Distance between some elements
	 * @return
	 */
	private JPanel createFilePannel(JLabel text, JComboBox<String> field, JButton button, int ipadxMargin) {
		GridBagLayout layout = new GridBagLayout();
		JPanel panel = new JPanel(layout);
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.anchor = GridBagConstants.WEST;
		constraints.fill = GridBagConstants.NONE;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.ipadx = ipadxMargin;
		panel.add(text, constraints);
		
		
		constraints.anchor = GridBagConstants.CENTER;
		constraints.fill = GridBagConstants.BOTH;
		constraints.gridx ++;
		constraints.weightx = 1;
		constraints.weighty = 0.1;
		panel.add(field, constraints);
		
		
		constraints.anchor = GridBagConstants.EAST;
		constraints.fill = GridBagConstants.NONE;
		constraints.gridx ++;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.ipadx = 0;
		constraints.insets = new Insets(0, 3, 0, 3);
		panel.add(button, constraints);
		
		return panel;
	}
	
	/**
	 * Crates the ComboBox that lets the user choose the algorithm to differentiate the files.
	 * @return the panel that contains the two components: Label + ComboBox
	 */
	private JPanel createComboBoxPanel() {
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel panel = new JPanel(layout);
		Integer[] types = {DiffOptions.AUTO, DiffOptions.XML_FAST, DiffOptions.XML_ACCURATE};
		JLabel labelAlgorithm= new JLabel();
		JComboBox<Integer> algorithmType = new JComboBox<Integer>(types);
		TranslatorImplementation tr = new TranslatorImplementation();
		labelAlgorithm.setText(tr.getTraslation(Tags.ALGORITHM));
		
		// takes the ID's from the combo Box and transforms them into their correlating name. 
		algorithmType.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				String rendererVal = "Auto";
				switch ((int)value) {
				case DiffOptions.XML_ACCURATE:
					rendererVal = "XML Accurate";
					break;
				case DiffOptions.XML_FAST:
					rendererVal = "XML Fast";
					break;
				default:
					break;
				}
				
				return super.getListCellRendererComponent(list, rendererVal, index, isSelected, cellHasFocus);
			}
		});
		
		//Action listener for the ComboBox. It is only remembered the chosen Algorithm.
		algorithmType.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
		        int algType = algorithmType.getItemAt(algorithmType.getSelectedIndex());
		        algorithmName = algType;
				
			}
		});
		
		constraints.fill = GridBagConstants.HORIZONTAL;
		constraints.anchor = GridBagConstants.WEST;
		constraints.gridx = 0;
		constraints.weightx = 1;
		constraints.weighty = 0;
		panel.add(labelAlgorithm, constraints);
		
		constraints.gridx++;
		constraints.insets = new Insets(0, 6, 0, 0);
		panel.add(algorithmType, constraints);
		return panel;
	}
	
	/**
	 * Creates a Browse button and remembers the given path
	 * in the field param.
	 * @param field -> The field responsible with remembering the Path
	 * @return the browsing Button for each of the files
	 */
	private ToolbarButton createBrowseButton(final JComboBox<String> field, boolean firstFile){
		ImageIcon imageIcon = Icons.getIcon(ImageConstants.DOC_BROWSE_BUTTON);
		AbstractAction browseAction = new AbstractAction("Browse", imageIcon) {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(mostRecentlySavedTextField == null) {
					if(firstFile) {
						mostRecentlySavedTextField = (String)secondComboBoxField.getSelectedItem();
					} else {
						mostRecentlySavedTextField = (String)firstComboBoxField.getSelectedItem();
					}
				}
				File file = null ;
				if(mostRecentlySavedTextField != null) {
					file = new File(mostRecentlySavedTextField);
					
				} 
				JFileChooser fileChooser;
				
				if(file == null || !(file.exists())) {
					fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				} else {
					fileChooser = new JFileChooser(file.getParent());
				}
				FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files","xml", "html");
				fileChooser.setFileFilter(filter);

				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					if(field.getItemCount() > 10) {
						field.removeItemAt(0);
					}
					field.addItem(selectedFile.toString());
					field.setSelectedItem(selectedFile.toString());
					
				}
				
				mostRecentlySavedTextField = (String)field.getSelectedItem();
			}
		};
		
		return new ToolbarButton(browseAction, false);
	}
	
	/**
	 * Creating the panel which contains the two functions "Generate Diff" and
	 * "Cancel" aligned at the right bottom corner 
	 * @return The panel with the two buttons: Compare and Cancel
	 */
	private JPanel createButtons(){
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		Component box1 = Box.createRigidArea(new Dimension(7,0));
		Component box2= Box.createRigidArea(new Dimension(7,0));
		box1.setBackground(Color.WHITE);
		box2.setBackground(Color.WHITE);
		
		TranslatorImplementation tr = new TranslatorImplementation();
		
		/**
		 * Generate Diff Button. 
		 */
		generateDiffButton = new JButton(tr.getTraslation(Tags.GENERATE_DIFF));
		generateDiffButton.setPreferredSize(new Dimension(97, 25));
		generateDiffButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generateDiff();
			}
		});
		panel.add(generateDiffButton);
		panel.add(box1);

		/**
		 * Cancel Button.
		 */
		JButton cancelButton = new JButton(tr.getTraslation(Tags.CLOSE));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand() != null)
					setVisible(false);
			}
		});
		cancelButton.setPreferredSize(new Dimension(75, 25));
		panel.add(cancelButton);
		panel.add(box2);
		
		return panel;
	}

	

	/**
	 * Gets the inputs, gives them to the generateHTML function.
	 * Is activated when the "Generate Diff" Button is pressed
	 * Upon pressing, the swingWorker method execute() sends the
	 * process in background. A Process Monitor will appear and
	 * the html page will pop when the progress bar reaches 100.
	 */
	private void generateDiff() {
		try {
			//filename of the output file
			String fileName = thirdComboBoxField.getSelectedItem().toString();
			
			//in case the user gives only the name of the file in which the HTML should be saved,
			//it will be created a file with that name on the desktop
			if(!fileName.contains(File.separator)) {
				fileName = FileSystemView.getFileSystemView().getHomeDirectory().toString() + File.separator + fileName;
				JOptionPane.showMessageDialog(null, 
						 "Output_File: " + fileName +"\nwas saved on Desktop: ", 
						 "", 
						 JOptionPane.INFORMATION_MESSAGE);
			}
			//if the user forgot to give the ".html" extension, it is automatically added
			if(fileName.length() > 5) {
				if(fileName.substring(fileName.length() - 5) != ".html") {
					fileName += ".html";
				}
			}else {
				fileName += ".html";
			}
			
			String leftFile = firstComboBoxField.getSelectedItem().toString();
			String rightFile = secondComboBoxField.getSelectedItem().toString();
			File outputFile = new File(fileName);
			
			// If any of the input files does not exist or have an unidentified extension, an exception is thrown
			if (!new File(leftFile).exists() || !new File(rightFile).exists() 
					|| ( fileName.length() > 5 && !outputFile.getParentFile().exists())){				
				throw new FileNotFoundException();
			}
			
			outputFile.createNewFile();
			

			progressMonitor = new ProgressMonitor(this, "Generating Diff", "", 0, 100);
			//"Generate Button" is pressed -> a new HTMLPageGenerator is created
			PageGenerator pageGenerator = new HTMLPageGenerator();
			/**
			 * SwingWorker execute() method works with the ProgressMonitor.
			 * It is used as a wrapper for the pageGenerator.
			 */
			swingWorker = new SwingWorker<Boolean,Void>() {
				/**
				 * Launches on execute() method.
				 * generateHTMLReport() for big inputs takes a long time so has to be ran in background 
				 * @throws DiffException 
				 * @throws MalformedURLException 
				 * 
				 */
				@Override
				protected Boolean doInBackground()  {
					try {
						pageGenerator.generateHTMLReport(new File(leftFile).toURI().toURL(),
								new File(rightFile).toURI().toURL(),
								outputFile,
								algorithmName);
					} catch (DiffException e) {
						JOptionPane.showMessageDialog(null, 
								 "Cannot perform comparison: " + e.getLocalizedMessage(), 
								 "", 
								 JOptionPane.INFORMATION_MESSAGE);
						 setVisible(true);
						 progressMonitor.setProgress(100);
						 
						e.printStackTrace();
						return false;
					}catch (MalformedURLException ew) {
					
					}
					return true;
				}
				/**
				 * When the Progress Bar reaches 100, the HTML is launched in the browser and the
				 * Dialog disappears
				 */
				@Override
				protected void done() {
					try {
						boolean isDone = get().booleanValue();
						Toolkit.getDefaultToolkit().beep();
						if (Desktop.isDesktopSupported()) {
							if(isDone) {
								try {
									// save the paths in case the Main program (Oxygen) closes
									contentPersister.savePath(DiffReportFileChooserDialogue.this);
									Desktop.getDesktop().browse(outputFile.toURI());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						DiffReportFileChooserDialogue.getInstance().setVisible(false);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						e1.printStackTrace();
					}
			    }
				
			};
			
			reportGenerator.setPageGenerator(pageGenerator);
			swingWorker.addPropertyChangeListener(this);
			
			/**
			 * Sets an interface used as a wrapper for the ProgressMonitor that only uses
			 * two of the classes methods.
			 */
			pageGenerator.setProgressMonitor(new IProgressMonitor() {
				
				@Override
				public void setProgress(int progress) {
					progressMonitor.setProgress(progress);
				}
				
				
				@Override
				public void setNote(String note) {
					progressMonitor.setNote(note);
					
				}


				@Override
				public void setMillisToDecideToPopup(int mili) {
					progressMonitor.setMillisToDecideToPopup(mili);
					
				}
			});
			
			swingWorker.execute();

		} catch (FileNotFoundException e1) {
			//If the input files are not valid prints an error message.
			 JOptionPane.showMessageDialog(null, 
					 "The path is not Valid", 
					 "", 
					 JOptionPane.INFORMATION_MESSAGE);
			 setVisible(true);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}
	
	/**
	 * Occur whenever the value of a bound property changes for a bean.
	 * Is set on the SwingWorker.
	 * When pressing the Cancel Button on the Progress Dialog, it closes.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (progressMonitor != null) {
			if ("progress" == evt.getPropertyName()) {
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
				String message = String.format("Completed %d%%.\n", progress);
				progressMonitor.setNote(message);
				if (progressMonitor.isCanceled() || swingWorker.isDone()) {
					Toolkit.getDefaultToolkit().beep();
					if (progressMonitor.isCanceled()) {
						swingWorker.cancel(true);
					} else {
					}
				}
			}
		}
    }
	

}
