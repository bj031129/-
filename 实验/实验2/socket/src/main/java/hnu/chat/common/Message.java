package hnu.chat.common;

import java.util.Date;

public class Message {
    private String sender;
    private String content;
    private Date timestamp;
    private MessageType type;
    private byte[] imageData;
    private String fileName;

    public enum MessageType {
        TEXT,
        IMAGE,
        FILE,
        AUDIO,
        VIDEO,
        SYSTEM
    }

    public Message(String sender, String content, MessageType type) {
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.timestamp = new Date();
    }

    // Getters and setters
    public String getSender() { return sender; }
    public String getContent() { return content; }
    public Date getTimestamp() { return timestamp; }
    public MessageType getType() { return type; }
    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] imageData) { this.imageData = imageData; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public boolean isSystemMessage() {
        return type == MessageType.SYSTEM;
    }

    public boolean isImageMessage() {
        return type == MessageType.IMAGE;
    }
} 