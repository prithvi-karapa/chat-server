import java.net.Socket;
import java.net.InetAddress;

public class Client {
	private static final InetAddress SERVER_ADDRESS = InetAddress.getByName("10.40.41.221"); //This is Seth's computer ip
	private static final int SERVER_PORT_NUMBER = 4000; 
	private static final String CLIENT_HOST_NAME = "c02s421ng8wl"; //This is Prithvi's host name
	private static final int CLIENT_PORT_NUMBER = 4050;

	public static void main(String[] args) {
		Socket serverSocket = new Socket(SERVER_ADDRESS, SERVER_PORT_NUMBER); 
		Socket clientSocket = new Socket(CLIENT_HOST_NAME, CLIENT_PORT_NUMBER);
	}
}