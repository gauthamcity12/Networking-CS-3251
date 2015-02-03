import java.net.*;
import java.io.*;

public class TCPServer {
	
	private static final int BUFSIZE = 32;
	public static void main(String[] args) throws IOException{
		if(args.length != 1){
			throw new IllegalArgumentException("Parameters:[<port>]");
		}
		
		
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
			
			while((recvMessageSize = in.read(byteBuffer))!= -1){
				out.write(byteBuffer, 0, recvMessageSize);
			}
			
			clientSocket.close();
		}
		
		
	}
}