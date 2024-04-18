import javax.swing.SwingUtilities

//import com.formdev.flatlaf.FlatLightLaf;
object ftpClientt {
    @JvmStatic
    fun main(args: Array<String>) {
        SwingUtilities.invokeLater { val manager = ftpClientManager() }
    }
}
