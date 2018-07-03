import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	private static final int PORT_NUMBER = 4000; //TODO do this a better way
  private static List<ClientConnection> activeConnections;

  private final static class ClientConnection implements Runnable {
    private final Socket incomingSocket;
    private DataOutputStream outputStream;

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
        while (true) {
          String data;
          data = inputSteam.readUTF();
          if (data.equals("exit")) { //TODO exit no longer works
            incomingSocket.close();
            inputSteam.close();
            outputStream.close();
            activeConnections.remove(this);
            break;
          }
          System.out.println(data);
          broadCastMessage(data);
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
      ServerSocket listeningServer = new ServerSocket(PORT_NUMBER); //We need the ServerSocket to detect the incoming connections
      while (true) {
        Socket incomingSocket = listeningServer.accept();//We need this socket to facilitate communication
        System.out.println(incomingSocket);

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
