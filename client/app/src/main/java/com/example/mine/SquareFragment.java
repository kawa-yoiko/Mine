package com.example.mine;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

public class SquareFragment extends Fragment {
    public static class Item { }
    public static class DateItem extends Item {
        public String date;
        public DateItem(String date) { this.date = date; }
    }
    public static class PostItem extends Item {
        public int id;
    }
    public static class TextItem extends PostItem {
        public String caption;
        public String contents;
        public TextItem(int id, String caption, String contents) {
            this.id = id;
            this.caption = caption;
            this.contents = contents;
        }
    }
    public static class ImageItem extends PostItem {
        public String image;
        public ImageItem(int id, String image) {
            this.id = id;
            this.image = image;
        }
    }
    public static class IconItem extends PostItem {
        public int icon;
        public IconItem(int id, int icon) {
            this.id = id;
            this.icon = icon;
        }
    }

    private RecyclerView recyclerView;
    private View headingView;
    List<Collection.PostBrief> posts;
    private LinkedList<Item> dateAndImages;
    private SquareAdapter adapter;

    public SquareFragment() {
        this.dateAndImages = new LinkedList<>();
        for (int i = 0; i < 1; i++) {
            dateAndImages.add(new DateItem("2021年5月"));
            dateAndImages.add(new TextItem(1, "五条悟跳舞", "ll the beautiful things shining all around us,\n" +
                    "                all the beautiful things shining all around us; all the beautiful things shining all around us"));
            dateAndImages.add(new DateItem("2021年6月"));
            dateAndImages.add(new TextItem(2, "五条悟跳舞", "ll the beautiful things shining all around us,\n" +
                    "                all the beautiful things shining all around us; all the beautiful things shining all around us"));
        }
    }
    public SquareFragment(View headingView, List<Collection.PostBrief> posts) {
        this.headingView = headingView;
        this.posts = posts;
        this.dateAndImages = new LinkedList<>();
        rebuildItems();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        if (headingView != null) {
            SingleViewAdapter adapterHeading = new SingleViewAdapter(headingView);
            adapter = new SquareAdapter(dateAndImages, 1);
            ConcatAdapter adapterConcat = new ConcatAdapter(adapterHeading, adapter);
            recyclerView.setAdapter(adapterConcat);
        } else {
            adapter = new SquareAdapter(dateAndImages, 0);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.square_fragment, container, false);
    }

    private void rebuildItems() {
        dateAndImages.clear();
        dateAndImages.add(new DateItem("2021 年 6 月"));
        for (Collection.PostBrief post : posts) {
            if (post.type == 0) {
                dateAndImages.add(new TextItem(post.id, post.caption, post.contents));
            } else if (post.type == 1) {
                dateAndImages.add(new ImageItem(post.id, post.contents));
            } else if (post.type == 2) {
                dateAndImages.add(new IconItem(post.id, R.drawable.music));
            } else if (post.type == 3) {
                dateAndImages.add(new IconItem(post.id, R.drawable.video));
            }
        }
    }

    public void notifyDatasetChanged() {
        rebuildItems();
        adapter.notifyDataSetChanged();
    }

    public void reverse() {
        int n = dateAndImages.size();
        for (int i = 0; i < n / 2; i++) {
            Item t = dateAndImages.get(i);
            dateAndImages.set(i, dateAndImages.get(n - i - 1));
            dateAndImages.set(n - i - 1, t);
            adapter.notifyItemMoved(i, n - i - 1);
            adapter.notifyItemMoved(n - i - 2, i);
        }
    }
}
