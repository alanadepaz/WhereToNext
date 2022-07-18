package com.alana.wheretonext.ui.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alana.wheretonext.exceptions.UserException;
import com.alana.wheretonext.service.UserService;
import com.alana.wheretonext.ui.login.LoginActivity;

import com.alana.wheretonext.R;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = "SignUpActivity";

    private Button btnNewUserSignUp;
    private EditText etSignUpUsername;
    private EditText etSignUpEmail;
    private EditText etSignUpPassword;

    private UserService userService = new UserService();

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
                    userService.saveUser(username, password, email);
                    Toast.makeText(SignUpActivity.this, "User successfully created!", Toast.LENGTH_SHORT).show();

                    // After signing up, force the user to log in with their new credentials
                    userService.logoutUser();
                    goLoginActivity();

                } catch (UserException e) {
                    Log.e(TAG, "Could not save user: " + e);
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void goLoginActivity() {
        Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(i);
        finish();   // Finishing main activity once we've done the navigation
    }
}
