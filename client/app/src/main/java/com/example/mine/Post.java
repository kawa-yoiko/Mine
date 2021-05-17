package com.example.mine;

public class Post {
    private String collection;
    private String tag;
    private int avatar;
    private String nickname;
    private String timestamp;
    private String caption;
    private Object content;
    private int contentType;  //0 means image and 1 means text
    private String flower_num;
    private String comment_num;
    private String star_num;

    public Post(String collection, String tag, int avatar, String nickname, String timestamp, String caption, Object content,
                String flower_num, String comment_num, String star_num) {
        this.collection = collection;
        this.tag = tag;
        this.avatar = avatar;
        this.nickname = nickname;
        this.caption = caption;
        this.content = content;
        this.timestamp = timestamp;
        this.flower_num = flower_num;
        this.comment_num = comment_num;
        this.star_num = star_num;
        if (content instanceof String) {
            this.contentType = 1;
        }
        else {
            this.contentType = 0;
        }
    }

    public String getCollection() {
        return collection;
    }

    public String getTag() {
        return tag;
    }

    public int getAvatar() {
        return avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public String getCaption() {
        return caption;
    }

    public Object getContent() {
        return content;
    }

    public int getContentType() {
        return contentType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getComment_num() {
        return comment_num;
    }

    public String getFlower_num() {
        return flower_num;
    }

    public String getStar_num() {
        return star_num;
    }
}
