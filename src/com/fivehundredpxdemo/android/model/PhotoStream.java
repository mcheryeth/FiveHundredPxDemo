package com.fivehundredpxdemo.android.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mcheryeth on 7/17/13.
 * Photo stream object that contains a list of photos and other meta data that maps to the 500px REST API
 */
public class PhotoStream implements Serializable{

    @JsonProperty("feature")
    private String feature;

    @JsonProperty("filters")
    private Filter filters;

    @JsonProperty("current_page")
    private int current_page;

    @JsonProperty("total_pages")
    private int total_pages;

    @JsonProperty("total_items")
    private int total_items;

    @JsonProperty("photos")
    private List<Photo> photos;

    public List<Photo> getPhotos() {
        return photos;
    }

    public int getTotal_items() {
        return total_items;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public Filter getFilters() {
        return filters;
    }

    public String getFeature() {
        return feature;
    }

    @Override
    public String toString() {
        return "PhotoStream{" +
                "feature='" + feature + '\'' +
                ", filters=" + filters +
                ", current_page=" + current_page +
                ", total_pages=" + total_pages +
                ", total_items=" + total_items +
                ", photos=" + photos +
                '}';
    }

}
