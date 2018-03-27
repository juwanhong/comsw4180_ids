import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class server_ids {

	public static void main(String args[]) {
		int port = Integer.parseInt(args[1]);
		
				
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
				// read first input: command
				String command = clientIn.readUTF();
				// read file name
				String name = clientIn.readUTF();
				// read client file size
				int lengthClient = clientIn.readInt();
				// read client packets
				byte[] clientPacket = new byte[lengthClient];
				clientIn.read(clientPacket);
				
				// IDS portion:
				// convert command, name, lengthClient, and clientPacket to byte[]
				// run byte[] through ids.checkPattern and return pattern match number
				// if not 0, log issue and respond to client in correct fasion.
				// if 0, go on to server side processing
				int checkCommand = ids.checkPattern(command.getBytes());
				int checkName = ids.checkPattern(name.getBytes());
				int checkLength = ids.checkPattern(BigInteger.valueOf(lengthClient).toByteArray());
				int checkFile = ids.checkPattern(clientPacket);
				
				if((checkCommand+checkName+checkLength+checkFile) != 0) {
					
				}
				
				// Server portion:
				// process command and act accordingly
				switch(command) {
				
				case "put":
					
				case "get":
					
				case "ls":
					
				case "exit":
					
				default:
						
				}
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

