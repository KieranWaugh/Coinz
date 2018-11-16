package com.kieranwaugh.coinz.coinz;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransferWindow extends AppCompatActivity {

    private TextView curView;
    private EditText email;
    private Button transfer;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String dateDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    private String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_window);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .6));


        Intent intent = getIntent();
        coin c = (coin) intent.getSerializableExtra("coin");
        double gold = (double) intent.getDoubleExtra("gold", 0.0);
        String reference = (String) intent.getStringExtra("reference");
        double rate = (double) intent.getDoubleExtra("rate", 0.0);
        curView = findViewById(R.id.currencyConfirm);
        curView.setText(c.getCurrency() + ": " + c.getValue());
        email = findViewById(R.id.emailText);
        transfer = findViewById(R.id.confirmButton);

        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("wallet").document(email.getText().toString()).collection("collected ("+dateDB +")").add(c).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(!email.getText().toString().equals(userEmail)){
                            db.collection("wallet").document(userEmail).collection("collected (" + dateDB + ")").document(reference).update("banked", true);
                            Intent refresh = new Intent(TransferWindow.this, bankActivity.class);
                            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.nothing);
                            startActivity(refresh, options.toBundle());
                        }else{
                            email.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                        }

                    }
                });
            }
        });




    }
}
