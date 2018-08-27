package chat.server.actions;

import chat.common.Message;
import chat.server.ClientConnectionForActions;

public class ActiveUsersAction implements ChatAction{
  private static final String ACTIVE_USER_MESSAGE = "who";
  private ClientConnectionForActions clientConnectionForActions;

  public ActiveUsersAction (ClientConnectionForActions clientConnectionForActions) {
    this.clientConnectionForActions = clientConnectionForActions;
  }

  @Override
  public boolean attemptAction(String actionString, String clientName) {
    if (ACTIVE_USER_MESSAGE.equals(actionString)) {
      Message activeUsersMessage = new Message("server", Message.MessageType.COMMAND, String.join(", ", clientConnectionForActions.activeUsers()));
      clientConnectionForActions.sendToClient(activeUsersMessage);
      return true;
    }
    return false;
  }
}
