package chat.common;

import java.io.Serializable;

public class Message implements Serializable{
  private String sender;
  private String body;
  private MessageType type;
  private long timestamp;
  public enum MessageType {
    CONNECTION,
    MESSAGE,
    COMMAND,
    EXIT
  }

  public Message(String sender, MessageType type, String text) {
    this.sender = sender;
    this.type = type;
    this.body = text;
    this.timestamp = System.currentTimeMillis();
  }

  public String getSender() {
    return sender;
  }

  public String getBody() {
    return body;
  }

  public MessageType getType() {
    return type;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String toString() {
    if (type == MessageType.CONNECTION) {
      return "(" + type.name() + ") " + ": " + body;
    } else {
      return "(" + type.name() + ") " + sender + ": " + body;
    }
  }
}
