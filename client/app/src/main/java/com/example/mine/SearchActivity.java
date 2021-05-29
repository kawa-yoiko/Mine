package com.example.mine;

import android.os.Bundle;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        recyclerView = findViewById(R.id.tag_recycleview);
        recyclerView.setAdapter(new SearchAdapter(searches));
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
    }
}
