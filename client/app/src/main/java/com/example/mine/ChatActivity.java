package com.example.mine;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.LinkedList;

public class ChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String otherNickname = getIntent().getStringExtra("username");
        String otherAvatar = getIntent().getStringExtra("other_avatar");

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        recyclerView = findViewById(R.id.recyclerview);
        LinkedList<Chat> chats = new LinkedList<>();

        ChatAdapter adapter = new ChatAdapter(otherAvatar, chats);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        Handler handler = new Handler(Looper.getMainLooper());
        ServerReq.getJsonArray("/message/with/" + otherNickname + "?start=0&count=50", (JSONArray arr) -> {
            try {
                for (int i = 0; i < arr.length(); i++)
                    chats.add(new Chat(arr.getJSONObject(i)));
                handler.post(adapter::notifyDataSetChanged);
            } catch (Exception e) {
                Log.e("ChatActivity", e.toString());
            }
        });
    }
}
