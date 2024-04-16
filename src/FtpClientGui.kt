import com.alee.laf.WebLookAndFeel
import files.FileCommon
import mdlaf.MaterialLookAndFeel
import mdlaf.themes.MaterialLiteTheme
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.LineBorder
import kotlin.system.exitProcess


class FtpClientGui(private val manager: ftpClientManager) : JFrame() {
    private val usernameField: JTextField
    val hostField: JTextField
    private val connectButton: JButton
    private val mntmNewMenuItem: JMenuItem
    private val remotePathField: JTextField
    private val localPathField: JTextField
    private val btnChangeLocalPath: JButton
    private val btnChangeRemotePath: JButton
    private val localFilenameField: JTextField
    val remoteFilenameField: JTextField
    private val btnUpload: JButton
    private val btnDownload: JButton
    private val btnSave: JButton
    private val btnConnectSaved: JButton
    private val statusTextArea: JTextArea
    private val btnDisconnect: JButton
    private val btnMakeLocalDir: JButton
    private val btnMakeRemoteDir: JButton
    private val btnOpenRemoteFile: JButton
    private val passField: JPasswordField
    private val btnOpenLocalFile: JButton
    private val mntmNewMenuItem2: JMenuItem
    private val btnOpenLocalFileInDefaultApp: JButton
    val connectionType = arrayOf("FTP", "SFTP", "FTP -jftp")
    private val ctypeComboBox = JComboBox(connectionType)
    private val savedCombobox: JComboBox<String>
    private val localDispPanel: JPanel
    private val remoteDispPanel: JPanel
    private val localParentBtn: JButton
    private val remoteParentBtn: JButton
    private var remoteTable: JTable? = null
    private var localTable: JTable? = null
    private var localDisplayTable: DisplayTable? = null
    private var remoteDisplayTable: DisplayTable? = null

