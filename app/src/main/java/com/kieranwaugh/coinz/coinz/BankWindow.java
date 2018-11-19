package com.kieranwaugh.coinz.coinz;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
    String dateDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    private String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_window);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .6));

        Intent intent = getIntent();
        coin c = (coin) intent.getSerializableExtra("coin");
        double gold = intent.getDoubleExtra("gold", 0.0);
        String reference = intent.getStringExtra("reference");
        double rate = intent.getDoubleExtra("rate", 0.0);
        TextView curView = findViewById(R.id.currencyConfirm);
        curView.setText(c.getCurrency() + ": " + c.getValue());
        TextView goldView = findViewById(R.id.goldConfirm);
        goldView.setText(c.getValue()*rate + " GOLD");

        bankButton = findViewById(R.id.bankButton);
        bankButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("wallet").document(email).collection("collected (" + dateDB + ")").document(reference).update("banked", true);

            String[] ref = new String[1];
            CollectionReference cr = db.collection("bank").document(email).collection("gold");
            cr.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        ref[0] = document.getId();
                    }
                }
                db.collection("bank").document(email).collection("gold").document(ref[0]).update("balance", gold +(c.getValue() * rate));

                Intent refresh = new Intent(BankWindow.this, bankActivity.class);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.nothing);
                startActivity(refresh, options.toBundle());

            });
        });
    }
}
