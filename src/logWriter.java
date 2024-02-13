import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class logWriter {

	static String filename = "ftpclient.log";

	public static void log(String t) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String sDate = dateFormat.format(date) + " ";
		if(Files.exists(Paths.get(filename))) {
			try {
				Files.writeString(Paths.get(filename),sDate + t +"\n", StandardOpenOption.valueOf("APPEND"));
			} catch (IOException e) {
				
			}
		} else {
			try {
				Files.writeString(Paths.get(filename),sDate + t + "\n");
			} catch (IOException e) {
				
			}
		}
	}
}
