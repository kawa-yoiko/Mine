package com.example.mine;

public class MessageRecord {
    private String username;
    private String content;
    private String messageNum;
    private String date;
    private int avatar;   //TODO: String

    public MessageRecord(String username, String content, String messageNum, String date, int avatar) {
        this.username = username;
        this.content = content;
        this.messageNum = messageNum;
        this.date = date;
        this.avatar = avatar;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public int getAvatar() {
        return avatar;
    }

    public String getMessageNum() {
        return messageNum;
    }

    public String getUsername() {
        return username;
    }
}
