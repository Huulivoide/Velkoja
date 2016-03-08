package fi.huulivoide.velkoja.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import fi.huulivoide.velkoja.R;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.person_item)
public class PersonItemView extends LinearLayout
{
    private long mId;

    @ViewById(R.id.name_text)
    protected TextView mNameText;

    @ViewById(R.id.iban_text)
    protected TextView mIbanText;

    @ViewById(R.id.bic_text)
    protected TextView mBicText;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public PersonItemView(Context context) {
        super(context);
    }

    public void bind(long id, @NonNull String name, @NonNull String iban, @NonNull String bic) {
        mId = id;

        mNameText.setText(name);
        mIbanText.setText(iban);
        mBicText.setText(bic);
    }

    public long getPersonId() {
        return mId;
    }
}
