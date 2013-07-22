package com.fivehundredpxdemo.android.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.fivehundredpxdemo.android.R;
import com.fivehundredpxdemo.android.ui.adapter.MenuListAdapter;

/**
 * Created by mcheryeth on 7/20/13.
 */
public class MenuListFragment extends SherlockListFragment {

    private ParentMenuActivity parentMenuActivity;

    public interface ParentMenuActivity {
        void toggleMenu();
        public void loadFeature(int featureIndex);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        parentMenuActivity = (ParentMenuActivity)getActivity();
        getListView().setDividerHeight(1);
        getListView().setSmoothScrollbarEnabled(true);
        getListView().setCacheColorHint(getResources().getColor(R.color.drawer_background)); // For the edge fade
        getListView().setBackgroundColor(getResources().getColor(R.color.drawer_background));
        getListView().setSelector(R.drawable.drawer_list_selector);
        //getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        MenuListAdapter menuAdapter = new MenuListAdapter(getActivity(), android.R.layout.simple_selectable_list_item,
                getResources().getStringArray(R.array.menu_photos_feature));
        menuAdapter.setSelectedPosition(0);

        setListAdapter(menuAdapter);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        MenuListAdapter adapter = ((MenuListAdapter)l.getAdapter());
        int currPos = adapter.getSelectedPosition();
        adapter.setSelectedPosition(position);
        v.setSelected(true);
        parentMenuActivity.toggleMenu();
        if(currPos!=position){
            parentMenuActivity.loadFeature(position);
        }

    }

}
