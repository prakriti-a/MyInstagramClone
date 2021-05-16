package com.prakriti.instagramclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
// min SDK ver should be 19> for this app, as we use external libraries

//    private ConstraintLayout signUpScreen;
    private EditText edtSignUpEmail, edtSignUpUsername, edtSignUpPassword;
    private Button btnGoToLogin, btnSignUp;
 //   private GridLayout myGrid;

    private String email, username, password;
    //private String allPersonsName, allPersonsPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setTitle("Sign Up");

        edtSignUpEmail = findViewById(R.id.edtSignUpEmail);
        edtSignUpUsername = findViewById(R.id.edtSignUpUsername);
        edtSignUpPassword = findViewById(R.id.edtSignUpPassword);

//        myGrid = findViewById(R.id.myGrid);
//        myGrid.setVisibility(View.GONE);

        btnGoToLogin = findViewById(R.id.btnGoToLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnGoToLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

        // also create a listener for enter button on device keyboard
        edtSignUpPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // checking if user has pressed key
                    onClick(btnSignUp); // button is a view
                }
                return false;
            }
        });

//        if(ParseUser.getCurrentUser() != null) {
//            ParseUser.getCurrentUser().logOut();
//        }
    }

    // to disappear keyboard when user taps on screen -> set listener for root view of associated layout file
    public void hideKeyboard(View view) {
        // if keyboard is visible, only then hide it. Or else, crash
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
//            case R.id.signUpScreen:
//                hideKeyboard(signUpScreen);
//                break;
        //    case R.id.btnGetData:
                //   retrieveDataFromServer();
        //        break;
            case R.id.btnGoToLogin:
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.btnSignUp:
                signUpNewUser();
                //infoSubmitted();
                break;
        }
    }

    private void signUpNewUser() {
        if (AllAccess.isFieldNull(edtSignUpEmail) || AllAccess.isFieldNull(edtSignUpUsername) ||
                AllAccess.isFieldNull(edtSignUpPassword)) {
            return;
        } else {
            // put parse codes in try-catch block for input errors
            try {
                email = edtSignUpEmail.getText().toString().trim();
                username = edtSignUpUsername.getText().toString().trim();
                password = edtSignUpPassword.getText().toString().trim();
                // also write code for checking repetition of usernames in db

                // User class that already exists in Parse Server
                ParseUser appUser = new ParseUser();
                appUser.setEmail(email);
                appUser.setUsername(username);
                appUser.setPassword(password);

                ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
                progressDialog.setMessage("Signing up");
                progressDialog.show();

                appUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            FancyToast.makeText(SignUpActivity.this, "Sign Up Is Successful!", Toast.LENGTH_SHORT,
                                    FancyToast.SUCCESS, true).show();
                            edtSignUpEmail.setText("");
                            edtSignUpUsername.setText("");
                            edtSignUpPassword.setText("");
                            startActivity(new Intent(SignUpActivity.this, SocialMediaActivity.class));
                            // once logged in, user shouldn't log out by pressing back button
                            finish(); // finishing the Sign up activity so user cannot transition back to it
                        }
                        else {
                            FancyToast.makeText(SignUpActivity.this, "Unable to sign up\n" + e.getMessage() + "\nPlease try again",
                                    Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                });
            } catch (Exception e) {
                FancyToast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                e.printStackTrace();
            }
        }
    }
}

/*        // code to save Parse Object to server
    public void infoSubmitted() {
        // to save object to Parse Server, create a class name
        // pass attributes & save
        if (isFieldNull(edtName) || isFieldNull(edtPhone)) {
            return;
        }
        else {
            // put parse codes in try-catch block for input errors
            try {
                String name = edtName.getText().toString().trim();
                int phone = Integer.parseInt(edtPhone.getText().toString().trim());

                ParseObject person = new ParseObject("Person");
                person.put("name", name);
                person.put("phone", phone);
                // to avoid blocking main thread
                // saves info in db, different from sign up
                person.saveInBackground(new SaveCallback() { // callback sends response after task is completed
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            // use added external library for Fancy Toast messages
                            FancyToast.makeText(SignUpActivity.this, person.get("name") + " saved successfully",
                                    Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                            edtName.setText("");
                            edtPhone.setText("");
                        } else {
                            FancyToast.makeText(SignUpActivity.this, "Unable to save data\n" + e.getMessage(),
                                    Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                            e.printStackTrace();
                        }
                    }
                });
            } catch (NumberFormatException f) {
                FancyToast.makeText(SignUpActivity.this, "Invalid Input", Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                f.printStackTrace();
            } catch (Exception g) {
                FancyToast.makeText(SignUpActivity.this, g.getMessage(), Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
            }
        }
    }
*/

/*
    // retrieve data from Parse Server using Query
    private void retrieveDataFromServer() {
        allPersonsName = "";
        allPersonsPhone = "";
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Person"); // pass name of class
        // use FindInBackground to get all objects from server
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                // returns List of parse objects
                if(e == null && objects.size()>0) {
                    myGrid.setVisibility(View.VISIBLE);
                    for(ParseObject obj : objects) {
                        allPersonsName = allPersonsName + obj.getString("name") + "\n";
                        allPersonsPhone = allPersonsPhone + obj.getInt("phone") + "\n";
                        txtNameData.setText(allPersonsName);
                        txtPhoneData.setText(allPersonsPhone);
                    }
                    FancyToast.makeText(SignUpActivity.this, "Data retrieved successfully!", Toast.LENGTH_SHORT,
                            FancyToast.SUCCESS, true).show();
                }
                else {
                    FancyToast.makeText(SignUpActivity.this, "Unable to retrieve data\n" + e.getMessage(),
                            Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                    e.printStackTrace();
                }
            }
        });

        // getInBackground returns object with passed object ID
//                parseQuery.getInBackground("6G0Aqqx0FV", // object ID from server
//                        new GetCallback<ParseObject>() {
//                    @Override
//                    public void done(ParseObject object, ParseException e) {
//                        if (object != null && e == null) {
//                            myGrid.setVisibility(View.VISIBLE);
//                            txtData.setText(object.getString("name") + " [Phone No: " + object.getInt("phone") + "]");
//                        }
//                    }
//                });
    }
*/
