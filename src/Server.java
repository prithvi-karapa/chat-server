import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
  //TODO do this a better way
	private static final int PORT_NUMBER = 4000;
  private static final String EXIT_MESSAGE = "exit";
  private static List<ClientConnection> activeConnections;

  private final static class ClientConnection implements Runnable {
    private final Socket incomingSocket;
    private DataOutputStream outputStream;
    private String clientName;

    private ClientConnection(Socket incomingSocket) {
      this.incomingSocket = incomingSocket;
      try {
        this.outputStream = new DataOutputStream(incomingSocket.getOutputStream());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void run() {
      try {
        DataInputStream inputSteam = new DataInputStream(new BufferedInputStream(incomingSocket.getInputStream()));
        clientName = inputSteam.readUTF();
        while (true) {
          String data;
          data = inputSteam.readUTF();
          if (data.equals(EXIT_MESSAGE)) {
            incomingSocket.close();
            inputSteam.close();
            outputStream.close();
            activeConnections.remove(this);
            broadCastMessage(clientName + " has exited");
            break;
          }
          System.out.println(data);
          broadCastMessage(clientName + ": " + data);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public void sendMessage(String message) throws IOException {
      outputStream.writeUTF(message);
    }
  }

	public static void main(String[] args) {
    activeConnections = new ArrayList<>();
    try {
      //We need the ServerSocket to detect the incoming connections
      ServerSocket listeningServer = new ServerSocket(PORT_NUMBER);
      while (true) {
        //We need this socket to facilitate communication
        Socket incomingSocket = listeningServer.accept();
        System.out.println(incomingSocket); //TODO Make this message more friendly

        ClientConnection connection = new ClientConnection(incomingSocket);
        Thread connectionThread = new Thread(connection);
        connectionThread.start();
        activeConnections.add(connection);
      }
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  public static void broadCastMessage(String message) throws IOException {
    for (ClientConnection connection : activeConnections) {
      connection.sendMessage(message);
    }
  }
}
