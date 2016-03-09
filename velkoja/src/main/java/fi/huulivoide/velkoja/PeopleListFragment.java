package fi.huulivoide.velkoja;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import fi.huulivoide.velkoja.adapters.PeopleAdapter;
import fi.huulivoide.velkoja.model.PeopleDatabaseHelper;
import fi.huulivoide.velkoja.model.Person;
import fi.huulivoide.velkoja.ui.BackHandledFragment;
import fi.huulivoide.velkoja.ui.DividerItemDecoration;
import fi.huulivoide.velkoja.ui.PersonItemView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.people_list_layout)
public class PeopleListFragment extends BackHandledFragment
{
    private PeopleDatabaseHelper mPeople;
    private PeopleAdapter mAdapter;
    private MenuItem mSearchItem;

    @ViewById(R.id.toolbar_people)
    protected Toolbar mToolbar;

    @ViewById(R.id.people_list)
    protected RecyclerView mList;

    /**
     * Initialize and decorate the actual list widget.
     */
    @AfterViews
    protected void setupList() {
        mPeople = new PeopleDatabaseHelper(getActivity());
        mAdapter = mPeople.getAdapter();
        mAdapter.setOnClickListener(this::showPerson);

        mList.setHasFixedSize(true);
        mList.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        mList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mList.setAdapter(mAdapter);
    }

    /**
     * Create and add openDrawer and search items to the toolbar.
     */
    @AfterViews
    protected void setupToolbar() {
        mToolbar.setTitle(R.string.people_list_title);

        SearchView sv = new SearchView(getActivity());
        sv.setOnQueryTextListener(searchQueryListener);

        Drawable searchIcon = new IconicsDrawable(getActivity())
                .icon(GoogleMaterial.Icon.gmd_search)
                .sizeDp(24);

        mSearchItem = mToolbar.getMenu().add(getString(R.string.menu_search));
        mSearchItem.setIcon(searchIcon);
        mSearchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        mSearchItem.setActionView(sv);
        MenuItemCompat.setOnActionExpandListener(mSearchItem, searchExpandListener);

        Drawable backIcon = new IconicsDrawable(getActivity())
                .icon(GoogleMaterial.Icon.gmd_menu)
                .sizeDp(24);
        mToolbar.setNavigationIcon(backIcon);
        mToolbar.setNavigationOnClickListener((p) -> ((VelkojaActivity) getActivity()).openDrawer());

    }

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

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
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

    private void deletePerson(long id) {
        Person deleted = mPeople.delete(id);
        Snackbar undo = Snackbar.make(getView(), R.string.person_deleted, Snackbar.LENGTH_INDEFINITE);
        undo.setAction(R.string.undo, (v) -> {
            mPeople.insert(deleted);
            searchQueryListener.onQueryTextChange("");
        });
        searchQueryListener.onQueryTextChange("");
        undo.show();
    }

    private void showPerson(View view) {
        PersonFragment frag = new PersonFragment_();
        Bundle args = new Bundle();
        args.putLong("id", ((PersonItemView) view).getPersonId());
        frag.setArguments(args);

        getFragmentManager()
            .beginTransaction()
            .setCustomAnimations(R.animator.enter_from_left, R.animator.exit_to_right,
                    R.animator.enter_from_right, R.animator.exit_to_left)
            .replace(R.id.content_frame, frag)
            .addToBackStack(null)
            .commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check if we have a pending delete person request
        VelkojaActivity activity = (VelkojaActivity) getActivity();
        Long person = activity.personQueuedForDeletion();
        if (person != null) {
            deletePerson(person);
            activity.clearDeletionQueue();
        }
    }

    @Override
    public boolean onBackPressed() {
        // Hide the search bar
        if (mSearchItem.isActionViewExpanded()) {
            mSearchItem.collapseActionView();
            return true;
        }

        return false;
    }
}
