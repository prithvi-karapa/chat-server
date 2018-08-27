package chat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import chat.common.Message;
import chat.server.actions.ActiveUsersAction;
import chat.server.actions.ChatAction;
import chat.server.actions.ExitAction;

public final class ClientConnection implements Runnable, ClientConnectionForActions {
  private final Socket incomingSocket;
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;
  private String clientName;
  private ChatAction[] actions = {new ExitAction(this), new ActiveUsersAction(this)};
  private Server server;
  boolean active = true;

  public ClientConnection(Socket incomingSocket, Server server) {
    this.incomingSocket = incomingSocket;
    this.server = server;
    try {
      this.outputStream = new ObjectOutputStream(incomingSocket.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    try {
      inputStream = new ObjectInputStream(incomingSocket.getInputStream());
      Message connectionMessage = (Message)inputStream.readObject();
      clientName = connectionMessage.getSender();
      server.getActiveConnections().put(clientName, this);
      broadCastMessage(connectionMessage);
      while (active) {
        Message message = (Message)inputStream.readObject();
        String body = message.getBody();
        List<ChatAction> performedActions = Arrays.stream(actions).filter(action -> action.attemptAction(body, clientName)) //todo get this working with actions
            .collect(Collectors.toList());
        if (!performedActions.isEmpty()) {
          performedActions.forEach(action -> System.out.println(clientName + " performed Action " + action.getClass()));
        } else {
          System.out.println(clientName + ": " + body);
          broadCastMessage(message);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void sendMessage(Message message) throws IOException {
    outputStream.writeObject(message);
  }

  public void broadCastMessage(Message message) {
    server.broadCastMessage(message);
  }

  @Override
  public void sendToClient(Message message) {
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
    server.getActiveConnections().remove(clientName);
    active = false;
  }

  @Override
  public Collection<String> activeUsers() {
    return server.getActiveConnections().keySet();
  }
}