package chat.server;

import java.util.Collection;

public interface ClientConnectionForActions {
  void broadCastMessage(String message, String fromClient);
  void sendToClient(String message);
  void closeConnection();
  Collection<String> activeUsers();
}
