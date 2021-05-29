package com.example.mine;

public class    Chat {
    private String content;
    private int avatar;
    private String date;
    private int isUserOwn; // 0-- false 1-- true(is user own)

    public Chat(String content, int avatar, String date, int isUserOwn) {
        this.content = content;
        this.avatar = avatar;
        this.date = date;
        this.isUserOwn = isUserOwn;
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

    public int getIsUserOwn() {
        return isUserOwn;
    }
}
