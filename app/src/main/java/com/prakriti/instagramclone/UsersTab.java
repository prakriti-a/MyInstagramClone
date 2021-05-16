package com.prakriti.instagramclone;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class UsersTab extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    //click listener for each user in list
    // long clicks on users list to get their info

    private ListView listViewUsers;
    private ArrayList<String> usersList;
    private ArrayAdapter arrayAdapter;

    public UsersTab() {}

    public static UsersTab newInstance(String param1, String param2) {
        return new UsersTab();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users_tab, container, false);

        listViewUsers = view.findViewById(R.id.listViewUsers);
        // populate listview with users from server using query // using array adapter
        usersList = new ArrayList(); // holds usernames
        arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, usersList);
            // passing a predefined list/row layout

        // set onItemClick listener for listview
        listViewUsers.setOnItemClickListener(this);
        listViewUsers.setOnItemLongClickListener(this);

        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
            // don't show current user on list
        parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getString("username"));

        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null) {
                    if(objects.size()>0) {
                        for(ParseUser user : objects) {
                               usersList.add(user.getUsername());
                        }
                        listViewUsers.setAdapter(arrayAdapter);
                    }
                }
                else {
                    FancyToast.makeText(getContext(), "Unable to retrieve users\n" + e.getMessage(), Toast.LENGTH_SHORT,
                            FancyToast.ERROR, true).show();
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // get username of tapped user and open new activity to see the posts
        Intent intent = new Intent(getContext(), UsersPosts.class);
            // arraylist holds the String usernames
        intent.putExtra("username", usersList.get(position));
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
        userParseQuery.whereEqualTo("username", usersList.get(position));
        // get info from server
        userParseQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null && user != null) {
                    // dialog with user info -> external library
                    String dialogMessage = user.getUsername() + "\n" + "Joined On: " + user.getCreatedAt();
                    new PrettyDialog(getContext())
                            .setTitle("Instagram User Info")
                            .setIcon(R.drawable.instagram) // can create new vector asset
                            .setMessage(dialogMessage)
//                            .addButton("OK", R.color.black, R.color.white, new PrettyDialogCallback() {
//                                @Override
//                                public void onClick() {
//                                    // nameOfDialog.dismiss;
//                                }
//                            })
                            .show();
                }
                else {
                    FancyToast.makeText(getContext(), "Unable to display user info", Toast.LENGTH_SHORT,
                            FancyToast.ERROR, true).show();
                    e.printStackTrace();
                }
            }
        });
        return true;
    }
}