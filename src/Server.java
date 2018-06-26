import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	private static final int PORT_NUMBER = 4000; //TODO do this a better way

  private final static class ClientConnection implements Runnable {
    private final Socket socket;

    private ClientConnection(Socket connection) {
      this.socket = connection;
    }

    @Override
    public void run() {
      try {
        DataInputStream inputSteam = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        while (true) {
          String data;
          data = inputSteam.readUTF();
          if (data.equals("exit")) {
            socket.close();
            inputSteam.close();
            break;
          }

          System.out.println(data);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

	public static void main(String[] args) {
    List<ClientConnection> activeConnections = new ArrayList<>();
    try {
      ServerSocket listeningServer = new ServerSocket(PORT_NUMBER); //We need the ServerSocket to detect the incoming connections
      while (true) {
        Socket socket = listeningServer.accept(); //We need this socket to facilitate communication
        System.out.println(socket);

        ClientConnection connection = new ClientConnection(socket);
        Thread connectionThread = new Thread(connection);
        connectionThread.start();
        activeConnections.add(connection);
      }
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}
