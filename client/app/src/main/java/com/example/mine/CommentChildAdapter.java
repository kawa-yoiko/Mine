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
    private LinkedList<Comment> data;

    public static class CommentChildHolder extends RecyclerView.ViewHolder {
        public CommentChildHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    public CommentChildAdapter(LinkedList<Comment> data)
    {
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
        avatarView.setImageResource(comment.getAvatar());
        TextView nicknameView = item.findViewById(R.id.nickname);
        nicknameView.setText(comment.getNickname());
        TextView contentView = item.findViewById(R.id.content);
        contentView.setText(comment.getContent());
        TextView dateView = item.findViewById(R.id.date);
        dateView.setText(comment.getDate());
        TextView replyNicknameView = item.findViewById(R.id.reply_nickname);
        replyNicknameView.setText(comment.getReplyNickname());
        TextView flowerNum = item.findViewById(R.id.flower_num);
        flowerNum.setText(comment.getFlowerNum());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

