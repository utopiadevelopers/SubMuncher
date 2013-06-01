package gui;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.management.Query;
import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.ListSelectionModel;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import data.operations.Database;
import data.operations.ListDataTableModel;
import data.operations.OpenSubtitlesHasher;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JButton;

import org.apache.xmlrpc.XmlRpcException;

import xmlrpc.ClientService;
import xmlrpc.Constants;

public class Dashboard extends JFrame implements TreeSelectionListener {

	private JToolBar toolbar;
	private JTree tree;
	private JTable table;
	private MyAction addFolder, addFiles;
	private JFileChooser fileChooser;
	private ClientService clientService;
	Object[] data = null;
	String[] mediaFileExtension;
	private boolean loginFailed = false;
	List<JProgressBar> progressbars;
	ListDataTableModel tableModel;
	Database db;
	Map<String, ResultSet> tableContent;
	ArrayList<String> treeFolderPath;
	DefaultTreeModel treeModel;
	int currentlySelectedNodeIndex;

	/**
	 * Create the application.
	 */
	public Dashboard() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mediaFileExtension = new String[] { "avi", "mpg", "mpeg", "mpe", "m1v",
				"m2v", "mpv2", "mp2v", "pva", "evo", "m2p", "ts", "tp", "trp",
				"m2t", "m2ts", "mts", "rec", "vob", "ifo", "mkv", "webM",
				"mp4", "m4v", "mp4v", "mpv4", "hdmov", "mov", "3gp", "3gpp",
				"3ga", "3g2", "3gp2", "flv", "f4v", "ogm", "ogv", "rm", "ram",
				"rpm", "rmm", "rt", "rp", "smi", "smil", "wmv", "wmp", "wm",
				"asf" };

