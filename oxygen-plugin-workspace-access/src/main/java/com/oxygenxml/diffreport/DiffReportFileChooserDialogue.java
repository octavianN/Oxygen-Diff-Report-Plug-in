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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.apache.batik.ext.swing.GridBagConstants;

import ro.sync.exml.workspace.api.standalone.ui.ToolbarButton;

/**
 * Dialog with three fields. Compare two files and choose where to save the result
 * @author intern3
 *
 */
public class DiffReportFileChooserDialogue extends JDialog {
	
	private JButton generateDiffButton;
	private JTextField firstLabelField;
	private JTextField secondLabelField;
	private JTextField thirdLabelField;
	private ReportGenerator reportGenerator; 
	
	/**
	 * The Singleton Instance of the object
	 */
	private static volatile DiffReportFileChooserDialogue instance;

	/**
	 * Constructor.
	 * Sets the preferences of the dialogie and builds it
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
		this.setPreferredSize(new Dimension(450, 180));
		this.pack();
		this.setLocationRelativeTo(null);
		this.setTitle("Diff Report Generator");
		
		
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
	public JTextField getThirdLabelField() {
		return thirdLabelField;
	}

	public void setThirdLabelField(JTextField thirdLabelField) {
		this.thirdLabelField = thirdLabelField;
	}
	
	
	public JButton getCompareButton() {
		return generateDiffButton;
	}


	public JTextField getFirstLabelField() {
		return firstLabelField;
	}

	public void setFirstLabelField(JTextField firstLabelField) {
		this.firstLabelField = firstLabelField;
	}

	
	public JTextField getSecondLabelField() {
		return secondLabelField;
	}


	public void setSecondLabelField(JTextField secondLabelField) {
		this.secondLabelField = secondLabelField;
	}


	public void setReportGenerator(ReportGenerator reportGenerator) {
		this.reportGenerator = reportGenerator;
	}

	public Object getReportGenerator() {
		return this.reportGenerator;
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
		fileChooserPanel.setBorder(new EmptyBorder(15, 5, 15, 5));
		
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
		
		JLabel fileOne_Label = new JLabel("Left File:  ");
		JLabel fileTwo_Label = new JLabel("Right File: ");
		JLabel fileThree_Label = new JLabel("Output:  ");
		
		firstLabelField = new JTextField(20);
		secondLabelField = new JTextField(20);
		thirdLabelField = new JTextField(20);
		
		ToolbarButton browseButton1 = createBrowseButton(firstLabelField);
		ToolbarButton browseButton2 = createBrowseButton(secondLabelField);
		ToolbarButton browseButton3 = createBrowseButton(thirdLabelField);
		
		firstLabelField.setText("C:/Users/intern3/Desktop/myFiles/diffSample/EngliGB.xml");
		secondLabelField.setText("C:/Users/intern3/Desktop/myFiles/diffSample/EngliUS.xml");
		thirdLabelField.setText("C:/Users/intern3/Desktop/myFiles/diffSample/htmlFile.html");
		
		constraints.fill = GridBagConstants.BOTH;
		constraints.anchor = GridBagConstants.WEST;
		constraints.gridx = 0;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.insets = new Insets(0, 6, 5, 0);

		constraints.gridy = 0;
		auxiliaryPanel = createFilePannel(fileOne_Label, firstLabelField, browseButton1, 13);
		panel.add(auxiliaryPanel, constraints);

		constraints.gridy ++ ;
		auxiliaryPanel = createFilePannel(fileTwo_Label, secondLabelField, browseButton2, 10);
		panel.add(auxiliaryPanel, constraints);

		constraints.gridy++;
		auxiliaryPanel = createFilePannel(fileThree_Label, thirdLabelField, browseButton3, 17);
		constraints.insets = new Insets(0, 6, 0, 0);
		panel.add(auxiliaryPanel, constraints);


		

		
		return panel;
	}
	/**
	 * Puts all the parameters together into a panel with certain constraints
	 * @param text - the text area 
	 * @param field - The field with the URL path to the file
	 * @param button - Browse button to choose the path
	 * @param ipadxMargin - Distance between some elements
	 * @return
	 */
	private JPanel createFilePannel(JLabel text, JTextField field, JButton button, int ipadxMargin) {
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
	 * Creates a Browse button and remembers the given path
	 * in the field param.
	 * @param field -> The field responsible with remembering the Path
	 * @return the browsing Button for each of the files
	 */
	private ToolbarButton createBrowseButton(final JTextField field){
		ImageIcon imageIcon = new ImageIcon("C:\\Users\\intern3\\git\\Oxygen-Diff-Report-Plug-in\\oxygen-plugin-workspace-access\\src\\Resources\\Open16.png");
		AbstractAction browseAction = new AbstractAction("Browse", imageIcon) {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files","xml", "html");
				fileChooser.setFileFilter(filter);

				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					field.setText(selectedFile.toString());
					
				}
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
		
		/**
		 * Generate Diff Button.
		 */
		generateDiffButton = new JButton("Generate Diff");
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
		JButton cancelButton = new JButton("Cancel");
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

	ProgressMonitor progressMonitor = new ProgressMonitor(this, "Generating Diff", "", 0, 100);

	/**
	 * Gets the inputs, gives them to the generateHTML function.
	 * Is activated when the "Generate Diff" Button is pressed
	 */
	private void generateDiff() {
		progressMonitor.setProgress(progressMonitor.getMaximum());
		try {
			String leftFile = getFirstLabelField().getText();
			String rightFile = getSecondLabelField().getText();
			File outputFile = new File(getThirdLabelField().getText());

			if (!new File(leftFile).exists() || !new File(rightFile).exists()){				
				throw new FileNotFoundException();
			}
			reportGenerator.generateHTMLReport(new File(leftFile).toURI().toURL(), new File(rightFile).toURI().toURL(),
					outputFile, progressMonitor);

			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().browse(outputFile.toURI());
			}

			setVisible(false);
		} catch (FileNotFoundException e1) {
			 JOptionPane.showMessageDialog(null, "The path is not Valid", "", JOptionPane.INFORMATION_MESSAGE);
			 setVisible(true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
		}
	}
	
	public static void main(String[] args) {
		 try {
		        UIManager.setLookAndFeel(
		            UIManager.getSystemLookAndFeelClassName());
		    } catch (Exception e) { }
		 DiffReportFileChooserDialogue dial = new DiffReportFileChooserDialogue();
		 dial.setVisible(true);
	}

}
