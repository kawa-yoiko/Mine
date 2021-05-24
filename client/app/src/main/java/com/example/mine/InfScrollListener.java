package com.example.mine;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class InfScrollListener extends RecyclerView.OnScrollListener {
    protected RecyclerView.LayoutManager _layoutManager;
    protected int _threshold;
    protected boolean _isLoading;
    protected boolean _isComplete;

    public InfScrollListener(RecyclerView.LayoutManager layoutManager, int threshold) {
        _layoutManager = layoutManager;
        _threshold = threshold;
        _isLoading = false;
        _isComplete = false;
    }

    public abstract void load(int start);

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int totalItems = _layoutManager.getItemCount();
        int lastVisible = 0;
        if (_layoutManager instanceof LinearLayoutManager) {
            lastVisible = ((LinearLayoutManager) _layoutManager).findLastVisibleItemPosition();
        }

        if (!_isComplete && !_isLoading && lastVisible > totalItems - _threshold) {
            _isLoading = true;
            load(totalItems);
        }
    }

    public void finishLoad(boolean complete) {
        _isLoading = false;
        if (complete) _isComplete = true;
    }
}
