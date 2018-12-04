package com.kieranwaugh.coinz.coinz;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class BankWindow extends AppCompatActivity {

    Button bankButton;
    String dateDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()); // date for the database
    private String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(); // users email
    private int multi; // integer value for multiplicity of coin (shop bonus feature)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_window);
        DisplayMetrics dm = new DisplayMetrics(); // init for activity size, since popup
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .6)); // sets the activity to display width 80% and height 60% of previous activity

        TextView multiView = findViewById(R.id.multiText);
        multiView.setVisibility(View.INVISIBLE); // sets users multi to invisible

        Intent intent = getIntent(); // gets the intent from the bank activity
        Coin c = (Coin) intent.getSerializableExtra("coin"); // gets the coin value from the bank activity
        double gold = intent.getDoubleExtra("gold", 0.0); // gets the players current gold balance
        String reference = intent.getStringExtra("reference"); // gets the coins object reference in fireBase
        double rate = intent.getDoubleExtra("rate", 0.0); // gets the exchange rate fo the coins currency

        String tag = "BankWindow";
        Log.d(tag, "[onCreate] received intent extra - " + c + " " + gold + " " + rate);

        Log.d("BankWindow", "rate is " + rate);
        TextView curView = findViewById(R.id.currencyConfirm);
        curView.setText(c.getCurrency() + ": " + c.getValue()); // sets the display for the coins currency and value
        TextView goldView = findViewById(R.id.goldConfirm);
        goldView.setText(c.getValue()*rate + " GOLD"); // sets the gold value the coin is worth
        FirebaseFirestore db = FirebaseFirestore.getInstance();// init for fireBase firestore


        CollectionReference cr = db.collection("user").document(email).collection("INFO"); // getting the players multiplier (if purchased from the shop)
        cr.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    multi = Integer.parseInt(Objects.requireNonNull(document.get("multi")).toString());
                }
            }
            if (multi > 1){ // default is 1 so any other has been purchased from the shop.
                multiView.setVisibility(View.VISIBLE); // makes the players multi visible as it exists
                multiView.setText("With " + multi + "X Multiplier"); // displays the multi
                curView.setText(c.getCurrency() + ": " + c.getValue() + " -> " + multi + "X: " + c.getValue() * multi);
                goldView.setText((c.getValue() * multi)*rate + " GOLD"); // displays the new gold value

            }else{
                curView.setText(c.getCurrency() + ": " + c.getValue()); // refreshes the coin information view
                goldView.setText((c.getValue() * multi)*rate + " GOLD");// refreshes the gold value view
            }
        });

        bankButton = findViewById(R.id.bankButton); // user wishes to bank the coin
        bankButton.setOnClickListener(v -> {

            db.collection("wallet").document(email).collection("collected (" + dateDB + ")").document(reference).update("banked", true); // updates the coin to banked in the database

            String[] ref = new String[1];
            CollectionReference col = db.collection("user").document(email).collection("INFO"); // to update gold balance
            col.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        ref[0] = document.getId(); // gets user document id
                    }
                }
                db.collection("user").document(email).collection("INFO").document(ref[0]).update("gold", gold +((c.getValue() * multi) * rate)); // updates the gold balance with the rate and multi

                Intent refresh = new Intent(BankWindow.this, BankActivity.class);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.nothing);
                startActivity(refresh, options.toBundle()); // returns to bank activity, refreshing to allow gold balance to update

            });
        });
    }
}
