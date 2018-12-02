package com.kieranwaugh.coinz.coinz;

import android.app.ActivityOptions;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;


import android.graphics.Typeface;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


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




import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class BankActivity extends AppCompatActivity {
    public String mapData; // GeoJSON data from the informatics server
    public double SHILLrate; // Daily exchange rate for SHILL
    public double QUIDrate; // Daily exchange rate for QUID
    public double PENYrate; // Daily exchange rate for PENY
    public double DOLRrate; // Daily exchange rate for DOLR
    private Spinner spinner; // Drop down menu for coin selection
    private int selectedCoin; // location of the coin selected in the list to allow banking
    private int bankedCount; // total number banked coins that day
    public ArrayList<Coin> collected = new ArrayList<>(); // all coins collected by the player that day
    private LinkedHashMap<String, Coin> collectedMap = new LinkedHashMap<>(); // Hashmap with the object reference id as the Key and a related coin.
    String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date()); // date for shared preferences
    String tag = "BankActivity"; // Log tag
    private double goldBal; // Players gold balance
    String dateDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()); // date for the firebase entry
    private String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(); // Users email address
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance(); // Initiates Firestore database root


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_bank_activity);
        Button bankButton =findViewById(R.id.bankButton); // Button to bank the coin
        bankButton.setOnClickListener(bankClick); // setting the onClick listener to a function bankClick
        Button transferButton = findViewById(R.id.transferButton); // Button to transfer a coin
        transferButton.setOnClickListener(transferClick);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/game_font.ttf"); // creates a typeface of the games font for consistency
        bankButton.setTypeface(typeface); // setting the font
        transferButton.setTypeface(typeface);
        TextView welc = findViewById(R.id.welcome);
        welc.setTypeface(typeface); // setting the font
        TextView coinz = findViewById(R.id.coinzBank);
        TextView bal = findViewById(R.id.balance);
        coinz.setTypeface(typeface); // setting the font
        bal.setTypeface(typeface); // setting the font

        getCollected(); // runs the getCollected function to get the users collected coins
        getRates(); // runs the getRates method to get today's exchange rates

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation); // setting the bottom nav bar
        Menu menu = bottomNavigationView.getMenu();  // creates a menu
        MenuItem menuItem = menu.getItem(2); // sets the location of the bottom nav bar to location 2
        menuItem.setChecked(true); // highlights the location
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out); // creates the animation for the activity start
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){ // gets the menu bar item selection
                case R.id.navigation_stats: // stats activity
                    Intent intent0 = new Intent(BankActivity.this, Test_Player_Activity.class); // creates the intent
                    startActivity(intent0,options.toBundle()); // starts the activity with the animations
                    break;

                case R.id.navigation_map: // map activity
                    Intent intent1 = new Intent(BankActivity.this, mapActivity.class);
                    startActivity(intent1, options.toBundle());
                    break;

                case R.id.navigation_bank: // bank activity
                    // DO NOTHING
                    break;
            }
            return false;
        });

    }

    public void getRates(){ // method for getting the rates and the users gold amount
        String savedMapData = "mapData";
        SharedPreferences FromFile = getSharedPreferences(savedMapData, Context.MODE_PRIVATE); // gets the mapData from shared preferences
        mapData = FromFile.getString(date, ""); // sets the map data
        Log.d(tag, "[getRates] fetching map data");

        try {
            Log.d(tag, "[onCreate] getting exchange rates");
            JSONObject json = new JSONObject(mapData); // creates a json object from the mapData downloaded from the informatics server
            SHILLrate = json.getJSONObject("rates").getDouble("SHIL"); // sets the SHILLrate
            Log.d(tag, "[getRates] SHIL rate " + SHILLrate);
            QUIDrate = json.getJSONObject("rates").getDouble("QUID"); // sets the QUIDrate
            Log.d(tag, "[getRates] QUID rate " + QUIDrate);
            PENYrate = json.getJSONObject("rates").getDouble("PENY"); // sets the PENYrate
            Log.d(tag, "[getRates] PENY rate " + PENYrate);
            DOLRrate = json.getJSONObject("rates").getDouble("DOLR"); // sets the DOLRrate
            Log.d(tag, "[getRates] DOLR rate " + DOLRrate);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CollectionReference cr = rootRef.collection("user").document(email).collection("INFO"); // creates a collection reference to the users INFO location
        cr.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    goldBal = Double.parseDouble(Objects.requireNonNull(document.get("gold")).toString()); // sets the players gold amount
                }
                updateGoldUI(goldBal); // uses the method updateGoldUI to update the textView after the download from firebase is done/
            }
        });

    }

    @Override
    public void onBackPressed(){
        Log.d(tag, "[onBackPressed] back pressed");
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.bottom_down, R.anim.nothing); // when android back is pressed returns player to main play screen (MainActivity)
        startActivity(new Intent(BankActivity.this, MainActivity.class), options.toBundle());
    }

    @Override
    public void onStart(){ // android life cycle
        super.onStart();


    }

    public void onDestroy(){ // android life cycle
        super.onDestroy();

    }


    public void getCollected(){ // method to get the players collected coins
        Log.d(tag, "[getCollected] fetching collected coins");
        CollectionReference cr = rootRef.collection("wallet").document(email).collection("collected ("+dateDB +")"); // gets the wallet for the user
        cr.get().addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                   String ref = (document.getId()); // gets the objects reference to allow update later
                   Coin c = document.toObject(Coin.class); // re-creates the coin object from firestore
                   assert c != null;
                   if (!c.isBanked()){ // if the coin is not marked as banked
                       collectedMap.put(ref, c); //  adds the object reference and the coin to teh hashMap
                       collected.add(c); // adds the coin to the collected arrayList
                   }else{
                       bankedCount +=1; // increment the banked count
                   }
               }
               Log.d(tag, "[getCollected] collected coins - " + collected.toString());
               updateSpinnerUI(collected); // use the method updateSpinnerUI to update the spinner menu with each collected coin
           }

       });



    }

    private View.OnClickListener bankClick = new View.OnClickListener(){ //  when the bank button is clicked

        @Override
        public void onClick(View v) {
            Log.d(tag, "[onClick] bank clicked");
            if (bankedCount > 25){  //  Cannot bank more than 25 coins per day as per specification
                if (selectedCoin !=-1){ // -1 is the value for the default "select coin" menu item
                    Log.d(tag, "[onClick] banked 25 coins");
                    Snackbar.make(findViewById(R.id.viewSnack), "You have already banked 25 coins today!, transfer your spare change.", Snackbar.LENGTH_LONG).show(); //  displays not allowed to player
                }
            }else{
                Log.d(tag, "[onClick] banked fewer than 25 coins");
                if (selectedCoin != -1){
                    List<String> docRefs = new ArrayList<>(collectedMap.keySet()); // creates an array list of all the firestore object references
                    String reference = docRefs.get(selectedCoin); // gets the reference for the coin in fireBase to allow an update

                    Coin c = collectedMap.get(reference); // gets the coin the user wishes to bank
                    Log.d(tag, "[onClick] retrieved selected coin");
                    Intent bankPopUp = new Intent(BankActivity.this, BankWindow.class); // creates the intent to start the pop up activity to bank the coin
                    assert c != null;
                    switch(c.getCurrency()){ // passing the exchange rate to the activity for the coins currency.
                        case ("DOLR"): // when the coins currency is DOLR
                            bankPopUp.putExtra("rate", DOLRrate); // adds the exchange rate to the intent
                            break;
                        case ("QUID"): // when the coins currency is QUID
                            bankPopUp.putExtra("rate", QUIDrate); // adds the exchange rate to the intent
                            break;
                        case ("SHIL"): // when the coins currency is SHIL
                            bankPopUp.putExtra("rate", SHILLrate); // adds the exchange rate to the intent
                            break;
                        case ("PENY"): // when the coins currency is PENY
                            bankPopUp.putExtra("rate", PENYrate); // adds the exchange rate to the intent
                            break;
                    }


                    bankPopUp.putExtra("coin", c); // adds the coin to the intent
                    bankPopUp.putExtra("gold", goldBal); // adds the players gold amount to the intent
                    bankPopUp.putExtra("reference", reference); // adds the objects reference to the intent to allow for value update in fireBase
                    startActivity(bankPopUp);

                }
            }


            }
        };

    private View.OnClickListener transferClick = new View.OnClickListener(){ // when transfer button is clicked

        @Override
        public void onClick(View v) {
            Log.d(tag, "[onClick] transfer clicked");
                if (selectedCoin != -1) { // -1 is the value for the default "select coin" menu item
                    if (bankedCount < 25) { // cannot send spare change if you don't have any
                        Snackbar.make(findViewById(R.id.viewSnack), "You do not have any spare change, bank 25 coins today!", Snackbar.LENGTH_SHORT).show();
                    } else {
                    List<String> docRefs = new ArrayList<>(collectedMap.keySet()); // creates an array list of all the firestore object references
                    String reference = docRefs.get(selectedCoin); // gets the reference for the coin in fireBase to allow an update

                    Coin c = collectedMap.get(reference); // gets the coin the user wishes to transfer
                    assert c != null;
                    if (c.getId().length() > 29) { // a coin id is always 29 characters, hence if the id has the word SENT at the end it cannot be sent again
                        Snackbar.make(findViewById(R.id.viewSnack), "This coin was transferred to you.", Snackbar.LENGTH_SHORT).show();

                    } else {
                        Intent transferPopup = new Intent(BankActivity.this, TransferWindow.class); // creates the intent to start the pop up activity to bank the coin
                        switch (c.getCurrency()) { // passing the exchange rate to the activity for the coins currency.
                            case ("DOLR"): // when the coins currency is DOLR
                                transferPopup.putExtra("rate", DOLRrate); // adds the exchange rate to the intent
                                break;
                            case ("QUID"):  // when the coins currency is QUID
                                transferPopup.putExtra("rate", QUIDrate); // adds the exchange rate to the intent
                                break;
                            case ("SHIL"): // when the coins currency is SHIL
                                transferPopup.putExtra("rate", SHILLrate); // adds the exchange rate to the intent
                                break;
                            case ("PENY"): // when the coins currency is PENY
                                transferPopup.putExtra("rate", PENYrate); // adds the exchange rate to the intent
                                break;
                        }


                        transferPopup.putExtra("coin", c); // adds the coin to the intent
                        startActivity(transferPopup);
                        }
                    }


                }
            }

    };


    public void updateSpinnerUI(ArrayList<Coin> collected){ // method to update the spinner menu
        Log.d(tag, "[updateSpinnerUI] updating UI");
        String[] drop = new String[collected.size() + 1]; // array for thr drop down list
        drop[0] = "Select coin."; // adds a default value to the menu
        for (int i = 0; i < collected.size(); i++){
            String s  = collected.get(i).getCurrency() + ": " + collected.get(i).getValue(); // populates the drop down menu with the currency and value
            drop[i+1] = s; // populates the drop down menu with the currency and value
        }
        spinner = findViewById(R.id.spinner); // creates the drop down menu spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(BankActivity.this, android.R.layout.simple_spinner_item, drop); // creates the menu

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //sets a layout to the menu
        spinner.setAdapter(adapter); // adds the menu to the spinner

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { // when an item is selected in the menu
                int sp = spinner.getSelectedItemPosition(); // gets the selection
                if (sp != 0) { // do not want the default item
                    selectedCoin = sp-1; // decreases the item by 1 as to match the selected con with he index of the coin list

                }else{
                    selectedCoin = -1; // first list item has been selected
                }
                Log.d(tag, "[getCollected] " + selectedCoin);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    public void updateGoldUI(double gold){
        Log.d(tag, "[updateGoldUI] updating UI");
        TextView goldBalView = findViewById(R.id.goldBalView);
        goldBalView.setText(gold + " GOLD"); // sets the players gold balance
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/game_font.ttf"); // sets the font
        goldBalView.setTypeface(typeface);
    }





}





