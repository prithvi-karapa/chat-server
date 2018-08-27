package chat.server.actions;

import chat.common.Message;
import chat.server.ClientConnectionForActions;

public class ConnectAction implements ChatAction {

  private ClientConnectionForActions clientConnectionForActions;

  public ConnectAction(ClientConnectionForActions clientConnectionForActions) {
    this.clientConnectionForActions = clientConnectionForActions;
  }

  @Override
  public boolean attemptAction(Message message) {
    if (Message.MessageType.CONNECTION.equals(message.getType())) {
      clientConnectionForActions.connect(message.getSender());
      clientConnectionForActions.broadCastMessage(message);
      return true;
    }
    return false;
  }
}
