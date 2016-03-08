package fi.huulivoide.velkoja;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import fi.huulivoide.velkoja.model.PeopleDatabaseHelper;

import fi.huulivoide.velkoja.ui.DefaultToolbarItems;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import org.androidannotations.annotations.ViewById;
import org.iban4j.BicFormatException;
import org.iban4j.BicUtil;
import org.iban4j.IbanFormatException;
import org.iban4j.IbanUtil;
import org.iban4j.InvalidCheckDigitException;
import org.iban4j.UnsupportedCountryException;

@EFragment(R.layout.new_person)
public class NewPersonFragment extends Fragment
{
    private PeopleDatabaseHelper people;

    @ViewById(R.id.toolbar_new_person)
    protected Toolbar mToolbar;

    @ViewById(R.id.name_wrapper)
    protected TextInputLayout mName;

    @ViewById(R.id.iban_wrapper)
    protected TextInputLayout mIban;

    @ViewById(R.id.bic_wrapper)
    protected TextInputLayout mBic;

    @AfterViews
    protected void setupToolbar() {
        mToolbar.setTitle(R.string.new_person_title);

        DefaultToolbarItems.addBack(this, mToolbar);
        DefaultToolbarItems.addAccept(getActivity(), mToolbar, this::onSavePersonClick);
    }

    @AfterViews
    protected void setupCapsFilters() {
        mIban.getEditText().setFilters(new InputFilter[] { new InputFilter.AllCaps() });
        mBic.getEditText().setFilters(new InputFilter[] { new InputFilter.AllCaps() });
    }

    @AfterViews
    protected void initDBHelper() {
        people = new PeopleDatabaseHelper(getActivity());
    }

    /**
     * Extract and sanitize text from the name field.
     * Trims leading and trailing whitespace.
     *
     * @return cleaned name
     */
    private String getName() {
        return mName.getEditText().getText().toString().trim();
    }

    /**
     * Extract and sanitize text from the IBAN field.
     * Removes any grouping with whitesapce.
     *
     * @return cleaned IBAN
     */
    private String getIban() {
        return mIban.getEditText().getText().toString().replace(" ", "");
    }

    /**
     * Extract and sanitize text from the name field.
     * Removes any grouping with whitesapce.
     *
     * @return cleaned BIC
     */
    private String getBic() {
        return mBic.getEditText().getText().toString().replace(" ", "");
    }

    /**
     * Makes sure that a name is given and that it doesn't already exist in the db.
     * Sets appropriate error on the name TextInputLayout.
     *
     * @return true if all good
     */
    private boolean validateName() {
        String error = null;
        String name = getName();

        if (name.length() == 0) {
            error = getString(R.string.name_error_empty);
        } else if (people.personExists(name)) {
            error = getString(R.string.name_error_exists);
        }

        mName.setError(error);
        return (error == null);
    }

    /**
     * Fetches an appropriate localized string for the given exception.
     *
     * @param e source exception
     * @return localized error string
     */
    private String ibanFormatViolationToString(IbanFormatException e) {
        switch (e.getFormatViolation()) {
            case IBAN_NOT_EMPTY:
                return getString(R.string.iban_error_empty);

            case COUNTRY_CODE_TWO_LETTERS:
            case COUNTRY_CODE_EXISTS:
                return getString(R.string.iban_error_invalid_country);

            case CHECK_DIGIT_TWO_DIGITS:
            case CHECK_DIGIT_ONLY_DIGITS:
                return getString(R.string.iban_error_invalid_check);

            case BBAN_LENGTH: // BBAN length + country code + check digits
                return String.format(getString(R.string.iban_error_length), (int) e.getExpected() + 4);

            case BBAN_ONLY_DIGITS:
            case BBAN_ONLY_DIGITS_OR_LETTERS:
                return getString(R.string.iban_error_invalid_character);

            default:
                return getString(R.string.iban_error_general);
        }
    }

    /**
     * Makes sure that a valid IBAN is given and that it doesn't already exist in the db.
     * Sets appropriate error on the name TextInputLayout.
     *
     * @return true if all good
     */
    private boolean validateIban() {
        String iban = getIban();
        if (people.ibanExists(iban)) {
            mIban.setError(getString(R.string.iban_error_exists));
        } else {
            try {
                IbanUtil.validate(iban);
                mIban.setError(null);
                return true;
            } catch (IbanFormatException e) {
                mIban.setError(ibanFormatViolationToString(e));
            } catch (InvalidCheckDigitException e) {
                mIban.setError(getString(R.string.iban_error_nomatch));
            } catch (UnsupportedCountryException e) {
                mIban.setError(getString(R.string.iban_error_invalid_country));
            }
        }

        return false;
    }

    /**
     * Fetches an appropriate localized string for the given exception.
     *
     * @param e source exception
     * @return localized error string
     */
    private String bicFormatViolationToString(BicFormatException e) {
        switch (e.getFormatViolation()) {
            case BIC_NOT_EMPTY:return getString(R.string.bic_error_empty);
            case BIC_LENGTH_8_OR_11: return getString(R.string.bic_error_length);
            default: return getString(R.string.bic_error_general);
        }
    }

    /**
     * Makes sure that a valid IBAN is given and that it doesn't already exist in the db.
     * Sets appropriate error on the name TextInputLayout.
     *
     * @return true if all good
     */
    private boolean validateBic() {
        try {
            BicUtil.validate(getBic());
            mBic.setError(null);
            return true;
        } catch (BicFormatException e) {
            mBic.setError(bicFormatViolationToString(e));
        } catch (UnsupportedCountryException e) {
            mBic.setError(getString(R.string.bic_error_general));
        }

        return false;
    }

    /**
     * Tests each entry field for valid data.
     * Sets error on the entry fields if needed.
     *
     * @return true if all valid.
     */
    private boolean dataIsValid() {
        boolean isValid = validateName();
        isValid &= validateIban();
        isValid &= validateBic();

        return isValid;
    }

    /**
     * Validate entry fields and insert data into db if valid, when save action
     * is clicked in the toolbar. Finally returns back to PeopleList.
     *
     * @param item not used
     * @return true, overrides all (==none) other MenuItemClick handlers
     */
    private boolean onSavePersonClick(MenuItem item) {
        if (dataIsValid()) {
            people.insert(getName(), getIban(), getBic());
            getFragmentManager().popBackStack();
        }

        return true;
    }
}
