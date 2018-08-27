package chat.server;

import java.util.Collection;

import chat.common.Message;

public interface ClientConnectionForActions {
  void broadCastMessage(Message message);
  void sendToClient(Message message);
  void closeConnection();
  Collection<String> activeUsers();
}
