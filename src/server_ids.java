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
			// load patterns into ids
			ids.readPattern();
			// Create ServerSocket for ids to listen for client connections
			ServerSocket idsServerSocket = new ServerSocket(port);
			
			// Accept connection from client
			System.out.println("Waiting for client connection...");
			Socket clientSocket = idsServerSocket.accept();
			String clientAddress = clientSocket.getRemoteSocketAddress().toString();
			String serverAddress = clientSocket.getLocalAddress().toString();
			
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
				// read file name
				String filename = clientIn.readUTF();
				// read client file size
				int lengthClient = clientIn.readInt();
				// read client packets
				byte[] clientFile = new byte[lengthClient];
				clientIn.read(clientFile);
				
				// IDS portion: client -->> server
				// convert command, name, lengthClient, and clientPacket to byte[]
				// run byte[] through ids.checkPattern and return pattern match number
				// if not 0, log issue and respond to client in correct fashion.
				// if 0, go on to server side processing
				int checkCommand = ids.checkPattern(command.getBytes(),clientAddress);
				int checkName = ids.checkPattern(filename.getBytes(),clientAddress);
				int checkLength = ids.checkPattern(BigInteger.valueOf(lengthClient).toByteArray(),clientAddress);
				int checkFile = ids.checkPattern(clientFile,clientAddress);
				
				if((checkCommand+checkName+checkLength+checkFile) != 0) {
					System.out.println("Pattern has been matched.");
					continue;
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
					int checkPutReturn = ids.checkPattern(msgPut.getBytes(),serverAddress);
					
					if(checkPutReturn != 0) {
						System.out.println("Pattern has been matched");
						continue;
					}
					
					// write back to client
					clientOut.writeUTF(msgPut);
					System.out.println(msgPut);
					
					continue;
					
					
				case "get":
					// get file
					Path getPath = Paths.get(fileFolder + "/" + filename);
					byte[] getFile;
					String okMsg = "ok";
					try {
						getFile = Files.readAllBytes(getPath);
					
						// IDS portion: server -->> client
						int checkOK = ids.checkPattern(okMsg.getBytes(), serverAddress);
						int checkGetReturnLength = ids.checkPattern(BigInteger.valueOf(getFile.length).toByteArray(),serverAddress);
						int checkGetReturnFile = ids.checkPattern(getFile,serverAddress);
					
						if(checkOK + checkGetReturnLength + checkGetReturnFile != 0) {
							System.out.println("Pattern has been matched");
							continue;
						}
					
						// write file back to client
						clientOut.writeUTF(okMsg);
						clientOut.writeInt(getFile.length);
						clientOut.write(getFile);
					
						continue;
					}
					catch (IOException e) {
						String errorMsg = "File not found - check name of file.";
						System.out.println("File: " + filename + " not found.");
						
						okMsg = "no";
						
						// IDS portion: server -->> client
						int checkNO = ids.checkPattern(okMsg.getBytes(), serverAddress);
						int checkErrorMsg = ids.checkPattern(errorMsg.getBytes(), serverAddress);
						
						if(checkNO + checkErrorMsg != 0) {
							continue;
						}
						
						// write okMSg and errorMsg
						clientOut.writeUTF(okMsg);
						clientOut.writeUTF(errorMsg);
					}
					
					
				case "ls":
					// get file names
					File[] files = new File(fileFolder).listFiles();
					String lsNames = "";
					String newline = System.getProperty("line.separator");
					for(int i = 0; i<files.length; i++) {
						lsNames += files[i].getName() + newline;
					}
					
					// IDS portion: server -->> client
					int checkLS = ids.checkPattern(lsNames.getBytes(),serverAddress);
					
					if(checkLS != 0) {
						System.out.println("Pattern has been matched");
						continue;
					}
					
					// write ls back to client
					clientOut.writeUTF(lsNames);
					
					continue;
					
				case "exit":
					// return exit message to client
					String msgExit = "Connection exiting...";
					
					// IDS portion: server -->> client
					int checkExit = ids.checkPattern(msgExit.getBytes(),serverAddress);
					
					if(checkExit != 0) {
						System.out.println("Pattern has been matched");
						continue;
					}
					
					// write msg back to client
					clientOut.writeUTF(msgExit);
					
					System.out.println(msgExit);
					
					//exit
					return;
					
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

