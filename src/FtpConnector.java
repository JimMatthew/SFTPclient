import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import files.FileCommon;
import files.FileFTPFile;

public class FtpConnector implements RemoteConnector{

    // Implements RemoteConnector as FTP client
    private final String host;
    private final String user;
    private final String pass;
    private final String path = "/";
    private FTPClient client;
    private ftpClientManager.OSType systemType;
    private SystemType type;

    public FtpConnector(String host, String user, String pass) {
        this.host = host;
        this.user = user;
        this.pass = pass;
    }

    public boolean connect() {
        try {
            client = new FTPClient();
            client.connect(host);
            client.login(user, pass);
            setupSystem();
            return true;
        } catch (Exception e) {
        	logWriter.log(e.toString());
            return false;
        }
    }

    private void setupSystem() {
        String type;
        try {
            type = client.getSystemType().toLowerCase();
            System.out.print(client.getSystemType().toLowerCase());
        } catch (IOException e) {
        	logWriter.log(e.toString());
            return;
        }

        if (type.contains("windows")) {
            systemType = ftpClientManager.OSType.Windows;
        }
        if (type.contains("unix")) {
            systemType = ftpClientManager.OSType.Linux;
        }
    }

    public boolean uploadFile(String localFileFullName, String fileName, String hostDir) {
        try (InputStream input = new FileInputStream(localFileFullName)) {
            return this.client.storeFile(hostDir + fileName, input);
        } catch (Exception e) {
        	logWriter.log(e.toString());
            return false;
        }
    }

    public ftpClientManager.OSType getSystemType() {
        if (systemType == null) {
            String type = null;
            try {
                type = client.getSystemType().toLowerCase();
                System.out.print(client.getSystemType().toLowerCase());
            } catch (IOException e) {
            	logWriter.log(e.toString());
            }
            systemType = type.contains("windows") ? ftpClientManager.OSType.Windows : ftpClientManager.OSType.Linux;
        }
        return systemType;
    }

    public SystemType SystemType() {
        if (type == null) {
            String stype = null;
            try {
                stype = client.getSystemType().toLowerCase();
                System.out.print(client.getSystemType().toLowerCase());
            } catch (IOException e) {
            	logWriter.log(e.toString());
            }
            if (stype.contains("windows")) {
                type = SystemType.Windows;
            }
            if (stype.contains("unix")) {
                type = SystemType.Linux;
            }
        }
        return type;
    }

    public boolean downloadFile(String remoteFile, String localFile) {
        try {
            return this.client.retrieveFile(remoteFile, new FileOutputStream(localFile));
        } catch (Exception e) {
        	logWriter.log(e.toString());
            return false;
        }
    }

    public boolean makeDirectory(String path, String name) {
        try {
            return client.makeDirectory(path + name);
        } catch (IOException e) {
        	logWriter.log(e.toString());
            return false;
        }
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public boolean disconnect() {
        try {
            client.disconnect();
            return true;
        } catch (IOException e) {
        	logWriter.log(e.toString());
            return false;
        }
    }
    
    public List<FileCommon> getCommonFileList(String path) throws IOException{
		List<FileCommon> fileList = new ArrayList<>();
		List<FTPFile> list = Arrays.asList(client.listFiles(path));
		
		for (FTPFile file: list) {
			if (file.isDirectory()) {
				FileCommon filec = new FileFTPFile(file);
				fileList.add(filec);
    		}
		}
		for (FTPFile file : list) {
			if (file.isFile()) {
				FileCommon filec = new FileFTPFile(file);
				fileList.add(filec);
    		}
			
		}
		return fileList;
	}

    public enum SystemType {
        Windows, Linux
    }
}
