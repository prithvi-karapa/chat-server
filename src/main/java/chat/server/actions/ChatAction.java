package chat.server.actions;

import chat.common.Message;

public interface ChatAction {
  /**
   * Attenpt to perform the action
   *
   * @param actionString value for the action
   */
  void performAction(Message message);
}
