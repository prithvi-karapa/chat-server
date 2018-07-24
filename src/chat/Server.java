package chat;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import chat.actions.ActiveUsersAction;
import chat.actions.ChatAction;
import chat.actions.ExitAction;

public class Server{
  //TODO do this a better way
	private static final int PORT_NUMBER = 4000;
  private Map<String, ClientConnection> activeConnections;

  private final class ClientConnection implements Runnable, ClientConnectionForActions {
    private final Socket incomingSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String clientName;
    private ChatAction[] actions = {new ExitAction(this), new ActiveUsersAction(this)};
    boolean active = true;

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
        inputStream = new DataInputStream(new BufferedInputStream(incomingSocket.getInputStream()));
        clientName = inputStream.readUTF();
        broadCastMessage(clientName + " has entered the chat", clientName);
        activeConnections.put(clientName, this);
        while (active) {
          String data;
          data = inputStream.readUTF();
          List<ChatAction> performedActions = Arrays.stream(actions).filter(action -> action.attemptAction(data, clientName))
              .collect(Collectors.toList());
          if (!performedActions.isEmpty()) {
            performedActions.forEach(action -> System.out.println(clientName + " performed Action " + action.getClass()));
          } else {
            System.out.println(clientName + ": " + data);
            broadCastMessage(clientName + ": " + data, clientName);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public void sendMessage(String message) throws IOException {
      outputStream.writeUTF(message);
    }

    public void broadCastMessage(String message, String fromClient) {
      Server.this.broadCastMessage(message, fromClient);
    }

    @Override
    public void sendToClient(String message) {
      try {
        this.sendMessage(message);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    public void closeConnection() {
      try {
        incomingSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        inputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        outputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      activeConnections.remove(clientName);
      active = false;
    }

    @Override
    public Collection<String> activeUsers() {
      return activeConnections.keySet();
    }
  }

	public static void main(String[] args) {
    Server server = new Server();
    server.acceptConnections();
  }

  public void acceptConnections() {
    activeConnections = new HashMap<>();
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
      }
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  public void broadCastMessage(String message, String fromClient) {
    for (Map.Entry<String,ClientConnection> connection : activeConnections.entrySet()) {
      if (!connection.getKey().equals(fromClient)) {
        try {
          connection.getValue().sendMessage(message);
        } catch (IOException e) {
          e.printStackTrace();
          System.out.println("Error sending message to : " + fromClient);
        }
      }
    }
  }
}
