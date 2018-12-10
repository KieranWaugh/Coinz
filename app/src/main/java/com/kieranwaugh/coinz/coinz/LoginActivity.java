package com.kieranwaugh.coinz.coinz;


import android.app.ActivityOptions;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword; // input boxes
    private FirebaseAuth auth; // firebase auth variable
    private ProgressBar progressBar; // progress bar for logging in to the app

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        // set the view now
        setContentView(R.layout.activity_login_test); // sets activity layout

        inputEmail = findViewById(R.id.email); // email input
        inputPassword = findViewById(R.id.password); // password input
        progressBar = findViewById(R.id.progressBar); // progress indicator (spinner)
        Button btnSignup = findViewById(R.id.btn_signup); // button to transfer to sign up activity
        Button btnLogin = findViewById(R.id.btn_login);// Log in button
        Button btnReset = findViewById(R.id.btn_reset_password); // button to transfer to password reset activity
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/game_font.ttf"); // game font
        btnLogin.setTypeface(typeface); // setting the game font
        btnReset.setTypeface(typeface);
        btnSignup.setTypeface(typeface);



        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, findViewById(R.id.imageView), "transition");
            startActivity(intent, options.toBundle()); // transfers user to sign up activity, shared element transition with the app logo.
        });

        btnReset.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, findViewById(R.id.imageView), "transition");
            startActivity(intent, options.toBundle());// transfers user to password reset activity, shared element transition with the app logo.
        });

        btnLogin.setOnClickListener(v -> {

            try  {
                InputMethodManager im = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                assert im != null;
                im.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
            } catch (Exception ignored) {

            }

            String email = inputEmail.getText().toString(); // gets the users inputted email
            final String password = inputPassword.getText().toString(); // gets users inputted password

            if (TextUtils.isEmpty(email)) { // if the user has not entered an email address
                Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); // vibrate phone
                assert v1 != null;
                v1.vibrate(500);

                inputEmail.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                Snackbar.make(findViewById(R.id.viewSnack), "Enter email address!",Snackbar.LENGTH_LONG).show();
                return; // sets the text input box to red and displays a snackbar stating to input an email.
            }

            if (TextUtils.isEmpty(password)) { // if password is empty
                Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); // vibrate device
                assert v1 != null;
                v1.vibrate(500);

                inputPassword.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                Snackbar.make(findViewById(R.id.viewSnack), "Enter password!",Snackbar.LENGTH_LONG).show();
                return; // turn input box red and display snackbar
            }

            progressBar.setVisibility(View.VISIBLE); //shows the logging in progress bar

            //authenticate user
            auth.signInWithEmailAndPassword(email, password) // authenticates the user via firebase
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        progressBar.setVisibility(View.GONE); // removes progress bar
                        if (!task.isSuccessful()) {
                            // there was an error, sets the input bars to red and vibrates the phone, snackbar states the error
                            Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            assert v1 != null;
                            v1.vibrate(500);
                            inputPassword.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                            inputEmail.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                            Snackbar.make(findViewById(R.id.viewSnack), getString(R.string.auth_failed),Snackbar.LENGTH_LONG).show();
                            inputEmail.setError(getString(R.string.minimum_password));
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MapActivity.class); // login successful, transfer to map activity
                            startActivity(intent);
                            finish();
                        }
                    });
        });
    }

}
