/**
 * 
 */
package com.fivehundredpxdemo.android.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.fivehundredpxdemo.Application;
import com.fivehundredpxdemo.android.constants.Category;
import com.fivehundredpxdemo.android.constants.Feature;
import com.fivehundredpxdemo.android.imageloader.DiskBitmapCache;
import com.fivehundredpxdemo.android.imageloader.ImageFetcher;
import com.fivehundredpxdemo.android.model.CurrentUser;
import com.fivehundredpxdemo.android.model.Photo;
import com.fivehundredpxdemo.android.service.PhotoServiceApi;
import com.fivehundredpxdemo.android.R;
import com.google.inject.Inject;
import com.sherlock.roboguice.activity.RoboSherlockFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mcheryeth
 *
 */
public class ImageFeedActivity extends RoboSherlockFragmentActivity implements PhotoServiceApi.PhotoServiceApiDelegate {

    @Inject CurrentUser user;

    private PhotoServiceApi mPhotoServiceApi;

    private int currentPage = 1;
    private int currentImageSize = 3; //280 x 280
    private String selectedCategory = Category.LANDSCAPES; //default
    private String selectedFeature = Feature.POPULAR; //default
    private GridView gridView;
    private ImageFetcher imageFetcher;

    private final static String CONSUMER_KEY = "vFlLsHWW5WfvrCoYMLqtgIe5sOZgaLJNs7Rd4R57";

    private boolean isLoadingInBackground;
    private static final String TAG = ImageFeedActivity.class.getName();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	    setContentView(R.layout.activity_main);

        setTitle(user.fullname);

        if(mPhotoServiceApi==null){
            mPhotoServiceApi = new PhotoServiceApi(this, this);
        }

        imageFetcher = ImageFetcher.getImageFetcher(this, user.accessToken.getToken());
        imageFetcher.setImageFadeIn(false);

        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new ImageAdapter(this, new ArrayList<Photo>()));

        gridView.setOnItemClickListener(new OnItemClickListener() {
	        public 	void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Toast.makeText(ImageFeedActivity.this, "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });

        prepareEndlessScroll();

        //consumerKey = getString(R.string.px_consumer_key);
        //Log.d(ImageFeedActivity.class.getName(), "Access token is: " + token);

        mPhotoServiceApi.asyncFetchPhotos(selectedFeature, "rating", currentImageSize, currentPage, selectedCategory, CONSUMER_KEY);
        isLoadingInBackground = true;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                SharedPreferences preferences = getSharedPreferences(Application.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(Application.PREF_ACCES_TOKEN);
                editor.remove(Application.PREF_TOKEN_SECRET);
                editor.commit();

                Intent i = new Intent(ImageFeedActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void prepareEndlessScroll() {

        gridView.setOnScrollListener(new GridView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view,
                                             int scrollState) {
                // nada
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean isOlderAvailable = totalItemCount < PhotoServiceApi.MAX_PHOTOS;
                if (!isLoadingInBackground && (firstVisibleItem + visibleItemCount >= totalItemCount) && isOlderAvailable) {
                    isLoadingInBackground = true;
                    //fetch next page of photos
                    currentPage++;
                    Log.d(TAG, "firstVisibleItem="+firstVisibleItem + ", visibleItemCount="+visibleItemCount + ", totalItemCount="+totalItemCount);
                    Log.d(TAG, "Loading more photos....page:" + currentPage);
                    mPhotoServiceApi.asyncFetchPhotos(selectedFeature, "rating", currentImageSize, currentPage, selectedCategory, CONSUMER_KEY);
                }
            }
        });
    }

    @Override
    public void onFetchPhotosComplete(List<Photo> photos) {
        if(photos!=null){
            if(gridView.getCount() < 1){
                gridView.setAdapter(new ImageAdapter(this, photos));
            }
            else {
                //append photos to the bottom
                ImageAdapter adapter = (ImageAdapter) gridView.getAdapter();
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
        isLoadingInBackground = false;
    }


    public class ImageAdapter extends BaseAdapter {
	    private Context mContext;
        private List<Photo> photos;
        private ImageLoader imageLoader;

	    public ImageAdapter(Context c, List<Photo> photos) {
	        mContext = c;
            this.photos = photos;
            RequestQueue requestQueue = Volley.newRequestQueue(c);
            imageLoader = new ImageLoader(requestQueue, new DiskBitmapCache(getCacheDir()));
	    }

        public List<Photo> getPhotos() {
            return photos;
        }

	    public int getCount() {
	        return photos.size();
	    }

	    public Object getItem(int position) {
	        return photos.get(position);
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	        //ViewHolder viewHolder;
            Photo photo = (Photo)getItem(position);
            NetworkImageView imageView;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
                //viewHolder = new ViewHolder();
                //convertView = lf.inflate(R.layout.row_listview,null);

                imageView = new NetworkImageView(mContext); //ImageView(mContext);
	            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
	            imageView.setScaleType(ImageView.ScaleType.CENTER);
	            imageView.setPadding(8, 8, 8, 8);
	        } else {
	            imageView = (NetworkImageView) convertView;
	        }

	        imageView.setImageUrl(photo.getImage_url(), imageLoader);
	        return imageView;


	    }

        public class ViewHolder {
            ImageView thumbnailView;
        }
	}

}
