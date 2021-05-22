package com.example.mine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Collection implements Serializable {
    public String authorName;
    public String authorAvatar;
    public String title;
    public String description;
    public ArrayList<PostBrief> posts;
    public String tags;

    public static class PostBrief implements Serializable {
        public int id;
        public int type;
        public String caption;
        public String contents;

        PostBrief(JSONObject obj) throws JSONException {
            this.id = obj.getInt("id");
            this.type = obj.getInt("type");
            this.caption = obj.getString("caption");
            if (obj.has("contents"))
                this.contents = obj.getString("contents");
        }
    }

    public Collection() {}

    public Collection(JSONObject obj) {
        try {
            JSONObject author = obj.getJSONObject("author");
            this.authorName = author.getString("nickname");
            this.authorAvatar = author.getString("avatar");
            this.title = obj.getString("title");
            if (this.title.equals(""))
                this.title = this.authorName + " 的未分类合集";
            this.description = obj.getString("description");
            JSONArray posts = obj.getJSONArray("posts");
            this.posts = new ArrayList<>();
            for (int i = 0; i < posts.length(); i++)
                this.posts.add(new PostBrief(posts.getJSONObject(i)));
            // TODO: reduce duplication
            StringBuilder builder = new StringBuilder();
            JSONArray tags = obj.getJSONArray("tags");
            for (int i = 0; i < tags.length(); i++) {
                builder.append(i == 0 ? "#" : " #");
                builder.append(tags.getString(i));
            }
            this.tags = builder.toString();
        } catch (JSONException e) {
            android.util.Log.e("Collection", "Invalid JSON object " + e.toString());
        }
    }
}
