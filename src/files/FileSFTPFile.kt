package files

import com.jcraft.jsch.ChannelSftp.LsEntry

class FileSFTPFile(private val file: LsEntry) : FileCommon {
    override fun getFileName(): String {
        return file.filename
    }

    override fun getFileSize(): Long {
        return file.attrs.size
    }

    override fun isFile(): Boolean {
        return file.attrs.isReg
    }

    override fun isDirectory(): Boolean {
        return file.attrs.isDir
    }
}
