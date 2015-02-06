import java.net.*;
import java.util.HashMap;
import java.util.Random;
import java.io.*;

public class TCPServer {
	
	private static HashMap<String, String> map = new HashMap<String, String>();
	private static final int BUFSIZE = 64;
	
	public static String nonceCreation(){
		Random ran = new Random();
		int size = 63;
		char data = ' ';
		String nonce = "";

		for (int i=0; i<=size; i++) {
		  data = (char)(ran.nextInt(25)+97);
		  nonce = data + nonce;
		}

		System.out.println("Nonce:" + nonce);
		return nonce;
	}
	
	public static void main(String[] args) throws IOException{
		if(args.length != 1){
			throw new IllegalArgumentException("Parameters:[<port>]");
		}
		
		// 3 users
		map.put("User", "Pass");
		map.put("George", "Burdell");
		map.put("GT", "2015");
		
		
		int serverPort = Integer.parseInt(args[0]);
		
		ServerSocket servSocket = new ServerSocket(serverPort);
		System.out.println("Started new server");
		
		int recvMessageSize;
		byte[] byteBuffer = new byte[BUFSIZE];
		
		for(;;){
			System.out.println("Blocking");
			Socket clientSocket = servSocket.accept();
			System.out.println("Handling client at "+clientSocket.getInetAddress()+" on port "+clientSocket.getPort());
			
			InputStream in = clientSocket.getInputStream();
			OutputStream out = clientSocket.getOutputStream();

			String welcome = "Welcome to our service.";
			
			// AUTHENTICATION REQUEST RECEIVED HERE
			in.read(byteBuffer, 0 ,1);
			String input = new String(byteBuffer);
			System.out.println("Y:" + input);
			
			/// Write out cryptographic nonce
			String nonce = nonceCreation();
			out.write(nonce.getBytes());
			
			/// Received Username & Hash
			in.read(byteBuffer);
			String userHash = new String(byteBuffer);
			System.out.println("User & Hash:"+userHash);
			
			String user = "";
			String hash = "";
			for(int i =0; i < userHash.length(); i++){
				if(userHash.charAt(i) == '*'){
					user = userHash.substring(0, i);
					hash = userHash.substring(i+1);
				}
			}
			System.out.println("User:"+user+"----"+"Hash:"+hash);
			
			
			
			
			clientSocket.close();
		}
		
	}
}