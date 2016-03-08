package fi.huulivoide.velkoja;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsFragment;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import fi.huulivoide.velkoja.ui.BackHandledFragment;

import org.androidannotations.annotations.EActivity;

@EActivity
public class VelkojaActivity extends AppCompatActivity implements BackHandledFragment.BackHandlerInterface
{
    private Drawer mDrawer;
    private PrimaryDrawerItem mMenuPeople;
    private PrimaryDrawerItem mMenuAbout;

    private Fragment mCurrentFragment;

    private Long mPersonTobeDeleted;

    private LibsFragment buildAbouFragment() {
        return new LibsBuilder()
                .withLibraries("androidannotations")
                .withFields(R.string.class.getFields())
                .fragment();
    }

    private boolean onMenuItemClick(View view, int position, IDrawerItem drawerItem) {
        Fragment frag;

        if (drawerItem == mMenuPeople) {
            frag = new PeopleListFragment_();
        } else if (drawerItem == mMenuAbout){
            frag = buildAbouFragment();
        } else {
            return false;
        }

        getFragmentManager().beginTransaction().replace(R.id.content_frame, frag).commit();
        mDrawer.closeDrawer();

        return true;
    }

    private void setupDrawer() {
        mMenuPeople = new PrimaryDrawerItem()
                .withName(getResources().getString(R.string.dmi_people_list))
                .withIcon(GoogleMaterial.Icon.gmd_people);

        mMenuAbout = new PrimaryDrawerItem()
                .withName(getResources().getString(R.string.dmi_about))
                .withIcon(GoogleMaterial.Icon.gmd_help);

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .addDrawerItems(mMenuPeople, new DividerDrawerItem(), mMenuAbout)
                .withOnDrawerItemClickListener(this::onMenuItemClick)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        setupDrawer();
        onMenuItemClick(null, 0, mMenuPeople);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        if (mCurrentFragment instanceof BackHandledFragment && !((BackHandledFragment)mCurrentFragment).onBackPressed())
        {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                this.finish();
            } else {
                getFragmentManager().popBackStack();
            }
        }
    }

    public void openDrawer() {
        mDrawer.openDrawer();
    }

    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment)
    {
        mCurrentFragment = backHandledFragment;
    }

    public void queuePersonForDeletion(long id) {
        mPersonTobeDeleted = id;
    }

    public Long personQueuedForDeletion() {
        return mPersonTobeDeleted;
    }

    public void clearDeletionQueue() {
        mPersonTobeDeleted = null;
    }
}
