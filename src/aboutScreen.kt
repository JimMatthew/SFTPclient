import java.awt.Dimension
import java.awt.Font
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.JFrame
import javax.swing.JLabel

class aboutScreen : JFrame() {
    init {
        title = "FTP Client"
        defaultCloseOperation = DISPOSE_ON_CLOSE
        isResizable = true
        preferredSize = Dimension(400, 500)
        pack()
        contentPane.layout = null

        val lblNewLabel = JLabel("SFTP Client")
        lblNewLabel.font = Font("Georgia", Font.BOLD, 24)
        lblNewLabel.setBounds(35, 38, 182, 74)
        contentPane.add(lblNewLabel)

        val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = Date()
        println(dateFormat.format(date))
        val lblNewLabel_1 = JLabel("Supports FTP & SFTP")
        lblNewLabel_1.font = Font("Tahoma", Font.PLAIN, 10)
        lblNewLabel_1.setBounds(35, 326, 285, 14)
        contentPane.add(lblNewLabel_1)
        val lblNewLabel_3 = JLabel("JSCH .2.16")
        lblNewLabel_3.font = Font("Tahoma", Font.PLAIN, 10)
        lblNewLabel_3.setBounds(35, 356, 285, 14)
        contentPane.add(lblNewLabel_3)
        val lblNewLabel_2 = JLabel("2024 James Lindstrom")
        lblNewLabel_2.font = Font("Tahoma", Font.PLAIN, 15)
        lblNewLabel_2.setBounds(31, 403, 243, 19)
        contentPane.add(lblNewLabel_2)
        setLocationRelativeTo(null)
        //setVisible(true);
    }
}
