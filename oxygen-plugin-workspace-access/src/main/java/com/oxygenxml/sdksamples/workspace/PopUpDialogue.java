package com.oxygenxml.sdksamples.workspace;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;




public class PopUpDialogue {
	
	private JButton compareButton;
	private JFrame dialog;
	private JPanel mainPanel;
	private JButton okButton;
	private JTextField firstLabelField;
	private JTextField secondLabelField;
	private ActionListener CompareButtonActionListener;
	
	//Constructor----------------------
	public PopUpDialogue() {
//		if(dialog == null)
//			loadDialogue();
	}
	//----------------------------------
	
	
	//Getters and Setters
	
	
	public JFrame getDialog(){
		return dialog;
	}
	
	public JButton getCompareButton() {
		return compareButton;
	}


	public void setCompareButton(JButton compareButton) {
		this.compareButton = compareButton;
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


	public JButton getOkButton() {
		return okButton;
	}

	public void setOkButton(JButton okButton) {
		this.okButton = okButton;
	}




	/**
	 * The main function that is invoked in the Constructor
	 * It creates the Dialog that has the file chooser and
	 * two buttons
	 */
	
	public void showDialogue() {
		dialog = new JFrame();
		dialog.setTitle("File differentiator");
		dialog.setLayout( new FlowLayout() ); 
		mainPanel = crateMainPanel();
		
		dialog.setModalExclusionType(null);
		dialog.add(mainPanel, BorderLayout.CENTER);
		dialog.setSize(380, 200);
		dialog.setLocationRelativeTo(null);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);

	}
	
	/**
	 * In this function the Panel that contains the file choosers and
	 * the one that has the buttons are unified into one panel
	 * @return the resulted Panel
	 */
	
	private JPanel crateMainPanel(){
		JPanel mainPanel = new JPanel(new GridBagLayout());	
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel fileChooserPanel = createFileChooserPanel();
		JPanel buttonPanel = createButtons();

		//add fileChooserPanel to main panel with constraints
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		mainPanel.add(fileChooserPanel,constraints);
		
		//add buttonPanel to main panel with constraints
		constraints.gridy++;
		constraints.anchor = GridBagConstraints.SOUTHEAST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0;
		mainPanel.add(buttonPanel, constraints);

		
		return mainPanel;
	}
	
	/**
	 * This method creates the Panel that contains the whole file chooser
	 * components: Label + Shown Path + Browse Button (with File Chooser)
	 * showing the chosen path in a TextField  
	 * @return the panel with all the given components
	 */
	private JPanel createFileChooserPanel(){
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		JLabel fileOneLabel = new JLabel("Choose first File: ");
		JLabel fileTwoLabel = new JLabel("Choose second File: ");
		firstLabelField = new JTextField(20);
		secondLabelField = new JTextField(20);
		JButton browseButton1 = createBrowseButton(firstLabelField);
		JButton browseButton2 = createBrowseButton(secondLabelField);
		firstLabelField.setText("C:/Users/intern3/Desktop/myFiles/diffSample/EngliGB.xml");
		secondLabelField.setText("C:/Users/intern3/Desktop/myFiles/diffSample/EngliUS.xml");
		
		firstLabelField.setEditable(true);
		firstLabelField.setBackground(Color.LIGHT_GRAY);
		firstLabelField.setHorizontalAlignment(JTextField.CENTER);
		
		secondLabelField.setEditable(true);
		secondLabelField.setBackground(Color.LIGHT_GRAY);
		secondLabelField.setHorizontalAlignment(JTextField.CENTER);
		
		// add the first label
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.anchor = GridBagConstraints.WEST;
		panel.add(fileOneLabel, constraints);
		
		//add the TextField for first Path
		constraints.gridx++;
		constraints.weightx = 1;
		panel.add(firstLabelField, constraints);
		
		//add the first browse Button
		constraints.gridx++;
		constraints.gridwidth = 1;
		constraints.weightx = 0;
		panel.add(browseButton1, constraints);
		
		//add the second label
		constraints.gridx = 0;
		constraints.gridy ++ ;
		constraints.weightx = 0;
		constraints.anchor = GridBagConstraints.WEST;
		panel.add(fileTwoLabel, constraints);
		
		//add the TextField for the second Path
		constraints.gridx ++;
		constraints.weightx = 1;
		panel.add(secondLabelField, constraints);
		
		//add the second browse Button
		constraints.gridx++;
		constraints.gridwidth = 1;
		constraints.weightx = 0;
		panel.add(browseButton2, constraints);

		
		return panel;
	}
	
	/**
	 * Creates a Browse button and remembers the given path
	 * in the field param
	 * @param field -> The field responsible with remembering the Path
	 * @return the browsing Button for each of the files
	 */
	private JButton createBrowseButton(final JTextField field){
		JButton browse = new JButton("Browse");
		
		ActionListener ac = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files","xml", "html");
				fileChooser.setFileFilter(filter);

				int returnValue = fileChooser.showOpenDialog(null);
				// int returnValue = jfc.showSaveDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					field.setText(selectedFile.toString());
					
					//System.out.println(selectedFile.getAbsolutePath());
				}
			}
		};
		
		browse.addActionListener(ac);
		return browse;
	}
	
	/**
	 * Creating the panel which contains the two functions "Compare" and
	 * "Cancel" aligned at the right bottom corner 
	 * @return The panel with the two buttons: Compare and Cancel
	 */
	private JPanel createButtons(){
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		Component box1 = Box.createRigidArea(new Dimension(7,0));
		Component box2= Box.createRigidArea(new Dimension(7,0));
		box1.setBackground(Color.WHITE);
		box2.setBackground(Color.WHITE);
		
		
		//Compare button starts here-------------------------------------------------------
		compareButton = new JButton("Compare");
		compareButton.setPreferredSize(new Dimension(75, 25));
		panel.add(compareButton);
		panel.add(box1);
		
		//ends here--------------------------------------------------------------------
		
		
		
		
		//cancel button starts here ----------------------------------------------------
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand() != null)
					dialog.dispose();

			}
		});
		cancelButton.setPreferredSize(new Dimension(75, 25));
		panel.add(cancelButton);
		panel.add(box2);
		//ends here----------------------------------------------------------------------
		
		return panel;
	}

	
//	public static void main(String[] args) {
//		 try {
//		        UIManager.setLookAndFeel(
//		            UIManager.getSystemLookAndFeelClassName());
//		    } catch (Exception e) { }
//		 PopUpDialogue program = new PopUpDialogue();
//		 program.showDialogue();
//	}
}
