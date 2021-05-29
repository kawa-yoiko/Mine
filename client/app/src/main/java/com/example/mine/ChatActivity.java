package com.example.mine;

import android.os.Bundle;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class ChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        recyclerView = findViewById(R.id.recyclerview);
        LinkedList<Chat> chats = new LinkedList<>();
        chats.add(new Chat("一只青蛙一张嘴，两只眼睛四条腿", R.drawable.luoxiaohei, "12:00", 0));
        chats.add(new Chat("两只青蛙两张嘴，四只眼睛八条腿", R.drawable.luoxiaohei2, "12:02", 1));
        chats.add(new Chat("四只青蛙四张嘴，八只眼睛十六条腿", R.drawable.luoxiaohei, "12:10", 0));
        chats.add(new Chat("中午去哪吃", R.drawable.luoxiaohei, "12:15", 0));

        recyclerView.setAdapter(new ChatAdapter(chats));
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
    }
}
