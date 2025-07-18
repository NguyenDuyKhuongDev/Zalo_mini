package Models;

import java.util.Date;

public class Message {
    private String senderId;
    private String sendAt;
    private String content;

    public String getSenderId() {
        return senderId;
    }

    public Message() {
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSendAt() {
        return sendAt;
    }

    public void setSendAt(String sendAt) {
        this.sendAt = sendAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Message(String senderId, String sendAt, String content) {
        this.senderId = senderId;
        this.sendAt = sendAt;
        this.content = content;
    }
}
