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

public class MessageRecordAdapter extends RecyclerView.Adapter<MessageRecordAdapter.MessageRecordViewHolder> {
    private LinkedList<MessageRecord> data;

    public static class MessageRecordViewHolder extends RecyclerView.ViewHolder {
        public MessageRecordViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public MessageRecordAdapter(LinkedList<MessageRecord> data)
    {
        this.data = data;
    }

    @NonNull
    @Override
    public MessageRecordAdapter.MessageRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageRecordViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageRecordViewHolder holder, int position) {
        MessageRecord messageRecord = data.get(position);
        View item = holder.itemView;
        TextView contentText = item.findViewById(R.id.content);
        contentText.setText(messageRecord.getContent());
        TextView usernameText = item.findViewById(R.id.username);
        usernameText.setText(messageRecord.getUsername());
        String messageNum = messageRecord.getMessageNum();
        TextView messageNumText = item.findViewById(R.id.message_num);
        if (messageNum.equals("0")) {
            messageNumText.setVisibility(View.GONE);
        }
        else {
            messageNumText.setText(messageNum);
        }
        ImageView avatarImage = item.findViewById(R.id.avatar);
        ServerReq.Utils.loadImage("/upload/" + messageRecord.getAvatar(), avatarImage);
        TextView dateText = item.findViewById(R.id.date);
        dateText.setText(messageRecord.getDate());

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(v.getContext(), ChatActivity.class);
                intent.putExtra("username", messageRecord.getUsername());
                intent.putExtra("other_avatar", messageRecord.getAvatar());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
