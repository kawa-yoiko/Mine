package com.example.mine;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.DiscoverViewHolder> {
    private LinkedList<Discover> data;

    public static class DiscoverViewHolder extends RecyclerView.ViewHolder {
        public DiscoverViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public DiscoverAdapter(LinkedList<Discover> data)
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
        Discover discover = data.get(position);
        View item = holder.itemView;
        TextView captionText = item.findViewById(R.id.caption);
        captionText.setText(discover.getCaption());
        TextView tagText = item.findViewById(R.id.tag);
        tagText.setText(discover.getTag());
        TextView fondNumText = item.findViewById(R.id.fond_num);
        fondNumText.setText(discover.getFondNum());
        ImageView image = item.findViewById(R.id.image);
        image.setImageResource(discover.getImage());
//        ImageView avatarIcon = item.findViewById(R.id.avatar_icon);
//        avatarIcon.setImageResource(contact.getAvatarIcon());
    }

    @Override
    public int getItemCount() {
        // TODO
        return data.size();
    }
}
