import files.FileCommon
import files.FileFile
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import java.awt.Desktop
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.*

class ftpClientManager {
    private val gui: FtpClientGui
    private val statusLog: StringBuilder
    private var connector: RemoteConnector? = null
    private var localPath: String? = null
    private var selectedDirIndex = -1
    private var remoteSystemType: OSType? = null
    private var localSystemType: OSType? = null
    private var localFileCommonList: List<FileCommon>? = null
    private var remoteFileCommonList: List<FileCommon>? = null

    init {
        localOSType()
        gui = FtpClientGui(this)
        gui.setLocalPath(localPath)
        statusLog = StringBuilder()
        logEvent("local System: $localSystemType")
        changeLocalFilePath()
    }

    fun connectPressed() {
        val p = StringBuilder()
        for (c in gui.passwordField) {
            p.append(c)
        }
        val proto = gui.connectionType
        connectToServer(gui.hostField, gui.user, p.toString(), proto)
    }

    fun connectToServer(server: String, user: String?, pass: String?, proto: Int) {
        connector = when (proto) {
            0 -> FtpConnector(server, user, pass)
            1 -> SftpConnector(server, user, pass)
            2 -> JftpConnector(server, user, pass)
            else -> throw IllegalArgumentException("Unexpected value: $proto")
        }

        Thread {
            if (connector!!.connect()) {
                val remotePath = "/"
                gui.setRemotePath(remotePath)
                logEvent("Connected to $server")
                remoteSystemType = connector!!.systemType
                changeRemoteFilePath()
            } else {
                logEvent("Error Connecting to $server")
            }
        }.start()
    }

    fun connectSavedServer(server: Int) {
        val o = getSelectedItem(server)
        if (o != null) {
            connectToServer(o["server"].toString(), o["user"].toString(), o["pass"].toString(), 1)
        }
    }

