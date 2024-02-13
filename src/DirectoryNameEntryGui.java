import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class DirectoryNameEntryGui extends JFrame {
    private final JTextField newDirNameField;
    private final ftpClientManager manager;
    private final JButton btnCreate;
    private final JButton btnCancel;
    private DirectoryType type;

    public DirectoryNameEntryGui(ftpClientManager manager) {
        this.manager = manager;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setPreferredSize(new Dimension(355, 180));
        pack();
        getContentPane().setLayout(null);
        setLocationRelativeTo(null);
        JLabel lblPleaseEnterA = new JLabel("Please enter a name for the new directory");
        lblPleaseEnterA.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblPleaseEnterA.setBounds(25, 22, 358, 14);
        getContentPane().add(lblPleaseEnterA);

        newDirNameField = new JTextField();
        newDirNameField.setBorder(new LineBorder(new Color(171, 173, 179)));
        newDirNameField.setBounds(65, 47, 214, 20);
        getContentPane().add(newDirNameField);
        newDirNameField.setColumns(10);

        btnCreate = SwingFactory.newButton("Create");
        btnCreate.setBounds(65, 93, 89, 23);
        getContentPane().add(btnCreate);

        btnCancel = SwingFactory.newButton("Cancel");
        btnCancel.setBounds(190, 93, 89, 23);
        getContentPane().add(btnCancel);
        addAction();
    }

    public void launchAndGetName(DirectoryType type) {
        setVisible(true);
        this.type = type;
        setTitle("Add " + type.toString() + " Directory");
    }

    private void addAction() {
        btnCancel.addActionListener(event -> dispose());
        btnCreate.addActionListener(event -> returnNameToManager());
        newDirNameField.addActionListener(event -> returnNameToManager());
    }

    private void returnNameToManager() {
        manager.makeDirectory(type, newDirNameField.getText());
        setVisible(false);
        dispose();
    }
}
