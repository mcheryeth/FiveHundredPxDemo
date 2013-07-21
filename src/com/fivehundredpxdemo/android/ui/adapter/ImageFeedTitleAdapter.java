/**
 * 
 */
package com.fivehundredpxdemo.android.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fivehundredpxdemo.android.R;

import java.util.ArrayList;

/**
 * @author mcheryeth
 *
 */
public class ImageFeedTitleAdapter extends ArrayAdapter<String> {

	Context context;
    ArrayList<String> categories = new ArrayList<String>();
    String featureTitle;
	int layoutResourceId;
	LayoutInflater inflater;


//    public static class ImageFeedTitleItem {
//        public ImageFeedTitleItem(String longString, String shortString) {
//            this.feature = longString;
//            this.category = shortString;
//        }
//        final public String feature;
//        final public String category;
//    }

	public ImageFeedTitleAdapter(Context context, String feature, int resourceCategory,
                                 int textViewResourceId) {
		super(context, textViewResourceId);

		featureTitle = feature;
        String[] arrayCategories = context.getResources().getStringArray(resourceCategory);

        for (int i = 0; i < arrayCategories.length; i++) {
            categories.add(arrayCategories[i]);
        }

	    inflater = (LayoutInflater) context
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    this.context = context;
	    this.layoutResourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

	    View actionBarView = inflater.inflate(R.layout.image_feed_title_view, null);
        TextView title = (TextView) actionBarView
                .findViewById(R.id.feature_title);
	    TextView subtitle = (TextView) actionBarView
	            .findViewById(R.id.category_subtitle);
	    title.setText(featureTitle);
	    subtitle.setText(getItem(position));
	    
	    return actionBarView;
	}

//	@Override
//	public View getDropDownView(int position, View convertView, ViewGroup parent) {
//		View actionBarDropDownView = inflater.inflate(
//	            R.layout.inbox_feed_dropdown_view, null);
//	    TextView dropDownTitle = (TextView) actionBarDropDownView
//	            .findViewById(R.id.inbox_dropdown_title);
//
//	    dropDownTitle.setText(getItem(position).longString);
//
//	    return actionBarDropDownView;
//
//	}

	@Override
	public int getCount() {
	    return categories.size();
	}

	@Override
	public String getItem(int position) {
	    return categories.get(position);
	}

	@Override
	public long getItemId(int position) {
	    return 0;
	}

}
