/**
 * 
 */
package com.fivehundredpxdemo.android.ui;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.fivehundredpxdemo.android.R;
import com.fivehundredpxdemo.android.model.CurrentUser;
import com.fivehundredpxdemo.android.ui.fragment.ImageFeedFragment;
import com.fivehundredpxdemo.android.ui.fragment.MenuListFragment;
import com.google.inject.Inject;
import com.sherlock.roboguice.activity.RoboSherlockFragmentActivity;

/**
 * This is the main photo feed activity that displays a list of photos on a grid. Uses a side drawer to select the
 * appropriate feature and a drop down menu to select the category
 * @author mcheryeth
 *
 */
public class ImageFeedActivity extends RoboSherlockFragmentActivity implements MenuListFragment.ParentMenuActivity, ImageFeedFragment.ImageFeedParentActivity{

    @Inject CurrentUser user;

    private static final String TAG = ImageFeedActivity.class.getName();
    private ActionBarDrawerToggle drawerToggle;

    protected DrawerLayout drawerLayout;
    protected ActionBar supportActionBar;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(getLayout());

        supportActionBar = getSupportActionBar();
        //supportActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeButtonEnabled(true);
        supportActionBar.show();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                supportActionBar.setTitle(user.getFullname());
                supportActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                supportActionBar.setTitle("");
                supportActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);


        ImageFeedFragment imageFeedFragment = new ImageFeedFragment();
        String defaultFeature = getResources().getStringArray(R.array.menu_photos_feature)[0];
        Bundle extras = new Bundle();
        extras.putString(ImageFeedFragment.EXTRA_CURRENT_USER_TOKEN, user.accessToken.getToken());
        extras.putInt(ImageFeedFragment.EXTRA_SELECTED_FEATURE, 0);
        imageFeedFragment.setArguments(extras);

        loadFragment(imageFeedFragment, defaultFeature);

	}

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // syncState() method should be called onPostCreate.
        // for more details, see Android Developer Site
        // http://developer.android.com/reference/android/support/v4/app/ActionBarDrawerToggle.html#syncState()
        drawerToggle.syncState();
    }

    public void loadFragment(Fragment frag, String tag) {
        //here we just replace the current fragment with the new fragment since we are already in Home
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, frag, tag);
        fragmentTransaction.commit();
    }

    @Override
    public void loadFeature(int featureIndex){
        ImageFeedFragment imageFeedFragment = new ImageFeedFragment();
        String defaultFeature = getResources().getStringArray(R.array.menu_photos_feature)[featureIndex];
        Bundle extras = new Bundle();
        extras.putString(ImageFeedFragment.EXTRA_CURRENT_USER_TOKEN, user.accessToken.getToken());
        extras.putInt(ImageFeedFragment.EXTRA_SELECTED_FEATURE, featureIndex);
        imageFeedFragment.setArguments(extras);

        loadFragment(imageFeedFragment, defaultFeature);

    }

    protected int getLayout() {
        return R.layout.home_layout;
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                toggleMenu();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void toggleMenu() {
        if (!isDrawerOpen()) {
            drawerLayout.openDrawer(Gravity.START);
        } else {
            drawerLayout.closeDrawers();
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean isDrawerOpen() {
        return drawerLayout != null ? drawerLayout.isDrawerOpen(Gravity.START) : false;
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }
}
