package files;

import jftp.JFTP.JFTPFile;

public class FileJFTPFile implements FileCommon {

	private final JFTPFile file;
	
	public FileJFTPFile(JFTPFile file) {
		this.file = file;
	}
	@Override
	public String getFileName() {
		return file.getName();
	}

	@Override
	public long getFileSize() {
		return Long.parseLong(file.getSize().trim());
	}

	@Override
	public boolean isFile() {
		return file.isFile();
	}

	@Override
	public boolean isDirectory() {
		return file.isDirectory();
	}

}
