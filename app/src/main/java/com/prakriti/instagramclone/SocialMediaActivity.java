package com.prakriti.instagramclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;

public class SocialMediaActivity extends AppCompatActivity {

    private Toolbar myToolbar; // import the right packages
    private ViewPager myViewPager;
    private TabLayout myTabLayout; // added dependency
    private TabAdapter tabAdapter;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);

        setTitle(R.string.app_name);

        myToolbar = findViewById(R.id.myToolbar);
        // activity already has Action Bar, so specify no action bar for this activity in manifests & themes file
        setSupportActionBar(myToolbar);

        myViewPager = findViewById(R.id.myViewPager);
        tabAdapter = new TabAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        myViewPager.setAdapter(tabAdapter); // now view pager can display all data in the tabs

        myTabLayout = findViewById(R.id.myTabLayout);
        myTabLayout.setupWithViewPager(myViewPager, true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.itemPostImage:
                // capture image from device - external storage permissions
                // code for permission asked from activity
                if(Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 16661); // unique request code
                    // will send permission result
                }
                else { captureImage(); }
                break;

            case R.id.itemLogout:
                ParseUser.getCurrentUser().logOut();
                finish(); // activity removed from stack
                // on logging out, move to sign up page instead of closing app
                startActivity(new Intent(SocialMediaActivity.this, SignUpActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 16661) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            }
        }
    }

    private void captureImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // type of action, data uri passed (application/service to be accessed)
        startActivityForResult(intent, 19991); // activity result sent
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            // Intent 'data' might be null -> @Nullable
        super.onActivityResult(requestCode, resultCode, data);
        // accessing images from activity is diff from fragment
        if(requestCode == 19991) {
            if(resultCode == Activity.RESULT_OK && data != null) {
                try {
                    // parse codes to upload image to server
                    Uri capturedImage = data.getData(); // data has the image that is selected
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), capturedImage);

                    // upload image to server
                    uploadCapturedImageToServer();
                }
                catch (Exception e) {
                    FancyToast.makeText(this, "Unable to access image\n" + e.getMessage(), Toast.LENGTH_SHORT,
                            FancyToast.ERROR, true).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadCapturedImageToServer() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        // compress bitmap to byte array
        byte[] bytes = outputStream.toByteArray();

     //   caption = edtPostCaption.getText().toString().trim();
        ParseFile parseFile = new ParseFile("img.png", bytes);
        ParseObject parseObject = new ParseObject("Photo"); // same class already created once
        parseObject.put("picture", parseFile);
     //   parseObject.put("caption", caption);
        parseObject.put("username", ParseUser.getCurrentUser().getUsername());

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    FancyToast.makeText(SocialMediaActivity.this, "Image uploaded!", Toast.LENGTH_SHORT,
                            FancyToast.SUCCESS, true).show();
                }
                else {
                    FancyToast.makeText(SocialMediaActivity.this, "Unable to upload image\n" + e.getMessage(), Toast.LENGTH_SHORT,
                            FancyToast.ERROR, true).show();
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        });
    }
}