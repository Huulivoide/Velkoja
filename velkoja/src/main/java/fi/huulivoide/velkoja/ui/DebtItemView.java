package fi.huulivoide.velkoja.ui;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;

import fi.huulivoide.velkoja.R;
import fi.huulivoide.velkoja.model.Debt;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.debt_item)
public class DebtItemView extends LinearLayout {
    private static final NumberFormat mCurrencyFormatter = NumberFormat.getCurrencyInstance();
    private static final DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

    protected long mId;

    @ViewById(R.id.debt_description)
    protected TextView mDescription;

    @ViewById(R.id.debt_due)
    protected TextView mDue;

    @ViewById(R.id.debt_sum)
    protected TextView mSum;

    @ViewById(R.id.debt_paid)
    protected TextView mPaid;

    public DebtItemView(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public void bind(@NonNull Debt debt) {
        mId = debt.id;
        mDescription.setText(debt.description);
        mSum.setText(mCurrencyFormatter.format(debt.sum));
        mDue.setText(String.format(getResources().getString(R.string.due),
                                   dateFormatter.format(debt.due.getTime())));

        if (debt.paid != null) {
            mPaid.setText(String.format(getResources().getString(R.string.paid),
                                        dateFormatter.format(debt.paid.getTime())));
            mSum.setPaintFlags(mSum.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            mPaid.setText("");
            mSum.setPaintFlags(mSum.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    public long getDebtId() {
        return mId;
    }
}
