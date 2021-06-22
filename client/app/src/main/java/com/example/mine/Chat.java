package com.example.mine;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONObject;

public class    Chat {
    private String content;
    private String date;
    private int isUserOwn; // 0-- false 1-- true(is user own)

    public Chat(String content, String date, int isUserOwn) {
        this.content = content;
        this.date = date;
        this.isUserOwn = isUserOwn;
    }

    public Chat(JSONObject obj) {
        try {
            this.content = obj.getString("contents");
            this.date = DateUtils.getRelativeTimeSpanString(obj.getLong("timestamp") * 1000).toString();
            this.isUserOwn = (obj.getBoolean("from_me") ? 1 : 0);
        } catch (Exception e) {
            Log.e("Chat", e.toString());
        }
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public int getIsUserOwn() {
        return isUserOwn;
    }
}
