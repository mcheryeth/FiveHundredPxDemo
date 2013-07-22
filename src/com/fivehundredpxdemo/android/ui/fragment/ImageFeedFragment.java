package com.fivehundredpxdemo.android.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.fivehundredpxdemo.android.FPXApplication;
import com.fivehundredpxdemo.android.R;
import com.fivehundredpxdemo.android.controller.PhotoServiceApi;
import com.fivehundredpxdemo.android.model.Photo;
import com.fivehundredpxdemo.android.model.PhotoStream;
import com.fivehundredpxdemo.android.ui.ImageDetailActivity;
import com.fivehundredpxdemo.android.ui.LoginActivity;
import com.fivehundredpxdemo.android.ui.adapter.ImageFeedAdapter;
import com.fivehundredpxdemo.android.ui.adapter.ImageFeedTitleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the main photo feed fragment that displays a list of photos on a grid. Uses a side drawer to select the
 * appropriate feature and a drop down menu to select the category
 * Created by mcheryeth on 7/20/13.
 */
public class ImageFeedFragment extends SherlockFragment implements PhotoServiceApi.PhotoServiceApiDelegate {

    private PhotoServiceApi mPhotoServiceApi;
    private String[] categories;
    private String selectedCategory;
    private String[] features;
    private String selectedFeature;
    private GridView gridView;
    protected ActionBar supportActionBar;
    private String accessToken;
    private ImageFeedParentActivity parentActivity;

    private int currentPage;
    private boolean isLoadingInBackground;
    private boolean isFirstLoad;
    private int totalPages;

    public static final String EXTRA_CURRENT_USER_TOKEN = "extra_current_user_token";
    public static final String EXTRA_SELECTED_FEATURE = "extra_selected_feature";

    private final static int IMAGE_FEED_THUMBNAIL = IMAGE_SIZE.IMAGE_280x280.getPosition() + 1;
    private final static int IMAGE_DETAIL_THUMBNAIL = IMAGE_SIZE.IMAGE_900x900.getPosition() + 1;
    public static final int MAX_PHOTOS = 600;

    private static final String TAG = ImageFeedFragment.class.getName();

    private enum IMAGE_SIZE {
        IMAGE_70x70(0), IMAGE_140x140(1), IMAGE_280x280(2), IMAGE_900x900(3);
        int position;

        IMAGE_SIZE(int position){
            this.position = position;
        }
        public int getPosition(){return position;}
    };

    public static interface ImageFeedParentActivity {
        boolean isDrawerOpen();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Call specific methods to parent activity through this interface.
        // Like check if drawer is open
        parentActivity = (ImageFeedParentActivity) getActivity();

        supportActionBar = getSherlockActivity().getSupportActionBar();
        supportActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.show();

        getActivity().setProgressBarIndeterminateVisibility(true);

        supportActionBar.setTitle("");

        // Process arguments
        Bundle extras = getArguments();
        if (extras == null) {
            extras = getActivity().getIntent().getExtras();
        }

        Resources res = getResources();
        categories = res.getStringArray(R.array.photos_category);
        selectedCategory = categories[0]; //default

        features = res.getStringArray(R.array.photos_feature);
        int extraFeature = extras.getInt(EXTRA_SELECTED_FEATURE);
        selectedFeature = features[extraFeature];
        currentPage = 1;

        String[] menuFeatureItems = res.getStringArray(R.array.menu_photos_feature);
        ImageFeedTitleAdapter imageFeedTitleAdapter = new ImageFeedTitleAdapter(getSherlockActivity(), menuFeatureItems[extraFeature], R.array.photos_category,
                android.R.layout.simple_spinner_dropdown_item);

