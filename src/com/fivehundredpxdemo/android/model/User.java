package com.fivehundredpxdemo.android.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Created by mcheryeth on 7/17/13.
 * Photo stream object that contains a list of photos and other meta data that maps to the 500px REST API
 */

public class User implements Serializable{

    @JsonProperty("fullname")
	private String fullname;

    @JsonProperty("userpic_url")
	private String userpic_url;

    @JsonProperty("id")
    private long id;

    @JsonProperty("user_name")
    private String user_name;

    @JsonProperty("firstname")
    private String firstname;

    @JsonProperty("lastname")
    private String lastname;

    @JsonProperty("city")
    private String city;

    @JsonProperty("country")
    private String country;

    @JsonProperty("upgrade_status")
    private int upgrade_status;

    @JsonProperty("followers_count")
    private int followers_count;

    @JsonProperty("affection")
    private int affection;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUserpic_url() {
        return userpic_url;
    }

    public void setUserpic_url(String userpic_url) {
        this.userpic_url = userpic_url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getUpgrade_status() {
        return upgrade_status;
    }

    public void setUpgrade_status(int upgrade_status) {
        this.upgrade_status = upgrade_status;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public int getAffection() {
        return affection;
    }

    public void setAffection(int affection) {
        this.affection = affection;
    }
}
