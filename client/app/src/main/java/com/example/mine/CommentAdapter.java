package com.example.mine;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private LinkedList<Comment> data;

    public static class CommentHolder extends RecyclerView.ViewHolder {
        public CommentHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    public CommentAdapter(LinkedList<Comment> data)
    {
        this.data = data;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentHolder(mItemView);
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
        View bt = item.findViewById(R.id.more);
        TextView flowerNum = item.findViewById(R.id.flower_num);
        flowerNum.setText(comment.getFlowerNum());
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView recyclerView = item.findViewById(R.id.child_comment);
                LinkedList<Comment> comments = new LinkedList<>();
                comments.add(new Comment(R.drawable.flower, "kuriko1024", "大师兄说的对阿", "05-12", "", ""));
                comments.add(new Comment(R.drawable.luoxiaohei, "kuriko1025", "二师兄说的对阿", "05-12", "kuriko1024", ""));
                comments.add(new Comment(R.drawable.luoxiaohei, "kuriko1026", "三师弟说的对阿", "05-12", "kuriko1025", ""));
                CommentChildAdapter commentChildAdapter = new CommentChildAdapter(comments);
                recyclerView.setAdapter(commentChildAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
