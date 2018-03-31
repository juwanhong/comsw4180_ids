
public class ftp {
	
	public static void main(String[] args) {
		
		String mode = args[0];
		
		switch(mode) {
		
		case "client":
			client.Client(args);
			
		case "server":
			server.Server(args);
			
		default:
			System.out.println("error");
		}
	}

}