        ActionBar.OnNavigationListener onNavigationListener = new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int position, long itemId) {
                fetchPhotos(selectedFeature, categories[position]);
                return true;

            }
        };
        supportActionBar.setListNavigationCallbacks(imageFeedTitleAdapter, onNavigationListener);
        supportActionBar.setSelectedNavigationItem(0);

        if(mPhotoServiceApi==null){
            mPhotoServiceApi = new PhotoServiceApi(getActivity(), this);
        }

        accessToken = extras.getString(EXTRA_CURRENT_USER_TOKEN);
        assert accessToken != null;

        gridView = (GridView) getActivity().findViewById(R.id.gridview);
        gridView.setAdapter(new ImageFeedAdapter(getActivity(), new ArrayList<Photo>()));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public 	void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageFeedAdapter imageFeedAdapter = (ImageFeedAdapter)gridView.getAdapter();
                Photo currentPhoto = (Photo)imageFeedAdapter.getItem(position);
                Intent imageDetailIntent = new Intent(getActivity(), ImageDetailActivity.class);
                imageDetailIntent.putExtra(ImageDetailActivity.EXTRA_CURRENT_PHOTO, currentPhoto);
                startActivity(imageDetailIntent);

            }
        });

        isFirstLoad = true;
        fetchPhotos(selectedFeature, selectedCategory);

        //Infinite scroll: Load new images as the user scrolls to the bottom
        prepareEndlessScroll();

        setHasOptionsMenu(true);
    }

    /**
     * Fetch and populate photos asynchronously
     */
    private void fetchPhotos(String feature, String category){
        if(!category.equals(selectedCategory) || !feature.equals(selectedFeature)){
            ImageFeedAdapter adapter = (ImageFeedAdapter) gridView.getAdapter();
            adapter.clearPhotos();
            currentPage = 1;
            //We have switch feature or category. Clear our grid to load new photos
            gridView.setAdapter(new ImageFeedAdapter(getActivity(), new ArrayList<Photo>()));
        }
        selectedFeature = feature;
        selectedCategory = category;

        showProgress(true);
        isLoadingInBackground = true;

        mPhotoServiceApi.addRequest(selectedFeature, "rating",
                IMAGE_FEED_THUMBNAIL, IMAGE_DETAIL_THUMBNAIL, currentPage, selectedCategory, accessToken);

    }

    private void reload(){
        ImageFeedAdapter adapter = (ImageFeedAdapter) gridView.getAdapter();
        adapter.clearPhotos();
        currentPage = 1;
        gridView.setAdapter(new ImageFeedAdapter(getActivity(), new ArrayList<Photo>()));

        fetchPhotos(selectedFeature, selectedCategory);

    }

    private void logout(){
        SharedPreferences preferences = getActivity().getSharedPreferences(FPXApplication.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(FPXApplication.PREF_ACCESS_TOKEN);
        editor.remove(FPXApplication.PREF_TOKEN_SECRET);
        editor.commit();

        Intent i = new Intent(getActivity(), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }

    /**
     * Infinite scroll: Load new images as the user scrolls to the bottom
     */
    private void prepareEndlessScroll() {

        gridView.setOnScrollListener(new GridView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // nada
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean isOlderAvailable = currentPage <= totalPages && totalItemCount < MAX_PHOTOS;
                if (!isFirstLoad && !isLoadingInBackground && (firstVisibleItem + visibleItemCount >= totalItemCount) && isOlderAvailable) {

                    //fetch next page of photos
                    currentPage++;
                    Log.d(TAG, "firstVisibleItem=" + firstVisibleItem + ", visibleItemCount=" + visibleItemCount + ", totalItemCount=" + totalItemCount);
                    Log.d(TAG, "Loading more photos....page:" + currentPage);

                    fetchPhotos(selectedFeature, selectedCategory);
                    //mPhotoServiceApi.addRequest(selectedFeature, "rating", IMAGE_FEED_THUMBNAIL, currentPage, selectedCategory, CONSUMER_KEY);
                }
            }
        });
    }

    @Override
    public void onFetchPhotosComplete(PhotoStream photoStream) {

        if(photoStream!=null){
            List<Photo>photos = photoStream.getPhotos();
            if(photos!=null){
                totalPages = photoStream.getTotal_pages();
                if(gridView.getCount() < 1){
                    gridView.setAdapter(new ImageFeedAdapter(getActivity(), photos));
                }
                else {
                    //append photos to the bottom
                    ImageFeedAdapter adapter = (ImageFeedAdapter) gridView.getAdapter();
                    List<Photo> currentPhotos = adapter.getPhotos();
                    for(Photo photo : photos){
                        if(!currentPhotos.contains(photo)){
                            currentPhotos.add(photo);
                        }
                    }
                    adapter.notifyDataSetChanged();

                }
            }
        }
        isFirstLoad = false;
        showProgress(false);
        isLoadingInBackground = false;
    }

    @Override
    public void onFetchPhotosError(String errorMsg) {
        isFirstLoad = false;
        showProgress(false);
        isLoadingInBackground = false;
        Toast.makeText(getActivity(), "Unable to load feed. " + errorMsg, Toast.LENGTH_LONG).show();
    }

    private void showProgress(boolean on){
        // This shows an indeterminate progress bar in the action bar
        if(getActivity()==null)
            return;

        getActivity().setProgressBarIndeterminateVisibility(on);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getSherlockActivity().getSupportMenuInflater().inflate(R.menu.feed_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (parentActivity.isDrawerOpen()) {
            showProgress(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logout();
                return true;
            case R.id.menu_refresh:
                reload();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        mPhotoServiceApi.cancelRequests();

        super.onDestroy();
    }
}
