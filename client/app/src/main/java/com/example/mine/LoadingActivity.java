package com.example.mine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class LoadingActivity extends AppCompatActivity {
    public enum DestType {
        post,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_layout);

        Intent inIntent = getIntent();
        DestType destType = (DestType)inIntent.getSerializableExtra("type");
        Log.d("loading", destType.toString());
        switch (destType) {
            case post:
                ServerReq.get("/post/1", (String s) -> {
                    Log.d("loading", s);
                    Intent intent = new Intent(this, PostActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME |
                            Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    this.finish();
                });
                break;
        }
    }
}