package com.example.dichvudatxeandroid;

public class Message {
    private String key;
    private String iv;
    private String encryptedMessage;

    public Message() {
    }

    public Message(String key, String iv, String encryptedMessage) {
        this.key = key;
        this.iv = iv;
        this.encryptedMessage = encryptedMessage;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getEncryptedMessage() {
        return encryptedMessage;
    }

    public void setEncryptedMessage(String encryptedMessage) {
        this.encryptedMessage = encryptedMessage;
    }
}

