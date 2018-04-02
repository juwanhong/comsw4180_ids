import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ids {
	private static ArrayList<String> patterns;
	
	
	public static void readPattern(){
		patterns = new ArrayList<String>();
		
		// read file line-by-line as String and remove any spaces
		// all patterns are saved in patterns arraylist with each row being a pattern
		try {
			FileInputStream fstream = new FileInputStream("patterns.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line;
			while ((line = br.readLine()) != null) {
				patterns.add(line.replaceAll("\\s+","").toLowerCase());
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public static int checkPattern(byte[] data, String address) {
		ArrayList<Integer> patternMatch = new ArrayList<Integer>();
		
		// read in byte array and convert to hex string
		StringBuilder builder = new StringBuilder();
        for (byte b : data) {
            builder.append(String.format("%02x", b).toLowerCase());
        }
        String hexData = builder.toString();
        
        // compare with each pattern
        for (int i = 0; i<patterns.size(); i++) {
        	String pattern = patterns.get(i);
        	if(hexData.contains(pattern)) {
        		patternMatch.add(i);
        	}
        }
        
        int result = 0;
        if(!patternMatch.isEmpty()) {
        	result = 1;
        	logPattern(patternMatch,address);
        }
        
		return result;
		
	}
	
	public static void logPattern(ArrayList<Integer> patternMatch, String address) {
		
		Logger logger = Logger.getLogger("Pattern Log");  
	    FileHandler fh;
	    
	    try {
			fh = new FileHandler("/ids.log");
			logger.addHandler(fh);
			
			SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);
	        
	        for (Integer i : patternMatch) {
	        	logger.warning("Pattern Found: " + i + "	From: " + address);
	        }
	        
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
	}
}
