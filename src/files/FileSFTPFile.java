package files;

import com.jcraft.jsch.ChannelSftp.LsEntry;

public class FileSFTPFile implements FileCommon {
	
	private final LsEntry file;
	
	public FileSFTPFile(LsEntry file) {
		this.file = file;
	}

	@Override
	public String getFileName() {
		return file.getFilename();
	}

	@Override
	public long getFileSize() {
		return file.getAttrs().getSize();
	}

	@Override
	public boolean isFile() {
		return file.getAttrs().isReg();
	}

	@Override
	public boolean isDirectory() {
		return file.getAttrs().isDir();
	}

}
