package com.prakriti.instagramclone;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AllAccess {

    // check for empty fields submitted
    public static boolean isFieldNull(EditText field) {
        if (field.getText().toString().trim().equalsIgnoreCase("")) {
            field.setError("This field cannot be blank");
            field.requestFocus();
            return true;
        }
        return false;
        // equals() compares contents, == compares objects
     }

    public void hideKeyboard(View view) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

//    // to disappear keyboard when user taps on screen -> set listener for root view of associated layout file
//    public static void hideKeyboard(Activity activity, Context context, View view) {
//        // if keyboard is visible, only then hide it. Or else, crash
//        try {
//            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//            if(activity.getCurrentFocus() != null) {
//                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
//            }
//            view.clearFocus();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}