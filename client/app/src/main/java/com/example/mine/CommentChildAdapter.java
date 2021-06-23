package com.example.mine;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.function.Consumer;

public class CommentChildAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final int postId;
    private final LinkedList<Comment> data;
    private final Consumer<Comment> replyCallback;

    public static class CommentChildHolder extends RecyclerView.ViewHolder {
        public CommentChildHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    public CommentChildAdapter(int postId, LinkedList<Comment> data, Consumer<Comment> replyCallback) {
        this.postId = postId;
        this.data = data;
        this.replyCallback = replyCallback;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_comment_item, parent, false);
        return new CommentChildHolder(mItemView);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Comment comment = data.get(position);
        View item = holder.itemView;
        item.setOnClickListener((View v) -> this.replyCallback.accept(comment));

        ImageView avatarView = item.findViewById(R.id.avatar);
        ServerReq.Utils.loadImage("/upload/" + comment.getAvatar(), avatarView);
        TextView nicknameView = item.findViewById(R.id.nickname);
        nicknameView.setText(comment.getNickname());
        TextView contentView = item.findViewById(R.id.content);
        contentView.setText(comment.getContent());
        TextView dateView = item.findViewById(R.id.date);
        dateView.setText(comment.getDate());
        TextView replyNicknameView = item.findViewById(R.id.reply_nickname);
        replyNicknameView.setText(comment.getReplyNickname());

        ImageView flowerIcon = item.findViewById(R.id.flower_icon);
        flowerIcon.setColorFilter(Color.parseColor("#FBBABA"));

        // Flower button
        ToggleReqButton toggleFlower = new ToggleReqButton(
                item.findViewById(R.id.flower_button),
                item.findViewById(R.id.flower_icon),
                item.findViewById(R.id.flower_num),
                R.drawable.flower_monochrome, R.drawable.flower, R.drawable.flower_monochrome,
                "/post/" + this.postId + "/comment/" + comment.id + "/upvote",
                "upvote");
        toggleFlower.setState(comment.myUpvote ? 1 : 0, comment.getFlowerNum());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

