package com.example.mine;

import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private LinkedList<Notification> data;

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public NotificationAdapter(LinkedList<Notification> data)
    {
        this.data = data;
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = data.get(position);
        View item = holder.itemView;
        TextView contentText = item.findViewById(R.id.content);
        contentText.setText(notification.getContent());
        TextView dateText = item.findViewById(R.id.date);
        dateText.setText(notification.getDate());
        ImageView image = item.findViewById(R.id.image);
        image.setImageResource(notification.getImage());
        ImageView icon = item.findViewById(R.id.type_icon);
        int type = notification.getType();
        switch (type) {
            case 0 :
                icon.setImageResource(R.drawable.flower);
                break;
            case 1:
                icon.setImageResource(R.drawable.comment);
                break;
            case 2:
                icon.setImageResource(R.drawable.star);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
