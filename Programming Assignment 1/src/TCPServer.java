import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;

public class TCPServer {
	
	private static HashMap<String, String> map = new HashMap<String, String>();
	private static final int BUFSIZE = 64;
	private static String hash = "";
	private static String debug;
	private static boolean dPrint = false;
	private static InputStream in;
	private static byte[] byteBuffer;
	private static int counter = 0;
	private static boolean challFailed = false;
	
	public static String nonceCreation(){
		Random ran = new Random();
		int size = 63;
		char data = ' ';
		String nonce = "";

		for (int i=0; i<=size; i++) {
		  data = (char)(ran.nextInt(25)+97);
		  nonce = data + nonce;
		}

		//System.out.println("Nonce:" + nonce);
		return nonce;
	}
	
	public static byte[] MD5(String user, String password, String nonce) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		String concat = user+password+nonce;
		byte[] theTextToDigestAsBytes = concat.getBytes("UTF-8");
		MessageDigest md= MessageDigest.getInstance("MD5");
		md.update(theTextToDigestAsBytes);
		byte[] digest= md.digest();
		return digest;
	}
	public static byte[] testUserHash(byte[] byteBuffer, String nonce){
		String userHash = new String(byteBuffer);
		//System.out.println("User & Hash:"+userHash);
		
		String user = "";
		hash = "";
		for(int i =0; i < userHash.length(); i++){
			if(userHash.charAt(i) == '*'){
				user = userHash.substring(0, i);
				hash = userHash.substring(i+1);
				break;
			}
		}
		//System.out.println("User:"+user+"----"+"Hash:"+hash);
		
		byte[] test = null;
		try {
			test = MD5(user, map.get(user), nonce);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Algorithm Exception: Problem computing the hash, exiting the program");
			System.exit(0);
		} catch (UnsupportedEncodingException e) {
			System.out.println(e);
			System.exit(0);
		}
		return test;
	}
	
	public static void main(String[] args) throws IOException{
		try{
			if(args.length < 1 || args.length > 2){
				throw new IllegalArgumentException("Parameters:[<port>] <-d(optional>");
			}
		}
		catch(IllegalArgumentException e){
			System.out.println("Error: Parameters:[<port>], Please try again");
			System.exit(0);
		}
		
		if(args.length == 2){
			String debug = args[1];
			if(debug.charAt(1)=='d' && debug.charAt(0)=='-' && debug.length()==2){
				dPrint = true;
			}
			else{
				System.out.println("Invalid debug parameter, please try again");
				System.out.println("Exiting...");
				System.exit(0);
			}
		}
		
		// 3 users
		map.put("user", "pass");
		map.put("George", "Burdell");
		map.put("GT", "2015");
		
		
		int serverPort = Integer.parseInt(args[0]);
		ServerSocket servSocket = null;
		try{
			servSocket = new ServerSocket(serverPort);
		}
		catch(Exception e){
			System.out.println("Problem opening Server Socket to port:"+serverPort);
			System.out.println(e);
			System.exit(0);
		}
		System.out.println("Started new server");
		
		int recvMessageSize;
		byteBuffer = new byte[BUFSIZE];
		
		for(;;){
			challFailed = false;
			counter = 0;
			System.out.println("Blocking");
			Socket clientSocket = servSocket.accept();
			System.out.println("Handling client at "+clientSocket.getInetAddress()+" on port "+clientSocket.getPort());
			
			in = null;
			OutputStream out = null;
			
			try{
				in = clientSocket.getInputStream();
				out = clientSocket.getOutputStream();
			}
			catch(Exception e){
				System.out.println("Problem opening I/O Stream for client allocated socket");
				System.out.println(e);
				System.exit(0);
			}

			String welcome = "Welcome to our service.";
			
			// AUTHENTICATION REQUEST RECEIVED HERE
			in.read(byteBuffer, 0 ,1);
			String input = new String(byteBuffer).substring(0,1);
			if(!input.equals("Y")){
				out.write("Access to Server Denied".getBytes());
				System.out.println("Improper Authorization Request: Closing Connection with Client");
				clientSocket.close();
				continue;
			}
			if(dPrint){
				System.out.println("Authentication Request Received:" + input);
			}
			/// Send cryptographic nonce
			String nonce = nonceCreation();
			out.write(nonce.getBytes());
			
			if(dPrint){
				System.out.println("Nonce sent to client:"+nonce);
			}
			
			/// Received Username & Hash
			byteBuffer = new byte[BUFSIZE];
			in.read(byteBuffer);
			
			int delay = 5000; // delay for 5 sec.
			int period = 1000; // repeat every sec.
			
			Timer timer = new Timer();
			counter = 0;
		    timer.schedule(new TimerTask()
		      {
		        public void run()
		        {
		          System.out.println("Waiting..");
		          try {
					in.read(byteBuffer);
					if(!byteBuffer.equals(new byte[BUFSIZE])){
						timer.cancel();
						timer.purge();
					}
					else{
						counter++;
						if(dPrint){
							System.out.println("Haven't Receive Challenge Response Yet.");
						}
					}
					if(counter > 5){
						System.out.println("Client is Unresponsive. Authentication Challenge Note Completed. Closing Connection.");
						timer.cancel();
						timer.purge();
						clientSocket.close();
						challFailed = true;
						
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println(e);
				}
		        }
		      }, delay, period);
			
			if(challFailed){
				continue;
			}
			timer.cancel();
			timer.purge();
			byte[] test = testUserHash(byteBuffer, nonce);
			
			if(dPrint){
				System.out.println("Computed Hash from Username:"+new String(test));
				System.out.println("  Hash Received from Client:"+hash);
			}
			if((new String(test).trim()).equals(hash.trim())){
				System.out.println("Client Authorized");
				out.write("Welcome to our service".getBytes());
				clientSocket.close();
			}
			else{
				out.write("Access to Server Denied".getBytes());
				System.out.println("Client Authorization Failed");
				clientSocket.close();
			}
			
		}
		
	}
}