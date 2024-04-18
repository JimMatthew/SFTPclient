package files;

import java.io.File;

public class FileFile implements FileCommon {

	private final File file;
	
	public FileFile(File file) {
		this.file = file;
	}
	@Override
	public String getFileName() {
		return file.getName();
	}

	@Override
	public long getFileSize() {
		return file.length();
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
