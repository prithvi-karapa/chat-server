package chat.client;

import java.io.IOException;
import java.util.List;

public interface ClientMessageSender {
  String EXIT_MESSAGE = "exit";
  String ACTIVE_USERS_MESSAGE = "who";

  void connectToServer() throws IOException;

  void sendMessage(String clientName, String body, List<String> audience) throws
      IOException;

  void sendClientNameToServer(String clientName);

  boolean isConnectionActive();
}
