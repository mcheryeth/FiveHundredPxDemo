package com.fivehundredpxdemo.android.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Created by mcheryeth on 7/17/13.
 * Photo stream object that contains a list of photos and other meta data that maps to the 500px REST API
 */

public class User implements Serializable{

    @JsonProperty("fullname")
	public String fullname;

    @JsonProperty("userpic_url")
	public String userpic_url;

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
	
}
