package fi.huulivoide.velkoja;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

@EFragment
public class PeopleListFragment extends BackHandledFragment
{
    private Context mContext;
    private Toolbar mToolbar;
    private RecyclerView mList;
    private PeopleAdapter mAdapter;
    private MenuItem mSearchItem;

    private PeopleDatabaseHelper mPeople;

    /**
     * Filters the list when text is entered in the SearchView.
     */
    private SearchView.OnQueryTextListener searchQueryListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String s)
        {
            mAdapter.updateCursor(mPeople.query(s));
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String s)
        {
            onQueryTextChange(s);
            return true;
        }
    };

    /**
     * Clears the filtering on the list when user leaves the search mode.
     */
    private MenuItemCompat.OnActionExpandListener searchExpandListener = new MenuItemCompat.OnActionExpandListener() {
        @Override
        public boolean onMenuItemActionCollapse(MenuItem menuItem) {
            SearchView sv = ((SearchView) menuItem.getActionView());
            sv.setQuery("", true);

            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(sv.getWindowToken(), 0);

            return true;
        }

        @Override
        public boolean onMenuItemActionExpand(MenuItem menuItem) {
            ((SearchView) menuItem.getActionView()).setIconified(false);
            return true;
        }
    };

    /**
     * Activate a new NewPersonFragment when the floating add button is clicked.
     */
    @Click(R.id.new_person_fab)
    protected void createNewPerson() {
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.enter_from_left, R.animator.exit_to_right,
                                     R.animator.enter_from_right, R.animator.exit_to_left)
                .replace(R.id.content_frame, new NewPersonFragment_())
                .addToBackStack(null)
                .commit();
    }

    /**
     * Initialize and decorate the actual list widget.
     */
    private void setupList() {
        mAdapter = mPeople.getAdapter();

        mList.setHasFixedSize(true);
        mList.addItemDecoration(new DividerItemDecoration(mContext, null));
        mList.setLayoutManager(new LinearLayoutManager(mContext));
        mList.setAdapter(mAdapter);
    }

    /**
     * Create and add openDrawer and search items to the toolbar.
     */
    private void setupToolbar() {
        mToolbar.setTitle(R.string.new_person_title);

        SearchView sv = new SearchView(mContext);
        sv.setOnQueryTextListener(searchQueryListener);

        Drawable searchIcon = new IconicsDrawable(mContext)
                .icon(GoogleMaterial.Icon.gmd_search)
                .sizeDp(24);

        mSearchItem = mToolbar.getMenu().add(getString(R.string.menu_search));
        mSearchItem.setIcon(searchIcon);
        mSearchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        mSearchItem.setActionView(sv);
        MenuItemCompat.setOnActionExpandListener(mSearchItem, searchExpandListener);

        Drawable backIcon = new IconicsDrawable(mContext)
                .icon(GoogleMaterial.Icon.gmd_menu)
                .sizeDp(24);
        mToolbar.setNavigationIcon(backIcon);
        mToolbar.setNavigationOnClickListener((p) -> ((VelkojaActivity) getActivity()).openDrawer());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mContext = container.getContext();
        mPeople = new PeopleDatabaseHelper(container.getContext());

        View v = inflater.inflate(R.layout.people_list_layout, container, false);

        mToolbar = (Toolbar) v.findViewById(R.id.toolbar_people);
        mList = (RecyclerView) v.findViewById(R.id.people_list);

        setupList();
        setupToolbar();

        return v;
    }

    @Override
    public boolean onBackPressed() {
        if (mSearchItem.isActionViewExpanded()) {
            mSearchItem.collapseActionView();
            return true;
        }

        return false;
    }
}
