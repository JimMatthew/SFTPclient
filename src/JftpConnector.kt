import files.FileCommon
import jftp.JFTP
import java.io.IOException

class JftpConnector internal constructor(private val host: String, private val user: String, private val pass: String) :
    RemoteConnector {
    private var Ftp: JFTP? = null

    override fun connect(): Boolean {
        Ftp = JFTP()
        try {
            Ftp!!.connect(host, 21, user, pass)
            Ftp!!.cd("/")
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun uploadFile(localFileFullName: String, fileName: String, hostDir: String): Boolean {
        return Ftp!!.upload(localFileFullName, hostDir + fileName)
    }

    override fun getSystemType(): ftpClientManager.OSType {
        return ftpClientManager.OSType.Linux
    }

    override fun downloadFile(remoteFile: String, localFile: String): Boolean {
        return Ftp!!.download(localFile, remoteFile)
    }

    override fun makeDirectory(path: String, name: String): Boolean {
        try {
            Ftp!!.makeDirectory(path + name)
        } catch (e: IOException) {
            return false
        }
        return true
    }

    override fun isConnected(): Boolean {
        return Ftp!!.isConnected
    }

    override fun disconnect(): Boolean {
        Ftp!!.disconnect()
        return true
    }

    override fun getCommonFileList(path: String): List<FileCommon> {
        val fileList: MutableList<FileCommon> = ArrayList()
        var files: List<FileCommon> = ArrayList()
        try {
            files = Ftp!!.getJFTPFileList(path)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        for (file in files) {
            if (file.isDirectory) {
                fileList.add(file)
            }
        }
        for (file in files) {
            if (file.isFile) {
                fileList.add(file)
            }
        }
        return fileList
    }
}
