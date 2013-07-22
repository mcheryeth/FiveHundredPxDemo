package com.fivehundredpxdemo.android.storage;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by mcheryeth on 7/18/13.
 */
public class BitmapCache extends LruCache implements ImageLoader.ImageCache {
    public BitmapCache(int maxSize) {
        super(maxSize);
    }

    @Override
    public Bitmap getBitmap(String url) {
        return (Bitmap) get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}
