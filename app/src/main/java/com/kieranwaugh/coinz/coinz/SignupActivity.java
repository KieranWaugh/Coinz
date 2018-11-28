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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, reEnterPass, inputName;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_test);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        reEnterPass = findViewById(R.id.ReEnterPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        inputName = findViewById(R.id.name);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/game_font.ttf");
        btnSignIn.setTypeface(typeface);
        btnSignUp.setTypeface(typeface);


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignupActivity.this, findViewById(R.id.imageView), "transition");
                startActivity(intent, options.toBundle());
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String reEnter = reEnterPass.getText().toString().trim();
                String name = inputName.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    //Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    assert v1 != null;
                    v1.vibrate(500);
                    Snackbar.make(findViewById(R.id.viewSnack), "Enter email address!",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    assert v1 != null;
                    v1.vibrate(500);
                    //Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(R.id.viewSnack), "Enter password!",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (password.length() < 6) {
                    Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    assert v1 != null;
                    v1.vibrate(500);
                    //Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(R.id.viewSnack), "Password too short, enter minimum 6 characters!",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(name)){
                    Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    assert v1 != null;
                    v1.vibrate(500);
                    Snackbar.make(findViewById(R.id.viewSnack), "Enter Name!",Snackbar.LENGTH_LONG).show();
                    return;

                }

                if (!password.equals(reEnter)){
                    Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    assert v1 != null;
                    v1.vibrate(500);
                    Snackbar.make(findViewById(R.id.viewSnack), "Passwords do not match!",Snackbar.LENGTH_LONG).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    //Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
                                    Snackbar.make(findViewById(R.id.viewSnack), "Problem signing you up." + task.getException(),Snackbar.LENGTH_LONG).show();
                                } else {
                                    //String UID = FirebaseAuth.getInstance().getUid();
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    //Gold gold = new Gold(0.0);
                                    //db.collection("users").document(email).collection(UID).add(gold);
                                    //db.collection("bank").document(email).collection("gold").add(gold);
                                    User u = new User(email, name, 1,25, 1, 0); // initialises the new user to default icon, default  collection radius of 25m, multiplyer of 1x and a gold balance of 0
                                    db.collection("user").document(email).collection("INFO").add(u);
                                    PlayerStats ps = new PlayerStats(0, 0, 0, 0, 0);
                                    db.collection("user").document(email).collection("STATS").add(ps);
                                    //db.collection("user").document(email).collection("Friends").add(u);
                                    ActivityOptions options = ActivityOptions.makeCustomAnimation(SignupActivity.this, R.anim.nothing, R.anim.bottom_up);
                                    startActivity(new Intent(SignupActivity.this, HowToPlayActivity.class), options.toBundle());
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
