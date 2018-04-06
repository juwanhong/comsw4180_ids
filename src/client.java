import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
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
					// load file
					System.out.println(filepath.toString());
					byte[] file;
					try {
						file = Files.readAllBytes(filepath);
					}
					catch (IOException e) {
						System.out.println("File not found - check name.");
						continue;
					}
					
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
					clientSocket.setSoTimeout(3000);
					try {
						String msgPut = in.readUTF();
						System.out.println(msgPut);
					}
					catch (SocketTimeoutException e) {
						System.out.println("Packets dropped due to pattern match.");
						continue;
					}
			
					
					continue;
										
					
				case "get":
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
					String okMsg;
					int serverFileLength;
					byte[] serverFile = null;
					
					clientSocket.setSoTimeout(5000);
					try {
						okMsg = in.readUTF();
						
						if (okMsg.equals("ok")) {
							serverFileLength = in.readInt();
							serverFile = new byte[serverFileLength];
							in.read(serverFile);
						}
						
						else if(okMsg.equals("no")) {
							String errorMsg = in.readUTF();
							System.out.println(errorMsg);
							continue;
						}
					}
					catch (SocketTimeoutException e) {
						System.out.println("Packets dropped due to pattern match.");
						continue;
					}
					
					// save serverFile to /files
					Path putPath = Paths.get(fileFolder + "/" + filename);
					Files.write(putPath, serverFile);
					
					System.out.println("File saved to /files.");
					
					continue;
					
				case "ls":
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
					clientSocket.setSoTimeout(3000);
					try {
						String fileList = in.readUTF();
						System.out.println(fileList);
					}
					catch (SocketTimeoutException e) {
						System.out.println("Packets dropped due to pattern match.");
						continue;
					}
					
					
					
					continue;
					
				case "exit":
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
					clientSocket.setSoTimeout(3000);
					try{
						String serverExit = in.readUTF();
						System.out.println(serverExit);
					}
					catch (SocketTimeoutException e) {
						System.out.println("Packets dropped due to pattern match.");
						continue;
					}
					
					clientSocket.close();
					
					return;
					
				default:
					System.out.println("Wrong command. Use put, get, ls, and exit.");
					continue;
					
				}
				
		
				
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Server not found - retry with correct address and port.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Socket issue - retry.");
		}
		
		
	}

}
