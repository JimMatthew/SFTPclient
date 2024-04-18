import jftp.JFTP;
import files.FileCommon;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JftpConnector implements RemoteConnector {

	private final String host;
	private final String user;
	private final String pass;
	private JFTP Ftp;

	JftpConnector(String host, String user, String pass) {
		this.host = host;
		this.user = user;
		this.pass = pass;
	}

	@Override
	public boolean connect() {
		Ftp = new JFTP();
		try {
			Ftp.connect(host, 21, user, pass);
			Ftp.cd("/");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean uploadFile(String localFileFullName, String fileName, String hostDir) {
		return Ftp.upload(localFileFullName, hostDir + fileName);
	}

	@Override
	public ftpClientManager.OSType getSystemType() {
		return ftpClientManager.OSType.Linux;
	}

	@Override
	public boolean downloadFile(String remoteFile, String localFile) {
		return Ftp.download(localFile, remoteFile);
	}

	@Override
	public boolean makeDirectory(String path, String name) {
		try {
			Ftp.makeDirectory(path + name);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isConnected() {
		return Ftp.isConnected();
	}

	@Override
	public boolean disconnect() {
		Ftp.disconnect();
		return true;
	}

	@Override
	public List<FileCommon> getCommonFileList(String path) {

		List<FileCommon> fileList = new ArrayList<>();
		List<FileCommon> files = new ArrayList<>();
		try {
			files = Ftp.getJFTPFileList(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (FileCommon file : files) {
			if (file.isDirectory()) {
				fileList.add(file);
			}
		}
		for (FileCommon file : files) {
			if (file.isFile()) {
				fileList.add(file);
			}
		}
		return fileList;
	}
}
