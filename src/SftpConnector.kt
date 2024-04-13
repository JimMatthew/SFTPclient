import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.ChannelSftp.LsEntry
import com.jcraft.jsch.JSch
import com.jcraft.jsch.SftpException
import files.FileCommon
import files.FileSFTPFile
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class SftpConnector(// Implements RemoteConnector as SFTP client
    private val host: String, private val user: String, private val pass: String
) : RemoteConnector {
    private val path = "/"
    private var channel: ChannelSftp? = null

    override fun connect(): Boolean {
        try {
            val jsch = JSch()
            val knownhosts = "%USERPROFILE%\\.ssh\\known_hosts"
            jsch.setKnownHosts(knownhosts)
            val session = jsch.getSession(user, host, REMOTE_PORT)
            session.setPassword(pass)
            val config = Properties()
            config["StrictHostKeyChecking"] = "no"
            session.setConfig(config)
            session.connect()
            val sftp = session.openChannel("sftp")
            sftp.connect()
            channel = sftp as ChannelSftp
            setupSystem()
            return true
        } catch (e: Exception) {
            println(e)
            return false
        }
    }

    private fun setupSystem() {
        val systemType = ftpClientManager.OSType.Linux
    }

    override fun uploadFile(localFileFullName: String, fileName: String, hostDir: String): Boolean {
        try {
            FileInputStream(localFileFullName).use { input ->
                channel!!.put(input, hostDir + fileName)
                return true
            }
        } catch (e: Exception) {
            return false
        }
    }

    override fun getSystemType(): ftpClientManager.OSType {
        return ftpClientManager.OSType.Linux
    }

    override fun downloadFile(remoteFile: String, localFile: String): Boolean {
        try {
            channel!![remoteFile, FileOutputStream(localFile)]
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override fun makeDirectory(path: String, name: String): Boolean {
        try {
            channel!!.mkdir(path + name)
            return true
        } catch (e: SftpException) {
            e.printStackTrace()
            return false
        }
    }

    override fun isConnected(): Boolean {
        return channel!!.isConnected
    }

    override fun disconnect(): Boolean {
        channel!!.disconnect()
        return true
    }

    @Throws(IOException::class)
    override fun getCommonFileList(path: String): List<FileCommon> {
        return try {
            val dirList = getDirList(path)
            val fileList = getFileList(path)
            dirList + fileList
        } catch (e: SftpException) {
            throw IOException(e)
        }
    }

    @Throws(SftpException::class)
    private fun getDirList(path: String): MutableList<FileCommon> {
        val entryList: MutableList<FileCommon> = ArrayList()
        val files: Vector<*> = channel!!.ls(path)
        for (file in files) {
            val entry = file as LsEntry
            if ("." == entry.filename || ".." == entry.filename) {
                continue
            }
            if (entry.attrs.isDir) {
                entryList.add(FileSFTPFile(entry))
            }
        }
        return entryList
    }

    @Throws(SftpException::class)
    private fun getFileList(path: String): List<FileCommon> {
        val entryList: MutableList<FileCommon> = ArrayList()
        val files: Vector<*> = channel!!.ls(path)
        for (file in files) {
            val entry = file as LsEntry
            if ("." == entry.filename || ".." == entry.filename) {
                continue
            }
            if (entry.attrs.isReg) {
                entryList.add(FileSFTPFile(entry))
            }
        }
        return entryList
    }

    companion object {
        private const val REMOTE_PORT = 22
        private const val SESSION_TIMEOUT = 10000
        private const val CHANNEL_TIMEOUT = 5000
    }
}
