import java.awt.EventQueue;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
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
import javax.swing.ListSelectionModel;

public class GUI {

	private JFrame frmGroupProject;
	private JTextField searchTextField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 914, 22);
		frmGroupProject.getContentPane().add(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmIndexFiles = new JMenuItem("Index Files");
		mntmIndexFiles.addActionListener(e -> {
			// TODO: add in the code to allow the user to index files
		});
		mnFile.add(mntmIndexFiles);
		
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(e -> System.exit(0)); // quit the program
		mnFile.add(mntmQuit);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
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
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 21, 914, 530);
		frmGroupProject.getContentPane().add(tabbedPane);
		
		JPanel simpleTab = new JPanel();
		tabbedPane.addTab("Simple Search", null, simpleTab, null);
		simpleTab.setLayout(null);
		
		searchTextField = new JTextField();
		searchTextField.setBounds(6, 6, 485, 26);
		simpleTab.add(searchTextField);
		searchTextField.setColumns(10);
		
		JLabel lblQuery = new JLabel("Searched for: ");
		lblQuery.setBounds(6, 33, 285, 16);
		simpleTab.add(lblQuery);
		
		JLabel lblResultsNumber = new JLabel("0 results in 0 seconds");
		lblResultsNumber.setBounds(6, 56, 285, 16);
		simpleTab.add(lblResultsNumber);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 84, 881, 394);
		simpleTab.add(scrollPane);
		
		JList<Object> resultsList = new JList<>();
		resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		DefaultListModel<Object> dlm = new DefaultListModel<>();
		resultsList.setModel(dlm);
		
		scrollPane.setViewportView(resultsList);
		
		JLabel lblHitsInResult = new JLabel("\"Number\" of hits in \"List Selection\"");
		lblHitsInResult.setBounds(304, 33, 316, 16);
		simpleTab.add(lblHitsInResult);
		
		JProgressBar searchProgressBar = new JProgressBar();
		searchProgressBar.setStringPainted(true);
		searchProgressBar.setMinimum(0);
		searchProgressBar.setBounds(700, 12, 187, 20);
		simpleTab.add(searchProgressBar);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(e -> {
			dlm.removeAllElements();
			Thread t = new Thread() {
				@Override
				public void run() {
					long startTime = System.currentTimeMillis();
					File root = new File("wsj-1990"); // TODO: remove hard coded value
					String query = searchTextField.getText().trim();
					lblQuery.setText("Searched for: " + query); // update the searched for label
					File[] files = root.listFiles();
					searchProgressBar.setMaximum(files.length);
					for (int i = 0; i < files.length; i++) {
						try {
							dlm.addElement(SearchFiles.tokenisingTheUserInput(query, files[i].getPath()));
						} catch (Exception e) {
							e.printStackTrace();
						}
						searchProgressBar.setValue(i); // update the progress bar after every document
					}
					long endTime = System.currentTimeMillis();
					searchProgressBar.setValue(searchProgressBar.getMaximum()); // set progress bar to complete
					lblResultsNumber.setText(dlm.size() + " results in " + ((endTime - startTime)/1000) + " seconds"); // update the number of hits label
					
				}
			};
			t.start(); // start the thread to search the files
		});
		btnSearch.setBounds(503, 6, 117, 29);
		simpleTab.add(btnSearch);
		
		JLabel lblSearchProgress = new JLabel("Progress:");
		lblSearchProgress.setBounds(632, 11, 68, 16);
		simpleTab.add(lblSearchProgress);
		
		JPanel advancedTab = new JPanel();
		tabbedPane.addTab("Advanced Search", null, advancedTab, null);
	}
}
