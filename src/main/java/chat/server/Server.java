package chat.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import chat.common.Message;
import chat.server.actions.ActiveUsersAction;
import chat.server.actions.ChatAction;
import chat.server.actions.ExitAction;

public class Server{
	private int port = 4000;
  private Map<String, ClientConnection> activeConnections;

  private final class ClientConnection implements Runnable, ClientConnectionForActions {
    private final Socket incomingSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private String clientName;
    private ChatAction[] actions = {new ExitAction(this), new ActiveUsersAction(this)};
    boolean active = true;

    private ClientConnection(Socket incomingSocket) {
      this.incomingSocket = incomingSocket;
      try {
        this.outputStream = new ObjectOutputStream(incomingSocket.getOutputStream());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void run() {
      try {
        inputStream = new ObjectInputStream(new BufferedInputStream(incomingSocket.getInputStream()));
        Message connectionMessage = (Message)inputStream.readObject();
        clientName = connectionMessage.getSender();
        activeConnections.put(clientName, this);
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

    public void sendMessage(String body) throws IOException {
      outputStream.writeObject(body);
    }

    public void broadCastMessage(Message message) {
      Server.this.broadCastMessage(message);
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

	public static void main(String[] args) throws Exception {
    Server server = new Server();
    server.gatherCommandlineArgs(args);
    server.acceptConnections();
  }

  public void gatherCommandlineArgs(String[] args) throws ParseException {
    Options options = new Options();
    options.addOption("p", "port", true, "port for the chat server");
    options.addOption("h", "help", false, "print this help message");

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse( options, args);

    if (cmd.hasOption("h")) {
      HelpFormatter helpFormatter = new HelpFormatter();
      helpFormatter.printHelp("Server", options);
      return;
    }

    if (cmd.hasOption("p")) {
      port = Integer.valueOf(cmd.getOptionValue("p"));
    }
  }

  public void acceptConnections() {
    activeConnections = new HashMap<>();
    try {
      //We need the ServerSocket to detect the incoming connections
      ServerSocket listeningServer = new ServerSocket(port);
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

  public void broadCastMessage(Message message) {
    for (Map.Entry<String,ClientConnection> connection : activeConnections.entrySet()) {
      if (!connection.getKey().equals(message.getSender())) {
        try {
          connection.getValue().sendMessage(message.getBody());
        } catch (IOException e) {
          e.printStackTrace();
          System.out.println("Error sending message to : " + message.getSender());
        }
      }
    }
  }
}
