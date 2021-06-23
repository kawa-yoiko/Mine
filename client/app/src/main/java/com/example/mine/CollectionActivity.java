package com.example.mine;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class CollectionActivity extends AppCompatActivity {
    SquareFragment squareFragment;
    private Button subscribeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_collection);

        Collection collection = (Collection) getIntent().getSerializableExtra("collection");
        ((TextView) findViewById(R.id.title)).setText(collection.title);
        ((TextView) findViewById(R.id.description)).setText(collection.description);
        ((TextView) findViewById(R.id.tags)).setText(collection.tags);
        ((TextView) findViewById(R.id.nickname)).setText(collection.authorName);
        ServerReq.Utils.loadImage("/upload/" + collection.authorAvatar,
                (ImageView) findViewById(R.id.avatar));
        ((TextView) findViewById(R.id.count)).setText(
                String.format(Locale.CHINESE, "%d 篇作品", collection.posts.size())
        );
        if (!collection.cover.isEmpty()) {
            ServerReq.Utils.loadImage("/upload/" + collection.cover,
                    (ImageView) findViewById(R.id.cover));
        }

        // Reverse list
        int n = collection.posts.size();
        for (int i = 0; i < n / 2; i++) {
            Collection.PostBrief t = collection.posts.get(i);
            collection.posts.set(i, collection.posts.get(n - i - 1));
            collection.posts.set(n - i - 1, t);
        }

        squareFragment = new SquareFragment(this.findViewById(R.id.heading), collection.posts);
        FrameLayout layout = (FrameLayout)this.findViewById(R.id.squares_container);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.squares_container, squareFragment)
                .commit();

        ((Button) findViewById(R.id.order_button)).setOnClickListener(
                new View.OnClickListener() {
                    boolean asc = false;
                    @Override
                    public void onClick(View v) {
                        asc = !asc;
                        ((Button) v).setText(asc ? "正序" : "倒序");
                        squareFragment.reverse();
                    }
                });
        
        subscribeButton = (Button) findViewById(R.id.subscribe_button);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            boolean subscribed = collection.mySubscription;
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                subscribeButton.setEnabled(false);
                Handler handler = new Handler(Looper.getMainLooper());
                ServerReq.postJson("/collection/" + collection.id + "/subscribe",
                        List.of(new Pair<>("is_subscribe", subscribed ? "0" : "1")),
                        (JSONObject obj) -> handler.post(() -> {
                            subscribed = !subscribed;
                            updateSubscriptionStatus(subscribed);
                            subscribeButton.setEnabled(true);
                        }));
            }
        });

        updateSubscriptionStatus(collection.mySubscription);
    }

    private void updateSubscriptionStatus(boolean subscribed) {
        subscribeButton.setText(subscribed ? "已订阅" : "订阅");
        Drawable drawable = ContextCompat.getDrawable(getBaseContext(),
                subscribed ? R.drawable.exo_ic_check : R.drawable.mark);
        drawable.setBounds(0, 0, 42, 42);
        subscribeButton.setCompoundDrawables(drawable, null, null, null);
    }
}