package com.kieranwaugh.coinz.coinz;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class ResetPasswordActivity extends AppCompatActivity {

    //variables for activity
    private EditText inputEmail;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password); // sets layout

        //finding views
        inputEmail = findViewById(R.id.email);
        Button btnReset = findViewById(R.id.btn_reset_password);
        Button btnBack = findViewById(R.id.btn_back);
        TextView forgot = findViewById(R.id.fogotPasswordtext);
        TextView email = findViewById(R.id.enterEmailtext);
        progressBar = findViewById(R.id.progressBar);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/game_font.ttf");
        btnReset.setTypeface(typeface);
        btnBack.setTypeface(typeface);
        forgot.setTypeface(typeface);
        email.setTypeface(typeface);


        auth = FirebaseAuth.getInstance(); // getting firebase auth

        btnBack.setOnClickListener(v -> { // starts login activity
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ResetPasswordActivity.this, findViewById(R.id.imageView), "transition");
            startActivity(intent, options.toBundle());
        });

        btnReset.setOnClickListener(v -> { // reset button
            try  {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
            } catch (Exception ignored) {

            }

            String email1 = inputEmail.getText().toString().trim(); // gets input

            if (TextUtils.isEmpty(email1)) {
                Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); // if empty vibrate
                assert v1 != null;
                v1.vibrate(500);
                //Toast.makeText(getApplication(), "Enter your Email Address", Toast.LENGTH_SHORT).show();
                Snackbar.make(findViewById(R.id.viewSnack), "Enter your Email Address",Snackbar.LENGTH_LONG).show(); // display message
                return;
            }

            progressBar.setVisibility(View.VISIBLE); // shows spinner
            auth.sendPasswordResetEmail(email1) // send password rest email
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Snackbar.make(findViewById(R.id.viewSnack), "Check your email for password reset instructions",Snackbar.LENGTH_LONG).show();

                        } else {
                            Snackbar.make(findViewById(R.id.viewSnack), "Problem sending email, please try again.",Snackbar.LENGTH_LONG).show();

                        }

                        progressBar.setVisibility(View.GONE);
                    });
        });
    }

}
