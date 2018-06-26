import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private static final int SERVER_PORT_NUMBER = 4000;

	public static void main(String[] args) throws Exception {
		final InetAddress SERVER_ADDRESS = InetAddress.getByName("localhost");
		Socket serverSocket = new Socket(SERVER_ADDRESS, SERVER_PORT_NUMBER);
		DataOutputStream oStream = new DataOutputStream(serverSocket.getOutputStream());
		Scanner input = new Scanner(System.in);
		while (!serverSocket.isClosed()) {
			oStream.writeUTF(input.nextLine());
		}
	}
}
