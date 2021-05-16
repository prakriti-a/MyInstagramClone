package com.prakriti.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

public class UsersPosts extends AppCompatActivity {
// get username from users tsb fragment & display their posts and info

    private ParseFile userPostPicture;
    private String receivedUsername, userPostCaption;
    private ImageView imgUserPostImage;
    private TextView txtUserPostCaption;
    private LinearLayout llUsersPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_posts);

        // get username passed to it, getIntent() auto-identifies the passed intent
        Intent receivedUsernameIntent = getIntent();
        receivedUsername = receivedUsernameIntent.getStringExtra("username");

        setTitle(receivedUsername + "'s Profile");
        llUsersPosts = findViewById(R.id.llUsersPosts);

        getUserPostsFromServer();
    }

    private void getUserPostsFromServer() {
        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Photo");
        // only photos of selected user
        parseQuery.whereEqualTo("username", receivedUsername);
        // order by latest created on top
        parseQuery.orderByDescending("createdAt");

        // show progress dialog/bar here
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size()>0) {
                    for (ParseObject obj : objects) {
                            // init textview
                        txtUserPostCaption = new TextView(UsersPosts.this);
                        // get username & caption in textview, write code for validating null values
                        userPostCaption = obj.getString("caption");
                        txtUserPostCaption.setText(userPostCaption);

                        // get picture
                        userPostPicture = obj.getParseFile("picture");
                        userPostPicture.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if(e == null && data != null) {
                                    // convert byte array to bitmap
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        // init imageview
                                    imgUserPostImage = new ImageView(UsersPosts.this);
                                    LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams
                                            (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    img_params.setMargins(12, 12, 12, 12);
                                    imgUserPostImage.setLayoutParams(img_params);
                                    imgUserPostImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    imgUserPostImage.setImageBitmap(bitmap);

                                    // params for textview
                                    LinearLayout.LayoutParams text_params = new LinearLayout.LayoutParams
                                            (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    text_params.setMargins(12,12,12,12);
                                    txtUserPostCaption.setGravity(Gravity.LEFT);
                                    txtUserPostCaption.setTextSize(22f);
                                    txtUserPostCaption.setTextColor(Color.BLACK);

                                    // order in which to add items to linear layout
                                    llUsersPosts.addView(imgUserPostImage);
                                    llUsersPosts.addView(txtUserPostCaption);
                                }
                            }
                        });
                    }
                }
                // if there are no posts by user
                else {
                    FancyToast.makeText(UsersPosts.this, receivedUsername + " has no posts", Toast.LENGTH_SHORT,
                            FancyToast.INFO, true).show();
                    finish();
                }
            }
        });
    }
}