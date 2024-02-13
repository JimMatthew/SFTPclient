import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;

import files.FileCommon;
import files.FileSFTPFile;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SftpConnector implements RemoteConnector {

	// Implements RemoteConnector as SFTP client
	private final String host;
	private final String user;
	private final String pass;
	private final String path = "/";
	private static final int REMOTE_PORT = 22;
	private static final int SESSION_TIMEOUT = 10000;
	private static final int CHANNEL_TIMEOUT = 5000;
	private ChannelSftp channel;

	public SftpConnector(String host, String user, String pass) {
		this.host = host;
		this.user = user;
		this.pass = pass;
	}

	@Override
	public boolean connect() {
		try {
			JSch jsch = new JSch();
			String knownhosts = "%USERPROFILE%\\.ssh\\known_hosts";
			jsch.setKnownHosts(knownhosts);
			Session session = jsch.getSession(user, host, REMOTE_PORT);
			session.setPassword(pass);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			Channel sftp = session.openChannel("sftp");
			sftp.connect();
			channel = (ChannelSftp) sftp;
			setupSystem();
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

	private void setupSystem() {
		ftpClientManager.OSType systemType = ftpClientManager.OSType.Linux;
	}

	@Override
	public boolean uploadFile(String localFileFullName, String fileName, String hostDir) {
		try (InputStream input = new FileInputStream(localFileFullName)) {
			channel.put(input, hostDir + fileName);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public ftpClientManager.OSType getSystemType() {
		return ftpClientManager.OSType.Linux;
	}

	@Override
	public boolean downloadFile(String remoteFile, String localFile) {
		try {
			channel.get(remoteFile, new FileOutputStream(localFile));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean makeDirectory(String path, String name) {
		try {
			channel.mkdir(path + name);
			return true;
		} catch (SftpException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean isConnected() {
		return channel.isConnected();
	}

	@Override
	public boolean disconnect() {
		channel.disconnect();
		return true;
	}
	
	@Override
	public List<FileCommon> getCommonFileList(String path) throws IOException {
		List<FileCommon> fileList = new ArrayList<>();
		try {
			fileList = getDirList(path);
			fileList.addAll(getFileList(path));
		} catch (SftpException e) {
			throw new IOException(e);
		}
		return fileList;
	}

	private List<FileCommon> getDirList(String path) throws SftpException {

		List<FileCommon> entryList = new ArrayList<>();
		Vector<?> files = channel.ls(path);
		for (Object file : files) {
			LsEntry entry = (LsEntry) file;
			if (".".equals(entry.getFilename()) || "..".equals(entry.getFilename())) {
				continue;
			}
			if (entry.getAttrs().isDir()) {
				entryList.add(new FileSFTPFile(entry));
			}
		}
		return entryList;
	}

	private List<FileCommon> getFileList(String path) throws SftpException {

		List<FileCommon> entryList = new ArrayList<>();
		Vector<?> files = channel.ls(path);
		for (Object file : files) {
			LsEntry entry = (LsEntry) file;
			if (".".equals(entry.getFilename()) || "..".equals(entry.getFilename())) {
				continue;
			}
			if (entry.getAttrs().isReg()) {
				entryList.add(new FileSFTPFile(entry));
			}
		}
		return entryList;
	}
}
