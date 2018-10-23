package com.kieranwaugh.coinz.coinz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class CreateAccountActivity extends AppCompatActivity {
String tag = "CreateAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "In create account");
        setContentView(R.layout.activity_create_account);
    }
}
