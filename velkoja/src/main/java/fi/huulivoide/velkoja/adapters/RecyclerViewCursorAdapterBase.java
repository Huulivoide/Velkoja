package fi.huulivoide.velkoja.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import fi.huulivoide.velkoja.ui.ViewWrapper;

public abstract class RecyclerViewCursorAdapterBase <V extends View> extends RecyclerView.Adapter<ViewWrapper<V>>
{
    protected Cursor mCursor;

    protected abstract V onCreateItemView(ViewGroup parent, int viewType);

    public RecyclerViewCursorAdapterBase(Cursor cursor) {
        mCursor = cursor;
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public final ViewWrapper<V> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewWrapper<V>(onCreateItemView(parent, viewType));
    }

    public void updateCursor(Cursor newCursor) {
        mCursor.close();
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}
