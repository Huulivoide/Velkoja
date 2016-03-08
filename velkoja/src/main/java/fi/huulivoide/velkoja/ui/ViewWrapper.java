package fi.huulivoide.velkoja.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ViewWrapper<V extends View> extends RecyclerView.ViewHolder
{
    private V mView;

    public ViewWrapper(V itemView) {
        super(itemView);
        mView = itemView;
    }

    public V getView() {
        return mView;
    }
}
