package com.kieranwaugh.coinz.coinz;

import android.app.ActionBar;
import android.app.ActivityOptions;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        // set the view now
        setContentView(R.layout.activity_login_test);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        Button btnSignup = findViewById(R.id.btn_signup);
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnReset = (Button) findViewById(R.id.btn_reset_password);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/game_font.ttf");
        btnLogin.setTypeface(typeface);
        btnReset.setTypeface(typeface);
        btnSignup.setTypeface(typeface);



        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, findViewById(R.id.imageView), "transition");
            startActivity(intent, options.toBundle());
        });

        btnReset.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, findViewById(R.id.imageView), "transition");
            startActivity(intent, options.toBundle());
        });

        btnLogin.setOnClickListener(v -> {

            try  {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
            } catch (Exception ignored) {

            }

            String email = inputEmail.getText().toString();
            final String password = inputPassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                assert v1 != null;
                v1.vibrate(500);

                inputEmail.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                Snackbar.make(findViewById(R.id.viewSnack), "Enter email address!",Snackbar.LENGTH_LONG).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                assert v1 != null;
                v1.vibrate(500);

                inputPassword.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                Snackbar.make(findViewById(R.id.viewSnack), "Enter password!",Snackbar.LENGTH_LONG).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            //authenticate user
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                inputEmail.setError(getString(R.string.minimum_password));
                            } else {
                                Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                assert v1 != null;
                                v1.vibrate(500);
                                inputPassword.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                                inputEmail.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                                Snackbar.make(findViewById(R.id.viewSnack), getString(R.string.auth_failed),Snackbar.LENGTH_LONG).show();
                                inputEmail.setError(getString(R.string.minimum_password));
                            }
                        } else {
                            Intent intent = new Intent(LoginActivity.this, mapActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        });
    }

}
