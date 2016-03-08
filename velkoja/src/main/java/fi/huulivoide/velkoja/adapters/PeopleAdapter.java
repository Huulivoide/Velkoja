package fi.huulivoide.velkoja.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import fi.huulivoide.velkoja.ui.PersonItemView_;
import fi.huulivoide.velkoja.ui.PersonItemView;
import fi.huulivoide.velkoja.ui.ViewWrapper;

public class PeopleAdapter extends RecyclerViewCursorAdapterBase<PersonItemView>
{
    Context mContext;
    View.OnClickListener mClickListener;

    @Override
    protected PersonItemView onCreateItemView(ViewGroup parent, int viewType) {
        PersonItemView v = PersonItemView_.build(mContext);
        v.setOnClickListener(mClickListener);

        return v;
    }

    public PeopleAdapter(@NonNull Context context, @NonNull Cursor cursor) {
        super(cursor);
        mContext = context;
    }

    @Override
    public void onBindViewHolder(ViewWrapper<PersonItemView> viewHolder, int position) {
        PersonItemView view = viewHolder.getView();
        mCursor.moveToPosition(position);

        view.bind(mCursor.getLong(0), mCursor.getString(1), mCursor.getString(2), mCursor.getString(3));
    }

    /**
     * Associate the given click listener with all views contained in the list.
     * Must be set before the adapter is handed over to the RecyclerView
     *
     * @param listener
     */
    public void setOnClickListener(View.OnClickListener listener) {
        mClickListener = listener;
    }
}
