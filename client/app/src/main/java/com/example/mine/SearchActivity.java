package com.example.mine;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.LinkedList;

public class SearchActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: get search item
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        LinkedList<String> searches = new LinkedList<>();
        searches.add("狗粮");
        searches.add("摄影");
        searches.add("五条悟");
        SearchAdapter adapter = new SearchAdapter(searches);
        recyclerView = findViewById(R.id.tag_recycleview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        SearchView searchView = findViewById(R.id.search_box).findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            String queryText = "";

            @Override
            public boolean onQueryTextSubmit(String query) {
                searches.add(query);
                adapter.notifyDataSetChanged();
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextChange(String newText) {
                this.queryText = newText;
                Handler handler = new Handler();
                if (!newText.isEmpty()) {
                    ServerReq.getJsonArray("/search_tags?tag=" + newText, (JSONArray arr) -> {
                        if (!this.queryText.equals(newText)) return;
                        searches.clear();
                        try {
                            for (int i = 0; i < arr.length(); i++) {
                                searches.add(arr.getString(i));
                            }
                        } catch (Exception e) {
                            Log.e("SearchActivity", e.toString());
                        }
                        handler.post(adapter::notifyDataSetChanged);
                    });
                }
                return false;
            }
        });
    }
}
