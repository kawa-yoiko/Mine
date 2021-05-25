package com.example.mine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class CommentChildAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final int postId;
    private LinkedList<Comment> data;

    public static class CommentChildHolder extends RecyclerView.ViewHolder {
        public CommentChildHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    public CommentChildAdapter(int postId, LinkedList<Comment> data) {
        this.postId = postId;
        this.data = data;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_comment_item, parent, false);
        return new CommentChildHolder(mItemView);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Comment comment = data.get(position);
        View item =holder.itemView;
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

        // Flower button
        ToggleReqButton toggleFlower = new ToggleReqButton(
                item.findViewById(R.id.flower_button),
                item.findViewById(R.id.flower_icon),
                item.findViewById(R.id.flower_num),
                R.drawable.flower, R.drawable.star,
                "/post/" + this.postId + "/comment/" + comment.id + "/upvote",
                "upvote");
        toggleFlower.setState(comment.myUpvote ? 1 : 0, comment.getFlowerNum());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

