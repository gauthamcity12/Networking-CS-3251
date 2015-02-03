import java.net.*;
import java.io.*;

public class TCPClient {
	public static void main(String[] args) throws IOException{
		if((args.length < 2)||(args.length > 3)){
			throw new IllegalArgumentException("Parameters: <server> <word> [<port>]");
		}
		
		String server = args[0];
		byte[] byteBuffer = args[1].getBytes();
		
		int serverPort = (args.length == 3) ? Integer.parseInt(args[2]): 7;
		
		Socket socket = new Socket(server, serverPort);
		System.out.println("Connected to Server... Sending Echo Request");
		
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		
		out.write(byteBuffer); // sends encoded string to server
		
		int totalBytesRcvd = 0;
		int bytesRcvd = 0;
		while(totalBytesRcvd < byteBuffer.length){
			if((bytesRcvd = in.read(byteBuffer, totalBytesRcvd, byteBuffer.length - totalBytesRcvd)) == -1){
				throw new SocketException("Connection closed prematurely");
			}
			totalBytesRcvd += bytesRcvd;
			
		}
		System.out.println("Received:"+new String(byteBuffer));
		socket.close();
		
		
	}
}