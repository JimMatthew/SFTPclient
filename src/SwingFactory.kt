import java.awt.Color
import java.awt.Cursor
import javax.swing.JButton
import javax.swing.JTextField
import javax.swing.border.LineBorder

object SwingFactory {

    fun newField(): JTextField {
        val field = JTextField()
        field.border = LineBorder(Color.GRAY)
        return field
    }

    fun newUneditableField(): JTextField {
        val field = JTextField()
        field.border = LineBorder(Color.GRAY)
        field.isEditable = false
        return field
    }

    @JvmStatic
	fun newButton(label: String?): JButton {
        val button = JButton(label)
        button.background = Color.LIGHT_GRAY
        button.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        return button
    }
}
