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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.DownloadListener;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapFragment;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {



    private int LoggedIn = 1;
    private final String tag = "MainActivity";
    private String mapData = DownloadCompleteRunner.result;
    String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "[onCreate] The date is " + date + " fetching map");
        SharedPreferences FromFile = getSharedPreferences("mapData", Context.MODE_PRIVATE);
        if (FromFile.contains(date)){
            Log.d(tag, "[onCreate] Taking map data from file, moving on");
        }else {
            DownloadFileTask df = new DownloadFileTask();
            df.execute("http://homepages.inf.ed.ac.uk/stg/coinz/"+date+"/coinzmap.geojson");
            Log.d(tag, "[onCreate] Taking map data from server");
        }

        Intent intent = new Intent(MainActivity.this, mapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        setContentView(R.layout.activity_main);
        finish();

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

}