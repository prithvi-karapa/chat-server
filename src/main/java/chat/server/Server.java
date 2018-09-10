package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import chat.common.Message;

public class Server{
	private int port = 4000;
  private Map<String, ClientConnection> activeConnections;

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

        ClientConnection connection = new ClientConnection(incomingSocket, this);
        Thread connectionThread = new Thread(connection);
        connectionThread.start();
      }
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  public void broadCastMessage(Message message) {
    for (Map.Entry<String,ClientConnection> connection : activeConnections.entrySet()) {
      if (!connection.getKey().equals(message.getSender()) && (message.getAudienceMembers().isEmpty() || message.getAudienceMembers().contains(connection.getKey()))) {
        try {
          connection.getValue().sendMessage(message);
        } catch (IOException e) {
          e.printStackTrace();
          System.out.println("Error sending message to : " + message.getSender());
        }
      }
    }
  }

  public Map<String, ClientConnection> getActiveConnections() {
    return activeConnections;
  }
}
