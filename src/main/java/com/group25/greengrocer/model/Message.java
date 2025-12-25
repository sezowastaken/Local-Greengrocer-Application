package com.group25.greengrocer.model;

import java.sql.Timestamp;

public class Message {
    private int id;
    private int senderId; // Customer ID
    private String senderName; // For display convenience
    private String content;
    private Timestamp sentTime;
    private String reply;
    private Timestamp replyTime;

    public Message(int id, int senderId, String senderName, String content, Timestamp sentTime, String reply,
            Timestamp replyTime) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.sentTime = sentTime;
        this.reply = reply;
        this.replyTime = replyTime;
    }

    public int getId() {
        return id;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getSentTime() {
        return sentTime;
    }

    public String getReply() {
        return reply;
    }

    public Timestamp getReplyTime() {
        return replyTime;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public void setReplyTime(Timestamp replyTime) {
        this.replyTime = replyTime;
    }
}