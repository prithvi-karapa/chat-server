package chat.server.actions;

public interface ChatAction {
  /**
   * Attenpt to perform the action
   *
   * @param actionString value for the action
   * @return true if this action was performed otherwise false
   */
  boolean attemptAction(String actionString, String clientName);
}
