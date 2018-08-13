package chat.server.actions;

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
      clientConnectionForActions.sendToClient(String.join(", ", clientConnectionForActions.activeUsers()));
      return true;
    }
    return false;
  }
}
