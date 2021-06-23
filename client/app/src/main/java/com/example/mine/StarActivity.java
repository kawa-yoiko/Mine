package com.example.mine;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class StarActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_star);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(recyclerView.getContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);

        LinkedList<SquareFragment.Item> items = new LinkedList<>();
        SquareAdapter adapter = new SquareAdapter(items, 0);
        recyclerView.setAdapter(adapter);

        ServerReq.getJsonArray("/star_timeline?start=0&count=100", (JSONArray arr) -> {
            Log.d("StarActivity", arr.toString());
            LinkedList<Collection.PostBrief> posts = new LinkedList<>();
            for (int i = 0; i < arr.length(); i++) {
                try {
                    posts.add(new Collection.PostBrief(arr.getJSONObject(i).getJSONObject("post")));
                } catch (JSONException e) {
                    Log.e("StarActivity", e.toString());
                }
            }
            // TODO: Reduce duplication
            items.add(new SquareFragment.DateItem("2021 年 6 月"));
            // XXX: For demonstration purpose
            int i = 0;
            for (Collection.PostBrief post : posts) {
                if (++i == 5) {
                    items.add(new SquareFragment.DateItem("2021 年 5 月"));
                }
                if (post.type == 0) {
                    items.add(new SquareFragment.TextItem(post.id, post.caption, post.contents));
                } else if (post.type == 1) {
                    items.add(new SquareFragment.ImageItem(post.id, post.contents));
                }
            }
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        });
    }
}