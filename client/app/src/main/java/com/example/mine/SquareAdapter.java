package com.example.mine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class SquareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int DATE = 0;
    private static final int PICTURE = 1;
    private static final int TEXT = 2;
    private LinkedList<Object> data;
    private int itemsBefore = 0;

    public static class DateHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public DateHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.date);
        }
    }

    public static class ImageHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.square_image);
        }
    }

    public static class TextHolder extends RecyclerView.ViewHolder {
        TextView captionView;
        TextView contentView;
        public TextHolder(@NonNull View itemView) {
            super(itemView);
            captionView = itemView.findViewById(R.id.caption);
            contentView = itemView.findViewById(R.id.content);
        }
    }

    public SquareAdapter(LinkedList<Object> data)
    {
        this.data = data;
    }

    public SquareAdapter(LinkedList<Object> data, int itemsBefore) {
        this.data = data;
        this.itemsBefore = itemsBefore;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) instanceof String) {
            String s = (String) data.get(position);
            return (s.startsWith("+") ? PICTURE : DATE);
        }
        else{
            return TEXT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == DATE) {
            View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
            return new DateHolder(mItemView);
        }
        else if (viewType == PICTURE) {
            View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_square_image, parent, false);
            return new ImageHolder(mItemView);
        }
        else if (viewType == TEXT) {
            View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_square_text, parent, false);
            return new TextHolder(mItemView);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DateHolder) {
            String date = (String)data.get(position);
            ((DateHolder)holder).textView.setText(date);
        }
        else if (holder instanceof ImageHolder) {
            String image = ((String)data.get(position)).substring(1);
            ServerReq.Utils.loadImage(
                    "/upload/" + image,
                    ((ImageHolder)holder).imageView);
        }
        else if (holder instanceof TextHolder) {
            String[] text = (String[])data.get(position);
            String caption = text[0];
            String content = text[1];
            ((TextHolder)holder).captionView.setText(caption);
            ((TextHolder)holder).contentView.setText(content);
        }
    }


    //参考https://blog.csdn.net/qq_39121188/article/details/114818641
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (position < itemsBefore || getItemViewType(position - itemsBefore) == DATE)
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
