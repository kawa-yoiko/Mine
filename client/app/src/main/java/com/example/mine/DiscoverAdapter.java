package com.example.mine;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
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

        ImageView flowerIcon = item.findViewById(R.id.flower_icon);
        flowerIcon.setColorFilter(Color.parseColor("#FBBABA"));

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
        } else if (post.getContentType() == 3) {
            image.setImageResource(R.drawable.video);
            image.setColorFilter(ResourcesCompat.getColor(item.getResources(), R.color.themeyellow, null));
            text.setVisibility(View.GONE);
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
