package com.example.mine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lzy.imagepicker.bean.ImageItem;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class ImagePickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<ImageItem> data;

    public static class Holder extends RecyclerView.ViewHolder {
        public Holder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public ImagePickerAdapter(ArrayList<ImageItem> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imagepicker, parent, false);
        return new Holder(mItemView);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        View item = holder.itemView;
        String path = data.get(position).path;
        Bitmap bm = BitmapFactory.decodeFile(path);
        ImageView image = (ImageView) item.findViewById(R.id.image);
        image.setImageBitmap(bm);
    }


    @Override
    public int getItemCount() {

        return data.size();
    }
}
