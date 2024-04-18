import SwingFactory.newButton
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JTextField
import javax.swing.border.LineBorder

class DirectoryNameEntryGui(private val manager: ftpClientManager) : JFrame() {
    private val newDirNameField: JTextField
    private val btnCreate: JButton
    private val btnCancel: JButton
    private var type: DirectoryType? = null

    init {
        defaultCloseOperation = DISPOSE_ON_CLOSE
        isResizable = true
        preferredSize = Dimension(355, 180)
        pack()
        contentPane.layout = null
        setLocationRelativeTo(null)
        val lblPleaseEnterA = JLabel("Please enter a name for the new directory")
        lblPleaseEnterA.font = Font("Tahoma", Font.BOLD, 13)
        lblPleaseEnterA.setBounds(25, 22, 358, 14)
        contentPane.add(lblPleaseEnterA)

        newDirNameField = JTextField()
        newDirNameField.border = LineBorder(Color(171, 173, 179))
        newDirNameField.setBounds(65, 47, 214, 20)
        contentPane.add(newDirNameField)
        newDirNameField.columns = 10

        btnCreate = newButton("Create")
        btnCreate.setBounds(65, 93, 89, 23)
        contentPane.add(btnCreate)

        btnCancel = newButton("Cancel")
        btnCancel.setBounds(190, 93, 89, 23)
        contentPane.add(btnCancel)
        addAction()
    }

    fun launchAndGetName(type: DirectoryType) {
        isVisible = true
        this.type = type
        title = "Add $type Directory"
    }

    private fun addAction() {
        btnCancel.addActionListener { dispose() }
        btnCreate.addActionListener { returnNameToManager() }
        newDirNameField.addActionListener { returnNameToManager() }
    }

    private fun returnNameToManager() {
        manager.makeDirectory(type!!, newDirNameField.text)
        isVisible = false
        dispose()
    }
}
