import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class GUI {

	private JFrame frmGroupProject;
	private JTextField searchTextField; // simple search
	private JTextField allWordsTextfield; // advanced search
	private JTextField exactWordsTextfield;
	private JTextField anyWordsTextfield;
	private JTextField noneWordsTextfield;
	private JTextField numbersFromTextfield;
	private JTextField numbersToTextfield; // end of advanced search
	private JProgressBar progressBar;
	private JButton btnSimpleSearch, btnAdvancedSearch;
	private JList<Object> list; // list of search results
	private DefaultListModel<Object> dlm;
	private final String DEFAULT_SEARCH_SPACE = "wsj-1990"; // set the default search space
	private String searchSpace;
	private JLabel lblCurrentSearchSpace;

	/**
	 * Launch the application.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		SearchFiles.initialiseArrays(); // initialise stop word support
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frmGroupProject.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// initialise search space to default
		searchSpace = DEFAULT_SEARCH_SPACE;
		
		frmGroupProject = new JFrame();
		frmGroupProject.setResizable(false);
		frmGroupProject.setTitle("412 Group Project");
		frmGroupProject.setBounds(100, 100, 914, 579);
		frmGroupProject.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmGroupProject.getContentPane().setLayout(null);
		
		// Start of menu items
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 914, 22);
		frmGroupProject.getContentPane().add(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmIndexFiles = new JMenuItem("Index Files");
		mntmIndexFiles.addActionListener(e -> {
			dlm.removeAllElements();
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    int returnVal = chooser.showOpenDialog(frmGroupProject);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       // get number of files then we can set the maximum value for the progress bar
		       String[] list = chooser.getSelectedFile().list();
		       progressBar.setMaximum(list.length);
		       Thread t = new Thread() {
		    	   @Override
		    	   public void run() {
				       IndexFiles.indexFiles(chooser.getSelectedFile().getName(), progressBar, dlm);
		    	   }
		       };
		       t.start();
		    }
		});
		mnFile.add(mntmIndexFiles);
		
		JMenuItem mntmChangeSearchSpace = new JMenuItem("Change Search Space");
		mntmChangeSearchSpace.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = chooser.showOpenDialog(frmGroupProject);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				this.searchSpace = chooser.getSelectedFile().getPath();
				lblCurrentSearchSpace.setText("Current Search Space: \'" + this.searchSpace + "\'");
			}
		});
		mnFile.add(mntmChangeSearchSpace);
		mnFile.add(new JSeparator());
		
		JMenuItem mntmResetSearchSpace = new JMenuItem("Reset Search Space");
		mntmResetSearchSpace.addActionListener(e -> {
			// TODO: add in a popup asking the user to confirm the reset action
			this.searchSpace = DEFAULT_SEARCH_SPACE;
			lblCurrentSearchSpace.setText("Current search space: \'" + this.searchSpace + "\'");
		});
		mnFile.add(mntmResetSearchSpace);
		
		mnFile.add(new JSeparator());
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(e -> System.exit(0));
		mnFile.add(mntmQuit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmUserGuide = new JMenuItem("User Guide");
		mntmUserGuide.addActionListener(e -> {
			JOptionPane.showMessageDialog(null, getUserGuide(), "User Guide", JOptionPane.INFORMATION_MESSAGE);
		});
		mnHelp.add(mntmUserGuide);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(e -> {
			JOptionPane.showMessageDialog(null, getAbout(), "About", JOptionPane.INFORMATION_MESSAGE);
		});
		mnHelp.add(mntmAbout);
		
		menuBar.add(Box.createHorizontalGlue()); // move the search space string to the right side
		lblCurrentSearchSpace = new JLabel("Current Search Space: \'" + this.searchSpace + "\'");
		menuBar.add(lblCurrentSearchSpace);
		// End of Menu Items
		
		// Start of results panel setup
		JPanel panel = new JPanel();
		panel.setBounds(0, 267, 914, 290);
		frmGroupProject.getContentPane().add(panel);
		panel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 6, 902, 243);
		panel.add(scrollPane);
		
		list = new JList<Object>(); // TODO: allow the JList to be double clicked to open the document you clicked on
		dlm = new DefaultListModel<Object>();
		list.setModel(dlm);
		scrollPane.setViewportView(list);
		
		JLabel lblQuery = new JLabel("Searched for:");
		lblQuery.setBounds(6, 261, 377, 16);
		panel.add(lblQuery);
		
		JLabel lblResultsNumber = new JLabel("0 results in 0 seconds");
		lblResultsNumber.setBounds(395, 261, 295, 16);
		panel.add(lblResultsNumber);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(726, 257, 182, 20);
		progressBar.setMinimum(0);
		progressBar.setStringPainted(true);
		panel.add(progressBar);
		// End of results panel setup
		
		// Start of tabbed pane setup
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 21, 914, 246);
		frmGroupProject.getContentPane().add(tabbedPane);
		
		// Start of simple tab setup

		JPanel simpleTab = new JPanel();
		tabbedPane.addTab("Simple Search", null, simpleTab, null);
		simpleTab.setLayout(null);
		
		searchTextField = new JTextField();
		searchTextField.setBounds(6, 80, 706, 26);
		simpleTab.add(searchTextField);
		searchTextField.setColumns(10);
		
		JCheckBox chckbxRemoveRepeatingWordsSimple = new JCheckBox("Remove repeating words?");
		chckbxRemoveRepeatingWordsSimple.setBounds(6, 111, 677, 23);
		simpleTab.add(chckbxRemoveRepeatingWordsSimple);
		
		btnSimpleSearch = new JButton("Search");
		btnSimpleSearch.addActionListener(e -> {
			dlm.removeAllElements();
			Thread t = new Thread() {
				@Override
				public void run() {
					long startTime = System.currentTimeMillis();
					File root = new File(getSearchSpace());
					String query = searchTextField.getText().trim();
					lblQuery.setText("Searched for: " + query); // update the searched for label
					lblResultsNumber.setText("Calculating...");
					File[] files = root.listFiles();
					progressBar.setMaximum(files.length);
					for (int i = 0; i < files.length; i++) {
						try {
							dlm.addElement(SearchFiles.tokenisingTheUserInput(query, files[i].getPath(), chckbxRemoveRepeatingWordsSimple.isSelected()));
							// TODO: change above to use the GUIobject class variables.
						} catch (Exception e) {
							e.printStackTrace();
						}
						progressBar.setValue(i); // update the progress bar after every document
					}
					long endTime = System.currentTimeMillis();
					progressBar.setValue(progressBar.getMaximum()); // set progress bar to complete
					lblResultsNumber.setText(dlm.size() + " results in " + ((endTime - startTime)/1000) + " seconds"); // update the number of hits label
				}
			};
			t.start(); // start the thread to search the files
		});
		btnSimpleSearch.setBounds(713, 80, 174, 29);
		simpleTab.add(btnSimpleSearch);
		
		// End of simple tab setup
		
		// Start of advanced tab setup
		JPanel advancedTab = new JPanel();
		tabbedPane.addTab("Advanced Search", null, advancedTab, null);
		advancedTab.setLayout(null);
		
		JLabel lblFindDocumentsWith = new JLabel("Find documents with...");
		lblFindDocumentsWith.setBounds(6, 6, 155, 16);
		advancedTab.add(lblFindDocumentsWith);
		
		JLabel lblAllTheseWords = new JLabel("all these words:");
		lblAllTheseWords.setBounds(6, 28, 124, 16);
		advancedTab.add(lblAllTheseWords);
		
		JLabel lblThisExactWord = new JLabel("this exact word or phrase:");
		lblThisExactWord.setBounds(6, 56, 170, 16);
		advancedTab.add(lblThisExactWord);
		
		JLabel lblAnyOfThese = new JLabel("any of these words:");
		lblAnyOfThese.setBounds(6, 84, 137, 16);
		advancedTab.add(lblAnyOfThese);
		
		allWordsTextfield = new JTextField();
		allWordsTextfield.setBounds(188, 23, 699, 26);
		advancedTab.add(allWordsTextfield);
		allWordsTextfield.setColumns(10);
		
		exactWordsTextfield = new JTextField();
		exactWordsTextfield.setBounds(188, 51, 699, 26);
		advancedTab.add(exactWordsTextfield);
		exactWordsTextfield.setColumns(10);
		
		anyWordsTextfield = new JTextField();
		anyWordsTextfield.setBounds(188, 79, 699, 26);
		advancedTab.add(anyWordsTextfield);
		anyWordsTextfield.setColumns(10);
		
		JLabel lblNoneOfThese = new JLabel("none of these words:");
		lblNoneOfThese.setBounds(6, 112, 155, 16);
		advancedTab.add(lblNoneOfThese);
		
		noneWordsTextfield = new JTextField();
		noneWordsTextfield.setBounds(188, 107, 699, 26);
		advancedTab.add(noneWordsTextfield);
		noneWordsTextfield.setColumns(10);
		
		JLabel lblNumbersRangingFrom = new JLabel("numbers ranging from:");
		lblNumbersRangingFrom.setBounds(6, 140, 164, 16);
		advancedTab.add(lblNumbersRangingFrom);
		
		numbersFromTextfield = new JTextField();
		numbersFromTextfield.setBounds(188, 135, 301, 26);
		advancedTab.add(numbersFromTextfield);
		numbersFromTextfield.setColumns(10);
		
		JLabel lblTo = new JLabel("to");
		lblTo.setBounds(501, 140, 33, 16);
		advancedTab.add(lblTo);
		
		numbersToTextfield = new JTextField();
		numbersToTextfield.setBounds(546, 135, 341, 26);
		advancedTab.add(numbersToTextfield);
		numbersToTextfield.setColumns(10);
		
		JCheckBox chckbxRemoveRepeatingWordsAdv = new JCheckBox("Remove repeating words?");
		chckbxRemoveRepeatingWordsAdv.setBounds(129, 168, 457, 23);
		advancedTab.add(chckbxRemoveRepeatingWordsAdv);
		
		btnAdvancedSearch = new JButton("Search");
		btnAdvancedSearch.setBounds(0, 168, 117, 29);
		btnAdvancedSearch.addActionListener(e -> {
			dlm.removeAllElements();
			Thread t = new Thread() {
				@Override
				public void run() {
					// all of these words
					String allWords = allWordsTextfield.getText().trim();
					
					// exact words or phrase
					String exactWords = exactWordsTextfield.getText().trim();
					
					// any of these words
					String anyWords = anyWordsTextfield.getText().trim();
					
					// none of these words
					String noneWords = noneWordsTextfield.getText().trim();
					
					boolean doSearch = true;
					
					// check which of the textfields we want to use for advanced search
					String[] list = {allWords, exactWords, anyWords, noneWords};
					int index = -1;
					for (int i = 0; i < list.length; i++) {
						if (!list[i].equals("")) {
							index = i;
							break;
						}
 					}
					int numFrom = -1;
					int numTo = -1;
					if (index < 0) {
						// check the number fields. numbers ranging from _ to _ 
						try {
							numFrom = Integer.parseInt(numbersFromTextfield.getText().trim());
							numTo = Integer.parseInt(numbersToTextfield.getText().trim());
							index = 4; // indicates to use the number fields
						} catch (NumberFormatException e) {
							// Throw a popup alerting the user they input bad values
							JOptionPane.showMessageDialog(null, "Enter numbers only in: \n[Numbers ranging from.. to...] fields", "Error", JOptionPane.ERROR_MESSAGE);
							doSearch = false;
						}
					}
					
					if (doSearch) {
						long startTime = System.currentTimeMillis();
						if (index == 4) {
							lblQuery.setText("Searched for numbers ranging from " + numFrom + " to " + numTo); // special case for number fields
						} else {
							String query = list[index];
							lblQuery.setText("Searched for: " + query); // update the searched for label
						}
						lblResultsNumber.setText("Calculating...");
						File root = new File(getSearchSpace());
						File[] files = root.listFiles();
						progressBar.setMaximum(files.length);
						for (int i = 0; i < files.length; i++) {
							// do the search for each file
							switch (index) {
							case 0:
								dlm.addElement(SearchFiles.advancedAllWords(list[index], files[i].getPath()));
								break;
							case 1:
								try {
									dlm.addElement(SearchFiles.advancedExactWords(list[index], files[i].getPath()));
								} catch (FileNotFoundException e) {}
								break;
							case 2: 
								dlm.addElement(SearchFiles.advancedAnyWords(list[index], files[i].getPath()));
								break;
							case 3: 
								dlm.addElement(SearchFiles.advancedNoneWords(list[index], files[i].getPath()));
								break;
							case 4:
								try {
									dlm.addElement(SearchFiles.advancedRange(numFrom, numTo, files[i].getPath()));
								} catch (Exception e) {}
							}
							progressBar.setValue(i); // update the progress bar after every document
						}
						long endTime = System.currentTimeMillis();
						progressBar.setValue(progressBar.getMaximum()); // set progress bar to complete
						lblResultsNumber.setText(dlm.size() + " results in " + ((endTime - startTime)/1000) + " seconds"); // update the number of hits label
						
						// TODO: try to disable simple/advanced search buttons until current search is complete. Could maybe make a new thread that checks progress bar every now and then for max value
					}
				}
			};
			t.start(); // start the thread to search the files
		});
		advancedTab.add(btnAdvancedSearch);
		// End of advanced tab setup

		// Start of preferences tab setup
		JPanel preferencesTab = new JPanel();
		tabbedPane.addTab("Preferences", null, preferencesTab, null);
		preferencesTab.setLayout(null);
		
		JLabel lblTempPrefTabLabel = new JLabel("To be completed, duh...");
		lblTempPrefTabLabel.setBounds(365, 70, 176, 25);
		preferencesTab.add(lblTempPrefTabLabel);
		// End of preferences tab setup
	}
		
	private String getSearchSpace() {
		return searchSpace;
	}
	
	private String getUserGuide() {
		// TODO: write the user guide
		return "Searching\n\n"
				+ "You can search documents by using either the Simple or Advanced Search tab.\n"
				+ "- Simple Search allows you to enter a simple query.\n"
				+ "- Advanced Search allows you to customise your search using a combination of the given text fields.\n"
				+ "Results will be displayed in the box at the bottom of the application\n\n\n"
				+ "Indexing\n\n"
				+ "You can index files using the File -> Index Files menu option.\n"
				+ "Navigate your way to the desired folder in your file system and press ok.\n"
				+ "The files that are being indexed will be shown in the box at the bottom of the application.\n\n\n"
				+ "Changing the Search Space\n\n"
				+ "You can change which folder you want to search through using the File -> Change Search Space menu option.\n"
				+ "Navigate your way to the desired folder in your system and press ok.\n"
				+ "You should see the current search space at the top right corner of the application indicate the change.";
	}
	
	private String getAbout() {
		return "Authors:"
				+ "\n- Kieran Sharpe"
				+ "\n- Darren Tang"
				+ "\n- Omer Shah"
				+ "\n- Dave Stirrat";
	}
}
