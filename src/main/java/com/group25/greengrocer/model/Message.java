package com.group25.greengrocer.model;

import java.sql.Timestamp;

public class Message {
    private int id;
    private int senderId;
    private String senderName; // For display convenience
    private String subject;
    private String content; // This maps to 'body' in DB
    private Timestamp sentTime; // This maps to 'created_at' in DB

    public Message(int id, int senderId, String senderName, String subject, String content, Timestamp sentTime) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.subject = subject;
        this.content = content;
        this.sentTime = sentTime;
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

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getSentTime() {
        return sentTime;
    }
}