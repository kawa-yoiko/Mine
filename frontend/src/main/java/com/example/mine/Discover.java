package com.example.mine;

public class Discover {
    private String caption;
    private String tag;
    private String fondNum;
    private int image;
    public Discover(String caption, String tag, String fondNum, int image) {
        this.caption = caption;
        this.tag = tag;
        this.fondNum = fondNum;
        this.image = image;
    }

    public String getCaption() {
        return caption;
    }

    public String getFondNum() {
        return fondNum;
    }

    public String getTag() {
        return tag;
    }

    public int getImage() {
        return image;
    }
}
