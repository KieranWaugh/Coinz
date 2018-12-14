package com.kieranwaugh.coinz.coinz;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class PicChoice extends AppCompatActivity {
    // image views for picture selection
    ImageView one;
    ImageView two;
    ImageView three;
    private String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(); // players email


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_choice); // sets layout for shop
        DisplayMetrics dm = new DisplayMetrics(); // gets display dimensions
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .6)); // ses the size of the activity

        one = findViewById(R.id.pic1); // gets the first picture
        two = findViewById(R.id.pic2); // second picture
        three = findViewById(R.id.pic3); // third picture

        one.setOnClickListener(v -> { // sets the players picture to the first image and updates the database
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String[] ref = new String[1];
            CollectionReference cr = db.collection("user").document(email).collection("INFO");
            cr.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        ref[0] = document.getId();
                    }
                }
                Log.d("pic", "id is " + ref[0]);
                db.collection("user").document(email).collection("INFO").document(ref[0]).update("iconID", 2);

                Intent refresh = new Intent(PicChoice.this, PlayerActivity.class);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.nothing);
                startActivity(refresh, options.toBundle());

            });
        });

        two.setOnClickListener(v -> { // sets the players picture to the second image and updates the database
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String[] ref = new String[1];
            CollectionReference cr = db.collection("user").document(email).collection("INFO");
            cr.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        ref[0] = document.getId();
                    }
                }
                db.collection("user").document(email).collection("INFO").document(ref[0]).update("iconID", 3);

                Intent refresh = new Intent(PicChoice.this, PlayerActivity.class);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.nothing);
                startActivity(refresh, options.toBundle());

            });
        });

        three.setOnClickListener(v -> { // sets the players picture to the third image and updates the database
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String[] ref = new String[1];
            CollectionReference cr = db.collection("user").document(email).collection("INFO");
            cr.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        ref[0] = document.getId();
                    }
                }
                db.collection("user").document(email).collection("INFO").document(ref[0]).update("iconID", 4);

                Intent refresh = new Intent(PicChoice.this, PlayerActivity.class);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.nothing);
                startActivity(refresh, options.toBundle());

            });
        });

    }
}
