package com.fivehundredpxdemo.android.model;

import com.fivehundredpx.api.auth.AccessToken;
import com.fivehundredpxdemo.android.FPXApplication;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by mcheryeth on 7/17/13.
 */

@Singleton
public class CurrentUser extends User{


    @Inject
    FPXApplication application;


    public AccessToken accessToken;

}
