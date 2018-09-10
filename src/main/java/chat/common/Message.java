package chat.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Message implements Serializable{
  private String sender;
  private String body;
  private MessageType type;
  private long timestamp;
  private List<String> audienceMembers;

  public enum MessageType {
    CONNECTION,
    MESSAGE,
    COMMAND,
    RESPONSE,
    EXIT
  }

  public Message(String sender, MessageType type, String text) {
    this(sender, type, text, System.currentTimeMillis(), Collections.emptyList());
  }

  public Message(String sender, MessageType type, String text, long timestamp) {
    this(sender, type, text, timestamp, Collections.emptyList());
  }

  public Message(String sender, MessageType type, String text, List<String> audienceMembers) {
    this(sender, type, text, System.currentTimeMillis(), audienceMembers);
  }

  public Message(String sender, MessageType type, String text, long timestamp, List<String> audienceMembers) {
    this.sender = sender;
    this.type = type;
    this.body = text;
    this.timestamp = timestamp;
    this.audienceMembers = audienceMembers;
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

  public long getTimestamp() {
    return timestamp;
  }

  public List<String> getAudienceMembers() {
    return audienceMembers;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String toString() {
    if (type == MessageType.CONNECTION || type == MessageType.EXIT) {
      return "(" + type.name() + ") " + ": " + body;
    } else {
      return "(" + type.name() + ") " + sender + ": " + body;
    }
  }
}
