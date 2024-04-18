package files

import org.apache.commons.net.ftp.FTPFile

class FileFTPFile(private val file: FTPFile) : FileCommon {
    override fun getFileName(): String {
        return file.name
    }

    override fun getFileSize(): Long {
        return file.size
    }

    override fun isFile(): Boolean {
        return file.isFile
    }

    override fun isDirectory(): Boolean {
        return file.isDirectory
    }
}
