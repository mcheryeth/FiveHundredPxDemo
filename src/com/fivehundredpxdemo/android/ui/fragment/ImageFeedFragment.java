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
import com.fivehundredpxdemo.android.Application;
import com.fivehundredpxdemo.android.R;
import com.fivehundredpxdemo.android.imageloader.ImageFetcher;
import com.fivehundredpxdemo.android.model.Photo;
import com.fivehundredpxdemo.android.service.PhotoServiceApi;
import com.fivehundredpxdemo.android.ui.ImageDetailActivity;
import com.fivehundredpxdemo.android.ui.LoginActivity;
import com.fivehundredpxdemo.android.ui.adapter.ImageFeedAdapter;
import com.fivehundredpxdemo.android.ui.adapter.ImageFeedTitleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mcheryeth on 7/20/13.
 */
public class ImageFeedFragment extends SherlockFragment implements PhotoServiceApi.PhotoServiceApiDelegate {

    private PhotoServiceApi mPhotoServiceApi;
    private String[] categories;
    private String selectedCategory;
    private String[] features;
    private String selectedFeature;
    private GridView gridView;
    private ImageFetcher imageFetcher;
    protected ActionBar supportActionBar;
    private String accessToken;
    private ImageFeedParentActivity parentActivity;

    private int currentPage;
    private boolean isLoadingInBackground;
    private boolean isFirstLoad;

    private final static int IMAGE_FEED_THUMBNAIL = IMAGE_SIZE.IMAGE_280x280.getPosition() + 1;
    private final static int IMAGE_DETAIL_THUMBNAIL = IMAGE_SIZE.IMAGE_900x900.getPosition() + 1;
    private static final String TAG = ImageFeedFragment.class.getName();
    public static final String EXTRA_CURRENT_USER_TOKEN = "extra_current_user_token";
    public static final String EXTRA_SELECTED_FEATURE = "extra_selected_feature";

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

//        SpinnerAdapter photosCategoryAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.photos_category,
//                android.R.layout.simple_spinner_dropdown_item);
        String[] menuFeatureItems = res.getStringArray(R.array.menu_photos_feature);
        ImageFeedTitleAdapter imageFeedTitleAdapter = new ImageFeedTitleAdapter(getSherlockActivity(), menuFeatureItems[extraFeature], R.array.photos_category,
                android.R.layout.simple_spinner_dropdown_item);

        ActionBar.OnNavigationListener onNavigationListener = new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int position, long itemId) {
                refreshPhotos(selectedFeature, categories[position]);
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
        imageFetcher = ImageFetcher.getImageFetcher(getActivity(), accessToken);
        imageFetcher.setImageFadeIn(true);

        gridView = (GridView) getActivity().findViewById(R.id.gridview);
        gridView.setAdapter(new ImageFeedAdapter(getActivity(), new ArrayList<Photo>(), imageFetcher));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public 	void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
                ImageFeedAdapter imageFeedAdapter = (ImageFeedAdapter)gridView.getAdapter();
                Photo currentPhoto = (Photo)imageFeedAdapter.getItem(position);
                Intent imageDetailIntent = new Intent(getActivity(), ImageDetailActivity.class);
                imageDetailIntent.putExtra(ImageDetailActivity.EXTRA_CURRENT_PHOTO, currentPhoto);
                startActivity(imageDetailIntent);

            }
        });

        //consumerKey = getString(R.string.px_consumer_key);
        //Log.d(ImageFeedActivity.class.getName(), "Access token is: " + token);
        isFirstLoad = true;
        refreshPhotos(selectedFeature, selectedCategory);


        //Infinite scroll: Load new images as the user scrolls to the bottom
        prepareEndlessScroll();

        setHasOptionsMenu(true);
    }

    /**
     * Fetch and populate photos asynchronously
     */
    private void refreshPhotos(String feature, String category){
        if(!category.equals(selectedCategory) || !feature.equals(selectedFeature)){
            ImageFeedAdapter adapter = (ImageFeedAdapter) gridView.getAdapter();
            adapter.clearPhotos();
            //mPhotoServiceApi.cancelRequests();
            currentPage = 1;
            //We have switch feature or category. Clear our grid to load new photos
            gridView.setAdapter(new ImageFeedAdapter(getActivity(), new ArrayList<Photo>(), imageFetcher));
        }
        selectedFeature = feature;
        selectedCategory = category;

        showProgress(true);
        isLoadingInBackground = true;

        mPhotoServiceApi.fetchPhotosFromNetwork(selectedFeature, "rating",
                IMAGE_FEED_THUMBNAIL, IMAGE_DETAIL_THUMBNAIL, currentPage, selectedCategory, accessToken);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getSherlockActivity().getSupportMenuInflater().inflate(R.menu.activity_main, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (parentActivity.isDrawerOpen()) {
//            supportActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//            supportActionBar.setDisplayShowTitleEnabled(true);
            showProgress(false);
            //supportActionBar.setTitle(YammerApplication.getInstance().getModel().getSelectedNetwork().getName());
        }
//        else {
//            supportActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//            supportActionBar.setDisplayShowTitleEnabled(false);
//            supportActionBar.setTitle("");
//        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        SharedPreferences preferences = getActivity().getSharedPreferences(Application.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(Application.PREF_ACCESS_TOKEN);
        editor.remove(Application.PREF_TOKEN_SECRET);
        editor.commit();

        Intent i = new Intent(getActivity(), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

    }

    /**
     * Infinite scroll: Load new images as the user scrolls to the bottom
     */
    protected void prepareEndlessScroll() {

        gridView.setOnScrollListener(new GridView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // nada
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean isOlderAvailable = totalItemCount < PhotoServiceApi.MAX_PHOTOS;
                if (!isFirstLoad && !isLoadingInBackground && (firstVisibleItem + visibleItemCount >= totalItemCount) && isOlderAvailable) {

                    //fetch next page of photos
                    currentPage++;
                    Log.d(TAG, "firstVisibleItem=" + firstVisibleItem + ", visibleItemCount=" + visibleItemCount + ", totalItemCount=" + totalItemCount);
                    Log.d(TAG, "Loading more photos....page:" + currentPage);

                    refreshPhotos(selectedFeature, selectedCategory);
                    //mPhotoServiceApi.fetchPhotosFromNetwork(selectedFeature, "rating", IMAGE_FEED_THUMBNAIL, currentPage, selectedCategory, CONSUMER_KEY);
                }
            }
        });
    }

    @Override
    public void onFetchPhotosComplete(List<Photo> photos) {
        if(photos!=null){
            if(gridView.getCount() < 1){
                gridView.setAdapter(new ImageFeedAdapter(getActivity(), photos, imageFetcher));
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
                //adapter.getPhotos().addAll(photos);
                adapter.notifyDataSetChanged();

            }
        }
        isFirstLoad = false;
        showProgress(false);
        isLoadingInBackground = false;
    }

    @Override
    public void onFetchPhotosError(String errorMsg) {
        showProgress(false);
        isLoadingInBackground = false;
        Toast.makeText(getActivity(), "Unable to load feed. " + errorMsg, Toast.LENGTH_LONG).show();
    }

    private void showProgress(boolean on){
        // This shows an indeterminate progress bar in the action bar
        getActivity().setProgressBarIndeterminateVisibility(on);
    }

    @Override
    public void onDestroy() {
        mPhotoServiceApi.cancelRequests();

        super.onDestroy();
    }
}
