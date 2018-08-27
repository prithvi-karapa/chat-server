package chat.server.actions;

import chat.common.Message;
import chat.server.ClientConnectionForActions;

public class ExitAction implements ChatAction{
  private ClientConnectionForActions clientConnectionForActions;

  public ExitAction (ClientConnectionForActions clientConnectionForActions) {
    this.clientConnectionForActions = clientConnectionForActions;
  }

  @Override
  public boolean attemptAction(Message message) {
    if (Message.MessageType.EXIT.equals(message.getType())) {
      this.clientConnectionForActions.closeConnection();
      this.clientConnectionForActions.broadCastMessage(message);
      return true;
    }
    return false;
  }
}
