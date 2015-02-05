import java.net.*;
import java.io.*;

public class TCPClient {
	static byte[] user;
	static byte[] pass;
	static Socket socket;
	
	
	public static void connRequest(byte[] user, byte[] pass, String server, int serverPort) throws IOException{
		socket = new Socket(server, serverPort);
		System.out.println("Connected to Server... Sending Echo Request");
		
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		
		out.write(user); // sends encoded string to server
		out.write(pass);
		
	}
	public static void main(String[] args) throws IOException{
		if((args.length < 3)||(args.length > 3)){
			throw new IllegalArgumentException("Parameters: <server:[port]> <username> <password>");
		}
		
		String[] params = args[0].split(":");
		String server = params[0];
		
		//byte[] byteBuffer = args[1].getBytes();
		user = args[1].getBytes();
		pass = args[2].getBytes();
		
		int serverPort = Integer.parseInt(params[1]);
		
		connRequest(user, pass, server, serverPort);
	
		socket.close();
		
		
	}
}
