package chat.server.actions;

import chat.common.Message;
import chat.server.ClientConnectionForActions;

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
      Message exitMessage = new Message(clientName, Message.MessageType.EXIT, clientName + " has exited");
      this.clientConnectionForActions.broadCastMessage(exitMessage);
      return true;
    }
    return false;
  }
}
