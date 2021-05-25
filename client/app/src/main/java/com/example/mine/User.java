package com.example.mine;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class User {
    public String nickname;
    public String avatar;
    public String signature;
    public ArrayList<CollectionBrief> collections;

    public User(JSONObject obj) {
        try {
            nickname = obj.getString("nickname");
            avatar = obj.getString("avatar");
            signature = obj.getString("signature");
            JSONArray collectionsArr = obj.getJSONArray("collections");
            collections = new ArrayList<>();
            for (int i = 0; i < collectionsArr.length(); i++) {
                CollectionBrief collection = new CollectionBrief(collectionsArr.getJSONObject(i));
                if (collection.title.equals(""))
                    collection.title = this.nickname + " 的未分类合集";
                collections.add(collection);
            }
        } catch (JSONException e) {
            Log.e("User", e.toString());
        }
    }

    public static class CollectionBrief {
        public int id;
        public String title;
        public int postCount;

        public CollectionBrief(JSONObject obj) {
            try {
                id = obj.getInt("id");
                title = obj.getString("title");
                postCount = 1;
            } catch (JSONException e) {
                Log.e("CollectionBrief", e.toString());
            }
        }
    }
}
