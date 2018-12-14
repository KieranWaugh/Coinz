package com.kieranwaugh.coinz.coinz;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
public class TransferWindow extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // gets database
    private String dateDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()); // date for the database
    private String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(); // users email
    ArrayList<User> friendList = new ArrayList<>(); // arraylist of players friends
    private Spinner spinner;
    private int selectedfriend; // element of menu selected

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // sets activity view
        setContentView(R.layout.activity_transfer_window);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm); // gets display dimensions
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .6)); // sets the activity dimensions


        Intent intent = getIntent();
        Coin c = (Coin) intent.getSerializableExtra("coin"); // gets the selected coin
        String reference = intent.getStringExtra("reference"); // gets the database reference
        TextView curView = findViewById(R.id.currencyConfirm);
        curView.setText(c.getCurrency() + ": " + c.getValue()); // sets the confirmation text
        Button transfer = findViewById(R.id.confirmButton);


        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference cr = rootRef.collection("user").document(email).collection("Friends");
        cr.get().addOnCompleteListener(task -> { // pulls friends from the database
            if (task.isSuccessful()) {

                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) { // pulls players friends frm the database
                    friendList.add(document.toObject(User.class)); // re-creates user object

                }
                updateSpinnerUI(friendList); // updates the spinner
            }
        });

        transfer.setOnClickListener(v -> {

            if(selectedfriend != -1){
                c.setId(c.getId()+ email); // changes the coin ID to notify a sent coin
                Log.d("transfer", c.getId() + " " + c.getId().length());
                Log.d("transfer", reference);
                db.collection("wallet").document(friendList.get(selectedfriend).getEmail()).collection("collected ("+dateDB +")").add(c); // adds the coin to the receiving users wallet
                db.collection("wallet").document(email).collection("collected ("+dateDB +")").document(reference).update("banked", true); // changes the coin in the current players wallet to banked
                Intent i = new Intent(getApplicationContext(), BankActivity.class);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                startActivity(i, options.toBundle()); // restarts the bank activity
            }
        });

    }

    public void updateSpinnerUI(ArrayList<User> friends){
        String[] drop = new String[friends.size() + 1]; // drop down list for spinner
        drop[0] = "Select Friend:"; // default entry
        for (int i = 0; i < friends.size(); i++){
            String s  = friends.get(i).getName(); // gets the friends name for the list
            drop[i+1] = s;
        }
        spinner = findViewById(R.id.spinner); // spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(TransferWindow.this,
                android.R.layout.simple_spinner_item, drop);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter); // sets the adapter for the spinner

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int sp = spinner.getSelectedItemPosition();
                if (sp != 0) {
                    selectedfriend = sp-1;

                }else{
                    selectedfriend = -1; // first item in the list has been selected
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }
}
