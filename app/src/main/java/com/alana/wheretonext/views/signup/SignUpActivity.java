package com.alana.wheretonext.views.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alana.wheretonext.views.login.LoginActivity;
import com.parse.ParseException;
import com.parse.ParseUser;

import com.alana.wheretonext.R;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = "SignUpActivity";

    private Button btnNewUserSignUp;
    private EditText etSignUpUsername;
    private EditText etSignUpEmail;
    private EditText etSignUpPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnNewUserSignUp = findViewById(R.id.btnNewUserSignUp);
        etSignUpUsername = findViewById(R.id.etSignUpUsername);
        etSignUpEmail = findViewById(R.id.etSignUpEmail);
        etSignUpPassword = findViewById(R.id.etSignUpPassword);

        btnNewUserSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etSignUpUsername.getText().toString();
                if (username.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String password = etSignUpPassword.getText().toString();
                if (password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String email = etSignUpEmail.getText().toString();

                try {
                    saveUser(username, password, email);
                    Toast.makeText(SignUpActivity.this, "User successfully created!", Toast.LENGTH_SHORT).show();
                    logoutUser();
                } catch (ParseException e) {
                    Log.e(TAG, "Could not save user.");
                    e.printStackTrace();
                }
            }

            private void saveUser(String username, String password, String email) throws ParseException {
                ParseUser newUser = new ParseUser();
                newUser.put("username", username);
                newUser.put("password", password);
                newUser.put("email", email);
                newUser.signUp();
            }
        });
    }

    private void logoutUser() {
        Log.i(TAG, "Attempting to log out user");
        ParseUser.logOutInBackground();
        goLoginActivity();
    }

    private void goLoginActivity() {
        Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(i);
        finish();   // Finishing main activity once we've done the navigation
    }
}
