package com.example.mine;

public class Notification {
    private int type; // 0--flower  1--comment 2--update(?)
    private String content;
    private String date;
    private int image;

    public Notification(int type, String content, String date, int image) {
        this.type = type;
        this.content = content;
        this.date = date;
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public int getImage() {
        return image;
    }

    public int getType() {
        return type;
    }
}
