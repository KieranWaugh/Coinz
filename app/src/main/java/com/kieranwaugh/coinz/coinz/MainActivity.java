package com.kieranwaugh.coinz.coinz;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;

import android.content.Intent;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {


    String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date()); // Daily date for map download on informatics server
    private FirebaseAuth auth; // fireBase authentication
    private TextView tap; // text view prompting user to tap screen to begin



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance(); // gets fireBase authentication, null if no user is logged in


        setContentView(R.layout.activity_main);

        tap =findViewById(R.id.tapBegin); // init "tap to play" textView
        tap.setVisibility(View.INVISIBLE); // invisible to allow animation
        tap.setRotation(-30); // rotates text

        tap.postDelayed(() -> {
            tap.setVisibility(View.VISIBLE); // sets text to visible after delay of 1500 ms
            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/game_font.ttf"); // sets text font to game font
            tap.setTypeface(typeface);

            Animation anim = new AlphaAnimation(0.0f, 1.0f); // creates an animation to blink the text
            anim.setDuration(500); // blinking time set to 500ms
            anim.setStartOffset(100);
            anim.setRepeatMode(Animation.REVERSE); // gives hte illusion of the text is moving in and out
            anim.setRepeatCount(Animation.INFINITE); // animation is continuous
            tap.startAnimation(anim); // starts the animation
        }, 1500); // delay


        ImageView coin = findViewById(R.id.coinLogo); // gets the games logo vector asset
        Display display = getWindowManager().getDefaultDisplay(); // gets the device display information
        float width = display.getWidth(); // gets the displays width
        TranslateAnimation animation = new TranslateAnimation(width - 50, 0, 0, 0); // TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation.setDuration(700); // animation duration
        animation.setFillAfter(true); // starts the animation
        coin.startAnimation(animation); // start animation

        View view = findViewById(R.id.contentSpace); // finds the view for the activity
        view.setOnClickListener(v -> { // allows the player to touch anywhere to begin the game

                if (auth.getCurrentUser() != null) { // user is logged in

                    setProgressDialog(); // starts the logging you in dialog, used because the illusion of the ui hanging is stopped while fireBase is checking authentication


                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.bottom_up, R.anim.nothing); // creates animation for activity change
                    startActivity(new Intent(MainActivity.this, MapActivity.class), options.toBundle()); // starts the map activity

                } else { // user is not logged in
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);// creates animation for activity change
                    //ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out); // starts the login activity
                    @SuppressLint("CutPasteId") ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, findViewById(R.id.coinLogo), "transition");
                    startActivity(intent, options.toBundle());
                }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
    } // android lifecycle


    @Override
    public void onStop() {
        super.onStop();
    } // android life cycle

    @Override
    public void onBackPressed(){ // pressing android ui back will return to the android home screen
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }



    @SuppressLint("SetTextI18n")
    public void setProgressDialog() { // Spinner to display user login progress, keeps the ui active while authentication is being received.
        // https://stackoverflow.com/questions/51862117/how-can-i-center-the-title-in-a-progressdialog-android
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