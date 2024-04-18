package files

import java.io.File

class FileFile(private val file: File) : FileCommon {
    override fun getFileName(): String {
        return file.name
    }

    override fun getFileSize(): Long {
        return file.length()
    }

    override fun isFile(): Boolean {
        return file.isFile
    }

    override fun isDirectory(): Boolean {
        return file.isDirectory
    }
}
