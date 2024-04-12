import files.FileCommon
import java.awt.Dimension
import java.awt.Font
import java.io.File
import java.io.FileWriter
import java.io.IOException
import javax.swing.Icon
import javax.swing.JTable
import javax.swing.filechooser.FileSystemView
import javax.swing.table.AbstractTableModel

class DisplayTable {
    private val fileModel: FileModel
    val displayTable: JTable

    init {
        fileModel = FileModel()
        displayTable = JTable(fileModel)
        displayTable.fillsViewportHeight = true
        displayTable.setShowGrid(false)
        displayTable.font = Font("Serif", Font.PLAIN, 13)
        val tableHeader = displayTable.tableHeader
        tableHeader.font = Font("Serif", Font.BOLD, 13)
        displayTable.rowHeight = 20
        displayTable.intercellSpacing = Dimension(0, 0)
    }

    fun updateFileTable(fileList: List<FileCommon>?) {
        fileModel.setData(fileList)
        displayTable.columnModel.getColumn(0).preferredWidth = 10
        displayTable.columnModel.getColumn(0).minWidth = 30
        displayTable.columnModel.getColumn(1).minWidth = 320
        displayTable.columnModel.getColumn(2).preferredWidth = 7000
    }

    internal inner class FileModel : AbstractTableModel() {
        private val columns = arrayOf("", "Name", "Size")
        private var fileList: List<FileCommon>? = null

        fun setData(fileList: List<FileCommon>?) {
            this.fileList = fileList
            fireTableDataChanged()
        }

        override fun getRowCount(): Int {
            return fileList!!.size
        }

        override fun getColumnCount(): Int {
            return columns.size
        }

        override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
            val file = fileList!![rowIndex]

            when (columnIndex) {
                0 -> if (file.isDirectory) {
                    return folderIcon
                } else {
                    var ext = ""
                    if (file.fileName.contains(".")) {
                        ext = file.fileName.substring(file.fileName.lastIndexOf('.'))
                    }
                    return getExtIcon(ext)
                }

                1 -> return file.fileName
                2 -> return formatSize(file.fileSize)
                else -> return null
            }
        }

        override fun getColumnName(col: Int): String {
            return columns[col]
        }

        override fun getColumnClass(column: Int): Class<*> {
            if (column == 0) {
                return Icon::class.java
            }
            return super.getColumnClass(column)
        }

        override fun isCellEditable(row: Int, column: Int): Boolean {
            return false
        }
    }

    companion object {
        private val CACHE: MutableMap<String, Icon> = HashMap()
        private val folderIcon: Icon
            get() {
                val icon: Icon

                if (CACHE.containsKey("file-dir")) {
                    icon = CACHE["file-dir"]!!
                } else {
                    val temp = File(System.getProperty("java.io.tmpdir") + File.separator + "icon")
                    temp.mkdirs()

                    icon = FileSystemView.getFileSystemView().getSystemIcon(temp)

                    CACHE["file-dir"] = icon

                    temp.delete()
                }
                return icon
            }

        //returns the system icon for the provided extension
        //if extension is empty it will return the icon for an extensionless file
        private fun getExtIcon(ext: String): Icon {
            val icon: Icon
            if (CACHE.containsKey(ext)) {
                icon = CACHE[ext]!!
            } else {
                //create a temp file with given ext so we can grab the icon from it
                val t = File(System.getProperty("java.io.tmpdir") + File.separator + "ic." + ext)
                try {
                    val fw = FileWriter(t)
                    fw.write("t")
                    fw.close()
                } catch (_: IOException) {
                }
                icon = FileSystemView.getFileSystemView().getSystemIcon(t)

                CACHE[ext] = icon
                t.delete()
            }
            return icon
        }

        private fun formatSize(v: Long): String {
            if (v < 1024) return "$v  B"
            val z = (63 - java.lang.Long.numberOfLeadingZeros(v)) / 10
            return String.format("%.1f %sB", v.toDouble() / (1L shl (z * 10)), " KMGTPE"[z])
        }
    }
}
