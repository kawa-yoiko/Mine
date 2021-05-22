package com.example.mine;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.util.function.BiConsumer;

public class LoadingActivity extends AppCompatActivity {
    public enum DestType {
        post,
        collection,
    }

    // XXX: Straightforward but works
    private boolean animationComplete = false;

    @Override
    public void onEnterAnimationComplete() {
        animationComplete = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void requestNetworkAndSetUpIntent(String url, Class<?> cls, BiConsumer<JSONObject, Intent> handler) {
        ServerReq.getJson(url, (JSONObject obj) -> {
            try {
                while (!animationComplete) Thread.sleep(5);
            } catch (Exception e) {
            }
            Log.d("loading", obj.toString());
            Intent intent = new Intent(this, cls);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME |
                    Intent.FLAG_ACTIVITY_NO_ANIMATION);
            handler.accept(obj, intent);
            startActivity(intent);
            overridePendingTransition(0, 0);
            this.finish();
        });
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
                requestNetworkAndSetUpIntent("/post/1",
                        PostActivity.class,
                        (JSONObject obj, Intent intent) -> {
                            intent.putExtra("post", new Post(obj));
                        });
                break;
            case collection:
                int id = inIntent.getIntExtra("id", -1);
                requestNetworkAndSetUpIntent("/collection/" + id,
                        CollectionActivity.class,
                        (JSONObject obj, Intent intent) -> {
                            Log.d("loading collection", (new Collection(obj)).toString());
                            intent.putExtra("collection", new Collection(obj));
                        });
        }
    }
}