package fi.huulivoide.velkoja;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

public class PeopleAdapter extends RecyclerViewCursorAdapterBase<PersonItemView>
{
    Context mContext;

    @Override
    protected PersonItemView onCreateItemView(ViewGroup parent, int viewType) {
        return PersonItemView_.build(mContext);
    }

    public PeopleAdapter(@NonNull Context context, @NonNull Cursor cursor) {
        super(cursor);
        mContext = context;
    }

    @Override
    public void onBindViewHolder(ViewWrapper<PersonItemView> viewHolder, int position) {
        PersonItemView view = viewHolder.getView();
        mCursor.moveToPosition(position);

        view.bind(mCursor.getString(1), mCursor.getString(2), mCursor.getString(3));
    }
}
