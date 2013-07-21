package com.fivehundredpxdemo.android.model;

import com.fivehundredpx.api.auth.AccessToken;
import com.fivehundredpxdemo.android.Application;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by mcheryeth on 7/17/13.
 */

@Singleton
public class CurrentUser extends User{


    @Inject
    Application application;


    public AccessToken accessToken;

}
