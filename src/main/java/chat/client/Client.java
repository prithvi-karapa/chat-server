package chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import chat.common.Message;

public class Client {
  private static final String EXIT_MESSAGE = "exit";
  private static final String ACTIVE_USERS_MESSAGE = "who";

  private BufferedReader input;
  private String clientName;
  private ObjectOutputStream outputStream;
  private int port = 4000;
  private String host = "localhost";

  public void gatherCommandlineArgs(String[] args) throws ParseException {
    Options options = new Options();
    options.addOption("p", "port", true, "port for the chat server");
    options.addOption("s", "server", true, "server inet address");
    options.addOption("h", "help", false, "print this help message");

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse( options, args);

    if (cmd.hasOption("h")) {
      HelpFormatter helpFormatter = new HelpFormatter();
      helpFormatter.printHelp("Client", options);
      return;
    }

    if (cmd.hasOption("p")) {
      port = Integer.valueOf(cmd.getOptionValue("p"));
    }
    if (cmd.hasOption("s")) {
      host = cmd.getOptionValue("s");
    }
  }

  public void run() throws Exception {
    System.out.println("Starting up connecting to " + host + " port " + port);

    input = new BufferedReader(new InputStreamReader(System.in));

    assignClientName();
    final InetAddress SERVER_ADDRESS = InetAddress.getByName(host);
    Socket serverSocket = new Socket(SERVER_ADDRESS, port);
    ServerListener serverListener = new ServerListener(serverSocket, Thread.currentThread());
    Thread connectionThread = new Thread(serverListener);
    connectionThread.start();
    outputStream = new ObjectOutputStream(serverSocket.getOutputStream());
    sendClientNameToServer();
    while (!serverSocket.isClosed()) {
      String body = input.readLine();
      sendMessage(body, serverSocket, outputStream);
    }
  }

  private void assignClientName() throws IOException{
    System.out.println("Please enter your name: ");
    clientName = input.readLine();
  }

  private void sendClientNameToServer() {
    try {
      Message connectionMessage = new Message(clientName, Message.MessageType.CONNECTION, clientName + " has entered the chat");
      outputStream.writeObject(connectionMessage);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void sendMessage(String body, Socket serverSocket, ObjectOutputStream outputStream) throws IOException{

    Message message;
    if (EXIT_MESSAGE.equals(body)) {
      message = new Message(clientName, Message.MessageType.EXIT, clientName + " has exited");
      outputStream.writeObject(message);
      serverSocket.close();
    } else if (ACTIVE_USERS_MESSAGE.equals(body)) {
      message = new Message(clientName, Message.MessageType.COMMAND, "TODO figure out what to send");
      //TODO: Figure out the body of the message. Solution: Mapping 'COMMAND' to Action
      outputStream.writeObject(message);
    } else {
      message = new Message(clientName, Message.MessageType.MESSAGE, body);
      outputStream.writeObject(message);
    }
  }

	public static void main(String[] args) throws Exception {
    Client client = new Client();
	  client.gatherCommandlineArgs(args);
	  client.run();
	}
}
