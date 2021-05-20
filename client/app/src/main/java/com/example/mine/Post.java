package com.example.mine;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Post implements Serializable {
    private String collection;
    private String tag;
    private int avatar;
    private String nickname;
    private String timestamp;
    private String caption;
    private Object content;
    private int contentType;  //0 means image and 1 means text
    private int flower_num;
    private int comment_num;
    private int star_num;

    public Post(String collection, String tag, int avatar, String nickname, String timestamp, String caption, Object content,
                int flower_num, int comment_num, int star_num) {
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

    public Post(JSONObject obj) {
        try {
            this.collection = "粥粥的烹饪";
            StringBuilder builder = new StringBuilder();
            JSONArray tags = obj.getJSONArray("tags");
            for (int i = 0; i < tags.length(); i++) {
                builder.append(i == 0 ? "#" : " #");
                builder.append(tags.getString(i));
            }
            this.tag = builder.toString();
            this.avatar = R.drawable.flower;
            JSONObject author = obj.getJSONObject("author");
            this.nickname = author.getString("nickname");
            this.caption = obj.getString("caption");
            this.content = R.drawable.luoxiaohei;
            this.timestamp = DateUtils.getRelativeTimeSpanString(obj.getLong("timestamp") * 1000).toString();
            this.flower_num = obj.getInt("upvote_count");
            this.comment_num = obj.getInt("comment_count");
            this.star_num = obj.getInt("mark_count");
            this.contentType = obj.getInt("type");
        } catch (JSONException e) {
            android.util.Log.e("post", "Invalid JSON object " + e.toString());
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

    public int getComment_num() {
        return comment_num;
    }

    public int getFlower_num() {
        return flower_num;
    }

    public int getStar_num() {
        return star_num;
    }
}
