package chat.actions;

import chat.ClientConnectionForActions;

public class ExitAction implements ChatAction{
  private static final String EXIT_MESSAGE = "exit";
  private ClientConnectionForActions clientConnectionForActions;

  public ExitAction (ClientConnectionForActions clientConnectionForActions) {
    this.clientConnectionForActions = clientConnectionForActions;
  }

  @Override
  public boolean attemptAction(String actionString, String clientName) {
    if (actionString.equals(EXIT_MESSAGE)) {
      this.clientConnectionForActions.closeConnection();
      this.clientConnectionForActions.broadCastMessage(clientName + " has exited", clientName);
      return true;
    }
    return false;
  }
}
