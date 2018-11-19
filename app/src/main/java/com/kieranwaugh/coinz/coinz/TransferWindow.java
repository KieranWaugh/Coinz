package com.kieranwaugh.coinz.coinz;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
public class TransferWindow extends AppCompatActivity {

    private EditText email;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String dateDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    private String userEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

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
        String reference = intent.getStringExtra("reference");
        TextView curView = findViewById(R.id.currencyConfirm);
        curView.setText(c.getCurrency() + ": " + c.getValue());
        email = findViewById(R.id.emailText);
        Button transfer = findViewById(R.id.confirmButton);

        transfer.setOnClickListener(v -> db.collection("wallet").document(email.getText().toString()).collection("collected ("+dateDB +")").add(c).addOnCompleteListener(task -> {
            if(!email.getText().toString().equals(userEmail)){
                db.collection("wallet").document(userEmail).collection("collected (" + dateDB + ")").document(reference).update("banked", true);
                Intent refresh = new Intent(TransferWindow.this, bankActivity.class);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.nothing);
                startActivity(refresh, options.toBundle());
            }else{
                email.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
            }

        }));




    }
}
