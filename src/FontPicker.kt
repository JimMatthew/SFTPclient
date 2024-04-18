import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

public class FontPicker extends JFrame {

	 JButton btnSave;
	 JButton btnCancel;
	 private JTextField sizeTextField;
	 
	public FontPicker() {
		 setTitle("FTP Client");
	     setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	     GridLayout layout = new GridLayout(1, 1);
	     setResizable(true);
	     setPreferredSize(new Dimension(300, 200));
	     pack();
	     getContentPane().setLayout(null);
	     
	     btnSave = new JButton("New button");
	     btnSave.setBounds(24, 129, 89, 23);
	     getContentPane().add(btnSave);
	     
	     JButton btnCancel = new JButton("New button");
	     btnCancel.setBounds(164, 129, 89, 23);
	     getContentPane().add(btnCancel);
	     setVisible(true);
	     
	     
	     JLabel lblNewLabel = new JLabel("Font Size");
	     lblNewLabel.setBounds(10, 50, 49, 14);
	     getContentPane().add(lblNewLabel);
	     
	     JCheckBox chckbxNewCheckBox = new JCheckBox("Bold");
	     chckbxNewCheckBox.setBounds(164, 19, 99, 23);
	     getContentPane().add(chckbxNewCheckBox);
	     
	     sizeTextField = new JTextField();
	     sizeTextField.setBounds(69, 47, 96, 20);
	     getContentPane().add(sizeTextField);
	     sizeTextField.setColumns(10);
	}
}
