package com.example.mine;

import android.text.format.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class Comment {
    public int id;
    private String avatar;
    private String nickname;
    private String content;
    private String date;
    private String replyNickname;
    public int replyNum;
    private int flowerNum;
    public boolean myUpvote;

    public Comment(String avatar, String nickname, String content, String date, String replyNickname, int flowerNum) {
        this.avatar = avatar;
        this.nickname = nickname;
        this.content = content;
        this.date = date;
        this.replyNickname = replyNickname;
        this.flowerNum = flowerNum;
    }

    public Comment(JSONObject obj) {
        try {
            this.id = obj.getInt("id");
            JSONObject author = obj.getJSONObject("author");
            this.nickname = author.getString("nickname");
            this.avatar = author.getString("avatar");
            this.content = obj.getString("contents");
            this.date = DateUtils.getRelativeTimeSpanString(obj.getLong("timestamp") * 1000).toString();
            if (!obj.isNull("reply_count")) {
                this.replyNum = obj.getInt("reply_count");
            }
            if (!obj.isNull("reply_user")) {
                JSONObject replyUser = obj.getJSONObject("reply_user");
                this.replyNickname = replyUser.getString("nickname");
            }
            this.flowerNum = obj.getInt("upvote_count");
            this.myUpvote = obj.getBoolean("my_upvote");
        } catch (JSONException e) {
            android.util.Log.e("Comment", "Invalid JSON object " + e.toString());
        }
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatar() {
        return avatar;
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

    public int getFlowerNum() {
        return flowerNum;
    }
}
