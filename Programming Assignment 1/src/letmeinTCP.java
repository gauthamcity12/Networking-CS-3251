import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.*;

public class letmeinTCP {
	private static byte[] user;
	private static byte[] pass;
	private static byte[] nonce = new byte[64];
	private static Socket socket;
	private static InputStream in;
	private static OutputStream out;
	private static String debug;
	private static boolean dPrint = false;
	
	/**
	 * Sets up the socket based on the user input from the command line. Also, sends an authentication request to the server. 
	 * @param user username provided in the command line
	 * @param pass password provided in the command line
	 * @param server server IP address provided in the command line
	 * @param serverPort server Port # provided in the command line
	 * @throws IOException from opening input or output streams from the client socket
	 */
	public static void connRequest(byte[] user, byte[] pass, String server, int serverPort) throws IOException{
		try {
			socket = new Socket(server, serverPort);
		} catch(UnknownHostException e) {
			System.out.println("Unknown Host: IP address of server could not be determined. Please try again");
			System.exit(0);
		} catch(IOException e) {
			System.out.println("IO Exception: I/O Channel could not opened. Please Try Again.");
			System.exit(0);
		} catch(Exception e){
			System.out.println(e);
			System.exit(0);
		}
		
		in = socket.getInputStream();
		out = socket.getOutputStream();
		
		out.write("Y".getBytes("UTF-8")); 
		if(dPrint){
			System.out.println("Sending authentication requestion to server:"+server+":"+serverPort);
		}
	
	}
	/**
	 * Test MD5 digest computation
	 * !!! Borrowed code from link provided in assignment description !!!
	 * @author Roedy Green
	 * @version 1.0
	 * @since 2004-06-07
	 */
	public static byte[] MD5(String user, String password, String nonce) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		String concat = user+password+nonce;
		byte[] theTextToDigestAsBytes = concat.getBytes("UTF-8");
		MessageDigest md= MessageDigest.getInstance("MD5");
		md.update(theTextToDigestAsBytes);
		byte[] digest= md.digest();
		return digest;
	}
	
	/**
	 * Takes the command line input and sets up the socket for data transmission between the server and client. 
	 * Evaluates the nonce, sends the username & hash, and accepts the authentication request response. Driver for all helper functions.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		try{	
			if((args.length < 3)||(args.length > 4)){
				throw new IllegalArgumentException("Parameters: <server:[port]> <username> <password> <-d(optional)>");
			}
			CharSequence ast = "*";
			if(args[1].contains(ast)){
				throw new Exception("Username cannot contain *");
			}
		}
		catch(IllegalArgumentException e){
			System.out.println("Error: Invalid Parameters: <server:[port]> <username> <password> <-d(optional)>, Please try again");
			System.exit(0);
		} 
		catch(Exception e) {
			System.out.println(e);
			System.exit(0);
		}
		
		/**
		 * Parses command line arguments after confirming that the input is valid. 
		 */
		String[] params = args[0].split(":");
		String server = params[0];
		int serverPort = 7; // Default port number
		try{
			serverPort = Integer.parseInt(params[1]);
		}
		catch(Exception e){
			System.out.println("Invalid Argument: Must Include ':' between Server & Port #. Please try again.");
			System.exit(0);
		}
		
		user = args[1].getBytes("UTF-8");
		pass = args[2].getBytes("UTF-8");
		if(args.length == 4){
			debug = args[3];
			if(debug.charAt(0)=='-' && debug.charAt(1)=='d' && debug.length()==2){
				dPrint = true;
			}
			else{
				System.out.println("Invalid debug parameter, please try again");
				System.out.println("Exiting...");
				System.exit(0);
			}
		}
		
		
		
		/**
		 * Initialize the socket that connects and is the channel of communication with the server
		 * Read the cryptographic nonce
		 */
		connRequest(user, pass, server, serverPort);
		in.read(nonce);
		
		if("Access to Server Denied".trim().equals(new String(nonce).trim())){
			System.out.println("Acess to Server Denied");
			System.exit(0);
		}
		if(dPrint){
			System.out.println("Received this cryptographic nonce from the server:"+ new String(nonce));
		}
		
		
		/**
		 * Creates the MD5 hash based on the username, password, and cryptographic nonce
		 */
		byte[] hash = null;
		try {
			hash = MD5(new String(user), new String(pass), new String(nonce));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Algorithm Exception: Problem computing the hash, exiting the program");
			System.exit(0);
		} 
		
		if(dPrint){
			System.out.println("send this hash to the server:"+ new String(hash));
			System.out.println("sending this username to the server:"+new String(user));
		}
		
		/**
		 * Sends the concatenated username string and hash delimited by a '*' character
		 * Receives the result from the server and prints it out to screen
		 * Closes the socket aftewards
		 */
		String auth = new String(user) + "*" + new String(hash);
		byte[] trans = auth.getBytes("UTF-8");
		byte[] result = new byte[32];
		out.write(trans);
		
		in.read(result);
		System.out.println((new String(result)).trim());
		socket.close();
	}
}
