package files

import jftp.JFTP.JFTPFile

class FileJFTPFile(private val file: JFTPFile) : FileCommon {
    override fun getFileName(): String {
        return file.name
    }

    override fun getFileSize(): Long {
        return file.size.trim { it <= ' ' }.toLong()
    }

    override fun isFile(): Boolean {
        return file.isFile
    }

    override fun isDirectory(): Boolean {
        return file.isDirectory
    }
}
