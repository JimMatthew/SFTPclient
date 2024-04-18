import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object logWriter {
    var filename: String = "ftpclient.log"

    fun log(t: String) {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = Date()
        val sDate = dateFormat.format(date) + " "
        if (Files.exists(Paths.get(filename))) {
            try {
                Files.writeString(Paths.get(filename), sDate + t + "\n", StandardOpenOption.valueOf("APPEND"))
            } catch (e: IOException) {
            }
        } else {
            try {
                Files.writeString(Paths.get(filename), sDate + t + "\n")
            } catch (e: IOException) {
            }
        }
    }
}
