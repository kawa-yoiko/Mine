package com.example.mine;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

public class LoadingActivity extends AppCompatActivity {
    public enum DestType {
        post,
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_layout);

        Intent inIntent = getIntent();
        DestType destType = (DestType) inIntent.getSerializableExtra("type");
        Log.d("loading", destType.toString());
        switch (destType) {
            case post:
                ServerReq.getJson("/post/1", (JSONObject obj) -> {
                    Log.d("loading", obj.toString());
                    Intent intent = new Intent(this, PostActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME |
                            Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("post", new Post(obj));
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    this.finish();
                });
                break;
        }
    }
}