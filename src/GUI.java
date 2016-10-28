import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class GUI {

	private JFrame frmGroupProject;
	private JTextField searchTextField;
	private JTextField allWordsTextfield;
	private JTextField exactWordsTextfield;
	private JTextField anyWordsTextfield;
	private JTextField noneWordsTextfield;
	private JTextField numbersFromTextfield;
	private JTextField numbersToTextfield;
	private JProgressBar progressBar;
	private JButton btnSimpleSearch, btnAdvancedSearch;
	private JList<Object> list;
	private DefaultListModel<Object> dlm;
	private JScrollPane scrollPane;

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
		       // lock the search buttons so that they cannot be used whilst the indexing is happening
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
		
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(e -> System.exit(0));
		mnFile.add(mntmQuit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmUserGuide = new JMenuItem("User Guide");
		mntmUserGuide.addActionListener(e -> {
			// TODO: add in the code to show a popup detailing the user guide
		});
		mnHelp.add(mntmUserGuide);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(e -> {
			// TODO: add in the code to show a popup detailing the authors of the program
		});
		mnHelp.add(mntmAbout);
		// End of Menu Items
		
		// Start of results panel setup
		JPanel panel = new JPanel();
		panel.setBounds(0, 267, 914, 290);
		frmGroupProject.getContentPane().add(panel);
		panel.setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 6, 902, 243);
		panel.add(scrollPane);
		
		list = new JList<Object>();
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
		
		btnSimpleSearch = new JButton("Search");
		btnSimpleSearch.addActionListener(e -> {
			dlm.removeAllElements();
			Thread t = new Thread() {
				@Override
				public void run() {
					long startTime = System.currentTimeMillis();
					File root = new File("wsj-1990"); // TODO: remove hard coded value
					String query = searchTextField.getText().trim();
					lblQuery.setText("Searched for: " + query); // update the searched for label
					lblResultsNumber.setText("Calculating...");
					File[] files = root.listFiles();
					progressBar.setMaximum(files.length);
					for (int i = 0; i < files.length; i++) {
						try {
							dlm.addElement(SearchFiles.tokenisingTheUserInput(query, files[i].getPath()));
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
		
		btnAdvancedSearch = new JButton("Search");
		btnAdvancedSearch.setBounds(0, 168, 117, 29);
		btnAdvancedSearch.addActionListener(e -> {
			dlm.removeAllElements();
			Thread t = new Thread() {
				@Override
				public void run() {
					// all of these words
					String allWords = allWordsTextfield.getText();
					
					// exact words or phrase
					String exactWords = exactWordsTextfield.getText();
					
					// any of these words
					String anyWords = anyWordsTextfield.getText();
					
					// none of these words
					String noneWords = noneWordsTextfield.getText();
					
					// numbers ranging from _ to _ 
					int numFrom = 0;
					int numTo = 0;
					boolean doSearch = true;
					try {
						numFrom = Integer.parseInt(numbersFromTextfield.getText());
						numTo = Integer.parseInt(numbersToTextfield.getText());
					} catch (NumberFormatException e) {
						// TODO: throw popup telling the user theyre a fud.
						doSearch = false;
					}
					
					if (doSearch) {
						SearchFiles.doAdvancedSearch(allWords, exactWords, anyWords, noneWords, numFrom, numTo);
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
}
