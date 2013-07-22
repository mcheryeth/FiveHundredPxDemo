package com.fivehundredpxdemo.android.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fivehundredpx.api.FiveHundredException;
import com.fivehundredpx.api.PxApi;
import com.fivehundredpx.api.auth.AccessToken;
import com.fivehundredpx.api.tasks.UserDetailTask;
import com.fivehundredpx.api.tasks.XAuth500pxTask;
import com.fivehundredpxdemo.android.FPXApplication;
import com.fivehundredpxdemo.android.R;
import com.fivehundredpxdemo.android.model.CurrentUser;
import com.google.inject.Inject;
import com.sherlock.roboguice.activity.RoboSherlockFragmentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_login)
public class LoginActivity extends RoboSherlockFragmentActivity implements
		XAuth500pxTask.Delegate, UserDetailTask.Delegate {


	private static final String TAG = LoginActivity.class.getName();

	@InjectView(R.id.loadingView) RelativeLayout loadingView;
	@InjectView(R.id.relativeLayout1) RelativeLayout relativeLayout1;
	
	@InjectView(R.id.login_password) EditText passText;
	@InjectView(R.id.login_email) EditText loginText;
	@InjectView(R.id.login_btn) Button loginBtn;

	@Inject CurrentUser user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences preferences = getSharedPreferences(FPXApplication.SHARED_PREFERENCES, Context.MODE_PRIVATE);

		final String accesToken = preferences.getString(FPXApplication.PREF_ACCESS_TOKEN, null);
		final String tokenSecret = preferences
				.getString(FPXApplication.PREF_TOKEN_SECRET, null);

		if (null != accesToken && null != tokenSecret) {
			onSuccess(new AccessToken(accesToken, tokenSecret));
		}

		
		
		loginBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSpinner();
				final XAuth500pxTask loginTask = new XAuth500pxTask(
						LoginActivity.this);
				loginTask.execute(getString(R.string.px_consumer_key),
						getString(R.string.px_consumer_secret), loginText
								.getText().toString(), passText.getText()
								.toString());
			}
		});
	}

	@Override
	public void onSuccess(AccessToken result) {
		Log.d(TAG, "success");
		showSpinner();
		
		user.accessToken = result;
		
		SharedPreferences preferences = getSharedPreferences(FPXApplication.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(FPXApplication.PREF_ACCESS_TOKEN, result.getToken());
		editor.putString(FPXApplication.PREF_TOKEN_SECRET, result.getTokenSecret());
		editor.commit();
		
		
		final PxApi api = new PxApi(user.accessToken,
				getString(R.string.px_consumer_key),
				getString(R.string.px_consumer_secret));

		new UserDetailTask(this).execute(api);

	}
	
	private void showSpinner(){
		loginBtn.setEnabled(false);
		loadingView.setVisibility(View.VISIBLE);
		relativeLayout1.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
	}
	
	private void hideSpinner(){
		loginBtn.setEnabled(true);
		loadingView.setVisibility(View.GONE);
		relativeLayout1.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
	}

	@Override
	public void onSuccess(JSONObject user) {
		//Log.w(TAG, user.toString());
		try {
			this.user.setUserpic_url(user.getString("userpic_url"));
			this.user.setFullname(user.getString("fullname"));
		} catch (JSONException e) {
			Log.e(TAG, "", e);
		}

		startActivity(new Intent(LoginActivity.this, ImageFeedActivity.class));
		finish();
	}

	@Override
	public void onFail() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				hideSpinner();
				Toast.makeText(LoginActivity.this,
						"Login Failed, please try again.", Toast.LENGTH_LONG)
						.show();
			}
		});

	}

	@Override
	public void onFail(FiveHundredException e) {
		onFail();
	}

}
