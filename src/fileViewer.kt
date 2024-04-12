import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.swing.*

class fileViewer() : JFrame() {
    private val textArea: JTextArea
    val mntm: JMenuItem
    val miOpen: JMenuItem
    val miSave: JMenuItem
    val miSaveas: JMenuItem
    val miEdit: JMenuItem
    val miEditoff: JMenuItem
    val miFont: JMenuItem
    val miHex: JMenuItem
    private var currFile: File? = null
    var hex: String = ""
    val scrollPane: JScrollPane

    init {
        title = "FTP Client"
        defaultCloseOperation = DISPOSE_ON_CLOSE
        val layout = GridLayout(1, 1)
        isResizable = true
        preferredSize = Dimension(800, 1000)
        pack()
        contentPane.layout = BorderLayout(0, 0)
        textArea = JTextArea()
        textArea.wrapStyleWord = true
        textArea.lineWrap = true
        textArea.foreground = Color(0, 0, 0)
        textArea.isEditable = false

        val border = BorderFactory.createLineBorder(Color.BLACK)
        textArea.border = BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10))
        scrollPane = JScrollPane(textArea)
        contentPane.add(scrollPane, BorderLayout.CENTER)
        setLocationRelativeTo(null)
        val menuBar = JMenuBar()
        jMenuBar = menuBar

        val mnNewMenu = JMenu("menu")
        menuBar.add(mnNewMenu)
        mntm = JMenuItem("About")
        miOpen = JMenuItem("Open")
        miSave = JMenuItem("Save")
        miSaveas = JMenuItem("Save As")
        miEdit = JMenuItem("Enable Editing")
        miEditoff = JMenuItem("Disable Editing")
        miFont = JMenuItem("Font")
        miHex = JMenuItem("Hex")
        mnNewMenu.add(mntm)
        mnNewMenu.add(miOpen)
        mnNewMenu.add(miSave)
        mnNewMenu.add(miSaveas)
        val mnNewMenu1 = JMenu("Mode")
        mnNewMenu1.add(miEdit)
        mnNewMenu1.add(miEditoff)
        val mnNewMenu2 = JMenu("View")
        mnNewMenu2.add(miFont)
        mnNewMenu2.add(miHex)
        menuBar.add(mnNewMenu1)
        menuBar.add(mnNewMenu2)
        miEdit.addActionListener { textArea.isEditable = true }
        miEditoff.addActionListener { textArea.isEditable = false }
        miSaveas.addActionListener { saveAs() }
        miSave.addActionListener { saveFile(currFile) }
        miHex.addActionListener { textArea.text = stringToHex(textArea.text) }
        miFont.addActionListener { FontPicker() }
    }

    constructor(file: File) : this() {
        openFile(file)
    }

    private fun saveAs() {
        val fileChooser = JFileChooser()
        if (fileChooser.showSaveDialog(miSaveas) == JFileChooser.APPROVE_OPTION) {
            val file = fileChooser.selectedFile

            saveFile(file)
        }
    }

    @Throws(FileNotFoundException::class)
    fun openFile3(file: File) {
        currFile = file
        print("open file")
        val sb = StringBuilder()
        try {
            Scanner(file).use { sc ->
                while (sc.hasNextLine()) {
                    sb.append(sc.nextLine()).append('\n')
                }
            }
        } catch (e: FileNotFoundException) {
            dispose()
            throw e
        }
        title = file.name
        isVisible = true
        textArea.text = sb.toString()
    }

    fun openFile(file: File) {
        currFile = file

        print("open file")
        val sb = StringBuilder()
        try {
            Files.lines(Paths.get(file.path)).use { lines ->
                lines.forEach { line: String? -> sb.append(line).append("\n") }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        title = file.name
        isVisible = true
        textArea.text = sb.toString()
    }

    private fun saveFile(file: File?) {
        if (file != null){
            val writer: FileWriter
            try {
                writer = FileWriter(file)
                writer.write(textArea.text)
                writer.flush()
                writer.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun saveFile(file: String, data: String) {
        try {
            Files.writeString(Paths.get(file), data)
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    private fun showHex() {
        val s = toHex(textArea.text)
        textArea.text = s
    }

    fun toHex(value: String): String {
        textArea.wrapStyleWord = true
        return HexFormat.of().formatHex(value.toByteArray())
        //return "";
    }

    fun fromHex(value: String?): String {
        return String(HexFormat.of().parseHex(value))
        //return "";
    }

    companion object {
        private const val serialVersionUID = -1144784482245041604L
        fun stringToHex(string: String): String {
            val buf = StringBuilder(200)
            for (ch in string.toCharArray()) {
                if (buf.isNotEmpty()) buf.append(' ')
                buf.append(String.format("%02x", ch.code))
            }
            return buf.toString()
        }
    }
}
