package com.prakriti.instagramclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

//    private ConstraintLayout logInScreen;
    private EditText edtLoginID, edtLoginPassword;
    private Button btnLogin, btnGoToSignUp;
    private String loginID, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Log In");

        edtLoginID = findViewById(R.id.edtLoginID);
        edtLoginPassword = findViewById(R.id.edtLoginPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnGoToSignUp = findViewById(R.id.btnGoToSignUp);

        btnLogin.setOnClickListener(this);
        btnGoToSignUp.setOnClickListener(this);

        // also create a listener for enter button on device keyboard
        edtLoginPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // checking if user has pressed key
                    onClick(btnLogin); // button is a view
                }
                return false;
            }
        });

//        if(ParseUser.getCurrentUser() != null) {
//            ParseUser.getCurrentUser().logOut();
//        }
    }

    public void hideKeyboard(View view) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            view.clearFocus();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.logInScreen:
//                SignUpActivity.hideKeyboard(this, logInScreen);
//                break;
            case R.id.btnLogin:
                logInExistingUser();
                break;

            case R.id.btnGoToSignUp:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
                break;
        }
    }

    private void logInExistingUser() {
        if(AllAccess.isFieldNull(edtLoginID) || AllAccess.isFieldNull(edtLoginPassword)) {
            return;
        }
        else {
            try {
                loginID = edtLoginID.getText().toString().trim();
                password = edtLoginPassword.getText().toString().trim();

                ParseUser.logInInBackground(loginID, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(user != null && e == null) {
                            FancyToast.makeText(LoginActivity.this, user.getString("username") + " logged in successfully!",
                                    Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                            edtLoginID.setText("");
                            edtLoginPassword.setText("");
                            startActivity(new Intent(LoginActivity.this, SocialMediaActivity.class));
                            // finish this activity so user cannot log out by back button
                            finish();
                        }
                        else {
                            FancyToast.makeText(LoginActivity.this, "Unable to log in\n" + e.getMessage() + "\nPlease try again",
                                    Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
            catch (Exception e) {
                FancyToast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                e.printStackTrace();
            }
        }
    }
}