package com.kieranwaugh.coinz.coinz;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


public class FriendsFragment extends android.support.v4.app.Fragment {
    // Store instance variables
    private ListView listView;

    // newInstance constructor for creating fragment with arguments
    public static FriendsFragment newInstance( ) {
        FriendsFragment fragmentFirst = new FriendsFragment();
        Bundle args = new Bundle();


        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //https://www.mkyong.com/android/android-listview-example/

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        return view;
    }
}
