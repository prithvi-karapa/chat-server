package chat.server.actions;

import chat.common.Message;
import chat.server.ClientConnectionForActions;

public class ActiveUsersAction implements ChatAction{
  private ClientConnectionForActions clientConnectionForActions;

  public ActiveUsersAction (ClientConnectionForActions clientConnectionForActions) {
    this.clientConnectionForActions = clientConnectionForActions;
  }

  @Override
  public void performAction(Message requestMessage) {
    if (Message.MessageType.COMMAND.equals(requestMessage.getType())) { //todo allow multiple types
      Message returnMessage = new Message("server", Message.MessageType.RESPONSE, clientConnectionForActions.activeUsers().toString(), requestMessage.getTimestamp());
      clientConnectionForActions.sendToClient(returnMessage);
      System.out.println(requestMessage.getSender() + " has requested active user list");
    }
  }
}
