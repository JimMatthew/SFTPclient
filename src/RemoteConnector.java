import java.io.IOException;
import java.util.List;

import javax.swing.JTable;

import com.jcraft.jsch.SftpException;

import files.FileCommon;

public interface RemoteConnector {

	//This must be called first
	boolean connect();

	//Upload given file, returns true on success
	boolean uploadFile(String localFileFullName, String fileName, String hostDir);

	//This will return the OStype of the remote system
	ftpClientManager.OSType getSystemType();

	//download file, returns true on success
	boolean downloadFile(String remoteFile, String localFile);

	//Make a new directory at given path, returns true on success
	boolean makeDirectory(String path, String name);

	//Returns true if connected
	boolean isConnected();

	//disconnect current session. 
	boolean disconnect();
	
	//returns of List of FileCommon ojects representing each file at path
	List<FileCommon> getCommonFileList(String path) throws IOException;

}