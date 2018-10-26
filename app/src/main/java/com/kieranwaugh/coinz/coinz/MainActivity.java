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
    private String downloadDate = "todays date"; //Format YYY/MM/DD
    private String mapData = DownloadCompleteRunner.result;
    String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "[onCreate] The date is " + date + " fetching map");
//        String year = date.substring(0,4);
//        String month = date.substring(5,7);
//        String day = date.substring(8,10);
        SharedPreferences FromFile = getSharedPreferences("mapData", Context.MODE_PRIVATE);
        if (FromFile.contains(date)){
            Log.d(tag, "[onCreate] Taking map data from file, moving on");
        }else {
            DownloadFileTask df = new DownloadFileTask();
            df.execute("http://homepages.inf.ed.ac.uk/stg/coinz/"+date+"/coinzmap.geojson");
            Log.d(tag, "[onCreate] Taking map data from server");
        }

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

    }


    @Override
    public void onStop() {
        super.onStop();
    }

}