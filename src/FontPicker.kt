import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*

class FontPicker : JFrame() {
    var btnSave: JButton
    var btnCancel: JButton? = null
    private val sizeTextField: JTextField

    init {
        title = "FTP Client"
        defaultCloseOperation = DISPOSE_ON_CLOSE
        val layout = GridLayout(1, 1)
        isResizable = true
        preferredSize = Dimension(300, 200)
        pack()
        contentPane.layout = null

        btnSave = JButton("New button")
        btnSave.setBounds(24, 129, 89, 23)
        contentPane.add(btnSave)

        val btnCancel = JButton("New button")
        btnCancel.setBounds(164, 129, 89, 23)
        contentPane.add(btnCancel)
        isVisible = true


        val lblNewLabel = JLabel("Font Size")
        lblNewLabel.setBounds(10, 50, 49, 14)
        contentPane.add(lblNewLabel)

        val chckbxNewCheckBox = JCheckBox("Bold")
        chckbxNewCheckBox.setBounds(164, 19, 99, 23)
        contentPane.add(chckbxNewCheckBox)

        sizeTextField = JTextField()
        sizeTextField.setBounds(69, 47, 96, 20)
        contentPane.add(sizeTextField)
        sizeTextField.columns = 10
    }
}
