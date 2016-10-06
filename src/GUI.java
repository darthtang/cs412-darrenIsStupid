import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
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
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(e -> {
			// TODO: add in the code to search the indexed files.
			// searchTextField contains the query
		});
		btnSearch.setBounds(503, 6, 117, 29);
		simpleTab.add(btnSearch);
		
		JLabel lblQuery = new JLabel("Searched for \"text here\"");
		lblQuery.setBounds(6, 33, 285, 16);
		simpleTab.add(lblQuery);
		
		JLabel lblResultsNumber = new JLabel("\"Number\" results in \"time\" ms");
		lblResultsNumber.setBounds(6, 56, 285, 16);
		simpleTab.add(lblResultsNumber);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 84, 881, 394);
		simpleTab.add(scrollPane);
		
		JList<Object> resultsList = new JList<>();
		resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		DefaultListModel<Object> dlm = new DefaultListModel<>();
		dlm.addElement("Sample List Item 1");
		dlm.addElement("Sample List Item 2");
		dlm.addElement("Sample List Item 3");
		dlm.addElement("Sample List Item 4");
		dlm.addElement("Sample List Item 5");
		resultsList.setModel(dlm);
		
		scrollPane.setViewportView(resultsList);
		
		JLabel lblHitsInResult = new JLabel("\"Number\" of hits in \"List Selection\"");
		lblHitsInResult.setBounds(304, 33, 316, 16);
		simpleTab.add(lblHitsInResult);
		
		JPanel advancedTab = new JPanel();
		tabbedPane.addTab("Advanced Search", null, advancedTab, null);
	}
}
