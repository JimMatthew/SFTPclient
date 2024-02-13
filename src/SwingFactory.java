import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class SwingFactory {

	public SwingFactory() {
		
	}
	
	public static JTextField newField() {
		JTextField field = new JTextField();
		field.setBorder(new LineBorder(Color.GRAY));
		return field;
	}
	
	public static JTextField newUneditableField() {
		JTextField field = new JTextField();
		field.setBorder(new LineBorder(Color.GRAY));
		field.setEditable(false);
		return field;
	}
	
	public static JButton newButton(String label) {
		JButton button = new JButton(label);
		button.setBackground(Color.LIGHT_GRAY);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		return button;
	}
}
