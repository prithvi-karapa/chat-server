import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {
	private static final int SERVER_PORT_NUMBER = 4000;
	private static final String EXIT_MESSAGE = "exit";
	private static Socket serverSocket;
  private static Scanner input;
  private static String clientName;
  private static DataOutputStream outputStream;

  private static class ServerListener implements Runnable {
		Socket socket;
    Thread mainClientProcess;

		private ServerListener(Socket socket, Thread mainClientProcess) {
		  this.socket = socket;
		  this.mainClientProcess = mainClientProcess;
		}

		@Override
		public void run() {
			while(true) {
				try {
					DataInputStream inputSteam = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
					System.out.println(inputSteam.readUTF());
				} catch (IOException e) {
				  break;
				}
			}
      try {
        serverSocket.close();
        System.in.close();
        mainClientProcess.interrupt();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
	}

	public static void main(String[] args) throws Exception {
    input = new Scanner(System.in); //TODO Switch to a BufferedReader instead of System.in
    assignClientName();
    final InetAddress SERVER_ADDRESS = InetAddress.getByName("localhost");
		serverSocket = new Socket(SERVER_ADDRESS, SERVER_PORT_NUMBER);
		ServerListener serverListener = new ServerListener(serverSocket, Thread.currentThread());
		Thread connectionThread = new Thread(serverListener);
		connectionThread.start();
		outputStream = new DataOutputStream(serverSocket.getOutputStream());
		sendClientNameToServer();
		while (!serverSocket.isClosed()) {
		  try {
        String message = input.nextLine();
        outputStream.writeUTF(message);
      } catch (NoSuchElementException e) {
		    System.out.println("Closing main client process");
		    break;
      }
		}
	}

	private static void assignClientName() {
	  System.out.println("Please enter your name: ");
    clientName = input.nextLine();
  }

  private static void sendClientNameToServer() {
    try {
      outputStream.writeUTF(clientName);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
