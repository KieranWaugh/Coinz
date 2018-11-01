package com.kieranwaugh.coinz.coinz;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.DownloadListener;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    private final String tag = "MainActivity";
    private String mapData = DownloadCompleteRunner.result;
    String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
    private FirebaseAuth auth;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Log.d(tag, "[onCreate] The date is " + date + " fetching map");
        SharedPreferences FromFile = getSharedPreferences("mapData", Context.MODE_PRIVATE);
        if (FromFile.contains(date)){
            Log.d(tag, "[onCreate] Taking map data from file, moving on");
        }else {
            DownloadFileTask df = new DownloadFileTask();
            df.execute("http://homepages.inf.ed.ac.uk/stg/coinz/"+date+"/coinzmap.geojson");
            Log.d(tag, "[onCreate] Taking map data from server");
        }

//        Intent intent = new Intent(MainActivity.this, mapActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.nothing);
//        startActivity(intent);
        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.contentSpace);
        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view,MotionEvent event) {

                if (auth.getCurrentUser() != null) {

                    ProgressDialog nDialog;
                    nDialog = new ProgressDialog(MainActivity.this);
                    nDialog.setMessage("Loading..");
                    nDialog.setTitle("Logging you in");

                    nDialog.setIndeterminate(false);
                    nDialog.setCancelable(true);
                    nDialog.show();

                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.bottom_up, R.anim.nothing);
                    startActivity(new Intent(MainActivity.this, mapActivity.class), options.toBundle());

                }else{
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, findViewById(R.id.imageView), "transition");
                    startActivity(intent, options.toBundle());
                }

                return true;

            }
        });

        final Button playButton = (Button) findViewById(R.id.PlayButton);
        playButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if (auth.getCurrentUser() != null) {

                    ProgressDialog nDialog;
                    nDialog = new ProgressDialog(MainActivity.this);
                    nDialog.setMessage("Loading..");
                    nDialog.setTitle("Logging you in");

                    nDialog.setIndeterminate(false);
                    nDialog.setCancelable(true);
                    nDialog.show();

                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.bottom_up, R.anim.nothing);
                    startActivity(new Intent(MainActivity.this, mapActivity.class), options.toBundle());

                }else{
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, findViewById(R.id.imageView), "transition");
                    startActivity(intent, options.toBundle());
                }
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed(){
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }



}