package com.fivehundredpxdemo.android.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Created by mcheryeth on 7/17/13.
 */
public class Filter implements Serializable {

    @JsonProperty("category")
    private int category;

    @JsonProperty("exclude")
    private boolean exclude;

    public int getCategory() {
        return category;
    }

    public boolean isExclude() {
        return exclude;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "category=" + category +
                ", exclude=" + exclude +
                '}';
    }
}
