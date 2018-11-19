package com.kieranwaugh.coinz.coinz;

import android.app.ActivityOptions;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;


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


public class bankActivity extends AppCompatActivity {
    public String mapData;
    public double SHILLrate;
    public double QUIDrate;
    public double PENYrate;
    public double DOLRrate;
    private Spinner spinner;
    private int selectedCoin;
    private int bankedCount;
    //private Button popup;
    //public ArrayList <String> coinRefs = new ArrayList<>();
    public ArrayList<coin> collected = new ArrayList<>();
    private LinkedHashMap<String, coin> collectedMap = new LinkedHashMap<>();
    String tag = "bankActivity";
    //ArrayList totals;
    private double goldBal;
    //private TextView txt;
    String dateDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    //String UID = FirebaseAuth.getInstance().getUid();
    private String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_bank);
        Button bankButton =findViewById(R.id.bankButton);
        bankButton.setOnClickListener(bankClick);
        Button transferButton = findViewById(R.id.transferButton);
        transferButton.setOnClickListener(transferClick);

        getCollected();
        getRates();
        super.onCreate(savedInstanceState);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        //ActivityOptions options1 = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        ActivityOptions options2 = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.navigation_stats:
                    Intent intent1 = new Intent(bankActivity.this, statsActivity.class);
                    startActivity(intent1, options2.toBundle());
                    break;

                case R.id.navigation_map:
                    Intent intent2 = new Intent(bankActivity.this, mapActivity.class);
                    startActivity(intent2, options2.toBundle());
                    break;

                case R.id.navigation_bank:

                    break;
            }
            return false;
        });

        try {

            JSONObject json = new JSONObject(mapData);
            SHILLrate = json.getJSONObject("rates").getDouble("SHIL");
            QUIDrate = json.getJSONObject("rates").getDouble("QUID");
            PENYrate = json.getJSONObject("rates").getDouble("PENY");
            DOLRrate = json.getJSONObject("rates").getDouble("DOLR");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(tag, "[onCreate] " + collected.toString());
//        TextView txt = findViewById(R.id.ratesView);
//        txt.setText("Daily Exchange Rates:\nSHILL - " + SHILLrate + "\nQUID - " + QUIDrate + "\nPENY - " + PENYrate + "\nDOLR - " + DOLRrate);



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.threebutton, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.threebutton_signout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(bankActivity.this, MainActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getRates(){
        String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        String savedMapData = "mapData";
        SharedPreferences FromFile = getSharedPreferences(savedMapData, Context.MODE_PRIVATE);
        mapData = FromFile.getString(date, "");

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference cr = rootRef.collection("bank").document(email).collection("gold");
        cr.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    goldBal = Double.parseDouble(Objects.requireNonNull(document.get("balance")).toString());
                }
                updateGoldUI(goldBal);

            }

//            goldBalView = findViewById(R.id.goldBalView);
//            goldBalView.setText("Gold Balance: " + goldBal);
//            Log.d(tag, "[getRates] " + goldBal);
        });

    }

    @Override
    public void onBackPressed(){
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.bottom_down, R.anim.nothing);
        startActivity(new Intent(bankActivity.this, MainActivity.class), options.toBundle());
    }

    @Override
    public void onStart(){
        super.onStart();


    }

    public void onDestroy(){
        super.onDestroy();

    }

    public void getCollected(){
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference cr = rootRef.collection("wallet").document(email).collection("collected ("+dateDB +")");
       cr.get().addOnCompleteListener(task -> {
           if (task.isSuccessful()) {

               for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                   String ref = (document.getId());
                   Log.d(tag, ref);
                   coin c = document.toObject(coin.class);

                   assert c != null;
                   if (!c.isBanked()){
                       collectedMap.put(ref, c);
                       collected.add(c);
                   }else{
                       bankedCount +=1;
                       Log.d(tag, "no banked " + bankedCount);
                   }
               }
                updateSpinnerUI(collected);
           }
       });



    }

    private View.OnClickListener bankClick = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if (bankedCount > 25){
                if (selectedCoin !=51){
                    Snackbar.make(findViewById(R.id.viewSnack), "You have already banked 25 coins today!", Snackbar.LENGTH_LONG).show();
                }

            }else{
                if (selectedCoin != 51){
                    List<String> docRefs = new ArrayList<>(collectedMap.keySet());
                    String reference = docRefs.get(selectedCoin);

                    coin c = collectedMap.get(reference);
                    Intent popupIntent = new Intent(bankActivity.this, BankWindow.class);
                    assert c != null;
                    switch(c.getCurrency()){
                        case ("DOLR"):
                            popupIntent.putExtra("rate", DOLRrate);
                            break;
                        case ("QUID"):
                            popupIntent.putExtra("rate", QUIDrate);
                            break;
                        case ("SHIL"):
                            popupIntent.putExtra("rate", SHILLrate);
                            break;
                        case ("PENY"):
                            popupIntent.putExtra("rate", PENYrate);
                            break;
                    }


                    popupIntent.putExtra("coin", c);
                    popupIntent.putExtra("gold", goldBal);
                    popupIntent.putExtra("reference", reference);
                    startActivity(popupIntent);

                }
            }


            }
        };

    private View.OnClickListener transferClick = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if (bankedCount > 25){
                if (selectedCoin !=51){
                    Snackbar.make(findViewById(R.id.viewSnack), "You have already banked 25 coins today!", Snackbar.LENGTH_LONG).show();
                }

            }else{
                if (selectedCoin != 51){
                    List<String> docRefs = new ArrayList<>(collectedMap.keySet());
                    String reference = docRefs.get(selectedCoin);

                    coin c = collectedMap.get(reference);
                    Intent popupIntent = new Intent(bankActivity.this, TransferWindow.class);
                    assert c != null;
                    switch(c.getCurrency()){
                        case ("DOLR"):
                            popupIntent.putExtra("rate", DOLRrate);
                            break;
                        case ("QUID"):
                            popupIntent.putExtra("rate", QUIDrate);
                            break;
                        case ("SHIL"):
                            popupIntent.putExtra("rate", SHILLrate);
                            break;
                        case ("PENY"):
                            popupIntent.putExtra("rate", PENYrate);
                            break;
                    }


                    popupIntent.putExtra("coin", c);
                    popupIntent.putExtra("gold", goldBal);
                    popupIntent.putExtra("reference", reference);
                    startActivity(popupIntent);

                }
            }
        }
    };


    public void updateSpinnerUI(ArrayList<coin> collected){
        String[] drop = new String[collected.size() + 1];
        drop[0] = "Select coin.";
        for (int i = 0; i < collected.size(); i++){
            String s  = collected.get(i).getCurrency() + ": " + collected.get(i).getValue();
            drop[i+1] = s;
        }
        Log.d(tag, collectedMap.toString());
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(bankActivity.this,
                android.R.layout.simple_spinner_item, drop);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int sp = spinner.getSelectedItemPosition();
                if (sp != 0) {
                    selectedCoin = sp-1;

                }else{
                    selectedCoin = 51; // not possible ot get 51 coins, hence shows first list item has been selected
                }
                Log.d(tag, "[getCollected] " + selectedCoin);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    public void updateGoldUI(double gold){
        TextView goldBalView = findViewById(R.id.goldBalView);
        goldBalView.setText(gold + " GOLD");
    }





}





