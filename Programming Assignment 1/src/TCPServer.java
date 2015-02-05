import java.net.*;
import java.util.HashMap;
import java.io.*;

public class TCPServer {
	
	private static HashMap<String, String> map = new HashMap<String, String>();
	private static final int BUFSIZE = 32;
	
	
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
			
//			while((recvMessageSize = in.read(byteBuffer))!= -1){
//				out.write(byteBuffer, 0, recvMessageSize);
//			}
			String welcome = "Welcome to our service.";
			
			in.read(byteBuffer);
			String user = new String(byteBuffer);
			System.out.println(user);
			//in.read(byteBuffer);
			String pass = new String(byteBuffer);
			System.out.println(pass);
//			if(user.equals("user") && pass.equals("pass")){
//				
//				//out.write(welcome.getBytes());
//			}
//			out.write(welcome.getBytes());
			clientSocket.close();
		}
		
	}
}