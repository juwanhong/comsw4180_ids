import java.io.Console;
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
	
	public static void main(String[] clientArgs) {
		
		try {
			InetAddress idsIP = InetAddress.getByName(clientArgs[0]);
			int idsPort = Integer.parseInt(clientArgs[1]);
			String fileFolder = Paths.get("").toAbsolutePath().toString()+"/files";
			String filename = "";
			Path filepath = null;
			
			// Open socket to ids and input/output streams
			Socket clientSocket = new Socket(idsIP, idsPort);
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
					
			
			// Set up scanner for user input
			Scanner scanner = new Scanner(System.in);
			String[] commands;
			String commands1, command;
			
			
			while(true) {
				System.out.print(">>> ");
				commands1 = scanner.nextLine();
				System.out.println(commands1);
				commands = commands1.split(" ");
				command = commands[0];
				if(commands.length == 2) {
					filename = commands[1];
					System.out.println(filename);
					System.out.println(fileFolder);
					filepath = Paths.get(fileFolder + "/" + filename);		
				}
				else if(commands.length >= 3) {
					System.out.println("Input invalid!");
					continue;
				}
				
				switch(command) {
				
				case "put":
					System.out.println("In case: put");
					// load file
					System.out.println(filepath.toString());
					byte[] file = Files.readAllBytes(filepath);
					// write "put"
					out.writeUTF("put");
					// write filename
					out.writeUTF(filename);
					// write file packet length
					out.writeInt(file.length);
					// write file
					out.write(file);
					
					out.flush();
					
				
					// wait for server reply
					String msgPut = in.readUTF();
					System.out.println(msgPut);
					
					continue;
										
					
				case "get":
					System.out.println("In case: get");
					// write "get"
					out.writeUTF("get");
					//write empty filename
					out.writeUTF(filename);
					//write file packet length of 0
					out.writeInt(1);
					//write null file
					out.write(0);
					
					out.flush();
					
					// wait for server file
					int serverFileLength = in.readInt();
					byte[] serverFile = new byte[serverFileLength];
					in.read(serverFile);
					
					// save serverFile to /files
					Path putPath = Paths.get(fileFolder + "/" + filename);
					Files.write(putPath, serverFile);
					
					System.out.println("File saved to /files.");
					
					continue;
					
				case "ls":
					System.out.println("In case: ls");
					// write "ls"
					out.writeUTF("ls");
					//write empty filename
					out.writeUTF("");
					//write file packet length
					out.writeInt(1);
					//write null file
					out.write(0);
					
					out.flush();
					
					// wait for server file list
					String fileList = in.readUTF();
					
					System.out.println(fileList);
					
					continue;
					
				case "exit":
					System.out.println("In case: exit");
					// write "exit"
					out.writeUTF("exit");
					//write empty filename
					out.writeUTF("");
					//write file packet length
					out.writeInt(1);
					//write null file
					out.write(0);
					
					out.flush();
					
					// wait for server to acknowledge exit
					String serverExit = in.readUTF();
					
					System.out.println(serverExit);
					clientSocket.close();
					
					break;
					
				default:
					continue;
					
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
