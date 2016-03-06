package fi.huulivoide.velkoja;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.person_item)
public class PersonItemView extends LinearLayout
{
    @ViewById(R.id.name_text)
    protected TextView mNameText;

    @ViewById(R.id.iban_text)
    protected TextView mIbanText;

    @ViewById(R.id.bic_text)
    protected TextView mBicText;

    public PersonItemView(Context context) {
        super(context);
    }

    public void bind(@NonNull String name, @NonNull String iban, @NonNull String bic) {
        mNameText.setText(name);
        mIbanText.setText(iban);
        mBicText.setText(bic);
    }
}
