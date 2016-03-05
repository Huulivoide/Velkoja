package fi.huulivoide.velkoja;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsFragment;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import org.androidannotations.annotations.EActivity;

@EActivity
public class VelkojaActivity extends Activity
{
    private Drawer mDrawer;
    private PrimaryDrawerItem mMenuPeople;
    private PrimaryDrawerItem mMenuDebts;
    private PrimaryDrawerItem mMenuAbout;

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
        } else if (drawerItem == mMenuDebts){
            frag = new DebtsFragment_();
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

        mMenuDebts = new PrimaryDrawerItem()
                .withName(getResources().getString(R.string.dmi_debts))
                .withIcon(GoogleMaterial.Icon.gmd_monetization_on);

        mMenuAbout = new PrimaryDrawerItem()
                .withName(getResources().getString(R.string.dmi_about))
                .withIcon(GoogleMaterial.Icon.gmd_help);

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .addDrawerItems(mMenuPeople, mMenuDebts, new DividerDrawerItem(), mMenuAbout)
                .withOnDrawerItemClickListener(this::onMenuItemClick)
                .build();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        setupDrawer();
    }
}
