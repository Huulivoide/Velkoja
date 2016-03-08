/**
 * Copyright (c) 2016, Jesse Jaara <jesse.jaara@gmail.com>
 */

package fi.huulivoide.velkoja;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class RecyclerViewListAdapterBase<T, V extends View> extends RecyclerView.Adapter<ViewWrapper<V>>
{
    protected List<T> mItems;

    protected abstract V onCreateItemView(ViewGroup parent, int viewType);

    public RecyclerViewListAdapterBase(List items) {
        mItems = items;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public final ViewWrapper<V> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewWrapper<V>(onCreateItemView(parent, viewType));
    }
}
