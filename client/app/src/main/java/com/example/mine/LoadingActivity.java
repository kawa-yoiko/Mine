package com.example.mine;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

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
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading_layout);

        Intent inIntent = getIntent();
        DestType destType = (DestType) inIntent.getSerializableExtra("type");
        Log.d("loading", destType.toString());
        int id = inIntent.getIntExtra("id", -1);
        switch (destType) {
            case post:
                requestNetworkAndSetUpIntent("/post/" + id,
                        PostActivity.class,
                        (JSONObject obj, Intent intent) -> {
                            Post post = new Post(obj);
                            post.id = id;
                            intent.putExtra("post", post);
                        });
                break;
            case collection:
                requestNetworkAndSetUpIntent("/collection/" + id,
                        CollectionActivity.class,
                        (JSONObject obj, Intent intent) -> {
                            intent.putExtra("collection", new Collection(obj));
                        });
        }
    }
}