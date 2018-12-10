package com.kieranwaugh.coinz.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;

public class HowToPlayActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        Button b = findViewById(R.id.button);
        b.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), MapActivity.class);
            startActivity(i);
        });

    }
}
