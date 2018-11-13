package com.kieranwaugh.coinz.coinz;

import android.app.ActivityOptions;
import android.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        Log.d(tag, "[onCreate] The date is " + date + " fetching map");
        SharedPreferences FromFile = getSharedPreferences("mapData", Context.MODE_PRIVATE);
        if (FromFile.contains(date)){
            Log.d(tag, "[onCreate] Taking map data from file, moving on");
        }else {
            DownloadFileTask df = new DownloadFileTask();
            df.execute("http://homepages.inf.ed.ac.uk/stg/coinz/"+date+"/coinzmap.geojson");
            Log.d(tag, "[onCreate] Taking map data from server");
        }

        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.contentSpace);
//        ArrayList<String> goldRefs = new ArrayList<>();
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        CollectionReference cr = db.collection("bank");
//        cr.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
//                    Log.d(tag, "in loop");
//                    String ref = (document.getId());
//                    Log.d(tag, ref);
//                    goldRefs.add(ref);
//                }
//            }
//            Log.d(tag, goldRefs.toString());
//            if (!goldRefs.contains(UID)){
//                Log.d(tag,"[onCreate] User does Not Exist");
//                Gold gold = new Gold(0.0);
//                db.collection("bank").document(UID).collection("gold").add(gold);
//            }else{
//                Log.d(tag,"[onCreate] User does Not Exist");
//            }
//        });


        final Button playButton = (Button) findViewById(R.id.PlayButton);
        playButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if (auth.getCurrentUser() != null) {

                    setProgressDialog();

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