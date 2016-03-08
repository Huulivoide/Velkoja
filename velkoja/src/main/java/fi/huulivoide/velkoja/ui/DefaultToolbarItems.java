package fi.huulivoide.velkoja.ui;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import android.view.inputmethod.InputMethodManager;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import fi.huulivoide.velkoja.R;

public class DefaultToolbarItems {
    public static void addAccept(Context context, Toolbar toolbar, MenuItem.OnMenuItemClickListener action) {
        Drawable saveIcon = new IconicsDrawable(context)
            .icon(GoogleMaterial.Icon.gmd_done)
            .sizeDp(24);

        MenuItem saveItem = toolbar.getMenu().add(context.getString(R.string.menu_save));
        saveItem.setIcon(saveIcon);
        saveItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        saveItem.setOnMenuItemClickListener(action);
    }

    public static void addBack(Fragment frag, Toolbar toolbar) {
        Drawable backIcon = new IconicsDrawable(frag.getActivity())
            .icon(GoogleMaterial.Icon.gmd_arrow_back)
            .sizeDp(24);

        toolbar.setNavigationIcon(backIcon);
        toolbar.setNavigationOnClickListener((v) -> {
            InputMethodManager imm = (InputMethodManager) frag.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(toolbar.getWindowToken(), 0);
            frag.getFragmentManager().popBackStack();
        });
    }
}
