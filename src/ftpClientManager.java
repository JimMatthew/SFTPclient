import files.FileCommon;
import files.FileFile;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class ftpClientManager {

	private final FtpClientGui gui;
	private final StringBuilder statusLog;
	private RemoteConnector connector;
	private String localPath;
	private int selectedDirIndex = -1;
	private OSType remoteSystemType;
	private OSType localSystemType;
	private List<FileCommon> localFileCommonList = null;
	private List<FileCommon> remoteFileCommonList = null;

	public ftpClientManager() {

		localOSType();
		gui = new FtpClientGui(this);
		gui.setLocalPath(localPath);
		statusLog = new StringBuilder();
		logEvent("local System: " + localSystemType);
		changeLocalFilePath();
	}

	public void connectPressed() {
		StringBuilder p = new StringBuilder();
		for (char c : gui.getPasswordField()) {
			p.append(c);
			c = 0;
		}
		int proto = gui.getConnectionType();
		connectToServer(gui.getHostField(), gui.getUser(), p.toString(), proto);
	}

	public void connectToServer(String server, String user, String pass, int proto){
		connector = switch (proto) {
			case 0 -> new FtpConnector(server, user, pass);
			case 1 -> new SftpConnector(server, user, pass);
			case 2 -> new JftpConnector(server, user, pass);
			default -> throw new IllegalArgumentException("Unexpected value: " + proto);
		};

		new Thread(() -> {
			if (connector.connect()) {
				String remotePath = "/";
				gui.setRemotePath(remotePath);
				logEvent("Connected to " + server);
				remoteSystemType = connector.getSystemType();
				changeRemoteFilePath();
			} else {
				logEvent("Error Connecting to " + server);
			}
		}).start();
	}

	public void connectSavedServer(int server) {
		JSONObject o = getSelectedItem(server);
		if (o != null){
			connectToServer(o.get("server").toString(), o.get("user").toString(), o.get("pass").toString(),1);
		}
	}

	public void saveServer() {
		String server = gui.getHostField();
		String user = gui.getUser();
		StringBuilder p = new StringBuilder();
		for (char c : gui.getPasswordField()) {
			p.append(c);
		}
		JSONArray jsonArray = getJsonArray(p, server, user);

		try (FileWriter file = new FileWriter("entries.json")) {
			file.write(jsonArray.toJSONString());
			file.flush();
			System.out.println("Data has been written to " + "entries.json");
		} catch (IOException e) {
			e.printStackTrace();
		}
		gui.updateSavedServerList();
    }

	private JSONArray getJsonArray(StringBuilder p, String server, String user) {
		String pass = p.toString();
		int proto = gui.getConnectionType();

		JSONObject entry = new JSONObject();
		entry.put("server", server);
		entry.put("user", user);
		entry.put("pass", pass);
		entry.put("proto", proto);

		JSONArray jsonArray;
		try {
			FileReader fileReader = new FileReader("entries.json");
			JSONParser jsonParser = new JSONParser();
			jsonArray = (JSONArray) jsonParser.parse(fileReader);
			fileReader.close();
		} catch (IOException | ParseException e) {
			// If file doesn't exist or can't be parsed, create a new JSONArray
			jsonArray = new JSONArray();
		}
		jsonArray.add(entry);
		return jsonArray;
	}

	public String[] getAllHostnames() {
		JSONParser parser = new JSONParser();
		ArrayList<String> hostnames = new ArrayList<>();

		File file = new File("entries.json");
		if (file.exists()){
			try (FileReader reader = new FileReader("entries.json")) {
				Object obj = parser.parse(reader);
				JSONArray jsonArray = (JSONArray) obj;

				for (Object item : jsonArray) {
					JSONObject jsonObject = (JSONObject) item;
					String hostname = (String) jsonObject.get("server");
					hostnames.add(hostname);
				}
			} catch (Exception e) {
			}
		}
		return hostnames.toArray(new String[0]);
	}


	public static JSONObject getSelectedItem(int index) {
		JSONParser parser = new JSONParser();

		try (FileReader reader = new FileReader("entries.json")) {
			Object obj = parser.parse(reader);
			JSONArray jsonArray = (JSONArray) obj;
			if (index >= 0 && index < jsonArray.size()) {
				return (JSONObject) jsonArray.get(index);
			} else {
				System.err.println("Index out of bounds or file is empty.");
				return null;
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public OSType remoteSystemType() {
		return remoteSystemType;
	}

	public OSType localSystemType() {
		return localSystemType;
	}

	private void localOSType() {
		String OS = System.getProperty("os.name").toLowerCase();
		System.out.println(OS);
		if (OS.contains("win")) {
			localSystemType = OSType.Windows;
			localPath = "C:\\";
		} else if (OS.contains("nix") || OS.contains("nux")) {
			localSystemType = OSType.Linux;
			localPath = "/";
		}
	}

	public void openRemoteFile() {

		if (connector != null) { // Ensure we don't attempt to use a null connector
			String rPath = getPath(DirectoryType.Remote) + gui.getRemoteFilenameField();
			String lPath = System.getProperty("java.io.tmpdir") + File.separator + "tempf";

			new Thread(() -> {
				if (connector.downloadFile(rPath, lPath)) {
					logEvent(gui.getRemoteFilenameField() + " was downloaded successfully");
					fileViewer fileViewer = new fileViewer();
					fileViewer.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosed(WindowEvent e) {
							changeLocalFilePath();
							System.out.println("A has closed");
						}
					});
					File file = new File(System.getProperty("java.io.tmpdir") + File.separator + "tempf");
					fileViewer.openFile(file);
				} else {
					logEvent("Error Downloading File");
				}
			}).start();
		} else {
			logEvent("Not connected to a server");
		}
	}

	public void openFilePressed(DirectoryType type) {
		fileViewer fileViewer = new fileViewer();
		fileViewer.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				changeLocalFilePath();
				System.out.println("A has closed");
			}
		});
		if(type == DirectoryType.Local){
			File file = new File(getPath(DirectoryType.Local)+gui.getLocalFileNameField());
			new Thread(() -> fileViewer.openFile(file)).start();
		}
    }

	public void openFileDefaultPressed(DirectoryType type) {
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.edit(getFile(getPathField(type), gui.getLocalFileNameField(), type));
		} catch (IOException e) {
			System.out.println(e);
			logEvent("Error Opening File");
		}
	}

	public void uploadPressed() {
		if (connector != null) { // Ensure we don't attempt to use a null connector
			String localFile = gui.getLocalFileNameField();
			String remotePath = getPath(DirectoryType.Remote);
			String localPath = getPath(DirectoryType.Local);
			String remoteFile = gui.getRemoteFilenameField();

			if (connector.uploadFile(localPath + localFile, remoteFile, remotePath)) {
				logEvent(localFile + " was uploaded sucessfully!");
				changeRemoteFilePath();
			} else {
				logEvent("Error Uploading File");
			}
		}
	}

	public void downloadPressed() {
		if (connector != null) { // Ensure we don't attempt to use a null connector

			String lPath = getPath(DirectoryType.Local);
			String rPath = getPath(DirectoryType.Remote);

			new Thread(() -> {
				if (connector.downloadFile(rPath + gui.getRemoteFilenameField(), lPath + gui.getLocalFileNameField())) {
					logEvent(gui.getRemoteFilenameField() + " was downloaded sucessfully");
					changeLocalFilePath();
				} else {
					logEvent("Error Downloading File");
				}
			}).start();
		} else {
			logEvent("Not connected to FTP server");
		}
	}

	public String addEndSlashIfNotPresent(String path, OSType t) {

		char s = t == OSType.Windows ? '\\' : '/';
		if (!path.isEmpty() && path.charAt(path.length() - 1) != s) {
			path += s;
		}
		return path;
	}

	public void aboutPressed() {
		new aboutScreen().setVisible(true);
	}

	public void makeLocalDirectoryPressed() {
		new DirectoryNameEntryGui(this).launchAndGetName(DirectoryType.Local);
	}

	public void makeRemoteDirectoryPressed() {
		if (connector != null) {
			new DirectoryNameEntryGui(this).launchAndGetName(DirectoryType.Remote);
		} else {
			logEvent("No Remote Host is Connected!");
		}
	}

	public void makeDirectory(DirectoryType type, String name) {
		if (type.equals(DirectoryType.Local)) {
			if (new File(getPath(DirectoryType.Local) + name).mkdir()) {
				logEvent("Directory Created");
				changeLocalFilePath();
			} else {
				logEvent("Error Creating Directory");
			}
		}
		if (type.equals(DirectoryType.Remote)) {
			if (connector.makeDirectory(getPath(DirectoryType.Remote), name)) {
				logEvent("Directory Created");
				changeRemoteFilePath();
			} else {
				logEvent("Error Creating Directory!");
			}
		}
	}

	public void changeLocalFilePath() {
		String path = getPath(DirectoryType.Local);
		localFileCommonList = getCommonFileList(path);
		gui.displayLocalFiles(localFileCommonList);
	}

	public void changeRemoteFilePath() {
		if (connector != null) {
			try {
				remoteFileCommonList = connector.getCommonFileList(getPath(DirectoryType.Remote));
				gui.displayRemoteFiles(remoteFileCommonList);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void disconnectRemoteHost() {
		if (connector != null && connector.disconnect()) {
			logEvent("Remote host Disconnected");
			gui.remoteDisconnect();
		} else {
			logEvent("Error Disconnecting Remote Host");
		}
	}

	// This method is responsible for navigation through the file displays
	// Index is the line in the display
	public void mouseClickOnItem(int index, DirectoryType type) {

		List<FileCommon> fileList = type == DirectoryType.Local ? localFileCommonList : remoteFileCommonList;
		if (index >= fileList.size() || index < 0) {
			return;
		}
		if (fileList.get(index).isFile()) { 
			setFileNameField(fileList.get(index).getFileName(), type);
		} else if (fileList.get(index).isDirectory()) { 
			if (index == selectedDirIndex) { 
				setFilePathField(getPath(type) + fileList.get(index).getFileName(), type);
				changePath(type);
			} else {
				selectedDirIndex = index;
			}
		}
	}

	public void parentPressed(DirectoryType type, OSType t) {
		if (t == null)
			return;
		setFilePathField(ParentDirectory(getPathField(type), t), type);
		changePath(type);
	}

	private String Slash(OSType t) {
		return t == OSType.Windows ? "\\" : "/";
	}

	private void changePath(DirectoryType t) {
		selectedDirIndex = -1;
		if (t == DirectoryType.Local) {
			changeLocalFilePath();
		} else {
			changeRemoteFilePath();
		}
	}

	private String getPathField(DirectoryType t) {
		return t == DirectoryType.Local ? gui.getLocalPathField() : gui.getRemotePathField();
	}

	private String getPath(DirectoryType t) {

		if (t == DirectoryType.Local) {
			return addEndSlashIfNotPresent(gui.getLocalPathField(), localSystemType);
		} else {
			return addEndSlashIfNotPresent(gui.getRemotePathField(), remoteSystemType);
		}
	}

	private void setFilePathField(String path, DirectoryType t) {
		if (t == DirectoryType.Local) {
			gui.setLocalPath(path);
		} else {
			gui.setRemotePath(path);
		}
	}

	private void setFileNameField(String name, DirectoryType t) {
		if (t == DirectoryType.Local) {
			gui.setLocalFileNameField(name);
		} else {
			gui.setRemoteFileNameField(name);
		}
	}

	private String ParentDirectory(String dir, OSType t) {
		String parent = dir;
		int offset = dir.lastIndexOf(Slash(t));
		if (t.equals(OSType.Windows)) {
			if (offset != -1) {
				parent = dir.substring(0, offset);
			}
		} else {
			System.out.println("offset: "+offset);
			if (offset > 0) {
				parent = dir.substring(0, offset);
			} if (offset == 0) {
				parent = dir.substring(0, 1);
			}
		}
		return parent;
	}

	private void logEvent(String log) {
		logWriter.log(log);
		statusLog.append(log).append("\n");
		gui.setStatusField(statusLog.toString());
	}

	private File getFile(String path, String name, DirectoryType type) {
		path = getPath(type);
		return type == DirectoryType.Local ? new File(path+name): null;
	}
	
	public static List<FileCommon> getCommonFileList(String path){
    	List<FileCommon> fileList = new ArrayList<>();
    	
    	File[] list =new File(path).listFiles();
    	if (list != null) {
    		for (File f : list) {
        		if (f.isDirectory()) {
        			FileCommon filec = new FileFile(f);
        			fileList.add(filec);
        		}
        	}
        	for (File f : list) {
        		if (f.isFile()) {
        			FileCommon filec = new FileFile(f);
        			fileList.add(filec);
        		}
        	}
    	}
    	return fileList;
    }

	public enum OSType {
		Windows, Linux
	}
}
