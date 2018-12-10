package com.kieranwaugh.coinz.coinz;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ShopActivity extends AppCompatActivity {
    private String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private double goldBal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop); // sets view
        //gets all activity view elements
        ImageView item1 = findViewById(R.id.shopItem1);
        ImageView item2 = findViewById(R.id.shopItem2);
        ImageView item3 = findViewById(R.id.shopItem3);
        TextView text1 = findViewById(R.id.shopItem1Text);
        TextView text2 = findViewById(R.id.shopItem2Text);
        TextView text3 = findViewById(R.id.shopItem3Text);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm); // gets display dimensions
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .6)); // sets activity dimensions

        Intent i = getIntent();
        int radius = i.getIntExtra("radius", 25); // gets passed through intent
        int multi = i.getIntExtra("multi", 1);

        text1.setText("50m Collection\n250,000 GOLD"); // sets text for shop items and prices
        text2.setText("2x Coin Value\n500,000 GOLD");
        text3.setText("4x Coin Value\n750,000 GOLD");

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance(); // gets players gold balance
        CollectionReference cr = rootRef.collection("user").document(email).collection("INFO");
        cr.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            goldBal = Double.parseDouble(Objects.requireNonNull(document.get("gold")).toString());
                        }

                    }
                });


        // if first item is selected
        item1.setOnClickListener(v -> {// 50m
            if (radius == 50){ // already have item
                Snackbar.make(findViewById(R.id.viewSnack), "You already have this item!",Snackbar.LENGTH_SHORT).show();
            }else {


                if (goldBal < 250000) { // not enough gold
                    Snackbar.make(findViewById(R.id.viewSnack), "Not enough GOLD to buy this item!", Snackbar.LENGTH_SHORT).show();
                } else {
                    final String[] ref = new String[1];
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this, android.R.style.Theme_Material_Dialog_Alert); // creating dialog box
                    //player wishes to buy item
                    builder.setTitle("Buy 50M Collection?")
                            .setMessage("Are you sure you want to buy for 250,000 GOLD?")
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                CollectionReference cr1 = db.collection("user").document(email).collection("INFO");
                                cr1.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                            ref[0] = (document.getId());
                                        }
                                    }
                                    db.collection("user").document(email).collection("INFO").document(ref[0]).update("radius", 50); // updates radius
                                    db.collection("user").document(email).collection("INFO").document(ref[0]).update("gold", goldBal - 250000); // updates gold
                                    finish();
                                    Intent i1 = new Intent(getApplicationContext(), BankActivity.class);
                                    ActivityOptions o = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                                    startActivity(i1, o.toBundle());

                                });
                            }).show();
                }
            }

        });




        item2.setOnClickListener(v -> { // item 2

            if (multi == 2 || multi == 4){ // already have item
                Snackbar.make(findViewById(R.id.viewSnack), "You already have this item!",Snackbar.LENGTH_SHORT).show();
            }else {


                if (goldBal < 500000) { // not enough gold
                    Snackbar.make(findViewById(R.id.viewSnack), "Not enough GOLD to buy this item!", Snackbar.LENGTH_SHORT).show();
                } else {
                    final String[] ref = new String[1];
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    builder.setTitle("Buy 2X Coin Value Multiplier")
                            .setMessage("Are you sure you want to buy for 500,000 GOLD? This cannot be used in coin transfers only banking.") // creating dialog
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> { // an tto buy item
                                CollectionReference cr12 = db.collection("user").document(email).collection("INFO");
                                cr12.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                            ref[0] = (document.getId());
                                        }
                                    }
                                    db.collection("user").document(email).collection("INFO").document(ref[0]).update("multi", 2); // updates multiplier
                                    db.collection("user").document(email).collection("INFO").document(ref[0]).update("gold", goldBal - 500000); // updates gold
                                    finish();
                                    Intent i12 = new Intent(getApplicationContext(), BankActivity.class);
                                    ActivityOptions o = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                                    startActivity(i12, o.toBundle());

                                });
                            }).show();
                }
            }

        });

        item3.setOnClickListener(v -> { //item 3

            if (multi == 4){ // already have item
                Snackbar.make(findViewById(R.id.viewSnack), "You already have this item!",Snackbar.LENGTH_SHORT).show();
            }else {


                if (goldBal < 750000) { // not enough gold
                    Snackbar.make(findViewById(R.id.viewSnack), "Not enough GOLD to buy this item!", Snackbar.LENGTH_SHORT).show();
                } else {
                    final String[] ref = new String[1];
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this, android.R.style.Theme_Material_Dialog_Alert); // dialog box
                    builder.setTitle("Buy 4X Coin value multiplier?")
                            .setMessage("Are you sure you want to buy for 750,000 GOLD? This cannot be used in coin transfers only banking.")
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> { // want to buy
                                CollectionReference cr13 = db.collection("user").document(email).collection("INFO");
                                cr13.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                            ref[0] = (document.getId());
                                        }
                                    }
                                    db.collection("user").document(email).collection("INFO").document(ref[0]).update("multi", 4); // update multiplier
                                    db.collection("user").document(email).collection("INFO").document(ref[0]).update("gold", goldBal - 750000); // update gold
                                    finish();
                                    Intent i13 = new Intent(getApplicationContext(), BankActivity.class);
                                    ActivityOptions o = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                                    startActivity(i13, o.toBundle());

                                });
                            }).show();
                }
            }

        });



    }
}
