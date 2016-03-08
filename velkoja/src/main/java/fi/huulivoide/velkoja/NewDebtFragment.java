package fi.huulivoide.velkoja;

import android.app.Fragment;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;

import fi.huulivoide.velkoja.model.DebtsDatabaseHelper;
import fi.huulivoide.velkoja.ui.DefaultToolbarItems;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.new_debt_layout)
public class NewDebtFragment extends Fragment {
    private static final double MIN_SUM = 0.01;

    @ViewById(R.id.description_wrapper)
    protected TextInputLayout mDescription;

    @ViewById(R.id.sum_wrapper)
    protected TextInputLayout mSum;

    @ViewById(R.id.due_date_picker)
    protected DatePicker mDue;

    @ViewById(R.id.toolbar_new_debt)
    protected Toolbar mToolbar;

    private DebtsDatabaseHelper mDebtsHelper;

    @AfterViews
    protected void setupToolbar() {
        mToolbar.setTitle(R.string.new_debt_title);

        DefaultToolbarItems.addBack(this, mToolbar);
        DefaultToolbarItems.addAccept(getActivity(), mToolbar, this::clickAccept);
    }

    @AfterViews
    protected void createDBHelper() {
        mDebtsHelper = new DebtsDatabaseHelper(getActivity());
    }

    private boolean descriptionIsValid() {
        if (getDescription().length() > 0) {
            mDescription.setError(null);
            return true;
        }

        mDescription.setError(getString(R.string.description_empty_error));
        return false;
    }

    private boolean sumIsValid() {
        String text = mSum.getEditText().getText().toString();

        if (text.length() == 0) {
            mSum.setError(getString(R.string.sum_empty_error));
            return false;
        } else if (Double.parseDouble(text) <= MIN_SUM) {
            String minSumStr = NumberFormat.getCurrencyInstance().format(MIN_SUM);
            mSum.setError(String.format(getString(R.string.sum_min_error), minSumStr));
            return false;
        }

        mSum.setError(null);
        return true;
    }

    private String getDescription() {
        return mDescription.getEditText().getText().toString().trim();
    }

    private double getSum() {
        return Double.parseDouble(mSum.getEditText().getText().toString());
    }

    private Calendar getDate() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(mDue.getYear(), mDue.getMonth(), mDue.getDayOfMonth());

        return cal;
    }

    private boolean clickAccept(MenuItem item) {
        boolean allGood = descriptionIsValid();
        allGood &= sumIsValid();

        if (allGood) {
            mDebtsHelper.insert(getDescription(), getSum(), getDate(), getArguments().getLong("person"));
            closeKeyboard();
            getFragmentManager().popBackStack();
        }

        return true;
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mDescription.getWindowToken(), 0);
    }
}