    init {
        try {
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            // If you want the System L&F instead, comment out the above line and
            // uncomment the following:
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
            //UIManager.setLookAndFeel(WebLookAndFeel())
           // UIManager.setLookAndFeel(MaterialLookAndFeel(MaterialLiteTheme()))
        } catch (exc: Exception) {
            System.err.println("Error loading L&F: $exc")
        }
        title = "JFTP Client"
        defaultCloseOperation = EXIT_ON_CLOSE
        isResizable = true
        preferredSize = Dimension(1000, 910)
        pack()
        contentPane.layout = null
        setLocationRelativeTo(null)

        usernameField = SwingFactory.newField()
        usernameField.setBounds(111, 79, 106, 20)
        contentPane.add(usernameField)
        usernameField.columns = 10

        hostField = SwingFactory.newField()
        hostField.setBounds(111, 48, 106, 20)
        contentPane.add(hostField)
        hostField.columns = 10

        connectButton = SwingFactory.newButton("Connect")
        connectButton.setBounds(255, 109, 115, 23)
        contentPane.add(connectButton)

        btnSave = SwingFactory.newButton("Save")
        btnSave.setBounds(255, 141, 115, 23)
        contentPane.add(btnSave)

        btnConnectSaved = SwingFactory.newButton("Connect")
        btnConnectSaved.setBounds(255, 171, 115, 23)
        contentPane.add(btnConnectSaved)

        ctypeComboBox.setBounds(111, 141, 106, 23)
        contentPane.add(ctypeComboBox)

        val savedServers = arrayOf<String>()
        savedCombobox = JComboBox(savedServers)
        savedCombobox.setBounds(111, 171, 106, 23)
        contentPane.add(savedCombobox)

        val menuBar = JMenuBar()
        menuBar.setBounds(0, 0, 986, 22)
        contentPane.add(menuBar)

        val mnNewMenu = JMenu("Menu")
        menuBar.add(mnNewMenu)

        mntmNewMenuItem = JMenuItem("About")
        mnNewMenu.add(mntmNewMenuItem)

        mntmNewMenuItem2 = JMenuItem("Exit")
        mnNewMenu.add(mntmNewMenuItem2)

        val lblHost = JLabel("Server")
        lblHost.font = Font("Tahoma", Font.BOLD, 11)
        lblHost.setBounds(20, 50, 70, 14)
        contentPane.add(lblHost)

        val lblUser = JLabel("User Name")
        lblUser.font = Font("Tahoma", Font.BOLD, 11)
        lblUser.setBounds(20, 82, 81, 14)
        contentPane.add(lblUser)

        val lblPassword = JLabel("Password")
        lblPassword.font = Font("Tahoma", Font.BOLD, 11)
        lblPassword.setBounds(20, 113, 70, 14)
        contentPane.add(lblPassword)

        remoteDispPanel = JPanel()
        remoteDispPanel.setBounds(510, 331, 460, 490)
        contentPane.add(remoteDispPanel)

        remotePathField = JTextField()
        remotePathField.border = LineBorder(Color.GRAY)
        remotePathField.setBounds(670, 270, 294, 20)
        contentPane.add(remotePathField)
        remotePathField.columns = 10

        val lblPath = JLabel("Path")
        lblPath.setBounds(635, 273, 49, 14)
        contentPane.add(lblPath)

        localDispPanel = JPanel()
        localDispPanel.setBounds(10, 331, 460, 489)
        contentPane.add(localDispPanel)

        localPathField = JTextField()
        localPathField.border = LineBorder(Color.GRAY, 1, true)
        localPathField.setBounds(48, 267, 294, 20)
        contentPane.add(localPathField)
        localPathField.columns = 10

        val lblPath_1 = JLabel("Path")
        lblPath_1.setBounds(15, 270, 49, 14)
        contentPane.add(lblPath_1)

        btnChangeLocalPath = SwingFactory.newButton("Change")
        btnChangeLocalPath.setBounds(361, 269, 89, 23)
        contentPane.add(btnChangeLocalPath)

        btnChangeRemotePath = SwingFactory.newButton("Change")
        btnChangeRemotePath.setBounds(524, 269, 89, 23)
        contentPane.add(btnChangeRemotePath)

        localFilenameField = SwingFactory.newField()
        localFilenameField.setBounds(48, 300, 142, 20)
        contentPane.add(localFilenameField)
        localFilenameField.columns = 10

        btnOpenLocalFile = SwingFactory.newButton("Open File")
        btnOpenLocalFile.setBounds(180, 831, 90, 23)
        contentPane.add(btnOpenLocalFile)

        btnOpenLocalFileInDefaultApp = SwingFactory.newButton("Open w/")
        btnOpenLocalFileInDefaultApp.setBounds(280, 831, 90, 23)
        contentPane.add(btnOpenLocalFileInDefaultApp)

        val lblFile = JLabel("File")
        lblFile.setBounds(15, 306, 37, 14)
        contentPane.add(lblFile)

        remoteFilenameField = SwingFactory.newField()
        remoteFilenameField.setBounds(670, 300, 133, 20)
        contentPane.add(remoteFilenameField)
        remoteFilenameField.columns = 10

        val lblFile_1 = JLabel("File")
        lblFile_1.setBounds(635, 306, 44, 14)
        contentPane.add(lblFile_1)

        btnUpload = SwingFactory.newButton("->")
        btnUpload.setBounds(361, 297, 89, 23)
        contentPane.add(btnUpload)

        btnDownload = SwingFactory.newButton("<-")
        btnDownload.setBounds(524, 297, 89, 23)
        contentPane.add(btnDownload)

        val statusField = JTextField()

        statusTextArea = JTextArea()
        statusTextArea.setBounds(720, 48, 242, 90)
        statusTextArea.border = LineBorder(Color.GRAY)
        statusTextArea.isEditable = false
        contentPane.add(statusField)

        val StatusScrollPane = JScrollPane(statusTextArea)
        StatusScrollPane.setBounds(635, 46, 329, 140)
        contentPane.add(StatusScrollPane)

        btnDisconnect = SwingFactory.newButton("Disconnect")
        btnDisconnect.setBounds(255, 76, 115, 23)
        contentPane.add(btnDisconnect)

        btnMakeLocalDir = SwingFactory.newButton("Make Directory")
        btnMakeLocalDir.font = Font("Tahoma", Font.PLAIN, 10)
        btnMakeLocalDir.setBounds(20, 831, 113, 23)
        contentPane.add(btnMakeLocalDir)

        btnMakeRemoteDir = SwingFactory.newButton("Make Directory")
        btnMakeRemoteDir.font = Font("Tahoma", Font.PLAIN, 10)
        btnMakeRemoteDir.setBounds(534, 832, 113, 23)
        contentPane.add(btnMakeRemoteDir)

        btnOpenRemoteFile = SwingFactory.newButton("Open File")
        btnOpenRemoteFile.font = Font("Tahoma", Font.PLAIN, 10)
        btnOpenRemoteFile.setBounds(700, 832, 113, 23)
        contentPane.add(btnOpenRemoteFile)

        passField = JPasswordField()
        passField.border = LineBorder(Color.GRAY)
        passField.setBounds(111, 110, 106, 20)
        contentPane.add(passField)

        val lblProtocol = JLabel("Protocol")
        lblProtocol.font = Font("Tahoma", Font.BOLD, 11)
        lblProtocol.setBounds(20, 146, 70, 14)
        contentPane.add(lblProtocol)

        val lblSaved = JLabel("Saved")
        lblSaved.font = Font("Tahoma", Font.BOLD, 11)
        lblSaved.setBounds(20, 175, 70, 14)
        contentPane.add(lblSaved)

        localParentBtn = JButton("<-- back")
        localParentBtn.setBounds(253, 298, 89, 23)
        contentPane.add(localParentBtn)

        remoteParentBtn = JButton("<-- back")
        remoteParentBtn.setBounds(875, 299, 89, 23)
        contentPane.add(remoteParentBtn)
        updateSavedServerList()
        revalidate()
        repaint()
        isVisible = true
        setAction()
    }

