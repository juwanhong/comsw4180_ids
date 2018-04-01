import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class server_ids {

	public static void main(String args[]) {
		int port = Integer.parseInt(args[0]);
		
		String fileFolder = Paths.get("").toAbsolutePath().toString()+"/files";
		
				
		try {
			// Create ServerSocket for ids to listen for client connections
			ServerSocket idsServerSocket = new ServerSocket(port);
			
			// Accept connection from client
			Socket clientSocket = idsServerSocket.accept();
			
			// Get server ip (same as ids)
			InetAddress serverIP = InetAddress.getLocalHost();
			
			// open input/output datastreams for client and server
			DataOutputStream clientOut = new DataOutputStream(clientSocket.getOutputStream());
			DataInputStream clientIn = new DataInputStream(clientSocket.getInputStream());		
			
			// First receive all packets from client.
			// Order of packets are:
			// 1. command
			// 2. file name
			// 3. length of file size
			// 4. file
			// Note that if 2,3,4 are 0 or null, ids should still perform check for patterns
			// but server would not process them.
			while(true) {
				// print console message
				System.out.println(">> Waiting for client command...");
				
				// read first input: command
				String command = clientIn.readUTF();
				System.out.println("command: " + command);
				// read file name
				String filename = clientIn.readUTF();
				// read client file size
				int lengthClient = clientIn.readInt();
				// read client packets
				byte[] clientFile = new byte[lengthClient];
				System.out.println("file length: " + lengthClient);
				clientIn.read(clientFile);
				
				// IDS portion: client -->> server
				// convert command, name, lengthClient, and clientPacket to byte[]
				// run byte[] through ids.checkPattern and return pattern match number
				// if not 0, log issue and respond to client in correct fashion.
				// if 0, go on to server side processing
				int checkCommand = ids.checkPattern(command.getBytes());
				int checkName = ids.checkPattern(filename.getBytes());
				int checkLength = ids.checkPattern(BigInteger.valueOf(lengthClient).toByteArray());
				int checkFile = ids.checkPattern(clientFile);
				
				if((checkCommand+checkName+checkLength+checkFile) != 0) {
					
				}
				
				// Server portion:
				// process command and act accordingly
				switch(command) {
				
				case "put":
					// save file to /files
					Path putPath = Paths.get(fileFolder + "/" + filename);
					Files.write(putPath, clientFile);
					
					// message back to client
					String msgPut = "File <" + filename + "> saved.";
					
					// IDS portion: server -->> client
					int checkPutReturn = ids.checkPattern(msgPut.getBytes());
					
					// write back to client
					clientOut.writeUTF(msgPut);
					System.out.println(msgPut);
					
					continue;
					
					
				case "get":
					// get file
					Path getPath = Paths.get(fileFolder + "/" + filename);
					byte[] getFile = Files.readAllBytes(getPath);
					
					// IDS portion: server -->> client
					int checkGetReturnLength = ids.checkPattern(BigInteger.valueOf(getFile.length).toByteArray());
					int checkGetReturnFile = ids.checkPattern(getFile);
					
					// write file back to client
					clientOut.writeInt(getFile.length);
					clientOut.write(getFile);
					
					continue;
					
					
				case "ls":
					// get file names
					File[] files = new File(fileFolder).listFiles();
					String lsNames = "";
					String newline = System.getProperty("line.separator");
					for(int i = 0; i<files.length; i++) {
						lsNames += files[i].getName() + newline;
					}
					
					// IDS portion: server -->> client
					int checkLS = ids.checkPattern(lsNames.getBytes());
					
					// write ls back to client
					clientOut.writeUTF(lsNames);
					
					continue;
					
				case "exit":
					// return exit message to client
					String msgExit = "Connection exiting...";
					
					// IDS portion: server -->> client
					int checkExit = ids.checkPattern(msgExit.getBytes());
					
					// write msg back to client
					clientOut.writeUTF(msgExit);
					
					//exit
					break;
					
				default:
					// bad command
					System.out.println("Bad command.");
					
					continue;
						
				}
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

