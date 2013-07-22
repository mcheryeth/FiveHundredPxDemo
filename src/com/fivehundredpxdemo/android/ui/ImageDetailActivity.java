package com.fivehundredpxdemo.android.ui;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.fivehundredpxdemo.android.R;
import com.fivehundredpxdemo.android.imageloader.DiskBitmapCache;
import com.fivehundredpxdemo.android.model.Photo;
import com.fivehundredpxdemo.android.ui.views.TouchNetworkImageView;
import com.fivehundredpxdemo.android.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class ImageDetailActivity extends SherlockFragmentActivity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

    //Volley's network image loader
    private ImageLoader imageLoader;

    private Photo currentPhoto;
    private TouchNetworkImageView imageView;

    public static final String EXTRA_CURRENT_PHOTO = "extra_current_photo";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_image_detail);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeButtonEnabled(true);
        supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        supportActionBar.setCustomView(R.layout.image_detail_title);
        supportActionBar.show();

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.niv_large);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(requestQueue, new DiskBitmapCache(getCacheDir())); //volley's image loader

        String errorMsg;
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            try{
                Photo photo = (Photo)extras.getSerializable(EXTRA_CURRENT_PHOTO);
                if(photo!=null){
                    currentPhoto = photo;
                    //load photo
                    imageView = (TouchNetworkImageView)contentView;
                    imageView.setImageUrl(photo.getImage_url()[1], imageLoader);
                    //set image name
                    ((TextView)supportActionBar.getCustomView().findViewById(R.id.imageDetailTitle)).setText(photo.getName());
                    //set user name
                    ((TextView)findViewById(R.id.username)).setText(photo.getUser().getFullname());
                    //set rating
                    ((TextView)findViewById(R.id.rating)).setText("Rating: " + photo.getRating());
                }
                else{
                    errorMsg = "Unable to display photo. Photo not found!";
                    Log.e(ImageDetailActivity.class.getName(), errorMsg);
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e){
                errorMsg = "Unable to display photo. " + e.getMessage();
                Log.e(ImageDetailActivity.class.getName(), errorMsg);
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }

        }
		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.fullscreen_content_controls).setOnTouchListener(
				mDelayHideTouchListener);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imageView.setScaleType(TouchNetworkImageView.ScaleType.FIT_XY);
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            imageView.setScaleType(TouchNetworkImageView.ScaleType.FIT_CENTER);
        }
    }
}
