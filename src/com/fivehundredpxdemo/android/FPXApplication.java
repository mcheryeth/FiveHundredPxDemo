package com.fivehundredpxdemo.android;

import android.content.Context;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

public class FPXApplication extends android.app.Application {

	private static Context _applicationContext;
	
	public static final String PREF_TOKEN_SECRET = "FiveHundredPxDemo.tokenSecret";
	public static final String PREF_ACCESS_TOKEN = "FiveHundredPxDemo.accessToken";
	public static final String SHARED_PREFERENCES = "FiveHundredPxDemoSharedPreferences";
	
	@Override
	public void onCreate() {

	
		_applicationContext = getApplicationContext();

        // Force the overflow menu on phones with a hardware button.
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }

    }

	public static Context getContext() {
		return _applicationContext;
	}


}

