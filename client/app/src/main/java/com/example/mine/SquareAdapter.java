package com.example.mine;

import android.content.Intent;
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

public class SquareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int DATE = 0;
    private static final int PICTURE = 1;
    private static final int TEXT = 2;
    private LinkedList<SquareFragment.Item> data;
    private int itemsBefore = 0;
    private RecyclerView recyclerView;

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

    public SquareAdapter(LinkedList<SquareFragment.Item> data)
    {
        this.data = data;
    }

    public SquareAdapter(LinkedList<SquareFragment.Item> data, int itemsBefore) {
        this.data = data;
        this.itemsBefore = itemsBefore;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) instanceof SquareFragment.ImageItem ||
                data.get(position) instanceof SquareFragment.IconItem) {
            return PICTURE;
        } else if (data.get(position) instanceof SquareFragment.TextItem) {
            return TEXT;
        } else {
            return DATE;
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
        SquareFragment.Item item = data.get(position);
        if (holder instanceof DateHolder) {
            String date = ((SquareFragment.DateItem) item).date;
            ((DateHolder)holder).textView.setText(date);
        }
        else if (holder instanceof ImageHolder) {
            if (item instanceof SquareFragment.ImageItem) {
                String image = ((SquareFragment.ImageItem) item).image;
                ServerReq.Utils.loadImage(
                        "/upload/" + image,
                        ((ImageHolder) holder).imageView);
            } else {
                ((ImageHolder) holder).imageView.setImageResource(
                        ((SquareFragment.IconItem) item).icon
                );
                ((ImageHolder) holder).imageView.setColorFilter(
                        ResourcesCompat.getColor(recyclerView.getResources(), R.color.themeyellow, null));
            }
        }
        else if (holder instanceof TextHolder) {
            String caption = ((SquareFragment.TextItem) item).caption;
            String content = ((SquareFragment.TextItem) item).contents;
            ((TextHolder)holder).captionView.setText(caption);
            ((TextHolder)holder).contentView.setText(content);
        }
        holder.itemView.setOnClickListener((View v) -> {
            if (item instanceof SquareFragment.PostItem) {
                Intent intent = new Intent();
                intent.setClass(v.getContext(), LoadingActivity.class);
                intent.putExtra("type", LoadingActivity.DestType.post);
                intent.putExtra("id", ((SquareFragment.PostItem) item).id);
                v.getContext().startActivity(intent);
            }
        });
    }


    //参考https://blog.csdn.net/qq_39121188/article/details/114818641
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
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