    private fun setAction() {
        connectButton.addActionListener { manager.connectPressed() }
        btnChangeLocalPath.addActionListener { manager.changeLocalFilePath() }
        btnChangeRemotePath.addActionListener { manager.changeRemoteFilePath() }
        btnUpload.addActionListener { uploadFilePress() }
        btnDownload.addActionListener { downloadFilePress() }
        mntmNewMenuItem.addActionListener { manager.aboutPressed() }
        mntmNewMenuItem2.addActionListener { exitProcess(0) }
        btnMakeLocalDir.addActionListener { manager.makeLocalDirectoryPressed() }
        btnMakeRemoteDir.addActionListener { manager.makeRemoteDirectoryPressed() }
        btnDisconnect.addActionListener { manager.disconnectRemoteHost() }
        localPathField.addActionListener { manager.changeLocalFilePath() }
        remotePathField.addActionListener { manager.changeRemoteFilePath() }
        btnOpenLocalFile.addActionListener { manager.openFilePressed() }
        btnOpenLocalFileInDefaultApp.addActionListener {
            manager.openFileDefaultPressed(
                DirectoryType.Local
            )
        }
        localParentBtn.addActionListener {
            manager.parentPressed(
                DirectoryType.Local,
                manager.localSystemType()
            )
        }
        remoteParentBtn.addActionListener {
            manager.parentPressed(
                DirectoryType.Remote,
                manager.remoteSystemType()
            )
        }
        btnSave.addActionListener { manager.saveServer() }
        btnConnectSaved.addActionListener { connectSaved() }
        btnOpenRemoteFile.addActionListener { manager.openRemoteFile() }
    }

    private fun downloadFilePress(){
        val lPath = getLocalPathField()
        val rPath = getRemotePathField()
        val remoteFileName = getRemoteFilenameField()
        val localFileName = localFileNameField
        manager.downloadPress(localFileName!!, remoteFileName, lPath, rPath)
    }

    private fun uploadFilePress(){
        val lPath = getLocalPathField()
        val rPath = getRemotePathField()
        val remoteFileName = getRemoteFilenameField()
        val localFileName = localFileNameField
        manager.uploadPress(localFileName!!, remoteFileName, lPath, rPath)
    }

    private fun setMouseListener(table: JTable, t: DirectoryType) {
        val adapter: MouseAdapter = object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.button == MouseEvent.BUTTON1) {
                    val r = (e.source as JTable).rowAtPoint(e.point)
                    manager.mouseClickOnItem(r, t)
                }
            }
        }
        table.addMouseListener(adapter)
    }

    private fun connectSaved() {
        val t = savedCombobox.selectedIndex
        manager.connectSavedServer(t)
    }

    val user: String
        get() = usernameField.text

    fun displayRemoteFiles(fileList: List<FileCommon>) {
        if (remoteTable == null) {
            remoteDisplayTable = DisplayTable()
            remoteDisplayTable!!.updateFileTable(fileList)
            remoteTable = remoteDisplayTable!!.displayTable
            remoteDispPanel.add(JScrollPane(remoteTable))
            remoteDispPanel.revalidate()
            setMouseListener(remoteTable!!, DirectoryType.Remote)
        } else {
            remoteDisplayTable!!.updateFileTable(fileList)
        }
    }

    fun displayLocalFiles(fileList: List<FileCommon>) {
        if (localTable == null) {
            localDisplayTable = DisplayTable()
            localDisplayTable!!.updateFileTable(fileList)
            localTable = localDisplayTable!!.displayTable
            localDispPanel.add(JScrollPane(localTable))
            setMouseListener(localTable!!, DirectoryType.Local)
            localDispPanel.revalidate()
        } else {
            localDisplayTable!!.updateFileTable(fileList)
        }
    }

    fun updateSavedServerList() {
        val array = manager.allHostnames
        savedCombobox.removeAllItems()
        for (s in array) {
            savedCombobox.addItem(s)
        }
    }

    fun remoteDisconnect() {
        remoteTable = null
        remoteDispPanel.removeAll()
        remoteDispPanel.revalidate()
        remoteDispPanel.repaint()
    }

    fun getHostField(): String {
        return hostField.text
    }

    fun setLocalPath(path: String?) {
        localPathField.text = path
    }

    fun setRemotePath(path: String?) {
        remotePathField.text = path
    }

    fun getLocalPathField(): String {
        return localPathField.text
    }

    fun getRemotePathField(): String {
        return remotePathField.text
    }

    var localFileNameField: String?
        get() = localFilenameField.text
        set(name) {
            localFilenameField.text = name
        }

    fun getRemoteFilenameField(): String {
        return remoteFilenameField.text
    }

    fun setRemoteFileNameField(name: String?) {
        remoteFilenameField.text = name
    }

    fun setStatusField(text: String?) {
        statusTextArea.text = text
    }

    fun getConnectionType(): Int {
        return ctypeComboBox.selectedIndex
    }

    val passwordField: CharArray
        get() = passField.password

    companion object {
        private const val serialVersionUID = 1L
    }
}
