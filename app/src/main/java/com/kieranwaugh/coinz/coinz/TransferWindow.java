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

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String dateDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    private String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    ArrayList<User> friendList = new ArrayList<>();
    private Spinner spinner;
    private int selectedfriend;

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
        Button transfer = findViewById(R.id.confirmButton);


        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference cr = rootRef.collection("user").document(email).collection("Friends");
        cr.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    friendList.add((User) document.toObject(User.class));

                }
                updateSpinnerUI(friendList);
            }
        });

        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectedfriend != 51){
                    c.setId(c.getId()+"SENT");
                    Log.d("transfer", c.getId() + " " + c.getId().length());
                    db.collection("wallet").document(friendList.get(selectedfriend).getEmail()).collection("collected ("+dateDB +")").add(c);
                    db.collection("wallet").document(email).collection("collected ("+dateDB +")").document(reference).update("banked", true);
                    Intent i = new Intent(getApplicationContext(), BankActivity.class);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                    startActivity(i, options.toBundle());
                }else{

                }
            }
        });

    }

    public void updateSpinnerUI(ArrayList<User> friends){
        String[] drop = new String[friends.size() + 1];
        drop[0] = "Select Friend:";
        for (int i = 0; i < friends.size(); i++){
            String s  = friends.get(i).getName();
            drop[i+1] = s;
        }
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(TransferWindow.this,
                android.R.layout.simple_spinner_item, drop);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int sp = spinner.getSelectedItemPosition();
                if (sp != 0) {
                    selectedfriend = sp-1;

                }else{
                    selectedfriend = 51; // not possible ot get 51 coins, hence shows first list item has been selected
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }
}
