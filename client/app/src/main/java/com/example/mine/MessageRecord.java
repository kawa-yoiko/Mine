package com.example.mine;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONObject;

public class MessageRecord {
    private String username;
    private String content;
    private String messageNum;
    private String date;
    private String avatar;

    public MessageRecord(String username, String content, String messageNum, String date, String avatar) {
        this.username = username;
        this.content = content;
        this.messageNum = messageNum;
        this.date = date;
        this.avatar = avatar;
    }

    public MessageRecord(JSONObject obj) {
        try {
            this.username = obj.getJSONObject("from_user").getString("nickname");
            this.content = obj.getString("contents");
            this.messageNum = String.valueOf(obj.getInt("unread_count"));
            this.date = DateUtils.getRelativeTimeSpanString(obj.getLong("timestamp") * 1000).toString();
            this.avatar = obj.getJSONObject("from_user").getString("avatar");
        } catch (Exception e) {
            Log.e("MessageRecord", e.toString());
        }
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getMessageNum() {
        return messageNum;
    }

    public String getUsername() {
        return username;
    }
}
