import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class aboutScreen extends JFrame{

	public aboutScreen() {
		setTitle("FTP Client");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(true);
		setPreferredSize(new Dimension(400, 500));
		pack();
		getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("FTP Client");
		lblNewLabel.setFont(new Font("Georgia", Font.BOLD, 24));
		lblNewLabel.setBounds(35, 38, 182, 74);
		getContentPane().add(lblNewLabel);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		JLabel lblNewLabel_1 = new JLabel("Supports FTP & SFTP");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblNewLabel_1.setBounds(35, 326, 285, 14);
		getContentPane().add(lblNewLabel_1);
		JLabel lblNewLabel_3 = new JLabel("JSCH .2.16");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblNewLabel_3.setBounds(35, 356, 285, 14);
		getContentPane().add(lblNewLabel_3);
		JLabel lblNewLabel_2 = new JLabel("2024 James Lindstrom");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_2.setBounds(31, 403, 243, 19);
		getContentPane().add(lblNewLabel_2);
		setLocationRelativeTo(null);
		//setVisible(true);
	}
}
