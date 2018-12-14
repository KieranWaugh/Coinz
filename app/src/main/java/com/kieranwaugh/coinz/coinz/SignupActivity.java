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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, reEnterPass, inputName; // input boxes for user info
    private ProgressBar progressBar; // progress indicator
    private FirebaseAuth auth; // authentication object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup); // sets activity layout

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance(); // gets authentication

        // element layouts and typeface setting
        Button btnSignIn = findViewById(R.id.sign_in_button);
        Button btnSignUp = findViewById(R.id.sign_up_button);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        reEnterPass = findViewById(R.id.ReEnterPassword);
        progressBar = findViewById(R.id.progressBar);
        inputName = findViewById(R.id.name);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/game_font.ttf");
        btnSignIn.setTypeface(typeface);
        btnSignUp.setTypeface(typeface);


        // return to LoginActivity
        btnSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignupActivity.this, findViewById(R.id.imageView), "transition");
            startActivity(intent, options.toBundle());
        });

        // User wishes to sign up for app
        btnSignUp.setOnClickListener(v -> {
            try  {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
            } catch (Exception ignored) {

            }

            // gets the users inputted data
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String reEnter = reEnterPass.getText().toString().trim();
            String name = inputName.getText().toString().trim();

            if (TextUtils.isEmpty(email)) { // user has not inputted and email address
                Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                assert v1 != null; //
                v1.vibrate(500); // vibrate device
                Snackbar.make(findViewById(R.id.viewSnack), "Enter email address!",Snackbar.LENGTH_LONG).show(); // show error to user
                return;
            }

            if (TextUtils.isEmpty(password)) {// user has not inputted and email address
                Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                assert v1 != null;
                v1.vibrate(500); // vibrate device
                Snackbar.make(findViewById(R.id.viewSnack), "Enter password!",Snackbar.LENGTH_LONG).show();
                return; // show error to user
            }

            if (password.length() < 6) { // user has not inputted a strong enough password
                Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                assert v1 != null;
                v1.vibrate(500); // vibrate device
                Snackbar.make(findViewById(R.id.viewSnack), "Password too short, enter minimum 6 characters!",Snackbar.LENGTH_LONG).show(); // show error to user
                return;
            }

            if(TextUtils.isEmpty(name)){ // user has not inputted a name
                Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                assert v1 != null;
                v1.vibrate(500); // vibrate device
                Snackbar.make(findViewById(R.id.viewSnack), "Enter Name!",Snackbar.LENGTH_LONG).show(); // show error to user
                return;

            }

            if (!password.equals(reEnter)){ // user has not entered the same password
                Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                assert v1 != null;
                v1.vibrate(500); // vibrate device
                Snackbar.make(findViewById(R.id.viewSnack), "Passwords do not match!",Snackbar.LENGTH_LONG).show(); // show error to user
                return;
            }

            progressBar.setVisibility(View.VISIBLE); // shows the progress bar
            //create user
            auth.createUserWithEmailAndPassword(email, password) // creates the new user
                    .addOnCompleteListener(SignupActivity.this, task -> {
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message.
                        // If sign in succeeds the auth state listener will be notified
                        if (!task.isSuccessful()) {
                            Snackbar.make(findViewById(R.id.viewSnack), "Problem signing you up." + task.getException(),Snackbar.LENGTH_LONG).show();
                        } else {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            User u = new User(email, name, 1,25, 1, 0); // initialises the new user to default icon, default  collection radius of 25m, multiplyer of 1x and a gold balance of 0
                            db.collection("user").document(email).collection("INFO").add(u);
                            PlayerStats ps = new PlayerStats(0, 0, 0, 0, 0); // initialises the statistics for the player
                            db.collection("user").document(email).collection("STATS").add(ps);
                            ActivityOptions options = ActivityOptions.makeCustomAnimation(SignupActivity.this, R.anim.nothing, R.anim.bottom_up);
                            startActivity(new Intent(SignupActivity.this, HowToPlayActivity.class), options.toBundle()); // opens the map activity
                            finish();
                        }
                    });

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
