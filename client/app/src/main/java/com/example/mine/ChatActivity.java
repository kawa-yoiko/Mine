package com.example.mine;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

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

        ((TextView) findViewById(R.id.nickname)).setText(otherNickname);

        recyclerView = findViewById(R.id.recyclerview);
        LinkedList<Chat> chats = new LinkedList<>();

        ChatAdapter adapter = new ChatAdapter(otherAvatar, chats);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        View btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.this.finish();
            }
        });

        Handler handler = new Handler(Looper.getMainLooper());
        ServerReq.getJsonArray("/message/with/" + otherNickname + "?start=0&count=50", (JSONArray arr) -> handler.post(() -> {
            try {
                for (int i = 0; i < arr.length(); i++)
                    chats.add(new Chat(arr.getJSONObject(i)));
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);
            } catch (Exception e) {
                Log.e("ChatActivity", e.toString());
            }
        }));
        ServerReq.post("/message/read/" + otherNickname, List.of(), (String s) -> {});

        EditText commentEdit = findViewById(R.id.comment_edit);
        Button commentButton = findViewById(R.id.comment_button);
        commentButton.setOnClickListener((View v) -> {
            commentEdit.setEnabled(false);
            commentButton.setEnabled(false);
            String contents = commentEdit.getText().toString();
            ServerReq.postJson("/message/send", List.of(
                    new Pair<>("to_user", otherNickname),
                    new Pair<>("contents", contents)
            ), (JSONObject obj) -> handler.post(() -> {
                try {
                    chats.addFirst(new Chat(
                            contents,
                            DateUtils.getRelativeTimeSpanString(obj.getLong("timestamp") * 1000).toString(),
                            1
                    ));
                    adapter.notifyItemInserted(0);
                    recyclerView.smoothScrollToPosition(0);
                    Log.d("ChatActivity", obj.toString() + " " + chats.size());
                    commentEdit.setText("");
                    commentEdit.setEnabled(true);
                    commentButton.setEnabled(true);
                } catch (Exception e) {
                    Log.e("ChatActivity", e.toString());
                }
            }));
        });
    }
}
