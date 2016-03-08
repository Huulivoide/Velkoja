package fi.huulivoide.velkoja;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import fi.huulivoide.velkoja.adapters.DebtsAdapter;
import fi.huulivoide.velkoja.model.Debt;
import fi.huulivoide.velkoja.model.PeopleDatabaseHelper;
import fi.huulivoide.velkoja.model.Person;
import fi.huulivoide.velkoja.ui.DebtItemView;
import fi.huulivoide.velkoja.ui.DefaultToolbarItems;
import fi.huulivoide.velkoja.ui.DividerItemDecoration;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import org.iban4j.Iban;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.person_layout)
public class PersonFragment extends Fragment
{
    private static final NumberFormat mCurrencyFormatter = NumberFormat.getCurrencyInstance();

    private MenuItem mDeleteItem;
    private MenuItem mPayItem;

    private PeopleDatabaseHelper mPeople;
    private Person mPerson;

    private List<Long> mSelectedDebts = new ArrayList<>();


    @ViewById(R.id.toolbar_person)
    protected Toolbar mToolbar;

    @ViewById(R.id.debt_tabs)
    protected TabHost mTabHost;

    @ViewById(R.id.paid_debts_list)
    protected RecyclerView mPaid;

    @ViewById(R.id.unpaid_debts_list)
    protected RecyclerView mUnpaid;

    @ViewById(R.id.person_name)
    protected TextView mName;

    @ViewById(R.id.iban_text)
    protected TextView mIban;

    @ViewById(R.id.bic_text)
    protected TextView mBic;

    @ViewById(R.id.new_debt_fab)
    protected FloatingActionButton mNewDebtFab;


    @AfterViews
    protected void setupToolbar() {
        mToolbar.setTitle(R.string.person_title);

        DefaultToolbarItems.addBack(this, mToolbar);

        Drawable deleteIcon = new IconicsDrawable(getActivity())
                .icon(GoogleMaterial.Icon.gmd_delete)
                .sizeDp(24);

        mDeleteItem = mToolbar.getMenu().add(getString(R.string.menu_delete));
        mDeleteItem.setIcon(deleteIcon);
        mDeleteItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        mDeleteItem.setOnMenuItemClickListener(this::onDeletePersonClick);

        Drawable payIcon = new IconicsDrawable(getActivity())
                .icon(GoogleMaterial.Icon.gmd_payment)
                .sizeDp(24);

        mPayItem = mToolbar.getMenu().add(R.string.menu_pay);
        mPayItem.setIcon(payIcon);
        mPayItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        mPayItem.setOnMenuItemClickListener(this::paySelectedDebts);
        mPayItem.setVisible(false);
    }

    @AfterViews
    protected void setupTabs() {
        mTabHost.setup();

        TabHost.TabSpec unpaidTab = mTabHost.newTabSpec("unpaid");
        unpaidTab.setIndicator(getString(R.string.unpaid_debts_tab_title));
        unpaidTab.setContent(R.id.unpaid_debts);
        mTabHost.addTab(unpaidTab);

        TabHost.TabSpec paidTab = mTabHost.newTabSpec("paid");
        paidTab.setIndicator(getString(R.string.paid_debts_tab_title));
        paidTab.setContent(R.id.paid_debts);
        mTabHost.addTab(paidTab);
    }

    @AfterViews
    protected void setupRecyclers() {
        fetchPerson();

        mPaid.setHasFixedSize(true);
        mPaid.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPaid.addItemDecoration(new DividerItemDecoration(getActivity(), null));

        mUnpaid.setHasFixedSize(true);
        mUnpaid.setLayoutManager(new LinearLayoutManager(getActivity()));
        mUnpaid.addItemDecoration(new DividerItemDecoration(getActivity(), null));

        updateAdapters();
    }

    @Click(R.id.new_debt_fab)
    protected void createNewDebt() {
        Bundle args = new Bundle();
        args.putLong("person", getArguments().getLong("id"));
        NewDebtFragment frag = new NewDebtFragment_();
        frag.setArguments(args);

        getFragmentManager()
            .beginTransaction()
            .setCustomAnimations(R.animator.enter_from_left, R.animator.exit_to_right,
                                 R.animator.enter_from_right, R.animator.exit_to_left)
            .replace(R.id.content_frame, frag)
            .addToBackStack(null)
            .commit();
    }

    /**
     * Go back to PeopleListFragment and tell it to delete current person.
     *
     * @param item
     * @return
     */
    private boolean onDeletePersonClick(MenuItem item) {
        ((VelkojaActivity) getActivity()).queuePersonForDeletion(mPerson.id);
        getFragmentManager().popBackStack();
        return true;
    }

    private void fetchPerson() {
        mPeople = new PeopleDatabaseHelper(getActivity());
        mPerson = mPeople.findPerson(getArguments().getLong("id"));

        mName.setText(mPerson.name);
        mIban.setText(Iban.valueOf(mPerson.iban).toFormattedString());
        mBic.setText(mPerson.bic);
    }

    /**
     * Set toolbar's title to show the total amount of money the selected
     * debts add up to. Toggle between delete and pay icons.
     */
    private void updateToolbar() {
        if (mSelectedDebts.size() > 0) {
            double total = 0;

            for (Debt debt: mPerson.unpaid) {
                if (mSelectedDebts.contains(debt.id)) {
                    total += debt.sum;
                }
            }

            mToolbar.setTitle(String.format(getString(R.string.payoff_title), mCurrencyFormatter.format(total)));

            mDeleteItem.setVisible(false);
            mPayItem.setVisible(true);
        } else {
            mToolbar.setTitle(R.string.person_title);
            mDeleteItem.setVisible(true);
            mPayItem.setVisible(false);
        }
    }

    /**
     * OnClickListener for unpaid debts views.
     *
     * @param view
     */
    private void selectDebtForPaying(View view) {
        long id = ((DebtItemView) view).getDebtId();

        if (mSelectedDebts.contains(id)) {
            mSelectedDebts.remove(id);
            view.setBackgroundColor(Color.WHITE);
        } else {
            mSelectedDebts.add(id);
            view.setBackgroundColor(Color.GRAY);
        }

        updateToolbar();
    }

    /**
     * Mark the selected debts as payd and refresh the screen.
     *
     * @param item
     * @return
     */
    private boolean paySelectedDebts(MenuItem item) {
        for (long debt: mSelectedDebts) {
            mPeople.getDebtsHelper().markAsPaid(debt);
        }

        mSelectedDebts.clear();
        updateToolbar();
        mPerson = mPeople.findPerson(getArguments().getLong("id"));
        updateAdapters();

        return true;
    }

    private DebtsAdapter getUnpaidAdapter() {
        DebtsAdapter unpaidAdapter = new DebtsAdapter(getActivity(), mPerson.unpaid);
        unpaidAdapter.setOnClickListener(this::selectDebtForPaying);

        return unpaidAdapter;
    }

    private void updateAdapters() {
        mPaid.setAdapter(new DebtsAdapter(getActivity(), mPerson.paid));
        mUnpaid.setAdapter(getUnpaidAdapter());
    }
}
