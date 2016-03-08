package fi.huulivoide.velkoja;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import fi.huulivoide.velkoja.ui.DividerItemDecoration;

import org.androidannotations.annotations.EFragment;

import org.iban4j.Iban;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@EFragment
public class PersonFragment extends Fragment
{
    private static final NumberFormat mCurrencyFormatter = NumberFormat.getCurrencyInstance();

    private Context mContext;
    private Toolbar mToolbar;
    private TabHost mTabHost;

    private MenuItem mDeleteItem;
    private MenuItem mPayItem;

    private RecyclerView mPaid;
    private RecyclerView mUnpaid;

    private TextView mName;
    private TextView mIban;
    private TextView mBic;

    private PeopleDatabaseHelper mPeople;
    private Person mPerson;

    private List<Long> mSelectedDebts = new ArrayList<>();

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

    private void findViews(View root) {
        mToolbar = (Toolbar) root.findViewById(R.id.toolbar_person);
        mTabHost = (TabHost) root.findViewById(R.id.debt_tabs);

        mPaid = (RecyclerView) root.findViewById(R.id.paid_debts_list);
        mUnpaid = (RecyclerView) root.findViewById(R.id.unpaid_debts_list);

        mName = (TextView) root.findViewById(R.id.person_name);
        mIban = (TextView) root.findViewById(R.id.iban_text);
        mBic = (TextView) root.findViewById(R.id.bic_text);
    }

    private void setupToolbar() {
        mToolbar.setTitle(R.string.person_title);

        DefaultToolbarItems.addBack(this, mToolbar);

        Drawable deleteIcon = new IconicsDrawable(mContext)
                .icon(GoogleMaterial.Icon.gmd_delete)
                .sizeDp(24);

        mDeleteItem = mToolbar.getMenu().add(getString(R.string.menu_delete));
        mDeleteItem.setIcon(deleteIcon);
        mDeleteItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        mDeleteItem.setOnMenuItemClickListener(this::onDeletePersonClick);

        Drawable payIcon = new IconicsDrawable(mContext)
                .icon(GoogleMaterial.Icon.gmd_payment)
                .sizeDp(24);

        mPayItem = mToolbar.getMenu().add(R.string.menu_pay);
        mPayItem.setIcon(payIcon);
        mPayItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        mPayItem.setOnMenuItemClickListener(this::paySelectedDebts);
        mPayItem.setVisible(false);
    }

    private void setupTabs() {
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

    private DebtsAdapter getUnpaidAdapter() {
        DebtsAdapter unpaidAdapter = new DebtsAdapter(mContext, mPerson.unpaid);
        unpaidAdapter.setOnClickListener(this::selectDebtForPaying);

        return unpaidAdapter;
    }

    private void updateAdapters() {
        mPaid.setAdapter(new DebtsAdapter(mContext, mPerson.paid));
        mUnpaid.setAdapter(getUnpaidAdapter());
    }

    private void setupRecyclers() {
        mPaid.setHasFixedSize(true);
        mPaid.setLayoutManager(new LinearLayoutManager(mContext));
        mPaid.addItemDecoration(new DividerItemDecoration(mContext, null));

        mUnpaid.setHasFixedSize(true);
        mUnpaid.setLayoutManager(new LinearLayoutManager(mContext));
        mUnpaid.addItemDecoration(new DividerItemDecoration(mContext, null));

        updateAdapters();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.person_layout, container, false);

        mPeople = new PeopleDatabaseHelper(getActivity());
        mPerson = mPeople.findPerson(getArguments().getLong("id"));

        findViews(view);

        setupToolbar();
        setupTabs();
        setupRecyclers();

        mName.setText(mPerson.name);
        mIban.setText(Iban.valueOf(mPerson.iban).toFormattedString());
        mBic.setText(mPerson.bic);

        return view;
    }
}
