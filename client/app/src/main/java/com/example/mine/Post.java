package com.example.mine;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Post implements Serializable {
    public int id;
    private String collection;
    private int collectionID;
    private String tag;
    private String avatar;
    private String nickname;
    private String timestamp;
    private String caption;
    private String content;
    private int contentType; // 0 - text; 1 - image; 2 - audio; 3 - video
    private int flower_num;
    private int comment_num;
    private int star_num;

    public Post(String collection, String tag, String avatar, String nickname, String timestamp, String caption, String content, int contentType,
                int flower_num, int comment_num, int star_num) {
        this.collection = collection;
        this.tag = tag;
        this.avatar = avatar;
        this.nickname = nickname;
        this.caption = caption;
        this.content = content;
        this.contentType = contentType;
        this.timestamp = timestamp;
        this.flower_num = flower_num;
        this.comment_num = comment_num;
        this.star_num = star_num;
    }

    public Post(JSONObject obj) {
        try {
            StringBuilder builder = new StringBuilder();
            JSONArray tags = obj.getJSONArray("tags");
            for (int i = 0; i < tags.length(); i++) {
                builder.append(i == 0 ? "#" : " #");
                builder.append(tags.getString(i));
            }
            this.tag = builder.toString();
            JSONObject author = obj.getJSONObject("author");
            this.avatar = author.getString("avatar");
            this.nickname = author.getString("nickname");
            this.caption = obj.getString("caption");
            this.content = obj.getString("contents");
            this.contentType = obj.getInt("type");
            this.timestamp = DateUtils.getRelativeTimeSpanString(obj.getLong("timestamp") * 1000).toString();
            this.flower_num = obj.getInt("upvote_count");
            this.comment_num = obj.getInt("comment_count");
            this.star_num = obj.getInt("star_count");

            JSONObject collection = obj.getJSONObject("collection");
            this.collection = collection.getString("title");
            if (this.collection.equals("")) {
                this.collection = this.nickname + " 的未分类合集";
            }
            this.collectionID = collection.getInt("id");
        } catch (JSONException e) {
            android.util.Log.e("post", "Invalid JSON object " + e.toString());
        }
    }

    public String getCollection() {
        return collection;
    }

    public int getCollectionID() { return collectionID; }

    public String getTag() {
        return tag;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public String getCaption() {
        return caption;
    }

    public String getContent() {
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
