package chat.server.actions;

import chat.common.Message;
import chat.server.ClientConnectionForActions;

public class ActiveUsersAction implements ChatAction{
  private ClientConnectionForActions clientConnectionForActions;

  public ActiveUsersAction (ClientConnectionForActions clientConnectionForActions) {
    this.clientConnectionForActions = clientConnectionForActions;
  }

  @Override
  public void performAction(Message message) {
    if (Message.MessageType.COMMAND.equals(message.getType())) { //todo allow multiple types
      message.setBody(clientConnectionForActions.activeUsers().toString()); //TODO construct new message object in every action
      clientConnectionForActions.sendToClient(message);
      System.out.println(message.getSender() + " has requested active user list");
    }
  }
}
