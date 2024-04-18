import files.FileCommon
import files.FileFTPFile
import org.apache.commons.net.ftp.FTPClient
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class FtpConnector(// Implements RemoteConnector as FTP client
    private val host: String, private val user: String, private val pass: String
) : RemoteConnector {
    private val path = "/"
    private var client: FTPClient? = null
    private var systemType: ftpClientManager.OSType? = null
    private var type: SystemType? = null

    override fun connect(): Boolean {
        try {
            client = FTPClient()
            client!!.connect(host)
            client!!.login(user, pass)
            setupSystem()
            return true
        } catch (e: Exception) {
            logWriter.log(e.toString())
            return false
        }
    }

    private fun setupSystem() {
        val type: String
        try {
            type = client!!.systemType.lowercase(Locale.getDefault())
            print(client!!.systemType.lowercase(Locale.getDefault()))
        } catch (e: IOException) {
            logWriter.log(e.toString())
            return
        }

        if (type.contains("windows")) {
            systemType = ftpClientManager.OSType.Windows
        }
        if (type.contains("unix")) {
            systemType = ftpClientManager.OSType.Linux
        }
    }

    override fun uploadFile(localFileFullName: String, fileName: String, hostDir: String): Boolean {
        try {
            FileInputStream(localFileFullName).use { input ->
                return client!!.storeFile(hostDir + fileName, input)
            }
        } catch (e: Exception) {
            logWriter.log(e.toString())
            return false
        }
    }

    override fun getSystemType(): ftpClientManager.OSType {
        if (systemType == null) {
            var type: String? = null
            try {
                type = client!!.systemType.lowercase(Locale.getDefault())
                print(client!!.systemType.lowercase(Locale.getDefault()))
            } catch (e: IOException) {
                logWriter.log(e.toString())
            }
            systemType =
                if (type!!.contains("windows")) ftpClientManager.OSType.Windows else ftpClientManager.OSType.Linux
        }
        return systemType as ftpClientManager.OSType
    }



    override fun downloadFile(remoteFile: String, localFile: String): Boolean {
        try {
            return client!!.retrieveFile(remoteFile, FileOutputStream(localFile))
        } catch (e: Exception) {
            logWriter.log(e.toString())
            return false
        }
    }

    override fun makeDirectory(path: String, name: String): Boolean {
        try {
            return client!!.makeDirectory(path + name)
        } catch (e: IOException) {
            logWriter.log(e.toString())
            return false
        }
    }

    override fun isConnected(): Boolean {
        return client != null && client!!.isConnected
    }

    override fun disconnect(): Boolean {
        try {
            client!!.disconnect()
            return true
        } catch (e: IOException) {
            logWriter.log(e.toString())
            return false
        }
    }

    @Throws(IOException::class)
    override fun getCommonFileList(path: String): List<FileCommon> {
        val fileList: MutableList<FileCommon> = ArrayList()
        val list = Arrays.asList(*client!!.listFiles(path))

        for (file in list) {
            if (file.isDirectory) {
                val filec: FileCommon = FileFTPFile(file)
                fileList.add(filec)
            }
        }
        for (file in list) {
            if (file.isFile) {
                val filec: FileCommon = FileFTPFile(file)
                fileList.add(filec)
            }
        }
        return fileList
    }

    enum class SystemType {
        Windows, Linux
    }
}
