package fi.huulivoide.velkoja.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import fi.huulivoide.velkoja.model.Debt;
import fi.huulivoide.velkoja.ui.DebtItemView;
import fi.huulivoide.velkoja.ui.DebtItemView_;
import fi.huulivoide.velkoja.ui.ViewWrapper;

import java.util.List;

public class DebtsAdapter extends RecyclerViewListAdapterBase<Debt, DebtItemView>
{
    Context mContext;
    View.OnClickListener mClickListener;

    @Override
    protected DebtItemView onCreateItemView(ViewGroup parent, int viewType) {
        DebtItemView v = DebtItemView_.build(mContext);
        v.setOnClickListener(mClickListener);

        return v;
    }

    public DebtsAdapter(@NonNull Context context, @NonNull List<Debt> items) {
        super(items);
        mContext = context;
    }

    @Override
    public void onBindViewHolder(ViewWrapper<DebtItemView> viewHolder, int position) {
        DebtItemView view = viewHolder.getView();
        view.bind(mItems.get(position));
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