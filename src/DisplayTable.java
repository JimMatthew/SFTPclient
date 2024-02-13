import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.apache.commons.net.ftp.FTPFile;

import files.FileCommon;

public class DisplayTable {
	
	private static final Map<String, Icon> CACHE = new HashMap<>();
	private FileModel fileModel;
	private JTable fileTable;
	
	public DisplayTable() {
		
		fileModel = new FileModel();
		fileTable = new JTable(fileModel);
		fileTable.setFillsViewportHeight(true);
        fileTable.setShowGrid(false);
        fileTable.setFont(new Font("Serif", Font.PLAIN, 13));
        JTableHeader tableHeader = fileTable.getTableHeader();
        tableHeader.setFont(new Font("Serif", Font.BOLD, 13));
        fileTable.setRowHeight(20);
        fileTable.setIntercellSpacing(new Dimension(0, 0));
	}
	
	public void updateFileTable(List<FileCommon> fileList) {
		
		fileModel.setData(fileList);
		fileTable.getColumnModel().getColumn(0).setPreferredWidth(10);
        fileTable.getColumnModel().getColumn(0).setMinWidth(30);
        fileTable.getColumnModel().getColumn(1).setMinWidth(320);
        fileTable.getColumnModel().getColumn(2).setPreferredWidth(7000);
	}
	
	public JTable getDisplayTable() {
		return fileTable;
	}
	
	private static Icon getFolderIcon() {
        Icon icon;

        if (CACHE.containsKey("file-dir")) {
            icon = CACHE.get("file-dir");
        } else {
            File temp = new File(System.getProperty("java.io.tmpdir") + File.separator + "icon");
            temp.mkdirs();

            icon = FileSystemView.getFileSystemView().getSystemIcon(temp);

            CACHE.put("file-dir", icon);

            temp.delete();
        }
        return icon;
    }

    //returns the system icon for the provided extension
    //if extension is empty it will return the icon for an extensionless file
    private static Icon getExtIcon(String ext) {
        Icon icon;
        if (CACHE.containsKey(ext)) {
            icon = CACHE.get(ext);
        } else {
        	//create a temp file with given ext so we can grab the icon from it
            File t = new File(System.getProperty("java.io.tmpdir") + File.separator + "ic." + ext);
            try {
                FileWriter fw = new FileWriter(t);
                fw.write("t");
                fw.close();
            } catch (IOException e) {

            }
            icon = FileSystemView.getFileSystemView().getSystemIcon(t);

            CACHE.put(ext, icon);
            t.delete();
        }
        return icon;
    }
    
    private static String formatSize(long v) {
        if (v < 1024) return v + "  B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double) v / (1L << (z * 10)), " KMGTPE".charAt(z));
    }
    
    class FileModel extends AbstractTableModel {
    	
		private static final long serialVersionUID = 1L;
		private String[] columns = {"", "Name", "Size"};
    	private List<FileCommon> fileList;
    	
    	public void setData(List<FileCommon> fileList) {
    		this.fileList = fileList;
    		fireTableDataChanged();
    	}

		@Override
		public int getRowCount() {
			return fileList.size();
		}

		@Override
		public int getColumnCount() {
			return columns.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			FileCommon file = fileList.get(rowIndex);
			
			switch(columnIndex) {
				case 0: 
					if (file.isDirectory()) {
						return getFolderIcon();
					} else {
						String ext = "";
						if (file.getFileName().contains(".")) {
			        		ext = file.getFileName().substring(file.getFileName().lastIndexOf('.'));
			        	}
						return getExtIcon(ext);
					}
				case 1:
					return file.getFileName();
				case 2: 
					return formatSize(file.getFileSize());
				default:
					return null;
			}
		}
		
		public String getColumnName(int col) {
	            return columns[col];
	    }
		
		public Class<?> getColumnClass(int column) {
            if (column == 0) {
                return Icon.class;
            }
            return super.getColumnClass(column);
        }

        public boolean isCellEditable(int row, int column) {
            return false;
        }
    	
    }
}
