package com.kieranwaugh.coinz.coinz;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class bankActivity extends AppCompatActivity {
    public String mapData;
    private final String savedMapData = "mapData";
    public double SHILLrate;
    public double QUIDrate;
    public double PENYrate;
    public double DOLRrate;
    String tag = "bankActivity";
    private int goldBal;
    private TextView txt;
    String UID = FirebaseAuth.getInstance().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        ActivityOptions options1 = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        ActivityOptions options2 = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            }




        });

        try {

            getRates();
            JSONObject json = new JSONObject(mapData);
            SHILLrate = json.getJSONObject("rates").getDouble("SHIL");
            QUIDrate = json.getJSONObject("rates").getDouble("QUID");
            PENYrate = json.getJSONObject("rates").getDouble("PENY");
            DOLRrate = json.getJSONObject("rates").getDouble("DOLR");
            txt = (TextView)findViewById(R.id.ratesView);
            txt.setText("Rates:\nSHILL: " + SHILLrate +"\nQUID: " + QUIDrate + "\nPENY: " + PENYrate + "\nDOLR: " + DOLRrate);




        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        SharedPreferences FromFile = getSharedPreferences(savedMapData, Context.MODE_PRIVATE);
        mapData = FromFile.getString(date, "");
    }

    @Override
    public void onBackPressed(){
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.bottom_down, R.anim.nothing);
        startActivity(new Intent(bankActivity.this, MainActivity.class), options.toBundle());
    }

    public void getCollected(){

        db.collection("bank").document(UID).collection("balance").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (e == null){
                    //find gold balance here
                }
            }
        });
    }

}
