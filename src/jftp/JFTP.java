package jftp;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import files.FileCommon;

/*
 * A very basic FTP client.
 * Supports password authentication
 * Supports listing files, make / delete directory, upload / download
 */
public class JFTP {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String currpath;
    private String host;
    private int port;
    private String response;
    private boolean connected;

    public JFTP() {

    }

    public boolean connect(String host, int port, String user, String pass) throws IOException {
        this.host = host;
        this.port = port;

        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String response = reader.readLine();
        System.out.println(response);
        if (!response.startsWith("220 ")) {
            throw new IOException("shit's fucked " + response);
        }
        send("USER " + user);
        response = reader.readLine();
        System.out.println(response);
        if (!response.startsWith("331 ")) {
            throw new IOException(response);
        }
        send("PASS " + pass);
        response = reader.readLine();
        System.out.println(response);
        if (!response.startsWith("230 ")) {
            throw new IOException(" " + response);
        }
        send("STRU F");
        String response2 = reader.readLine();
        connected = true;
        return true;
    }

    //This method will send an FTP command to the server
    //The response must be handled separately
    private void send(String line) throws IOException {
        if (socket == null) {
            throw new IOException("FTP is not connected.");
        }
        new Thread(() -> {
            try {
                writer.write(line + "\r\n");
                writer.flush();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }).start();
        System.out.println("> " + line);
    }

    private String read() {
        String s = null;
        try {
            s = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(s);
        return s;
    }

    public String getReponse() {
    	return response;
    }
    //Sends a disconnect command to the server to end the session
    //and closes the socket
    public void disconnect() {
        try {
            send("QUIT");
            socket.close();
            connected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized String pwd() throws IOException {
        send("PWD");
        String dir = null;
        String response = reader.readLine();
        if (response.startsWith("257 ")) {
            int firstQuote = response.indexOf('\"');
            int secondQuote = response.indexOf('\"', firstQuote + 1);
            if (secondQuote > 0) {
                dir = response.substring(firstQuote + 1, secondQuote);
            }
        }
        return dir;
    }

    //change the current working directory
    public boolean cd(String dir) throws IOException {

        send("CWD " + dir);
        String response = reader.readLine();
        if (response.startsWith("250 ")) {
            currpath = dir;
            return true;
        }
        return false;
    }

    //This method will send the server the PASV code, to put the server into
    //passive mode. The server will reply with the ports it will be listening on for the
    //data connection. We the setup a new Socket pointing to that port
    private Socket passiveMode() throws IOException {
        send("PASV");
        String response = reader.readLine();
        System.out.println("PM: " + response);
        // The response will be in the form:
        // "227 Entering Passive Mode (192,168,1,1,250,124)".
        // where the port is (250 << 8) + 124
        if (!response.startsWith("227 ")) {
            throw new IOException(response);
        }

        int a = response.lastIndexOf(',');
        int b = Integer.parseInt(response.substring(a + 1, response.lastIndexOf(')')));
        int c = response.lastIndexOf(',', a - 1);
        int d = Integer.parseInt(response.substring(c + 1, a));

        int port = (d << 8) + b;

        return new Socket(host, port);
    }

    private BufferedReader makeBuffReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private BufferedWriter makeBuffWriter(Socket socket) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    //Upload a file to the ftp server
    //localfile is path+file to upload
    //remotefile is path+file to save file to
    public boolean upload(String localfile, String remotefile) {
        try {
            Socket socket = passiveMode();
            File file = new File(localfile);
            send("STOR " + remotefile);
            BufferedWriter writer = makeBuffWriter(socket);
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                writer.write(sc.nextLine() + "\n");
            }
            writer.close();
            socket.close();
            sc.close();
            System.out.println(reader.readLine());
            System.out.println(reader.readLine());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Download a file from the ftp server
    //localfile is the path+filename to save the file locally
    //remoteFile is path+file to download
    //returns true on success
    public boolean download(String localfile, String remoteFile) {
        try {
            Socket socket = passiveMode();
            BufferedWriter writer = new BufferedWriter(new FileWriter(localfile));
            send("RETR " + remoteFile);
            BufferedReader breader = makeBuffReader(socket);
            String data = breader.readLine();
            while (data != null) {
                writer.write(data + "\n");
                data = breader.readLine();
            }
            writer.close();
            System.out.println(reader.readLine());
            System.out.println(reader.readLine());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Returns a string of the LIST response, which
    //contains all files in directory 'path'
    //the string returned is not parsed, just returned as received
    public String ls(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        Socket socket = passiveMode();
        send("LIST " + path);
        System.out.println(reader.readLine());
        BufferedReader dreader = makeBuffReader(socket);
        String data = dreader.readLine();
        while (data != null) {
            sb.append(data).append("\n");
            data = dreader.readLine();
        }
        System.out.println(reader.readLine());
        return sb.toString();
    }

    //Returns a string of the LIST response, which
    //contains all files in current directory
    //the string returned is not parsed, just returned as received
    public String ls() throws IOException {
        return ls(currpath);
    }

    public List<String> getFileList(String path) throws IOException {
        List<String> fileList = new ArrayList<>();
        String slist = ls(path);
        Scanner scanner = new Scanner(slist);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.charAt(0) == '-') {
                fileList.add(line);
            } else {
            }
        }
        scanner.close();
        return fileList;
    }

    public List<String> getFileList() throws IOException {
        List<String> fileList = new ArrayList<>();
        String slist = ls();
        Scanner scanner = new Scanner(slist);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.charAt(0) == '-') {
                fileList.add(line);
            } else {
            }
        }
        scanner.close();
        return fileList;
    }
    
    public List<FileCommon> getJFTPFileList(String path) throws IOException {
        //56 name  size end 42
        System.out.println("jftp path: " + path);
        List<FileCommon> ftpFileList = new ArrayList<>();
        Scanner scanner = new Scanner(ls(path));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            int type;
            if (line.charAt(0) == '-') {
                type = 1;
            } else if (line.charAt(0) == 'd') {
                type = 0;
            } else {
                continue;
            }
            String name = line.substring(56);
            String size = line.substring(36, 42);
            FileCommon jftp = new JFTPFile(name, path, size, type);
            ftpFileList.add(jftp);
        }
        scanner.close();
        return ftpFileList;
    }

    //make a new directory
    //Pass path+name of directory to be created
    public boolean makeDirectory(String name) throws IOException {
        send("MKD " + name);
        String response = read();
        return response.startsWith("257 ");
    }

    public boolean deleteDirectory(String name) throws IOException {
        send("RMD " + name);
        String response = read();
        return response.startsWith("2");
    }

    public boolean isConnected() {
        return connected;
    }

    //JFTPFile is a simple structure to hold FTP file information
    //It can hold directories, type 0, and files, type 1.

    public static class JFTPFile implements FileCommon{

        private final String name;
        private final String path;
        private final String size;
        private final int type;

        JFTPFile(String name, String path, String size, int type) {
            this.name = name;
            this.path = path;
            this.size = size;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        public String getSize() {
            return size;
        }

        public boolean isFile() {
            return type == 1;
        }

        public boolean isDirectory() {
            return type == 0;
        }

		@Override
		public String getFileName() {
			return name;
		}

		@Override
		public long getFileSize() {
			return Long.valueOf(size.trim());
		}
    }
}

