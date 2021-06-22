package com.example.mine;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mine.Chat;
import com.example.mine.R;

import java.util.LinkedList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String otherAvatar;
    private LinkedList<Chat> data;
    private int LEFT = 0;
    private int RIGHT = 1;

    public static class LeftHolder extends RecyclerView.ViewHolder {
        public LeftHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class RightHolder extends RecyclerView.ViewHolder {
        public RightHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    public ChatAdapter(String otherAvatar, LinkedList<Chat> data) {
        this.otherAvatar = otherAvatar;
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).getIsUserOwn() == 0) {
            return LEFT;
        } else{
            return RIGHT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == LEFT) {
            View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bubble_left, parent, false);
            return new LeftHolder(mItemView);
        }
        else if(viewType == RIGHT) {
            View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bubble_right, parent, false);
            return new RightHolder(mItemView);
        }
        
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat chat = data.get(position);
        View item = holder.itemView;
        TextView contentText = item.findViewById(R.id.content);
        contentText.setText(chat.getContent());
        TextView dateText = item.findViewById(R.id.date);
        dateText.setText(chat.getDate());
        ImageView avatarImage = item.findViewById(R.id.avatar);
        ServerReq.Utils.loadImage("/upload/" +
                (chat.getIsUserOwn() == 1 ? ServerReq.getMyAvatar() : this.otherAvatar), avatarImage);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
