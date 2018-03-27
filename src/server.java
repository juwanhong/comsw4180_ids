import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class server {

	public static void Server(String[] serverArgs) {
		int serverPort = Integer.parseInt(serverArgs[1]);
		
		try {
			ServerSocket serverSocket = new ServerSocket(serverPort);
			
			Socket idsSocket = serverSocket.accept();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
