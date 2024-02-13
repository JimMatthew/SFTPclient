package files;

import org.apache.commons.net.ftp.FTPFile;

public class FileFTPFile implements FileCommon {
	
	private final FTPFile file;
	
	public FileFTPFile(FTPFile file) {
		this.file = file;
	}

	@Override
	public String getFileName() {
		return file.getName();
	}

	@Override
	public long getFileSize() {
		return file.getSize();
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
