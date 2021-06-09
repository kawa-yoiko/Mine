package com.example.mine;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    private ViewGroup fView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_post);
        Post post = (Post) getIntent().getSerializableExtra("post");
        View postView = getPostView(post);
        fView = findViewById(R.id.post_content);
        fView.addView(postView);
        CommentFragment commentFragment = new CommentFragment(post.id, findViewById(R.id.post_content_heading));
        getSupportFragmentManager().beginTransaction().replace(R.id.post_comment, commentFragment).commit();
        TextView comment_num_text = findViewById(R.id.comment_num);
        comment_num_text.setText(String.valueOf(post.getComment_num()));

        ImageView flowerIcon = findViewById(R.id.flower_icon);
        ImageView commentButton = findViewById(R.id.comment_button);
        ImageView starIcon = findViewById(R.id.star_icon);
        flowerIcon.setColorFilter(Color.parseColor("#FBBABA"));
        commentButton.setColorFilter(Color.parseColor("#A2E0FF"));
        starIcon.setColorFilter(Color.parseColor("#F4DB35"));

        // Flower button
        ToggleReqButton toggleFlower = new ToggleReqButton(
                findViewById(R.id.flower_button),
                findViewById(R.id.flower_icon),
                findViewById(R.id.flower_num),
                R.drawable.flower_monochrome, R.drawable.flower, R.drawable.flower_semi,
                "/post/" + post.id + "/upvote",
                "upvote");
        toggleFlower.setState(post.myUpvote ? 1 : 0, post.getFlower_num());

        // Star button
        ToggleReqButton toggleStar = new ToggleReqButton(
                findViewById(R.id.star_button),
                findViewById(R.id.star_icon),
                findViewById(R.id.star_num),
                R.drawable.star_monochrome, R.drawable.star, R.drawable.star_semi,
                "/post/" + post.id + "/star",
                "star");
        toggleStar.setState(post.myStar ? 1 : 0, post.getStar_num());

        EditText commentText = (EditText) findViewById(R.id.comment_edit);
//        Button commentButton = (Button) findViewById(R.id.comment_button);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                commentButton.setImageResource(R.drawable.star);
                commentText.setEnabled(false);
                ServerReq.postJson("/post/" + post.id + "/comment/new", List.of(
                        new Pair<>("reply_to", "-1"),
                        new Pair<>("contents", commentText.getText().toString())
                ), (JSONObject obj) -> {
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                    }
                    PostActivity.this.runOnUiThread(() -> {
                        commentButton.setImageResource(R.drawable.flower);
                        commentText.setEnabled(true);
                    });
                    int commentId = -1;
                    try {
                        commentId = obj.getInt("id");
                    } catch (Exception e) {
                    }
                    if (commentId == -1)
                        return;
                    PostActivity.this.runOnUiThread(() -> {
                        commentText.setText("");
                        commentFragment.refresh();
                    });
                });
            }
        });
    }

    private View getPostView(Post post) {
        View postView = getLayoutInflater().inflate(R.layout.post_content, null);

        TextView collection_text = postView.findViewById(R.id.collection);
        collection_text.setText(post.getCollection());
        TextView tag_text = postView.findViewById(R.id.tag);
        tag_text.setText(post.getTag());
        ImageView avatar_image = postView.findViewById(R.id.avatar);
        ServerReq.Utils.loadImage("/upload/" + post.getAvatar(), avatar_image);
        TextView nickname_text = postView.findViewById(R.id.nickname);
        nickname_text.setText(post.getNickname());
        TextView timestamp_text = postView.findViewById(R.id.timestamp);
        timestamp_text.setText(post.getTimestamp());
        TextView caption_text = postView.findViewById(R.id.caption);

        ImageView bookIcon = postView.findViewById(R.id.book_icon);
        bookIcon.setColorFilter(Color.parseColor("#FFFCE6"));

        caption_text.setText(post.getCaption());
        switch (post.getContentType()) {
            default:
            case 0:
                FrameLayout container = postView.findViewById(R.id.content);
                TextView text = new TextView(container.getContext());
                text.setText(post.getContent());
                text.setTextSize(16);
                container.addView(text);
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        new PhotoViewPagerFragment(new ArrayList<>(Arrays.asList(post.getContent().split(" "))))).commit();
                break;
            case 3:
                getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        new VideoPlayerFragment(Uri.parse(ServerReq.getUploadFullUrl(post.getContent())))).commit();
        }

        collection_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.util.Log.d("PostActivity", String.valueOf(post.getCollectionID()));
                Intent intent = new Intent();
                intent.setClass(v.getContext(), LoadingActivity.class);
                intent.putExtra("type", LoadingActivity.DestType.collection);
                intent.putExtra("id", post.getCollectionID());
                v.getContext().startActivity(intent);
            }
        });

        return postView;
    }
}
