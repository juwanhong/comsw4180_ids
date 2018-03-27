import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class client {
	
	public static void Client(String[] clientArgs) {
		
		try {
			InetAddress idsIP = InetAddress.getByName(clientArgs[1]);
			int idsPort = Integer.parseInt(clientArgs[2]);
			String fileFolder = Paths.get("").toAbsolutePath().toString()+"/files";
			String filename = "";
			Path filepath = null;
			
			// Open socket to ids and input/output streams
			Socket clientSocket = new Socket(idsIP, idsPort);
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
			
			// Set up scanner for user input
			Scanner scanner = new Scanner(System.in);
			
			while(true) {
				System.out.print(">>> ");
				String[] commands = scanner.next().toString().split(" ");
				String command = commands[0];
				if(commands.length == 2) {
					filename = commands[1];
					filepath = Paths.get(fileFolder + filename);		
				}
				else if(commands.length >= 3) {
					System.out.println("Input invalid!");
					continue;
				}
				
				switch(command) {
				
				case "put":
					// load file
					byte[] file = Files.readAllBytes(filepath);
					// write "put"
					out.writeUTF("put");
					// write filename
					out.writeUTF(filename);
					// write file packet length
					out.writeInt(file.length);
					// write file
					out.write(file);
					
					
				case "get":
					
				case "ls":
					
				case "exit":
					
				default:
					
				}
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
