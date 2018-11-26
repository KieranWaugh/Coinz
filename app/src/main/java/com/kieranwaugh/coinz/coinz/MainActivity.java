package com.kieranwaugh.coinz.coinz;

import android.app.ActivityOptions;
import android.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {


    private final String tag = "MainActivity";
    private String mapData = DownloadCompleteRunner.result;
    String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
    private FirebaseAuth auth;
    private String UID = FirebaseAuth.getInstance().getUid();
    private ImageView coin;
    private TextView tap;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        Log.d(tag, "[onCreate] The date is " + date + " fetching map");
        SharedPreferences FromFile = getSharedPreferences("mapData", Context.MODE_PRIVATE);
        if (FromFile.contains(date)) {
            Log.d(tag, "[onCreate] Taking map data from file, moving on");
        } else {
            DownloadFileTask df = new DownloadFileTask();
            df.execute("http://homepages.inf.ed.ac.uk/stg/coinz/" + date + "/coinzmap.geojson");
            Log.d(tag, "[onCreate] Taking map data from server");
        }

        setContentView(R.layout.activity_main);

        tap =findViewById(R.id.tapBegin);
        tap.setVisibility(View.INVISIBLE);
        tap.setRotation(-30);

        tap.postDelayed(() -> {
            tap.setVisibility(View.VISIBLE);
            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/game_font.ttf");
            tap.setTypeface(typeface);

            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(500); //You can manage the blinking time with this parameter
            anim.setStartOffset(100);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            tap.startAnimation(anim);
        }, 1500);


        coin = findViewById(R.id.coinLogo);
        Display display = getWindowManager().getDefaultDisplay();
        float width = display.getWidth();
        TranslateAnimation animation = new TranslateAnimation(width - 50, 0, 0, 0); // new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation.setDuration(700); // animation duration
        // left )
        animation.setFillAfter(true);

        coin.startAnimation(animation); // start animation




        View view = findViewById(R.id.contentSpace);
        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                tap.setVisibility(View.INVISIBLE);

                if (auth.getCurrentUser() != null) {

                    if (auth.getCurrentUser() != null) {

                        setProgressDialog();


                        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.bottom_up, R.anim.nothing);
                        startActivity(new Intent(MainActivity.this, mapActivity.class), options.toBundle());
                        return true;

                    } else {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                        startActivity(intent, options.toBundle());
                        return false;
                    }



                }
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                startActivity(intent, options.toBundle());
                return false;
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

    public void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("Logging You In ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(ll);

        AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }



}