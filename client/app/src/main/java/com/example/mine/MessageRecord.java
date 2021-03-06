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
    private int messageId;

    public MessageRecord(String username, String content, String messageNum, String date, String avatar) {
        this.username = username;
        this.content = content;
        this.messageNum = messageNum;
        this.date = date;
        this.avatar = avatar;
        this.messageId = -1;
    }

    public MessageRecord(JSONObject obj) {
        try {
            this.username = obj.getJSONObject("from_user").getString("nickname");
            if (!this.username.equals(ServerReq.getMyNickname())) {
                this.avatar = obj.getJSONObject("from_user").getString("avatar");
            } else {
                this.username = obj.getJSONObject("to_user").getString("nickname");
                this.avatar = obj.getJSONObject("to_user").getString("avatar");
            }
            this.content = obj.getString("contents");
            this.messageNum = String.valueOf(obj.getInt("unread_count"));
            this.date = DateUtils.getRelativeTimeSpanString(obj.getLong("timestamp") * 1000).toString();
            this.messageId = obj.getInt("id");
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

    public int getMessageId() {
        return messageId;
    }
}
