package chat.server.actions;

import chat.common.Message;
import chat.server.ClientConnectionForActions;

public class BroadcastAction implements ChatAction{
  private ClientConnectionForActions clientConnectionForActions;

  public BroadcastAction(ClientConnectionForActions clientConnectionForActions) {
    this.clientConnectionForActions = clientConnectionForActions;
  }

  @Override
  public boolean attemptAction(Message message) {
    if (!Message.MessageType.COMMAND.equals(message.getType())) {
      this.clientConnectionForActions.broadCastMessage(message);
    }
    return false;
  }
}
