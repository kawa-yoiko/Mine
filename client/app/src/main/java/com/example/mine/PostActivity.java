package com.example.mine;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    private ViewGroup fView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Post post = (Post) getIntent().getSerializableExtra("post");
        View postView = getPostView(post);
        fView = findViewById(R.id.post_content);
        fView.addView(postView);
        Fragment commentFragment = new CommentFragment(post.id, findViewById(R.id.post_content_heading));
        getSupportFragmentManager().beginTransaction().replace(R.id.post_comment, commentFragment).commit();
        TextView flower_num_text = findViewById(R.id.flower_num);
        flower_num_text.setText(String.valueOf(post.getFlower_num()));
        TextView comment_num_text = findViewById(R.id.comment_num);
        comment_num_text.setText(String.valueOf(post.getComment_num()));
        TextView star_num_text = findViewById(R.id.star_num);
        star_num_text.setText(String.valueOf(post.getStar_num()));

        EditText commentText = (EditText) findViewById(R.id.comment_edit);
        Button commentButton = (Button) findViewById(R.id.comment_button);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                commentButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star, 0, 0, 0);
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
                        commentButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flower, 0, 0, 0);
                        commentText.setEnabled(true);
                    });
                    int commentId = -1;
                    try {
                        commentId = obj.getInt("id");
                    } catch (Exception e) {
                    }
                    if (commentId == -1)
                        return;
                    PostActivity.this.runOnUiThread(() -> commentText.setText(""));
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
        caption_text.setText(post.getCaption());
        switch (post.getContentType()) {
            default:
            case 0:
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        new PhotoViewPagerFragment(Arrays.asList(post.getContent().split(" ")))).commit();
//                ImageView content_image = postView.findViewById(R.id.content);
//                ServerReq.Utils.loadImage("/upload/" + post.getContent().split(" ")[0], content_image);
                break;
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
