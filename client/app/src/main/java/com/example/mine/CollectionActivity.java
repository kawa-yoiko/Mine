package com.example.mine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class CollectionActivity extends AppCompatActivity {
    SquareFragment squareFragment;

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
    }
}