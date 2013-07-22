package com.fivehundredpxdemo.android.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JacksonRequest;
import com.android.volley.toolbox.Volley;
import com.fivehundredpxdemo.android.FPXApplication;
import com.fivehundredpxdemo.android.R;
import com.fivehundredpxdemo.android.model.PhotoStream;

/**
 * Main controller that services 500px REST Api async requests and returns a stream of photos.
 *
 * Created by mcheryeth on 7/17/13.
 */
public class PhotoServiceApi implements IPhotoService{

    private static final String TAG = "PhotoServiceApi";

    private static final String BASE_URL = "https://api.500px.com/v1/photos?include_store=store_download&include_states=voted";
    private Context mContext;

    private RequestQueue mRequestQueue;
    private JacksonRequest currRequest;
    private PhotoServiceApiDelegate delegate;

    private boolean isLoading = false;

    public interface PhotoServiceApiDelegate {
        public void onFetchPhotosComplete(PhotoStream photoStream);
        public void onFetchPhotosError(String errorMsg);

    }

    public PhotoServiceApi(Context context, PhotoServiceApiDelegate delegate){
        this.delegate = delegate;
        mContext = context;
        mRequestQueue =  Volley.newRequestQueue(context);

    }

    @Override
    public void addRequest(String feature, String sortBy, int feedThumbnailSize, int detailThumbnailSize,
                           int page, String category, String token){

        String consumerKey = getConsumerKey();
        final String finalUrl = String.format("%s&feature=%s&sort=%s&image_size[]=%s&image_size[]=%s&page=%s&only=%s&consumer_key=%s", BASE_URL,
                feature, sortBy, String.valueOf(feedThumbnailSize), String.valueOf(detailThumbnailSize),
                String.valueOf(page), category, consumerKey);

        currRequest =  new JacksonRequest(finalUrl, PhotoStream.class, token, new Response.Listener<PhotoStream>() {
            @Override
            public void onResponse(PhotoStream response) {
                Log.d(TAG, "Fetched photo stream..");
                if(response!=null && isLoading){
                    Log.d(TAG, response.toString());
                    isLoading = false;
                    delegate.onFetchPhotosComplete(response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
                Log.w(TAG, "Could not fetch photos due to failure. " + error.getMessage());
                delegate.onFetchPhotosError(error.getMessage());
            }
        }
        );
        isLoading = true;
        mRequestQueue.add(currRequest);
        mRequestQueue.start();

    }

    @Override
    public void cancelRequests(){
        isLoading = false;
        mRequestQueue.stop();
        mRequestQueue.cancelAll(currRequest);
        currRequest = null;
    }


    public String getAccessToken(){
        SharedPreferences preferences = mContext.getSharedPreferences(FPXApplication.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(FPXApplication.PREF_ACCESS_TOKEN, null);
    }

    public String getConsumerKey(){
        return mContext.getString(R.string.px_consumer_key);
    }

}
