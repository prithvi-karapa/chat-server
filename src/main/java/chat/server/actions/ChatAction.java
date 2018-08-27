package chat.server.actions;

import chat.common.Message;

public interface ChatAction {
  /**
   * Attenpt to perform the action
   *
   * @param actionString value for the action
   * @return true if this action was performed otherwise false
   */
  boolean attemptAction(Message message);
}
