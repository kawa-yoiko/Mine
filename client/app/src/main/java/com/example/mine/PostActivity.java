package com.example.mine;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    private ViewGroup fView;

    private Post post;
    private int commentCount;
    private TextView commentCountText;

    private Comment replyComment = null;
    private View bottomToolbar;
    private EditText commentText;

    private LinkedList<Comment> hotComments;
    private CommentAdapter hotCommentsAdapter;
    private View hotCommentContainer;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_post);
        post = (Post) getIntent().getSerializableExtra("post");
        View postView = getPostView(post);
        fView = findViewById(R.id.post_content);
        fView.addView(postView);

        CommentFragment commentFragment = new CommentFragment(post.id,
                findViewById(R.id.post_content_heading), this::setReplyComment);
        getSupportFragmentManager().beginTransaction().replace(R.id.post_comment, commentFragment).commit();
        commentCount = post.getComment_num();
        commentCountText = (TextView) PostActivity.this.findViewById(R.id.comment_num);
        commentCountText.setText("(" + commentCount + ")");

        // Hot comments section
        hotComments = new LinkedList<>();
        hotCommentsAdapter = new CommentAdapter(post.id, hotComments, this::setReplyComment);
        RecyclerView hotCommentsView = findViewById(R.id.hot_comment_list);
        hotCommentsView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        hotCommentsView.setAdapter(hotCommentsAdapter);
        hotCommentContainer = findViewById(R.id.hot_comment_container);
        refreshHotComments();

        ImageView flowerIcon = findViewById(R.id.flower_icon);
        ImageView commentButton = findViewById(R.id.comment_button);
        ImageView starIcon = findViewById(R.id.star_icon);
        commentButton.setColorFilter(Color.parseColor("#A2E0FF"));

        // Flower button
        ToggleReqButton toggleFlower = new ToggleReqButton(
                findViewById(R.id.flower_button),
                findViewById(R.id.flower_icon),
                findViewById(R.id.flower_num),
                R.drawable.flower_monochrome, R.drawable.flower,
                Color.parseColor("#FBBABA"),
                "/post/" + post.id + "/upvote",
                "upvote");
        toggleFlower.setState(post.myUpvote ? 1 : 0, post.getFlower_num());

        // Star button
        ToggleReqButton toggleStar = new ToggleReqButton(
                findViewById(R.id.star_button),
                findViewById(R.id.star_icon),
                findViewById(R.id.star_num),
                R.drawable.star_monochrome, R.drawable.star,
                Color.parseColor("#F4DB35"),
                "/post/" + post.id + "/star",
                "star");
        toggleStar.setState(post.myStar ? 1 : 0, post.getStar_num());

        commentText = (EditText) findViewById(R.id.comment_edit);
//        Button commentButton = (Button) findViewById(R.id.comment_button);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                commentButton.setImageResource(R.drawable.star);
                commentText.setEnabled(false);
                Log.d("PA", "posting");
                ServerReq.postJson("/post/" + post.id + "/comment/new", List.of(
                        new Pair<>("reply_to", String.valueOf(replyComment == null ? -1 : replyComment.id)),
                        new Pair<>("contents", commentText.getText().toString())
                ), (JSONObject obj) -> {
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                    }
                    PostActivity.this.runOnUiThread(() -> {
                        commentButton.setImageResource(R.drawable.send);
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
                        commentText.setHint("");
                        replyComment = null;
                        commentText.setText("");
                        refreshHotComments();
                        commentFragment.refresh();
                        commentCount += 1;
                        commentCountText.setText("(" + commentCount + ")");
                    });
                });
            }
        });

        bottomToolbar = findViewById(R.id.post_bottom_toolbar);
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
            case 2:
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

        postView.findViewById(R.id.author_container).setOnClickListener((View v) -> {
            Intent intent = new Intent(this, LoadingActivity.class);
            intent.putExtra("type", LoadingActivity.DestType.homepage);
            intent.putExtra("nickname", post.getNickname());
            this.startActivity(intent);
        });

        return postView;
    }

    private void setReplyComment(Comment comment) {
        replyComment = comment;
        commentText.setHint("回复 " + comment.getNickname());
        commentText.requestFocus();
    }

    // ref: https://stackoverflow.com/a/36411427
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                bottomToolbar.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    commentText.setHint("");
                    replyComment = null;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void refreshHotComments() {
        Handler handler = new Handler(Looper.getMainLooper());
        ServerReq.getJsonArray("/post/" + post.id + "/comments/hot", (JSONArray arr) -> handler.post(() -> {
            try {
                hotComments.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    hotComments.add(new Comment(obj));
                }
                hotCommentsAdapter.notifyDataSetChanged();
                hotCommentContainer.setVisibility(arr.length() > 0 ? View.VISIBLE : View.GONE);
            } catch (Exception e) {
                Log.e("PostActivity", e.toString());
            }
        }));
    }
}
