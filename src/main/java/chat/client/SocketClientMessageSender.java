package chat.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import chat.common.Message;

public class SocketClientMessageSender implements ClientMessageSender{
  private int port;
  private String host;
  private ObjectOutputStream outputStream;
  private Socket serverSocket;

  public SocketClientMessageSender(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void sendClientNameToServer(String clientName) {
    try {
      Message connectionMessage = new Message(clientName, Message.MessageType.CONNECTION, clientName + " has entered the chat");
      outputStream.writeObject(connectionMessage);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void connectToServer() throws IOException {
    final InetAddress inetAddress = InetAddress.getByName(host);
    serverSocket = new Socket(inetAddress, port);
    ServerListener serverListener = new ServerListener(serverSocket, Thread.currentThread());
    Thread connectionThread = new Thread(serverListener);
    connectionThread.start();
    outputStream = new ObjectOutputStream(serverSocket.getOutputStream());
  }

  @Override
  public boolean isConnectionActive() {
    return !serverSocket.isClosed();
  }

  public void sendMessage(String clientName, String body, List<String> audience) throws IOException{

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
      message = new Message(clientName, Message.MessageType.MESSAGE, body, audience);
      outputStream.writeObject(message);
    }
  }

}
