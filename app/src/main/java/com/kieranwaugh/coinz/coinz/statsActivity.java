package com.kieranwaugh.coinz.coinz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class statsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_stats:

                        break;

                    case R.id.navigation_map:
                        Intent intent2 = new Intent(statsActivity.this, mapActivity.class);
                        startActivity(intent2);
                        break;

                    case R.id.navigation_bank:
                        Intent intent3 = new Intent(statsActivity.this, bankActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });
    }
}