		setVisible(true);
		setTitle("SubMuncher");
		setBounds(100, 100, 800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(
				new MigLayout("", "[][grow][]", "[][][][grow][]"));
		progressbars = new ArrayList<JProgressBar>();
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		getContentPane().add(toolbar, "cell 1 1,grow");

		tableContent = new HashMap<String, ResultSet>();
		treeFolderPath = new ArrayList<String>();
		currentlySelectedNodeIndex = 1;
		JSplitPane splitPane = new JSplitPane();

		getContentPane().add(splitPane, "cell 1 3,grow");

		JPanel leftPanel = new JPanel();
		splitPane.setLeftComponent(leftPanel);
		leftPanel.setLayout(new MigLayout("", "[100px:150px:300px,grow]",
				"[grow]"));

		JScrollPane scrollPane_1 = new JScrollPane();
		leftPanel.add(scrollPane_1, "cell 0 0,grow");

		tree = new JTree();
		scrollPane_1.setViewportView(tree);

		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(this);
		JPanel rightPanel = new JPanel();
		splitPane.setRightComponent(rightPanel);
		rightPanel.setLayout(new MigLayout("", "[450px:n,grow]", "[grow]"));

		JScrollPane scrollPane = new JScrollPane();
		rightPanel.add(scrollPane, "cell 0 0,grow");

		table = new JTable();
		scrollPane.setViewportView(table);
		table.setFillsViewportHeight(true);
		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() {
				try {
					db = new Database();
					final ResultSet temp;
					db.executeQuery("select * from ud_sm_folders where folderID not in (1);");
					temp = db.getLastResultSet();

					if (temp != null) {
						treeModel = new DefaultTreeModel(
								new DefaultMutableTreeNode("All Folders") {
									{
										add(new DefaultMutableTreeNode(
												"All Files"));
										while (temp.next()) {
											add(new DefaultMutableTreeNode(temp
													.getString("folderName")));
											treeFolderPath.add(temp
													.getString("folderPath"));
										}
									}
								});
						tree.setModel(treeModel);

						// Starting from 2nd is desired as 1st row is Root
						ResultSet fileRootTemp;
						db.executeQuery("select * from ud_sm_files where parentFolderID=1;");
						fileRootTemp = db.getLastResultSet();
						tableContent.put("Root", fileRootTemp);
						for (int i = 0; i < treeFolderPath.size(); i++) {
							ResultSet fileTemp;
							db.executeQuery("select * from ud_sm_files where parentFolderPath='"
									+ treeFolderPath.get(i) + "';");
							fileTemp = db.getLastResultSet();
							tableContent.put(
									fileTemp.getString("parentFolderPath"),
									fileTemp);
						}

					} else {
						JOptionPane.showMessageDialog(Dashboard.this,
								"Sorry database operation failed.");
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(
							Dashboard.this,
							"Sorry database operation failed: "
									+ e.getMessage());
				} catch (SQLException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(
							Dashboard.this,
							"Sorry database operation failed: "
									+ e.getMessage());
				}

				return null;
			}

			@Override
			protected void done() {
				ResultSet allFileData = tableContent.get("Root");
				Object[][] tableData = generateObjects(allFileData);
				table.setModel(new ListDataTableModel(tableData, new String[] {
						"File Name", "Status", "Progress", "Last Modified" }));
				/*
				 * tableModel = (ListDataTableModel) table.getModel();
				 * tableModel.addTableModelListener(new TableModelListener(){
				 * public void tableChanged(TableModelEvent e) { int c =
				 * e.getColumn(); int r = e.getFirstRow(); if(c==1){
				 * tableModel.fireTableChanged(new
				 * TableModelEvent(tableModel,r)); //update the whole row } }
				 * });
				 */

				table.getColumnModel().getColumn(0).setPreferredWidth(250);
				table.getColumnModel().getColumn(0).setMinWidth(70);
				table.getColumnModel().getColumn(0).setMaxWidth(800);
				table.getColumnModel().getColumn(1).setMinWidth(40);
				table.getColumnModel().getColumn(1).setMaxWidth(400);
				table.getColumnModel().getColumn(2).setPreferredWidth(100);
				table.getColumnModel().getColumn(2).setMinWidth(75);
				table.getColumnModel().getColumn(2).setMaxWidth(400);
				table.getColumnModel().getColumn(3).setPreferredWidth(200);
				table.getColumnModel().getColumn(3).setMinWidth(100);
				table.getColumnModel().getColumn(3).setMaxWidth(400);

				tableModel = (ListDataTableModel) table.getModel();

				table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			}

		}.execute();

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnTasks = new JMenu("Tasks");
		menuBar.add(mnTasks);

		ImageIcon addFolderImage = new ImageIcon(
				Dashboard.class.getResource("/resources/folder_add.png"));
		Image scaledAddFolder = addFolderImage.getImage().getScaledInstance(48,
				48, Image.SCALE_SMOOTH);
		addFolder = new MyAction("Add Folder", new ImageIcon(scaledAddFolder),
				"Add all files inside the selected folder", new Integer(
						KeyEvent.VK_O));
		mnTasks.add(addFolder);
		toolbar.add(addFolder);

		ImageIcon addFilesImage = new ImageIcon(
				Dashboard.class.getResource("/resources/file_add.png"));
		Image scaledAddFiles = addFilesImage.getImage().getScaledInstance(48,
				48, Image.SCALE_SMOOTH);
		addFiles = new MyAction("Add Files", new ImageIcon(scaledAddFiles),
				"Add individual files", new Integer(KeyEvent.VK_F));
		mnTasks.add(addFiles);
		toolbar.add(addFiles);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenu mnConfig = new JMenu("Config");
		menuBar.add(mnConfig);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() {
				try {
					clientService = new ClientService();
					if (clientService.login() == false) {
						JOptionPane.showMessageDialog(Dashboard.this,
								"Sorry Service is Unavailable.");
					}

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(Dashboard.this,
							"Login Failed: " + e.getMessage());
					loginFailed = true;
				} catch (HeadlessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(Dashboard.this,
							"Login Failed: " + e.getMessage());
					loginFailed = true;
				} catch (XmlRpcException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(Dashboard.this,
							"Login Failed: " + e.getMessage());
					loginFailed = true;
				}
				return null;
			}
		}.execute();

	}

	public Object[][] generateObjects(ResultSet result) {
		Object[][] returnVal = null;
		ArrayList<Object[]> cache = new ArrayList<Object[]>();

		try {
			while (result.next()) {
				Object[] temp = { result.getString("fileName"),
						result.getString("status"), "Done",
						result.getString("lastModified") };
				cache.add(temp);
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}
		returnVal = new Object[cache.size()][];
		for (int i = 0; i < cache.size(); i++) {
			returnVal[i] = cache.get(i);
		}

		return returnVal;
	}

	public List<Object> buildSearchQuery(File file) throws IOException {
		List<Object> searchParams = new ArrayList<Object>();
		searchParams.add(Constants.token);
		Map<String, Object> search = new HashMap();
		search.put("sublanguageid", "eng");
		search.put("moviehash", OpenSubtitlesHasher.computeHash(file));
		search.put("moviebytesize", "0");
		search.put("imdbid", "0");
		search.put("query", file.getName().replaceFirst("[.][^.]+$", ""));
		search.put("season", "0");
		search.put("episode", "0");
		search.put("tag", "0");
		List<Object> temp = new ArrayList<Object>();
		temp.add(search);
		// temp.add(new ArrayList<String>());
		searchParams.add(temp);

		return searchParams;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		int selectedIndex = getSelectedNodeIndex();

		if (selectedIndex > 0) {
			if (selectedIndex != currentlySelectedNodeIndex) {
				if (selectedIndex == 1) {
					ResultSet allFileData = tableContent.get("Root");
					Object[][] tableData = generateObjects(allFileData);
					tableModel.setNumRows(0);
					for (int i = 0; i < tableData.length; i++) {
						tableModel.addRow(tableData[i]);
					}
				} else {
					ResultSet allFileData = tableContent.get(treeFolderPath
							.get(selectedIndex - 2));
					Object[][] tableData = generateObjects(allFileData);
					tableModel.setNumRows(0);
					for (int i = 0; i < tableData.length; i++) {
						tableModel.addRow(tableData[i]);
					}
				}

			}
			// Only When not top wala
			currentlySelectedNodeIndex = selectedIndex;
		}

	}

	public String getSelectedNodeName() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		return (String) node.getUserObject();
	}

	public int getSelectedNodeIndex() {
		return tree.getSelectionRows()[0];

	}

	public ArrayList<File> filterMediaFiles(File[] mediaFiles) {
		ArrayList<File> validFiles = new ArrayList<File>();
		Queue<File> directory = new LinkedList<File>();
		for (int i = 0; i < mediaFiles.length; i++) {
			if (mediaFiles[i].isDirectory()) {
				directory.add(mediaFiles[i]);
				while (!directory.isEmpty()) {
					File temp = directory.remove();
					File[] tempList = temp.listFiles();
					for (int j = 0; j < tempList.length; j++) {
						if (tempList[j].isDirectory()) {
							directory.add(tempList[j]);
						} else {
							if (acceptFile(tempList[j])) {
								validFiles.add(tempList[j]);
							}
						}
					}
				}
			}
			if (acceptFile(mediaFiles[i])) {
				validFiles.add(mediaFiles[i]);
			}
		}
		return validFiles;

	}

	public boolean acceptFile(File f) {
		int pos = f.getName().lastIndexOf('.');
		if (pos == -1) {
			return false;
		} else {
			String extension = f.getName().substring(pos + 1);
			for (String allowed_extension : mediaFileExtension) {
				if (extension.equalsIgnoreCase(allowed_extension)) {
					return true;
				}
			}
			return false;
		}
	}

	public void setupTable(ArrayList<File> selected) {
		for (int i = 0; i < selected.size(); i++) {
			System.out.println(selected.get(i).toString());
			tableModel.addRow(new Object[] { selected.get(i).getName(),
					"Starting", "Temp", new Date().toString() });
		}
		table.setModel(tableModel);
		TableColumnModel columnModel = table.getColumnModel();
		for (int i = 0; i < table.getRowCount(); i++) {
			System.out.println(i);
			progressbars.add(new JProgressBar(0, 100));
		}
		columnModel.getColumn(2).setCellRenderer(new TableCellRenderer() { // sets
					// a
					// progress
					// bar
					// as
					// renderer
					public Component getTableCellRendererComponent(
							JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						JProgressBar bar = progressbars.get(row);
						bar.setValue(0);
						bar.setStringPainted(true);
						bar.setString("0%");
						return bar;
					}
				});
	}

	class MyAction extends AbstractAction {
		public MyAction(String text, ImageIcon icon, String desc,
				Integer mnemonic) {
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}

		public void actionPerformed(ActionEvent e) {

			// File chooser
			if (e.getSource() == toolbar.getComponent(1)) {
				fileChooser = new JFileChooser();
				FileMediaFilter defaultFilter = new FileMediaFilter(
						"Media Files(all types)", mediaFileExtension);
				fileChooser.addChoosableFileFilter(defaultFilter);
				fileChooser.setFileFilter(defaultFilter);
				int returnVal = fileChooser.showOpenDialog(Dashboard.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					new getFileDataOverNetwork(fileChooser.getSelectedFile())
							.execute();
				} else {

				}
			}
			// Folder
			else if (e.getSource() == toolbar.getComponent(0)) {
				fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int returnVal = fileChooser.showOpenDialog(Dashboard.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					new getFolderDataOverNetwork().execute();
				}
			}
		}

	}

	class getFolderDataOverNetwork extends SwingWorker<Void, Void> {
		JProgressBar activeBar;

		public getFolderDataOverNetwork() {
			addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if ("progress".equals(evt.getPropertyName())) {
						activeBar.setValue((Integer) evt.getNewValue());
						activeBar.setStringPainted(true);
						activeBar.setString(evt.getNewValue() + "%");
					}

				}
			});
		}

		@Override
		protected void done() {

		}

		@Override
		protected Void doInBackground() {
			File fileDirectory = fileChooser.getSelectedFile();
			if (fileDirectory.isDirectory()) {

				ArrayList<File> mediaFiles = filterMediaFiles(fileDirectory
						.listFiles());
				setupTable(mediaFiles);
				for (int i = 0; i < mediaFiles.size(); i++) {
					final File file = mediaFiles.get(i);
					try {
						final List<Object> searchParams = buildSearchQuery(file);

						try {
							if (!clientService.isLoggedIn() && !loginFailed) {
								System.out.println("Sleeping");

								Thread.sleep(1000);

							} else if (!loginFailed) {
								data = clientService.search(searchParams);
								Map dataMap = null;
								if (data != null) {
									dataMap = (Map) data[0];
									System.out.println(dataMap.get("MovieName")
											+ "  "
											+ file.getAbsolutePath()
													.replaceFirst("[.][^.]+$",
															"")
											+ dataMap.get("SubFormat"));
									System.out.println(dataMap);
									clientService.saveUrl(
											file.getAbsolutePath()
													.replaceFirst("[.][^.]+$",
															"")
													+ "."
													+ dataMap.get("SubFormat"),
											(String) dataMap
													.get("SubDownloadLink"));
								} else {
									JOptionPane
											.showMessageDialog(
													Dashboard.this,
													"Sorry no subtitle found for "
															+ file.getName()
																	.replaceFirst(
																			"[.][^.]+$",
																			""));
								}
							} else {
								JOptionPane
										.showMessageDialog(Dashboard.this,
												"Sorry Please Connect to the net and restart the application");
							}
						} catch (InterruptedException e) {

							e.printStackTrace();
							JOptionPane.showMessageDialog(
									Dashboard.this,
									"Subtitle Download Failed: "
											+ e.getMessage());
						} catch (XmlRpcException e) {

							e.printStackTrace();
							JOptionPane.showMessageDialog(
									Dashboard.this,
									"Subtitle Download Failed: "
											+ e.getMessage());
						} catch (MalformedURLException e) {

							e.printStackTrace();
							JOptionPane.showMessageDialog(
									Dashboard.this,
									"Subtitle Download Failed: "
											+ e.getMessage());
						} catch (IOException e) {

							e.printStackTrace();
							JOptionPane.showMessageDialog(
									Dashboard.this,
									"Subtitle Download Failed: "
											+ e.getMessage());
						}

					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						JOptionPane.showMessageDialog(Dashboard.this,
								"Subtitle Download Failed: " + e1.getMessage());
					}

				}
			}
			return null;
		}

	}

	class getFileDataOverNetwork extends SwingWorker<Void, Void> {
		JProgressBar activeBar;
		File fileSingle;
		JLabel doneLabel;

		public getFileDataOverNetwork(File file) {
			fileSingle = file;
			doneLabel = new JLabel("Done");
			addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					// System.out.println(evt.getNewValue()+"out");
					if ("progress".equals(evt.getPropertyName())) {
						tableModel.setValueAt(evt.getNewValue(),
								tableModel.getRowCount() - 1, 2);
						tableModel.fireTableDataChanged();
						// System.out.println(evt.getNewValue());
					}

				}
			});

			activeBar = new JProgressBar(0, 100);
			if (currentlySelectedNodeIndex == 1) {
				tableModel = (ListDataTableModel) table.getModel();
				tableModel.addRow(new Object[] { fileSingle.getName(),
						"Started", "Temp", new Date().toString() });
				TableColumnModel columnModel = table.getColumnModel();

				columnModel.getColumn(2).setCellRenderer(
						new TableCellRenderer() { // sets
							// a
							// progress
							// bar
							// as
							// renderer
							public Component getTableCellRendererComponent(
									JTable table, Object value,
									boolean isSelected, boolean hasFocus,
									int row, int column) {
								// System.out.println(row+"  "+tableModel.getRowCount());
								// TODO
								if (isSelected) {
									setBackground(table
											.getSelectionBackground());
									setForeground(table
											.getSelectionForeground());
								} else {
									setBackground(table.getBackground());
									setForeground(table.getForeground());
								}
								if (row == tableModel.getRowCount() - 1) {
									int val = 0;
									if (value.getClass() == Integer.class) {
										val = (Integer) value;
									}

									if (val != 100) {
										activeBar.setValue(val);
										activeBar.setStringPainted(true);
										activeBar.setString(val + "%");
										return activeBar;
									} else {
										return new JLabel("Done");
									}

								} else {
									return doneLabel;
								}

							}
						});
			}
		}

		@Override
		protected void done() {

		}

		@Override
		protected Void doInBackground() {

			try {
				final List<Object> searchParams = buildSearchQuery(fileSingle);
				setProgress(10);
				try {
					if (!clientService.isLoggedIn() && !loginFailed) {
						System.out.println("Sleeping");

						Thread.sleep(1000);

					} else if (!loginFailed) {
						data = clientService.search(searchParams);
						setProgress(45);
						Map dataMap = null;
						if (data != null) {
							dataMap = (Map) data[0];
							System.out.println(dataMap.get("MovieName")
									+ "  "
									+ fileSingle.getAbsolutePath()
											.replaceFirst("[.][^.]+$", "")
									+ dataMap.get("SubFormat"));
							System.out.println(dataMap);
							clientService.saveUrl(fileSingle.getAbsolutePath()
									.replaceFirst("[.][^.]+$", "")
									+ "."
									+ dataMap.get("SubFormat"),
									(String) dataMap.get("SubDownloadLink"));
							Date date = new Date();
							db.executeInsertQuery("insert into ud_sm_files (lastModified,parentFolderID,fileName,filePath,status) values('"
									+ date.toString()
									+ "',1,'"
									+ fileSingle.getName()
									+ "','"
									+ fileSingle.getAbsolutePath()
									+ "','Completed')");
							setProgress(70);
							db.executeQuery("select * from ud_sm_files where parentFolderID=1;");
							ResultSet resultFile = db.getLastResultSet();
							tableContent.put("Root", resultFile);
							setProgress(100);
							if (currentlySelectedNodeIndex == 1) {
								tableModel = (ListDataTableModel) table
										.getModel();
								tableModel.setValueAt("Done",
										tableModel.getRowCount() - 1, 2);
								tableModel.setValueAt("Completed",
										tableModel.getRowCount() - 1, 1);
							} else {
								currentlySelectedNodeIndex = 1;
								ResultSet allFileData = tableContent
										.get("Root");
								Object[][] tableData = generateObjects(allFileData);
								tableModel.setNumRows(0);
								for (int i = 0; i < tableData.length; i++) {
									tableModel.addRow(tableData[i]);
								}
							}
						} else {
							JOptionPane.showMessageDialog(
									Dashboard.this,
									"Sorry no subtitle found for "
											+ fileSingle.getName()
													.replaceFirst("[.][^.]+$",
															""));
						}
					} else {
						JOptionPane
								.showMessageDialog(Dashboard.this,
										"Sorry Please Connect to the net and restart the application");
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(Dashboard.this,
							"Subtitle Download Failed: " + e.getMessage());
				} catch (XmlRpcException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(Dashboard.this,
							"Subtitle Download Failed: " + e.getMessage());
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(Dashboard.this,
							"Subtitle Download Failed: " + e.getMessage());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(Dashboard.this,
							"Subtitle Download Failed: " + e.getMessage());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(Dashboard.this,
							"Database Operation Failed: " + e.getMessage());
				}

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				JOptionPane.showMessageDialog(Dashboard.this,
						"Subtitle Download Failed: " + e1.getMessage());
			}
			return null;
		}

	}

	class FileMediaFilter extends FileFilter {
		public String filterName;
		public String[] extensions;

		public FileMediaFilter(String description, String[] allowedExtensions) {
			filterName = description;
			extensions = allowedExtensions;
		}

		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			int pos = f.getName().lastIndexOf('.');
			if (pos == -1) {
				return false;
			} else {
				String extension = f.getName().substring(pos + 1);
				for (String allowed_extension : extensions) {
					if (extension.equalsIgnoreCase(allowed_extension)) {
						return true;
					}
				}
				return false;
			}
		}

		@Override
		public String getDescription() {
			return filterName;
		}

	}

}
