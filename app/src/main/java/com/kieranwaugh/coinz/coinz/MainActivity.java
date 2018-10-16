package com.kieranwaugh.coinz.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;


import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapFragment;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private int LoggedIn = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        if (LoggedIn == 1){
            Intent intent2 = new Intent(MainActivity.this, mapActivity.class);
            startActivity(intent2);
        }else {
            Intent intent2 = new Intent(MainActivity.this, mapActivity.class);
            startActivity(intent2);
        }

        setContentView(R.layout.activity_main);

    }

}
