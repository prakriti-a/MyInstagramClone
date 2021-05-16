package com.prakriti.instagramclone;

import android.content.Intent;
import android.os.Bundle;

import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

public class ProfileTab extends Fragment implements View.OnClickListener {

    private TextView txtUserWelcome, txt_server_displayName, txt_server_bio;
    private EditText edtProfileDisplayName, edtProfileBio;
    private Button btnProfileUpdate, btnProfileSaveChanges, btnProfileLogout, btnProfileCancelChanges;
    private String profileDisplayName, profileBio;
    private LinearLayout llEditProfile;

    private ParseUser currentUser;


    public ProfileTab() {}

    public static ProfileTab newInstance() {
        return  new ProfileTab();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment -> this method is similar to setContentView()
        View view =  inflater.inflate(R.layout.fragment_profile_tab, container, false);
        // container of fragment is the activity
        // this 'view' holds reference to UI component's ID

        currentUser = ParseUser.getCurrentUser();

        // initialise UI components
        txtUserWelcome = view.findViewById(R.id.txtUserWelcome);
        txtUserWelcome.setText("Welcome " + currentUser.getString("username") + "!");

        txt_server_displayName = view.findViewById(R.id.txt_server_displayName);
        txt_server_bio = view.findViewById(R.id.txt_server_bio);
        updateProfileFields();

        edtProfileDisplayName = view.findViewById(R.id.edtProfileDisplayName);
        edtProfileBio = view.findViewById(R.id.edtProfileBio);

        btnProfileUpdate = view.findViewById(R.id.btnProfileUpdate);
        btnProfileSaveChanges = view.findViewById(R.id.btnProfileSaveChanges);
        btnProfileLogout = view.findViewById(R.id.btnProfileLogout);
        btnProfileCancelChanges = view.findViewById(R.id.btnProfileCancelChanges);

        btnProfileUpdate.setOnClickListener(this);
        btnProfileSaveChanges.setOnClickListener(this);
        btnProfileLogout.setOnClickListener(this);
        btnProfileCancelChanges.setOnClickListener(this);

        llEditProfile = view.findViewById(R.id.llEditProfile);

        return view;
    }

    @Override
    public void onClick(View v) {
        // data sent to server when button is clicked, and data to be displayed again when user logs in
        switch (v.getId()) {

            case R.id.btnProfileUpdate:
                llEditProfile.setVisibility(View.VISIBLE);
                edtProfileDisplayName.setText(currentUser.getString("displayName"));
                edtProfileBio.setText(currentUser.getString("bio"));
                break;

            case R.id.btnProfileSaveChanges:
                saveDataToServer();
                break;

            case R.id.btnProfileLogout:
                ParseUser.getCurrentUser().logOut();
                FancyToast.makeText(getContext(), "You have logged out", Toast.LENGTH_SHORT, FancyToast.DEFAULT, true).show();
                // code to exit fragment and go to login page
                startActivity(new Intent(getContext(), SignUpActivity.class));
                getActivity().finish();
                break;

            case R.id.btnProfileCancelChanges:
                edtProfileDisplayName.setText("");
                edtProfileBio.setText("");
                llEditProfile.setVisibility(View.GONE);
                break;
        }
    }


    private void updateProfileFields() {
        if(currentUser.getString("displayName") != null) {
            txt_server_displayName.setText("Name:  " + currentUser.getString("displayName"));
        }
        if(currentUser.getString("bio") != null) {
            txt_server_bio.setText("Bio:  " + currentUser.getString("bio"));
        }
    }


    private void saveDataToServer() {
        profileDisplayName = edtProfileDisplayName.getText().toString().trim();
        profileBio = edtProfileBio.getText().toString().trim();

        if(profileDisplayName != null) {
            currentUser.put("displayName", profileDisplayName);
        }
        if(profileBio != null) {
            currentUser.put("bio", profileBio);
        }

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null) {
                    // for fragments use getContext
                    FancyToast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT,
                            FancyToast.DEFAULT, true).show();
                } else {
                    FancyToast.makeText(getContext(), "Could not update\n" + e.getMessage() + "\nPlease try again",
                            Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                    e.printStackTrace();
                }
            }
        });
        llEditProfile.setVisibility(View.GONE);
        updateProfileFields();
    }

}