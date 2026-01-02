package com.group25.greengrocer.model;

import java.sql.Timestamp;

/**
 * Represents a message sent from a customer to the owner in the greengrocer application.
 * Messages allow customers to communicate with the owner about issues, questions, or feedback.
 * 
 */
public class Message {
    private int id;
    
    private int senderId;
    
    private String senderName;
    
    private String subject;
    
    private String content;
    
    private Timestamp sentTime;

    /**
     * Constructs a new Message with the specified details.
     * 
     * @param id the unique identifier for the message
     * @param senderId the ID of the sender
     * @param senderName the name of the sender
     * @param subject the subject line
     * @param content the message body
     * @param sentTime the timestamp when sent
     */
    public Message(int id, int senderId, String senderName, String subject, String content, Timestamp sentTime) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.subject = subject;
        this.content = content;
        this.sentTime = sentTime;
    }

    /**
     * Gets the unique identifier of the message.
     * 
     * @return the message ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the ID of the user who sent the message.
     * 
     * @return the sender ID
     */
    public int getSenderId() {
        return senderId;
    }

    /**
     * Gets the name of the sender.
     * 
     * @return the sender name
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * Gets the subject line of the message.
     * 
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Gets the body content of the message.
     * 
     * @return the message content
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the timestamp when the message was sent.
     * 
     * @return the sent time
     */
    public Timestamp getSentTime() {
        return sentTime;
    }
}
