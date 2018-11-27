package com.kieranwaugh.coinz.coinz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class ShopActivity extends AppCompatActivity {
    private String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private double goldBal;
    private String tag = "Shop";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        ImageView item1 = findViewById(R.id.shopItem1);
        ImageView item2 = findViewById(R.id.shopItem2);
        ImageView item3 = findViewById(R.id.shopItem3);
        TextView text1 = findViewById(R.id.shopItem1Text);
        TextView text2 = findViewById(R.id.shopItem2Text);
        TextView text3 = findViewById(R.id.shopItem3Text);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .6));

        text1.setText("50m Collection");
        text2.setText("2x Coin Value");
        text3.setText("4x Coin Value");

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference cr = rootRef.collection("bank").document(email).collection("gold");
        cr.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            goldBal = Double.parseDouble(Objects.requireNonNull(document.get("balance")).toString());
                        }

                    }
                });


        item1.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) { // 50m
                                         if (goldBal < 250000){
                                             Snackbar.make(findViewById(R.id.viewSnack), "Not enough GOLD to buy this item!",Snackbar.LENGTH_SHORT).show();
                                         }else{
                                             final String[] ref = new String[1];
                                             AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                                             builder.setTitle("Buy 50M Collection?")
                                                     .setMessage("Are you sure you want to buy?")
                                                     .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                         public void onClick(DialogInterface dialog, int which) {
                                                             CollectionReference cr = db.collection("user").document(email).collection("INFO");
                                                             cr.get().addOnCompleteListener(task -> {
                                                                 if (task.isSuccessful()) {

                                                                     for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                                         ref[0] = (document.getId());
                                                                     }
                                                                 }
                                                                 db.collection("user").document(email).collection("INFO").document(ref[0]).update("radius", 50);
                                                                 Log.d(tag, "changed radius");
                                                                 String[] refGold = new String[1];
                                                                 CollectionReference col = db.collection("bank").document(email).collection("gold");
                                                                 col.get().addOnCompleteListener(task2 -> {
                                                                     if (task2.isSuccessful()) {
                                                                         for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                                             refGold[0] = document.getId();
                                                                         }
                                                                     }
                                                                     Log.d(tag, refGold[0]);
                                                                     db.collection("bank").document(email).collection("gold").document(refGold[0]).update("balance", (goldBal - 250000));
                                                                     Log.d(tag, "updated gold " + (goldBal - 250000));
                                                                     //finish();
                                                                 });


                                                             });
                                                         }
                                                     }).show();
                                         }

                                     }
                                 });




        item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        item3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }
}
