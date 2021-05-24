package com.example.mine;

import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final int postId;
    private LinkedList<Comment> data;

    public static class CommentHolder extends RecyclerView.ViewHolder {
        public CommentHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    public CommentAdapter(int postId, LinkedList<Comment> data)
    {
        this.postId = postId;
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
        ServerReq.Utils.loadImage("/upload/" + comment.getAvatar(), avatarView);
        TextView nicknameView = item.findViewById(R.id.nickname);
        nicknameView.setText(comment.getNickname());
        TextView contentView = item.findViewById(R.id.content);
        contentView.setText(comment.getContent());
        TextView dateView = item.findViewById(R.id.date);
        dateView.setText(comment.getDate());
        TextView flowerNum = item.findViewById(R.id.flower_num);
        flowerNum.setText(String.valueOf(comment.getFlowerNum()));

        TextView moreButton = item.findViewById(R.id.more);
        String moreButtonTextExpand = "展开 " + comment.replyNum + " 条回复";
        String moreButtonTextCollapse = "收起回复";

        View loadingIndicator = item.findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        if (comment.replyNum > 0) {
            RecyclerView recyclerView = item.findViewById(R.id.child_comment);
            moreButton.setText(moreButtonTextExpand);
            moreButton.setOnClickListener(new View.OnClickListener() {
                boolean expanded = false;
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    expanded = !expanded;
                    moreButton.setText(expanded ? moreButtonTextCollapse : moreButtonTextExpand);
                    if (expanded) {
                        LinkedList<Comment> subcomments = new LinkedList<>();
                        CommentChildAdapter commentChildAdapter = new CommentChildAdapter(subcomments);
                        recyclerView.setAdapter(commentChildAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
                        loadingIndicator.setVisibility(View.VISIBLE);
                        Handler handler = new Handler(Looper.getMainLooper());
                        ServerReq.getJsonArray("/post/" + CommentAdapter.this.postId +
                                "/comments?start=0&count=50&reply_root=" + comment.id, (JSONArray arr) -> {
                            try {
                                int n = arr.length();
                                for (int i = 0; i < n; i++) {
                                    JSONObject obj = arr.getJSONObject(i);
                                    subcomments.add(new Comment(obj));
                                }
                                handler.post(() -> {
                                    commentChildAdapter.notifyItemRangeInserted(subcomments.size() - n, n);
                                    loadingIndicator.setVisibility(View.GONE);
                                });
                            } catch (Exception e) {
                                Log.e("CommentAdapter", "During parsing: " + e);
                            }
                        });
                    } else {
                        recyclerView.setAdapter(null);
                    }
                }
            });
        } else {
            moreButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
