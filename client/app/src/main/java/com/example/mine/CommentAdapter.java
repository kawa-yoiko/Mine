package com.example.mine;

import android.graphics.Color;
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
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.function.Consumer;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    private final int postId;
    private final LinkedList<Comment> data;
    private final Consumer<Comment> replyCallback;

    public static class CommentHolder extends RecyclerView.ViewHolder {
        public CommentHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    public CommentAdapter(int postId, LinkedList<Comment> data, @Nullable Consumer<Comment> replyCallback)
    {
        this.postId = postId;
        this.data = data;
        this.replyCallback = replyCallback;
    }



    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentHolder(mItemView);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
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
        ImageView flowerIcon = item.findViewById(R.id.flower_icon);

        // Flower button
        ToggleReqButton toggleFlower = new ToggleReqButton(
                item.findViewById(R.id.flower_button),
                item.findViewById(R.id.flower_icon),
                item.findViewById(R.id.flower_num),
                R.drawable.flower_monochrome, R.drawable.flower,
                Color.parseColor("#FBBABA"),
                "/post/" + this.postId + "/comment/" + comment.id + "/upvote",
                "upvote");
        toggleFlower.setState(comment.myUpvote ? 1 : 0, comment.getFlowerNum());

        TextView moreButton = item.findViewById(R.id.more);
        String moreButtonTextExpand = "?????? " + comment.replyNum + " ?????????";
        String moreButtonTextCollapse = "????????????";

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
                        CommentChildAdapter commentChildAdapter = new CommentChildAdapter(
                                CommentAdapter.this.postId, subcomments, replyCallback);
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
            recyclerView.setAdapter(null);
        } else {
            moreButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
