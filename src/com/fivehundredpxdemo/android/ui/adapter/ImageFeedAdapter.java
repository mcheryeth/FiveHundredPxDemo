package com.fivehundredpxdemo.android.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.fivehundredpxdemo.android.model.Photo;
import com.fivehundredpxdemo.android.storage.DiskBitmapCache;

import java.util.List;

/**
 * Created by mcheryeth on 7/20/13.
 */
public class ImageFeedAdapter extends BaseAdapter {
    private Context mContext;
    private List<Photo> photos;
    private ImageLoader imageLoader; //volley's image loader

    public ImageFeedAdapter(Context c, List<Photo> photos) {
        mContext = c;
        this.photos = photos;
        RequestQueue requestQueue = Volley.newRequestQueue(c);
        imageLoader = new ImageLoader(requestQueue, new DiskBitmapCache(c.getCacheDir()));  //use disk cache
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public int getCount() {
        return photos.size();
    }

    public Photo getItem(int position) {
        return photos.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public void clearPhotos(){
        photos.clear();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        //ViewHolder viewHolder;
        Photo photo = (Photo)getItem(position);
        NetworkImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new NetworkImageView(mContext); //ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setPadding(5, 5, 5, 5);
        } else {
            imageView = (NetworkImageView) convertView;
        }

        //imageFetcher.loadThumbnailImage(photo.getImage_url(), imageView);
        imageView.setImageUrl(photo.getImage_url()[0], imageLoader);
        return imageView;


    }

}
