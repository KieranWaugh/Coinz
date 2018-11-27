package com.kieranwaugh.coinz.coinz;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText inputEmail;
    private Button btnReset, btnBack;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private TextView forgot, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_test);

        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnBack = (Button) findViewById(R.id.btn_back);
        forgot = findViewById(R.id.fogotPasswordtext);
        email = findViewById(R.id.enterEmailtext);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/game_font.ttf");
        btnReset.setTypeface(typeface);
        btnBack.setTypeface(typeface);
        forgot.setTypeface(typeface);
        email.setTypeface(typeface);


        auth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ResetPasswordActivity.this, findViewById(R.id.imageView), "transition");
                startActivity(intent, options.toBundle());
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }

                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    assert v1 != null;
                    v1.vibrate(500);
                    //Toast.makeText(getApplication(), "Enter your Email Address", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(R.id.viewSnack), "Enter your Email Address",Snackbar.LENGTH_LONG).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //Toast.makeText(ResetPasswordActivity.this, "Check your email for password reset instructions", Toast.LENGTH_SHORT).show();
                                    Snackbar.make(findViewById(R.id.viewSnack), "Check your email for password reset instructions",Snackbar.LENGTH_LONG).show();

                                } else {
                                    //Toast.makeText(ResetPasswordActivity.this, "Problem sending email, please try again.", Toast.LENGTH_SHORT).show();
                                    Snackbar.make(findViewById(R.id.viewSnack), "Problem sending email, please try again.",Snackbar.LENGTH_LONG).show();

                                }

                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

}
