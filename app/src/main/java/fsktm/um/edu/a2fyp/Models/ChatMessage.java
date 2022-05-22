package fsktm.um.edu.a2fyp.Models;

import java.util.Date;

public class ChatMessage {
    public String senderId, receiverId, message, dateTime;
    public Date dateObject;
    public String conversationId, conversationName, conversationImg;

    public ChatMessage() {}

    public ChatMessage(String senderId, String receiverId, String message, String dateTime, Date dateObject, String conversationId, String conversationName, String conversationImg) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.dateTime = dateTime;
        this.dateObject = dateObject;
        this.conversationId = conversationId;
        this.conversationName = conversationName;
        this.conversationImg = conversationImg;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessage() {
        return message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public Date getDateObject() {
        return dateObject;
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getConversationName() {
        return conversationName;
    }

    public String getConversationImg() {
        return conversationImg;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setDateObject(Date dateObject) {
        this.dateObject = dateObject;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public void setConversationImg(String conversationImg) {
        this.conversationImg = conversationImg;
    }
}
