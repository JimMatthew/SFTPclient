
import javax.swing.SwingUtilities;
//import com.formdev.flatlaf.FlatLightLaf;

public class ftpClientt {

	public static void main(String[] args)
	{
		
		 SwingUtilities.invokeLater(new Runnable() {
		      public void run() {
		    	  ftpClientManager manager = new ftpClientManager();
		      }
		    });
	}
	
}
