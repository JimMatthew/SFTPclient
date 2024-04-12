import javax.swing.*;
import javax.swing.border.LineBorder;

import files.FileCommon;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class FtpClientGui extends JFrame {
   
	private static final long serialVersionUID = 1L;
	private final JTextField usernameField;
    private final JTextField hostField;
    private final JButton connectButton;
    private final JMenuItem mntmNewMenuItem;
    private final ftpClientManager manager;
    private final JTextField remotePathField;
    private final JTextField localPathField;
    private final JButton btnChangeLocalPath;
    private final JButton btnChangeRemotePath;
    private final JTextField localFilenameField;
    private final JTextField remoteFilenameField;
    private final JButton btnUpload;
    private final JButton btnDownload;
    private final JButton btnSave;
    private JButton btnConnectSaved;
    private final JTextArea statusTextArea;
    private final JButton btnDisconnect;
    private final JButton btnMakeLocalDir;
    private final JButton btnMakeRemoteDir;
    private JButton btnOpenRemoteFile;
    private final JPasswordField passField;
    private final JButton btnOpenLocalFile;
    private final JMenuItem mntmNewMenuItem2;
    private final JButton btnOpenLocalFileInDefaultApp;
    private final String[] connectionType = {"FTP", "SFTP", "FTP -jftp"};
    private final JComboBox<String> ctypeComboBox = new JComboBox<>(connectionType);
    private JComboBox<String> savedCombobox;
    private final JPanel localDispPanel;
    private final JPanel remoteDispPanel;
    private final JButton localParentBtn;
    private final JButton remoteParentBtn;
    private JTable remoteTable;
    private JTable localTable;
    private DisplayTable localDisplayTable;
    private DisplayTable remoteDisplayTable;

    public FtpClientGui(ftpClientManager manager) {
        this.manager = manager;
        
        try {
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            // If you want the System L&F instead, comment out the above line and
            // uncomment the following:
             UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exc) {
            System.err.println("Error loading L&F: " + exc);
        }
        setTitle("JFTP Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setPreferredSize(new Dimension(1000, 910));
        pack();
        getContentPane().setLayout(null);
        setLocationRelativeTo(null);

        usernameField = SwingFactory.newField();
        usernameField.setBounds(111, 79, 106, 20);
        getContentPane().add(usernameField);
        usernameField.setColumns(10);

        hostField = SwingFactory.newField();
        hostField.setBounds(111, 48, 106, 20);
        getContentPane().add(hostField);
        hostField.setColumns(10);

        connectButton = SwingFactory.newButton("Connect");
        connectButton.setBounds(255, 109, 115, 23);
        getContentPane().add(connectButton);

        btnSave = SwingFactory.newButton("Save");
        btnSave.setBounds(255, 141, 115, 23);
        getContentPane().add(btnSave);

        btnConnectSaved = SwingFactory.newButton("Connect");
        btnConnectSaved.setBounds(255, 171, 115, 23);
        getContentPane().add(btnConnectSaved);

        ctypeComboBox.setBounds(111, 141, 106, 23);
        getContentPane().add(ctypeComboBox);

        String[] savedServers = {};
        savedCombobox = new JComboBox<>(savedServers);
        savedCombobox.setBounds(111, 171, 106, 23);
        getContentPane().add(savedCombobox);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBounds(0, 0, 986, 22);
        getContentPane().add(menuBar);

        JMenu mnNewMenu = new JMenu("Menu");
        menuBar.add(mnNewMenu);

        mntmNewMenuItem = new JMenuItem("About");
        mnNewMenu.add(mntmNewMenuItem);
        
        mntmNewMenuItem2 = new JMenuItem("Exit");
        mnNewMenu.add(mntmNewMenuItem2);

        JLabel lblHost = new JLabel("Server");
        lblHost.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblHost.setBounds(20, 50, 70, 14);
        getContentPane().add(lblHost);

        JLabel lblUser = new JLabel("User Name");
        lblUser.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblUser.setBounds(20, 82, 81, 14);
        getContentPane().add(lblUser);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblPassword.setBounds(20, 113, 70, 14);
        getContentPane().add(lblPassword);
        
        remoteDispPanel = new JPanel();
        remoteDispPanel.setBounds(510, 331, 460, 490);
        getContentPane().add(remoteDispPanel);
        
        remotePathField = new JTextField();
        remotePathField.setBorder(new LineBorder(Color.GRAY));
        remotePathField.setBounds(670, 270, 294, 20);
        getContentPane().add(remotePathField);
        remotePathField.setColumns(10);

        JLabel lblPath = new JLabel("Path");
        lblPath.setBounds(635, 273, 49, 14);
        getContentPane().add(lblPath);

        localDispPanel = new JPanel();
        localDispPanel.setBounds(10, 331, 460, 489);
        getContentPane().add(localDispPanel);
        
        localPathField = new JTextField();
        localPathField.setBorder(new LineBorder(Color.GRAY, 1, true));
        localPathField.setBounds(48, 267, 294, 20);
        getContentPane().add(localPathField);
        localPathField.setColumns(10);

        JLabel lblPath_1 = new JLabel("Path");
        lblPath_1.setBounds(15, 270, 49, 14);
        getContentPane().add(lblPath_1);

        btnChangeLocalPath = SwingFactory.newButton("Change");
        btnChangeLocalPath.setBounds(361, 269, 89, 23);
        getContentPane().add(btnChangeLocalPath);

        btnChangeRemotePath = SwingFactory.newButton("Change");
        btnChangeRemotePath.setBounds(524, 269, 89, 23);
        getContentPane().add(btnChangeRemotePath);

        localFilenameField = SwingFactory.newField();
        localFilenameField.setBounds(48, 300, 142, 20);
        getContentPane().add(localFilenameField);
        localFilenameField.setColumns(10);

        btnOpenLocalFile = SwingFactory.newButton("Open File");
        btnOpenLocalFile.setBounds(180, 831, 90, 23);
        getContentPane().add(btnOpenLocalFile);

        btnOpenLocalFileInDefaultApp = SwingFactory.newButton("Open w/");
        btnOpenLocalFileInDefaultApp.setBounds(280, 831, 90, 23);
        getContentPane().add(btnOpenLocalFileInDefaultApp);

        JLabel lblFile = new JLabel("File");
        lblFile.setBounds(15, 306, 37, 14);
        getContentPane().add(lblFile);

        remoteFilenameField = SwingFactory.newField();
        remoteFilenameField.setBounds(670, 300, 133, 20);
        getContentPane().add(remoteFilenameField);
        remoteFilenameField.setColumns(10);

        JLabel lblFile_1 = new JLabel("File");
        lblFile_1.setBounds(635, 306, 44, 14);
        getContentPane().add(lblFile_1);

        btnUpload = SwingFactory.newButton("->");
        btnUpload.setBounds(361, 297, 89, 23);
        getContentPane().add(btnUpload);

        btnDownload = SwingFactory.newButton("<-");
        btnDownload.setBounds(524, 297, 89, 23);
        getContentPane().add(btnDownload);

        JTextField statusField = new JTextField();

        statusTextArea = new JTextArea();
        statusTextArea.setBounds(720, 48, 242, 90);
        statusTextArea.setBorder(new LineBorder(Color.GRAY));
        statusTextArea.setEditable(false);
        getContentPane().add(statusField);

        JScrollPane StatusScrollPane = new JScrollPane(statusTextArea);
        StatusScrollPane.setBounds(635, 46, 329, 140);
        getContentPane().add(StatusScrollPane);

        btnDisconnect = SwingFactory.newButton("Disconnect");
        btnDisconnect.setBounds(255, 76, 115, 23);
        getContentPane().add(btnDisconnect);

        btnMakeLocalDir = SwingFactory.newButton("Make Directory");
        btnMakeLocalDir.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btnMakeLocalDir.setBounds(20, 831, 113, 23);
        getContentPane().add(btnMakeLocalDir);

        btnMakeRemoteDir = SwingFactory.newButton("Make Directory");
        btnMakeRemoteDir.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btnMakeRemoteDir.setBounds(534, 832, 113, 23);
        getContentPane().add(btnMakeRemoteDir);

        btnOpenRemoteFile = SwingFactory.newButton("Open File");
        btnOpenRemoteFile.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btnOpenRemoteFile.setBounds(700, 832, 113, 23);
        getContentPane().add(btnOpenRemoteFile);

        passField = new JPasswordField();
        passField.setBorder(new LineBorder(Color.GRAY));
        passField.setBounds(111, 110, 106, 20);
        getContentPane().add(passField);
        
        JLabel lblProtocol = new JLabel("Protocol");
        lblProtocol.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblProtocol.setBounds(20, 146, 70, 14);
        getContentPane().add(lblProtocol);

        JLabel lblSaved = new JLabel("Saved");
        lblSaved.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblSaved.setBounds(20, 175, 70, 14);
        getContentPane().add(lblSaved);

        localParentBtn = new JButton("<-- back");
        localParentBtn.setBounds(253, 298, 89, 23);
        getContentPane().add(localParentBtn);
        
        remoteParentBtn = new JButton("<-- back");
        remoteParentBtn.setBounds(875, 299, 89, 23);
        getContentPane().add(remoteParentBtn);
        updateSavedServerList();
        revalidate();
        repaint();
        setVisible(true);
        setAction();
    }

    private void setAction() {
        connectButton.addActionListener(event -> manager.connectPressed());
        btnChangeLocalPath.addActionListener(event -> manager.changeLocalFilePath());
        btnChangeRemotePath.addActionListener(event -> manager.changeRemoteFilePath());
        btnUpload.addActionListener(event -> manager.uploadPressed());
        btnDownload.addActionListener(event -> manager.downloadPressed());
        mntmNewMenuItem.addActionListener(event -> manager.aboutPressed());
        mntmNewMenuItem2.addActionListener(event -> System.exit(0));
        btnMakeLocalDir.addActionListener(event -> manager.makeLocalDirectoryPressed());
        btnMakeRemoteDir.addActionListener(event -> manager.makeRemoteDirectoryPressed());
        btnDisconnect.addActionListener(event -> manager.disconnectRemoteHost());
        localPathField.addActionListener(event -> manager.changeLocalFilePath());
        remotePathField.addActionListener(event -> manager.changeRemoteFilePath());
        btnOpenLocalFile.addActionListener(event -> manager.openFilePressed());
        btnOpenLocalFileInDefaultApp.addActionListener(event -> manager.openFileDefaultPressed(DirectoryType.Local));
        localParentBtn.addActionListener(event -> manager.parentPressed(DirectoryType.Local, manager.localSystemType()));
        remoteParentBtn.addActionListener(event -> manager.parentPressed(DirectoryType.Remote, manager.remoteSystemType()));
        btnSave.addActionListener(event -> manager.saveServer());
        btnConnectSaved.addActionListener(event -> connectSaved());
        btnOpenRemoteFile.addActionListener(event -> manager.openRemoteFile());
    }
    
    private void setMouseListener(JTable table, DirectoryType t) {
    	
        MouseAdapter adapter = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    int r = ((JTable)e.getSource()).rowAtPoint(e.getPoint());
                    manager.mouseClickOnItem(r, t);
                }
            }
        };
        table.addMouseListener(adapter);
    }

    private void connectSaved(){
        int t = savedCombobox.getSelectedIndex();
        manager.connectSavedServer(t);
    }

    public String getUser() {
        return usernameField.getText();
    }
    
    public void displayRemoteFiles(List<FileCommon> fileList) {
    	if (remoteTable == null) {
    		remoteDisplayTable = new DisplayTable();
    		remoteDisplayTable.updateFileTable(fileList);
    		remoteTable = remoteDisplayTable.getDisplayTable();
    		remoteDispPanel.add(new JScrollPane(remoteTable));
    		remoteDispPanel.revalidate();
    		setMouseListener(remoteTable, DirectoryType.Remote);
    		
    	} else {
    		remoteDisplayTable.updateFileTable(fileList);
    	}
    }
    
    public void displayLocalFiles(List<FileCommon> fileList) {
    	if (localTable == null) {
    		localDisplayTable = new DisplayTable();
    		localDisplayTable.updateFileTable(fileList);
    		localTable = localDisplayTable.getDisplayTable();
        	localDispPanel.add(new JScrollPane(localTable));
        	setMouseListener(localTable, DirectoryType.Local);
        	localDispPanel.revalidate();
    	} else {
    		localDisplayTable.updateFileTable(fileList);
    	}
    }

    public void updateSavedServerList() {
        String[] array = manager.getAllHostnames();
        //System.out.println(array[0]);
        savedCombobox.removeAllItems();
        for(String s : array){
            savedCombobox.addItem(s);
        }
    }
    
    public void remoteDisconnect() {
    	remoteTable = null;
    	remoteDispPanel.removeAll();
    	remoteDispPanel.revalidate();
    	remoteDispPanel.repaint();
    }

    public String getHostField() {
        return hostField.getText();
    }

    public void setLocalPath(String path) {
        localPathField.setText(path);
    }

    public void setRemotePath(String path) {
        remotePathField.setText(path);
    }

    public String getLocalPathField() {
        return localPathField.getText();
    }

    public String getRemotePathField() {
        return remotePathField.getText();
    }

    public String getLocalFileNameField() {
        return localFilenameField.getText();
    }

    public void setLocalFileNameField(String name) {
        localFilenameField.setText(name);
    }

    public String getRemoteFilenameField() {
        return remoteFilenameField.getText();
    }

    public void setRemoteFileNameField(String name) {
        remoteFilenameField.setText(name);
    }

    public void setStatusField(String text) {
        statusTextArea.setText(text);
    }
    
    public int getConnectionType() {
    	return ctypeComboBox.getSelectedIndex();
    }

    public char[] getPasswordField() {
        return passField.getPassword();
    }
}
