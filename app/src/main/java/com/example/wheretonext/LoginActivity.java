package com.example.wheretonext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignUp;
    private ImageView ivIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        ivIcon = findViewById(R.id.ivIcon);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick sign up button");
                goSignUpActivity();
            }
        });
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user" + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Toast.makeText(LoginActivity.this, "Issue with login.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Issue with login", e);
                    return;
                }
                // Navigate to the feed activity if the user has signed in properly
                goMainActivity();
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();   // Finishing login activity once we've done the navigation
    }

    private void goSignUpActivity() {
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
        finish();   // Finishing login activity once we've done the navigation
    }
}