package com.fivehundredpxdemo.android.controller;

/**
 * Created by mcheryeth on 7/21/13.
 */
public interface IPhotoService {

    public void addRequest(String feature, String sortBy, int feedThumbnailSize, int detailThumbnailSize,
                                       int page, String category, String token);

    public void cancelRequests();


}
