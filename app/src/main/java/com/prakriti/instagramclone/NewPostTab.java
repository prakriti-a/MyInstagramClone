package com.prakriti.instagramclone;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;

public class NewPostTab extends Fragment implements View.OnClickListener {

    private ImageView imgPostImage;
    private EditText edtPostCaption;
    private Button btnPostShare;
    private String caption;

    private Bitmap receivedImageBitmap;

    public NewPostTab() {}


    public static NewPostTab newInstance() {
        return new NewPostTab();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_post_tab, container, false);

        imgPostImage = view.findViewById(R.id.imgPostImage);
        edtPostCaption = view.findViewById(R.id.edtPostCaption);
        btnPostShare = view.findViewById(R.id.btnPostShare);

        imgPostImage.setOnClickListener(this);
        btnPostShare.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnPostShare:
                if(receivedImageBitmap != null) {
                    uploadSelectedImageToServer();
                }
                else {
                    FancyToast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                }
                break;

            case R.id.imgPostImage: // ask device access permission to user
                if(Build.VERSION.SDK_INT >= 23 &&
                        ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 666);
                        // will send permission result
                }
                else {
                    getChosenImage();
                }
                break;
        }
    }


    private void getChosenImage() {
        // FancyToast.makeText(getContext(), "Access granted", Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // type of action, data uri passed (application/service to be accessed)
        startActivityForResult(intent, 999); // activity result sent
    }


    // called when user responds to the permission request dialog
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 666) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getChosenImage();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // accessing images from fragments is diff from activity
        if(requestCode == 999) {
            if(resultCode == Activity.RESULT_OK) {
                try {
                    Uri selectedImage = data.getData(); // data has the image that is selected
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getActivity().getContentResolver()
                            .query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]); // first value in array
                    String picturePath = cursor.getString(columnIndex); // String path
                    cursor.close(); /// necessary to save resources

                    receivedImageBitmap = BitmapFactory.decodeFile(picturePath);
                    imgPostImage.setImageBitmap(receivedImageBitmap);
                }
                catch (Exception e) {
                    FancyToast.makeText(getContext(), "Unable to access image\n" + e.getMessage(), Toast.LENGTH_SHORT,
                            FancyToast.ERROR, true).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadSelectedImageToServer() {
        // convert image to array of bytes to upload to server, since image is large
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        receivedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        byte[] bytes = output.toByteArray();

        caption = edtPostCaption.getText().toString().trim();
        
        ParseFile parseFile = new ParseFile("img.png", bytes);
        ParseObject parseObject = new ParseObject("Photo");
        parseObject.put("picture", parseFile);
        parseObject.put("caption", caption);
        parseObject.put("username", ParseUser.getCurrentUser().getUsername());

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    FancyToast.makeText(getContext(), "Image uploaded!", Toast.LENGTH_SHORT,
                            FancyToast.SUCCESS, true).show();
                }
                else {
                    FancyToast.makeText(getContext(), "Unable to upload image\n" + e.getMessage(), Toast.LENGTH_SHORT,
                            FancyToast.ERROR, true).show();
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        });
    }

}