    fun saveServer() {
        val server = gui.hostField
        val user = gui.user
        val p = StringBuilder()
        for (c in gui.passwordField) {
            p.append(c)
        }
        val jsonArray = getJsonArray(p, server, user)

        try {
            FileWriter("entries.json").use { file ->
                file.write(jsonArray.toJSONString())
                file.flush()
                println("Data has been written to " + "entries.json")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        gui.updateSavedServerList()
    }

    private fun getJsonArray(p: StringBuilder, server: String, user: String): JSONArray {
        val pass = p.toString()
        val proto = gui.connectionType

        val entry = JSONObject()
        entry["server"] = server
        entry["user"] = user
        entry["pass"] = pass
        entry["proto"] = proto

        var jsonArray: JSONArray
        try {
            val fileReader = FileReader("entries.json")
            val jsonParser = JSONParser()
            jsonArray = jsonParser.parse(fileReader) as JSONArray
            fileReader.close()
        } catch (e: IOException) {
            // If file doesn't exist or can't be parsed, create a new JSONArray
            jsonArray = JSONArray()
        } catch (e: ParseException) {
            jsonArray = JSONArray()
        }
        jsonArray.add(entry)
        return jsonArray
    }

    val allHostnames: Array<String>
        get() {
            val parser = JSONParser()
            val hostnames = ArrayList<String>()

            val file = File("entries.json")
            if (file.exists()) {
                try {
                    FileReader("entries.json").use { reader ->
                        val obj = parser.parse(reader)
                        val jsonArray = obj as JSONArray
                        for (item in jsonArray) {
                            val jsonObject = item as JSONObject
                            val hostname = jsonObject["server"] as String
                            hostnames.add(hostname)
                        }
                    }
                } catch (e: Exception) {
                }
            }
            return hostnames.toTypedArray<String>()
        }


    fun remoteSystemType(): OSType? {
        return remoteSystemType
    }

    fun localSystemType(): OSType? {
        return localSystemType
    }

    private fun localOSType() {
        val OS = System.getProperty("os.name").lowercase(Locale.getDefault())
        println(OS)
        if (OS.contains("win")) {
            localSystemType = OSType.Windows
            localPath = "C:\\"
        } else if (OS.contains("nix") || OS.contains("nux")) {
            localSystemType = OSType.Linux
            localPath = "/"
        }
    }

    fun openRemoteFile() {
        if (connector != null) { // Ensure we don't attempt to use a null connector
            val rPath = getPath(DirectoryType.Remote) + gui.remoteFilenameField
            val lPath = System.getProperty("java.io.tmpdir") + File.separator + "tempf"

            Thread {
                if (connector!!.downloadFile(rPath, lPath)) {
                    logEvent(gui.remoteFilenameField + " was downloaded successfully")
                    openFile(System.getProperty("java.io.tmpdir") + File.separator + "tempf")
                } else {
                    logEvent("Error Downloading File")
                }
            }.start()
        } else {
            logEvent("Not connected to a server")
        }
    }

    fun openFilePressed() {
        openFile(getPath(DirectoryType.Local) + gui.localFileNameField)
    }

    private fun openFile(filename: String) {
        val file = File(filename)
        if (file.exists()){
            val fileViewer = fileViewer()
            fileViewer.addWindowListener(object : WindowAdapter() {
                override fun windowClosed(e: WindowEvent) {
                    changeLocalFilePath()
                    println("A has closed")
                }
            })
            Thread { fileViewer.openFile(file) }.start()
        }
    }

    fun openFileDefaultPressed(type: DirectoryType) {
        val desktop = Desktop.getDesktop()
        try {
            desktop.edit(getFile(getPathField(type), gui.localFileNameField, type))
        } catch (e: IOException) {
            println(e)
            logEvent("Error Opening File")
        }
    }

    fun uploadPressed() {
        if (connector != null) { // Ensure we don't attempt to use a null connector
            val localFile = gui.localFileNameField
            val remotePath = getPath(DirectoryType.Remote)
            val localPath = getPath(DirectoryType.Local)
            val remoteFile = gui.remoteFilenameField

            if (connector!!.uploadFile(localPath + localFile, remoteFile, remotePath)) {
                logEvent("$localFile was uploaded sucessfully!")
                changeRemoteFilePath()
            } else {
                logEvent("Error Uploading File")
            }
        }
    }

    fun downloadPressed() {
        if (connector != null) { // Ensure we don't attempt to use a null connector

            val lPath = getPath(DirectoryType.Local)
            val rPath = getPath(DirectoryType.Remote)

            Thread {
                if (connector!!.downloadFile(rPath + gui.remoteFilenameField, lPath + gui.localFileNameField)) {
                    logEvent(gui.remoteFilenameField + " was downloaded sucessfully")
                    changeLocalFilePath()
                } else {
                    logEvent("Error Downloading File")
                }
            }.start()
        } else {
            logEvent("Not connected to FTP server")
        }
    }

    fun addEndSlashIfNotPresent(path: String, t: OSType?): String {
        var p = path
        val s = if (t == OSType.Windows) '\\' else '/'
        if (p.isNotEmpty() && p[p.length - 1] != s) {
            p += s
        }
        return p
    }

    fun aboutPressed() {
        aboutScreen().isVisible = true
    }

    fun makeLocalDirectoryPressed() {
        DirectoryNameEntryGui(this).launchAndGetName(DirectoryType.Local)
    }

    fun makeRemoteDirectoryPressed() {
        if (connector != null) {
            DirectoryNameEntryGui(this).launchAndGetName(DirectoryType.Remote)
        } else {
            logEvent("No Remote Host is Connected!")
        }
    }

    fun makeDirectory(type: DirectoryType, name: String) {
        if (type == DirectoryType.Local) {
            if (File(getPath(DirectoryType.Local) + name).mkdir()) {
                logEvent("Directory Created")
                changeLocalFilePath()
            } else {
                logEvent("Error Creating Directory")
            }
        }
        if (type == DirectoryType.Remote) {
            if (connector!!.makeDirectory(getPath(DirectoryType.Remote), name)) {
                logEvent("Directory Created")
                changeRemoteFilePath()
            } else {
                logEvent("Error Creating Directory!")
            }
        }
    }

    fun changeLocalFilePath() {
        val path = getPath(DirectoryType.Local)
        localFileCommonList = getCommonFileList(path)
        gui.displayLocalFiles(localFileCommonList)
    }

    fun changeRemoteFilePath() {
        if (connector != null) {
            try {
                remoteFileCommonList = connector!!.getCommonFileList(getPath(DirectoryType.Remote))
                gui.displayRemoteFiles(remoteFileCommonList)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun disconnectRemoteHost() {
        if (connector != null && connector!!.disconnect()) {
            logEvent("Remote host Disconnected")
            gui.remoteDisconnect()
        } else {
            logEvent("Error Disconnecting Remote Host")
        }
    }

    // This method is responsible for navigation through the file displays
    // Index is the line in the display
    fun mouseClickOnItem(index: Int, type: DirectoryType) {
        val fileList = if (type == DirectoryType.Local) localFileCommonList else remoteFileCommonList
        if (index >= fileList!!.size || index < 0) {
            return
        }
        if (fileList[index].isFile) {
            setFileNameField(fileList[index].fileName, type)
        } else if (fileList[index].isDirectory) {
            if (index == selectedDirIndex) {
                setFilePathField(getPath(type) + fileList[index].fileName, type)
                changePath(type)
            } else {
                selectedDirIndex = index
            }
        }
    }

    fun parentPressed(type: DirectoryType, t: OSType?) {
        if (t == null) return
        setFilePathField(ParentDirectory(getPathField(type), t), type)
        changePath(type)
    }

    private fun Slash(t: OSType): String {
        return if (t == OSType.Windows) "\\" else "/"
    }

    private fun changePath(t: DirectoryType) {
        selectedDirIndex = -1
        if (t == DirectoryType.Local) {
            changeLocalFilePath()
        } else {
            changeRemoteFilePath()
        }
    }

    private fun getPathField(t: DirectoryType): String {
        return if (t == DirectoryType.Local) gui.localPathField else gui.remotePathField
    }

    private fun getPath(t: DirectoryType): String {
        return if (t == DirectoryType.Local) {
            addEndSlashIfNotPresent(gui.localPathField, localSystemType)
        } else {
            addEndSlashIfNotPresent(gui.remotePathField, remoteSystemType)
        }
    }

    private fun setFilePathField(path: String, t: DirectoryType) {
        if (t == DirectoryType.Local) {
            gui.setLocalPath(path)
        } else {
            gui.setRemotePath(path)
        }
    }

    private fun setFileNameField(name: String, t: DirectoryType) {
        if (t == DirectoryType.Local) {
            gui.localFileNameField = name
        } else {
            gui.setRemoteFileNameField(name)
        }
    }

    private fun ParentDirectory(dir: String, t: OSType): String {
        var parent = dir
        val offset = dir.lastIndexOf(Slash(t))
        if (t == OSType.Windows) {
            if (offset != -1) {
                parent = dir.substring(0, offset)
            }
        } else {
            println("offset: $offset")
            if (offset > 0) {
                parent = dir.substring(0, offset)
            }
            if (offset == 0) {
                parent = dir.substring(0, 1)
            }
        }
        return parent
    }

    private fun logEvent(log: String) {
        logWriter.log(log)
        statusLog.append(log).append("\n")
        gui.setStatusField(statusLog.toString())
    }

    private fun getFile(path: String, name: String, type: DirectoryType): File? {
        var path = path
        path = getPath(type)
        return if (type == DirectoryType.Local) File(path + name) else null
    }

    enum class OSType {
        Windows, Linux
    }

    companion object {
        fun getSelectedItem(index: Int): JSONObject? {
            val parser = JSONParser()

            try {
                FileReader("entries.json").use { reader ->
                    val obj = parser.parse(reader)
                    val jsonArray = obj as JSONArray
                    if (index >= 0 && index < jsonArray.size) {
                        return jsonArray[index] as JSONObject?
                    } else {
                        System.err.println("Index out of bounds or file is empty.")
                        return null
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } catch (e: ParseException) {
                e.printStackTrace()
                return null
            }
        }

        fun getCommonFileList(path: String?): List<FileCommon> {
            val fileList: MutableList<FileCommon> = ArrayList()

            val list = File(path).listFiles()
            if (list != null) {
                for (f in list) {
                    if (f.isDirectory) {
                        val filec: FileCommon = FileFile(f)
                        fileList.add(filec)
                    }
                }
                for (f in list) {
                    if (f.isFile) {
                        val filec: FileCommon = FileFile(f)
                        fileList.add(filec)
                    }
                }
            }
            return fileList
        }
    }
}
