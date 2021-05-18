package com.example.mine;


import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class PostActivity extends AppCompatActivity {
    private ViewGroup fView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Post post = new Post("粥粥的烹饪", "#美食 #狗粮 #每周粥粥", R.drawable.flower, "kuriko", "15分钟前", "今天是甜粥粥。",
                R.drawable.luoxiaohei, "221", "221", "221");
        View postView = getPostView(post);
        fView = findViewById(R.id.post_content);
        fView.addView(postView);
        Fragment commentFragment = new CommentFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.post_comment, commentFragment).commit();
        TextView flower_num_text = findViewById(R.id.flower_num);
        flower_num_text.setText(post.getFlower_num());
        TextView comment_num_text = findViewById(R.id.comment_num);
        comment_num_text.setText(post.getComment_num());
        TextView star_num_text = findViewById(R.id.star_num);
        star_num_text.setText(post.getStar_num());
    }

    private View getPostView(Post post) {
        View postView = getLayoutInflater().inflate(R.layout.post_content, null);

        TextView collection_text = postView.findViewById(R.id.collection);
        collection_text.setText(post.getCollection());
        TextView tag_text = postView.findViewById(R.id.tag);
        tag_text.setText(post.getTag());
        ImageView avatar_image = postView.findViewById(R.id.avatar);
        avatar_image.setImageResource(post.getAvatar());
        TextView nickname_text = postView.findViewById(R.id.nickname);
        nickname_text.setText(post.getNickname());
        TextView timestamp_text = postView.findViewById(R.id.timestamp);
        timestamp_text.setText(post.getTimestamp());
        TextView caption_text = postView.findViewById(R.id.caption);
        caption_text.setText(post.getCaption());
        ImageView content_image = postView.findViewById(R.id.content);
        content_image.setImageResource((int)post.getContent());

        return postView;
    }
}
