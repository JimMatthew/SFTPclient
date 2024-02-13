import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HexFormat;
import java.util.Scanner;
import java.util.stream.Stream;

public class fileViewer extends JFrame {

    private static final long serialVersionUID = -1144784482245041604L;
    private final JTextArea textArea;
    final JMenuItem mntm;
    final JMenuItem miOpen;
    final JMenuItem miSave;
    final JMenuItem miSaveas;
    final JMenuItem miEdit;
    final JMenuItem miEditoff;
    final JMenuItem miFont;
    final JMenuItem miHex;
    private File currFile;
    String hex = "";
    final JScrollPane scrollPane;

    public fileViewer() {
        setTitle("FTP Client");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GridLayout layout = new GridLayout(1, 1);
        setResizable(true);
        setPreferredSize(new Dimension(800, 1000));
        pack();
        getContentPane().setLayout(new BorderLayout(0, 0));
        textArea = new JTextArea();
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setForeground(new Color(0, 0, 0));
        textArea.setEditable(false);

        Border border = BorderFactory.createLineBorder(Color.BLACK);
        textArea.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        scrollPane = new JScrollPane(textArea);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnNewMenu = new JMenu("menu");
        menuBar.add(mnNewMenu);
        mntm = new JMenuItem("About");
        miOpen = new JMenuItem("Open");
        miSave = new JMenuItem("Save");
        miSaveas = new JMenuItem("Save As");
        miEdit = new JMenuItem("Enable Editing");
        miEditoff = new JMenuItem("Disable Editing");
        miFont = new JMenuItem("Font");
        miHex = new JMenuItem("Hex");
        mnNewMenu.add(mntm);
        mnNewMenu.add(miOpen);
        mnNewMenu.add(miSave);
        mnNewMenu.add(miSaveas);
        JMenu mnNewMenu1 = new JMenu("Mode");
        mnNewMenu1.add(miEdit);
        mnNewMenu1.add(miEditoff);
        JMenu mnNewMenu2 = new JMenu("View");
        mnNewMenu2.add(miFont);
        mnNewMenu2.add(miHex);
        menuBar.add(mnNewMenu1);
        menuBar.add(mnNewMenu2);
        miEdit.addActionListener(event -> textArea.setEditable(true));
        miEditoff.addActionListener(event -> textArea.setEditable(false));
        miSaveas.addActionListener(event -> saveAs());
        miSave.addActionListener(event -> saveFile(currFile));
        miHex.addActionListener(event -> textArea.setText(stringToHex(textArea.getText())));
        miFont.addActionListener(event -> {new FontPicker();});
    }

    public fileViewer(File file) throws FileNotFoundException {
        this();
        openFile(file);
    }

    private void saveAs() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(miSaveas) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            saveFile(file);
        }
    }

    public void openFile3(File file) throws FileNotFoundException {
        currFile = file;
        System.out.print("open file");
        StringBuilder sb = new StringBuilder();
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine()).append('\n');
            }
        } catch (FileNotFoundException e) {
            dispose();
            throw e;
        }
        setTitle(file.getName());
        setVisible(true);
        textArea.setText(sb.toString());
    }

    public void openFile(File file) {
        currFile = file;

        System.out.print("open file");
        StringBuilder sb = new StringBuilder();
        try (Stream<String> lines = Files.lines(Paths.get(file.getPath()))) {
            lines.forEach(line -> sb.append(line).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setTitle(file.getName());
        setVisible(true);
        textArea.setText(sb.toString());
    }

    private void saveFile(File file) {
        FileWriter writer;
        try {
            writer = new FileWriter(file);
            writer.write(textArea.getText());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void saveFile(String file, String data) {
        try {
            Files.writeString(Paths.get(file), data);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void showHex() {
        String s = toHex(textArea.getText());
        textArea.setText(s);
    }

    static String stringToHex(String string) {
        StringBuilder buf = new StringBuilder(200);
        for (char ch : string.toCharArray()) {
            if (!buf.isEmpty())
                buf.append(' ');
            buf.append(String.format("%02x", (int) ch));
        }
        return buf.toString();
    }

    public String toHex(String value) {
        textArea.setWrapStyleWord(true);
        return HexFormat.of().formatHex(value.getBytes());
        //return "";
    }

    public String fromHex(String value) {
        return new String(HexFormat.of().parseHex(value));
        //return "";
    }
}
