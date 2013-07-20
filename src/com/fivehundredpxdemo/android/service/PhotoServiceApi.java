package com.fivehundredpxdemo.android.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JacksonRequest;
import com.android.volley.toolbox.Volley;
import com.fivehundredpxdemo.Application;
import com.fivehundredpxdemo.android.model.Photo;
import com.fivehundredpxdemo.android.model.PhotoStream;

import java.util.List;

/**
 * Created by mcheryeth on 7/17/13.
 */
public class PhotoServiceApi {

    private static final String TAG = "PhotoServiceApi";

    public static final int MAX_PHOTOS = 600;
    private static final String BASE_URL = "https://api.500px.com/v1/photos?include_store=store_download&include_states=voted";
    private Context mContext;

    //private static PhotoServiceApi mPhotoServiceApi;
    private RequestQueue mRequestQueue;
    private PhotoServiceApiDelegate delegate;

    public interface PhotoServiceApiDelegate {
        public void onFetchPhotosComplete(List<Photo> photos);

    }

    public PhotoServiceApi(Context context, PhotoServiceApiDelegate delegate){
        this.delegate = delegate;
        mContext = context;
        mRequestQueue =  Volley.newRequestQueue(context);

    }

    public void asyncFetchPhotos(String feature, String sortBy, int imageSize, int page, String category, String consumerKey){

        final String finalUrl = String.format("%s&feature=%s&sort=%s&image_size=%s&page=%s&only=%s&consumer_key=%s", BASE_URL,
                feature, sortBy, String.valueOf(imageSize), String.valueOf(page), category, consumerKey);

        mRequestQueue.add(
                new JacksonRequest(finalUrl, PhotoStream.class, "", new Response.Listener<PhotoStream>() {
                    @Override
                    public void onResponse(PhotoStream response) {
                        Log.d(TAG, "Fetched photo stream..");
                        if(response!=null){
                            Log.d(TAG, response.toString());
                               delegate.onFetchPhotosComplete(response.getPhotos());
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w(TAG, "Could not fetch photos due to failure. " + error.getMessage());
                    }
                }
        ));
        mRequestQueue.start();


    }

    public String getAccessToken(){
        SharedPreferences preferences = mContext.getSharedPreferences(Application.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(Application.PREF_ACCES_TOKEN, null);
    }

//    public static PhotoServiceApi getSingleton(){
//        if(mPhotoServiceApi==null){
//            mPhotoServiceApi = new PhotoServiceApi();
//        }
//        return mPhotoServiceApi;
//    }



}
