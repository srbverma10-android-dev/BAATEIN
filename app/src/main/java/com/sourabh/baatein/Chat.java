package com.sourabh.baatein;

public class Chat {

    public Chat() {
    }

    public Chat(String message_body, String seen, String sender, String receiver) {
        this.message_body = message_body;
        this.seen = seen;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getMessage_body() {
        return message_body;
    }

    public void setMessage_body(String message_body) {
        this.message_body = message_body;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String message_body, seen, sender, receiver;

}
