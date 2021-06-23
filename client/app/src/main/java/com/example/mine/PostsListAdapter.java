package com.example.mine;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class PostsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private LinkedList<Post> data;
    private static final int IMAGE = 0;
    private static final int TEXT = 1;
    private static final int AUDIO = 2;
    private static final int VIDEO = 3;

    public static class ImageHolder extends RecyclerView.ViewHolder {
        public ImageHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class TextHolder extends RecyclerView.ViewHolder {
        public TextHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public PostsListAdapter(LinkedList<Post> data)
    {
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        switch (data.get(position).getContentType()) {
            default:
            case 0: return TEXT;
            case 1: return IMAGE;
            case 2: return AUDIO;
            case 3: return VIDEO;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == IMAGE) {
            View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            return new ImageHolder(mItemView);
        }
        else if (viewType == TEXT) {
            View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_text, parent, false);
            return new TextHolder(mItemView);
        } else if (viewType == AUDIO || viewType == VIDEO) {
            View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            return new ImageHolder(mItemView);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = data.get(position);
        View item = holder.itemView;
        TextView collection_text = item.findViewById(R.id.collection);
        collection_text.setText(post.getCollection());
        TextView tag_text = item.findViewById(R.id.tag);
        tag_text.setText(post.getTag());
        ImageView avatar_image = item.findViewById(R.id.avatar);
        ServerReq.Utils.loadImage("/upload/" + post.getAvatar(), avatar_image);
        TextView nickname_text = item.findViewById(R.id.nickname);
        nickname_text.setText(post.getNickname());
        TextView timestamp_text = item.findViewById(R.id.timestamp);
        timestamp_text.setText(post.getTimestamp());
        TextView caption_text = item.findViewById(R.id.caption);
        caption_text.setText(post.getCaption());
        TextView flower_num_text = item.findViewById(R.id.flower_num);
        flower_num_text.setText(String.valueOf(post.getFlower_num()));
        TextView comment_num_text = item.findViewById(R.id.comment_num);
        comment_num_text.setText(String.valueOf(post.getComment_num()));
        TextView star_num_text = item.findViewById(R.id.star_num);
        star_num_text.setText(String.valueOf(post.getStar_num()));

        ImageView flowerIcon = item.findViewById(R.id.flower_icon);
        ImageView commentIcon = item.findViewById(R.id.comment_icon);
        ImageView starIcon = item.findViewById(R.id.star_icon);
        ImageView bookIcon = item.findViewById(R.id.book_icon);
        flowerIcon.setColorFilter(Color.parseColor("#FBBABA"));
        commentIcon.setColorFilter(Color.parseColor("#A2E0FF"));
        starIcon.setColorFilter(Color.parseColor("#F4DB35"));
        bookIcon.setColorFilter(Color.parseColor("#FFFCE6"));
//        ImageView test = item.findViewById(R.id.test);
//        test.setColorFilter(R.color.whitebule);

        if (holder instanceof ImageHolder) {
            ImageView content_image = item.findViewById(R.id.content);
            if (post.getContentType() == 0) {
                ServerReq.Utils.loadImage("/upload/" + post.getContent().split(" ")[0], content_image);
            } else if (post.getContentType() == 2) {
                content_image.setImageResource(R.drawable.music);
                content_image.setColorFilter(ResourcesCompat.getColor(item.getResources(), R.color.themeyellow, null));
            } else {
                content_image.setImageResource(R.drawable.video);
                content_image.setColorFilter(ResourcesCompat.getColor(item.getResources(), R.color.themeyellow, null));
            }
        }
        else if (holder instanceof TextHolder) {
            TextView content_text = item.findViewById(R.id.content);
            content_text.setText(post.getContent());
        }

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(v.getContext(), LoadingActivity.class);
                intent.putExtra("type", LoadingActivity.DestType.post);
                intent.putExtra("id", post.id);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
