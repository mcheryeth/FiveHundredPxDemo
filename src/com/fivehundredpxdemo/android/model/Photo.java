package com.fivehundredpxdemo.android.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Created by mcheryeth on 7/17/13.
 * Photo object that maps to the 500px REST API
 */
public class Photo implements Serializable{

    @JsonProperty("id")
    private long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("times_viewed")
    private int times_viewed;

    @JsonProperty("rating")
    private float rating;

    @JsonProperty("created_at")
    private String created_at;

    @JsonProperty("category")
    private int category;

    @JsonProperty("privacy")
    private boolean privacy;

    @JsonProperty("width")
    private int width;

    @JsonProperty("height")
    private int height;

    @JsonProperty("votes_count")
    private int votes_count;

    @JsonProperty("favorites_count")
    private int favorites_count;

    @JsonProperty("comments_count")
    private int comments_count;

    @JsonProperty("nsfw")
    private boolean nsfw;

    @JsonProperty("license_type")
    private int license_type;

    @JsonProperty("image_url")
    private String image_url;

    @JsonProperty("store_download")
    private boolean store_download;

    @JsonProperty("store_print")
    private boolean store_print;

    @JsonProperty("voted")
    private boolean voted;

    @JsonProperty("favorited")
    private boolean favorited;

    @JsonProperty("purchased")
    private boolean purchased;

    @JsonProperty("user")
    private User user;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getTimes_viewed() {
        return times_viewed;
    }

    public float getRating() {
        return rating;
    }

    public String getCreated_at() {
        return created_at;
    }

    public int getCategory() {
        return category;
    }

    public boolean isPrivacy() {
        return privacy;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getVotes_count() {
        return votes_count;
    }

    public int getFavorites_count() {
        return favorites_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public int getLicense_type() {
        return license_type;
    }

    public String getImage_url() {
        return image_url;
    }

    public boolean isStore_download() {
        return store_download;
    }

    public boolean isStore_print() {
        return store_print;
    }

    public boolean isVoted() {
        return voted;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", times_viewed=" + times_viewed +
                ", rating=" + rating +
                ", created_at='" + created_at + '\'' +
                ", category=" + category +
                ", privacy=" + privacy +
                ", width=" + width +
                ", height=" + height +
                ", votes_count=" + votes_count +
                ", favorites_count=" + favorites_count +
                ", comments_count=" + comments_count +
                ", nsfw=" + nsfw +
                ", license_type=" + license_type +
                ", image_url='" + image_url + '\'' +
                ", store_download=" + store_download +
                ", store_print=" + store_print +
                ", voted=" + voted +
                ", favorited=" + favorited +
                ", purchased=" + purchased +
                ", user=" + user +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Photo photo = (Photo) o;

        if (id != photo.id) return false;
        if (!image_url.equals(photo.image_url)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + image_url.hashCode();
        return result;
    }
}
