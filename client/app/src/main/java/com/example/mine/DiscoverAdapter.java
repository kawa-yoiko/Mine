package com.example.mine;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.DiscoverViewHolder> {
    private LinkedList<Post> data;

    public static class DiscoverViewHolder extends RecyclerView.ViewHolder {
        public DiscoverViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public DiscoverAdapter(LinkedList<Post> data)
    {
        this.data = data;
    }

    @NonNull
    @Override
    public DiscoverAdapter.DiscoverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discover, parent, false);
        return new DiscoverViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverViewHolder holder, int position) {
        Post post = data.get(position);
        View item = holder.itemView;
        TextView captionText = item.findViewById(R.id.caption);
        captionText.setText(post.getCaption());
        TextView tagText = item.findViewById(R.id.tag);
        tagText.setText(post.getTag());
        TextView fondNumText = item.findViewById(R.id.fond_num);
        fondNumText.setText(String.valueOf(post.getFlower_num()));

        TextView text = item.findViewById(R.id.text);
        ImageView image = item.findViewById(R.id.image);
        if (post.getContentType() == 0) {
            text.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
            text.setText(post.getContent());
        } else if (post.getContentType() == 1) {
            text.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
            ServerReq.Utils.loadImage("/upload/" + post.getContent(), image);
        }
//        ImageView avatarIcon = item.findViewById(R.id.avatar_icon);
//        avatarIcon.setImageResource(contact.getAvatarIcon());

        item.setOnClickListener((View v) -> {
            Intent intent = new Intent();
            intent.setClass(v.getContext(), LoadingActivity.class);
            intent.putExtra("type", LoadingActivity.DestType.post);
            intent.putExtra("id", post.id);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
