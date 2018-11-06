package com.kieranwaugh.coinz.coinz;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
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

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
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
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, ResetPasswordActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignupActivity.this, findViewById(R.id.imageView), "transition");
                startActivity(intent, options.toBundle());
            }
        });

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

                if (TextUtils.isEmpty(email)) {
                    //Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(R.id.viewSnack), "Enter email address!",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    //Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(R.id.viewSnack), "Enter password!",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (password.length() < 6) {
                    //Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(R.id.viewSnack), "Password too short, enter minimum 6 characters!",Snackbar.LENGTH_LONG).show();
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
                                    ActivityOptions options = ActivityOptions.makeCustomAnimation(SignupActivity.this, R.anim.nothing, R.anim.bottom_up);
                                    startActivity(new Intent(SignupActivity.this, mapActivity.class), options.toBundle());
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
