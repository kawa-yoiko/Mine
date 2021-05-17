package com.example.mine;

public class Comment {
    private int isChild; // 0 indicates comment and 1 indicates child comment
    private int avatar;
    private String nickname;
    private String content;
    private String date;
    private String replyNickname;
    private String flowerNum;

    public Comment(int avatar, String nickname, String content, String date, String replyNickname, String flowerNum) {
        this.avatar = avatar;
        this.nickname = nickname;
        this.content = content;
        this.date = date;
        this.replyNickname = replyNickname;
        this.flowerNum = flowerNum;
    }

    public String getNickname() {
        return nickname;
    }

    public int getAvatar() {
        return avatar;
    }

    public int getIsChild() {
        return isChild;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getReplyNickname() {
        return replyNickname;
    }

    public String getFlowerNum() {
        return flowerNum;
    }
}
