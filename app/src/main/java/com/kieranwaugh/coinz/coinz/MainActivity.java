package com.kieranwaugh.coinz.coinz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.DownloadListener;
import android.widget.TextView;


import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapFragment;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    public String mapData;
    private int LoggedIn = 1;
    private final String tag = "MainActivity";
    private String downloadDate = "todays date"; //Format YYY/MM/DD
    private final String preferencesFile = "MyPrefsFile"; //for storing preferences


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DownloadFileTask df = new DownloadFileTask();
        df.execute("http://homepages.inf.ed.ac.uk/stg/coinz/2018/10/03/coinzmap.geojson");

        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        if (LoggedIn == 1) {
            Intent intent2 = new Intent(MainActivity.this, mapActivity.class);
            startActivity(intent2);
        } else {
            Intent intent2 = new Intent(MainActivity.this, mapActivity.class);
            startActivity(intent2);
        }

        setContentView(R.layout.activity_main);

    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences settings = getSharedPreferences(preferencesFile, Context.MODE_PRIVATE);

        mapData = DownloadCompleteRunner.result;
        downloadDate = settings.getString("lastDownloadDate", "");
        Log.d(tag, "[OnStart] Recalled lastDownloadDate is '" + mapData + "'");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(tag, "[onStop] Storing lastDownloadDate of " + mapData);
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(preferencesFile, Context.MODE_PRIVATE);
        // We need an Editor object to make preference changes.
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("lastDownloadDate", mapData);
        // Apply the edits!
        editor.apply();
    }

}