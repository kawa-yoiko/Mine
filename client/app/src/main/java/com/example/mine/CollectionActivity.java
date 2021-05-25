package com.example.mine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class CollectionActivity extends AppCompatActivity {
    Fragment squareFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        squareFragment = new SquareFragment(this.findViewById(R.id.heading), collection.posts);
        FrameLayout layout = (FrameLayout)this.findViewById(R.id.squares_container);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.squares_container, squareFragment)
                .commit();
    }
}