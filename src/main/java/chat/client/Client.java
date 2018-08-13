package chat.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Client {
  private Scanner input;
  private String clientName;
  private DataOutputStream outputStream;
  private int port = 4000;
  private String host = "localhost";

  public void gatherCommandlineArgs(String[] args) throws ParseException {
    Options options = new Options();
    options.addOption("p", "port", true, "port for the chat server");
    options.addOption("s", "server", true, "server inet address");
    options.addOption("h", false, "print this help message");

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

    input = new Scanner(System.in); //TODO Switch to a BufferedReader instead of System.in
    assignClientName();
    final InetAddress SERVER_ADDRESS = InetAddress.getByName(host);
    Socket serverSocket = new Socket(SERVER_ADDRESS, port);
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

  private void assignClientName() {
    System.out.println("Please enter your name: ");
    clientName = input.nextLine();
  }

  private void sendClientNameToServer() {
    try {
      outputStream.writeUTF(clientName);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

	public static void main(String[] args) throws Exception {
	  Client client = new Client();
	  client.gatherCommandlineArgs(args);
	  client.run();
	}
}
