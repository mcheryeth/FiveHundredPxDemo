package com.fivehundredpxdemo.android.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.fivehundredpxdemo.android.R;

/**
 * Created by mcheryeth on 7/21/13.
 */
public class MenuListAdapter extends ArrayAdapter<String> {

    private int selectedPosition=0;

    public MenuListAdapter(Context context, int textViewResourceId, String[] objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if(position == selectedPosition)
        {
            view.setBackgroundResource(R.color.drawer_listview_item_selected_color);
        }
        else{
            view.setBackgroundResource(android.R.color.transparent);
        }
        return view;
    }

    public void setSelectedPosition(int position){
        selectedPosition = position;
        this.notifyDataSetChanged();
    }

    public int getSelectedPosition(){
        return selectedPosition;
    }
}