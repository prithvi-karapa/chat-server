import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private static final int SERVER_PORT_NUMBER = 4000;
	private static Socket serverSocket;

	private static class Listener implements Runnable {
		Socket socket;

		private Listener(Socket socket) {this.socket = socket;}

		@Override
		public void run() {
			while(true) {
				try {
					DataInputStream inputSteam = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
					System.out.println("Received from server: " + inputSteam.readUTF());
				} catch (IOException e) {
				  break;
				}
			}
      try {
        serverSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
	}

	public static void main(String[] args) throws Exception {
		final InetAddress SERVER_ADDRESS = InetAddress.getByName("localhost");
		serverSocket = new Socket(SERVER_ADDRESS, SERVER_PORT_NUMBER);
		Listener listener = new Listener(serverSocket);
		Thread connectionThread = new Thread(listener);
		connectionThread.start();
		DataOutputStream oStream = new DataOutputStream(serverSocket.getOutputStream());
		Scanner input = new Scanner(System.in);
		while (!serverSocket.isClosed()) {
			oStream.writeUTF(input.nextLine()); //TODO this is a blocking operation so it won't detect that the socket is closed
		}
	}
}
