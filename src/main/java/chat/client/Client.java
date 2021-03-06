package chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import chat.common.Message;

public class Client {
  private static final String AUDIENCE_COMMAND = "audience";

  private BufferedReader input;
  private String clientName;
  private int port = 4000;
  private String host = "localhost";
  private List<String> audience = new ArrayList<>();

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
    ClientMessageSender messageSender = new SocketClientMessageSender(host, port);
    messageSender.connectToServer();
    messageSender.sendClientNameToServer(clientName);
    while (messageSender.isConnectionActive()) {
      String body = input.readLine();
      if (!setAudienceCommand(body)) {
        messageSender.sendMessage(clientName, body, this.audience);
      }
    }
  }

  /**
   * @return trye if this is an audience command
   */
  private boolean setAudienceCommand(String command) {
    String[] parts = command.split(":");
    boolean isAudienceCommand = AUDIENCE_COMMAND.equals(parts[0]);
    if (isAudienceCommand) {
      if (parts.length > 1 && isAudienceCommand) {
        String[] audienceMembers = parts[1].split(",");
        audience = Arrays.stream(audienceMembers).collect(Collectors.toList());
        return true;
      } else {
        audience = Collections.emptyList();
      }
    }
    return isAudienceCommand;
  }

  private void assignClientName() throws IOException{
    System.out.println("Please enter your name: ");
    clientName = input.readLine();
  }

	public static void main(String[] args) throws Exception {
    Client client = new Client();
	  client.gatherCommandlineArgs(args);
	  client.run();
	}
}
